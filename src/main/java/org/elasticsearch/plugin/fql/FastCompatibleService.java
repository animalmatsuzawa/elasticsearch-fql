/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.plugin.fql;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterChangedEvent;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.ClusterStateListener;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterStateHealth;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * FAST Query時にAggregationを行うためのindexの作成を行う
 */
public class FastCompatibleService extends AbstractLifecycleComponent implements ClusterStateListener{
    protected final Logger logger = Loggers.getLogger(FastCompatibleService.class);
    public static final String DEFAULT_CONSTRAINT_TYPE = ".default";

    private static final String DEFAULT_CONSTRAINT_INDEX_NAME = ".fast";
    
    private final ClusterService clusterService;
    private final NamedWriteableRegistry namedWriteableRegistry;

    private Client client;
    protected String constraintIndex;

    private AtomicBoolean initializing = new AtomicBoolean(false);
    private AtomicBoolean created = new AtomicBoolean(false);
    
    public FastCompatibleService(Settings settings, Client client, NodeEnvironment nodeEnvironment, ClusterService clusterService,
            NamedWriteableRegistry namedWriteableRegistry, Function<Settings, Node> clientNodeBuilder) {
        super(settings);
        this.client = client ;
        this.clusterService = clusterService;
        this.namedWriteableRegistry = namedWriteableRegistry;
        this.deprecationLogger.deprecated("", "");
        this.created.set(false);
        
        constraintIndex = settings.get("auth.constraint.index",
                DEFAULT_CONSTRAINT_INDEX_NAME);
    }

    @Override
    protected void doStart() {
        logger.info("FastCompatibleService.doStart()");
        clusterService.addListener(this);
    }

    @Override
    protected void doStop() {
        clusterService.removeListener(this);
    }

    @Override
    protected void doClose() throws IOException {
    }
    
   /**
    * 初期処理
    * indexの作成を行う
    */
    public void init(final ActionListener<Void> listener) {
        client.admin().cluster().prepareHealth().setWaitForYellowStatus()
            .execute(new ActionListener<ClusterHealthResponse>() {
                    @Override
                    public void onResponse(final ClusterHealthResponse response) {
                        if (response.getStatus() == ClusterHealthStatus.RED ||
                                lifecycle.stoppedOrClosed() ){
// @@@@@                                lifecycle.stoppedOrClosed() ||
// @@@@@                                created.get()) {
//                            listener.onFailure(new RuntimeException("This cluster is not ready."));
                        } else {
                            // indexの存在確認
                            createConstraintIndexIfNotExist(listener);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
            });
    }

    protected void createConstraintIndexIfNotExist(final ActionListener<Void> listener) {
        client.admin().indices().prepareExists(constraintIndex)
                .execute(new ActionListener<IndicesExistsResponse>() {
                    @Override
                    public void onResponse(final IndicesExistsResponse response) {
                        if (response.isExists()) {
                            reload(listener);
                        } else {
                            if (lifecycle.stoppedOrClosed() ) {
                                listener.onFailure(new RuntimeException("This cluster is not ready."));
                            } else {                            
                                // 存在しない場合、作成
                                createConstraintIndex(listener);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    protected void createConstraintIndex(final ActionListener<Void> listener) {
        client.admin().indices().prepareCreate(constraintIndex)
                .setSettings(Settings.builder()
                    .put("index.number_of_shards",1)
                    .put("index.number_of_replicas",0))
                .execute(new ActionListener<CreateIndexResponse>() {
                    @Override
                    public void onResponse(final CreateIndexResponse response) {
                        // TODO health check
                        reload(listener);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    public void reload(final ActionListener<Void> listener) {
        client.admin().indices().prepareRefresh(constraintIndex)
                .execute(new ActionListener<RefreshResponse>() {
                    @Override
                    public void onResponse(final RefreshResponse response) {
                        logger.debug("onResponse().");
                        created.set(true);
                        listener.onResponse(null);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    @Override
    public void clusterChanged(ClusterChangedEvent event) {
        if (lifecycle.stoppedOrClosed()) {
            return;
        }

        final ClusterState state = event.state();

        if (state.nodes().isLocalNodeElectedMaster() == false) {
            // not our job to recover
            return;
        }
        
        ClusterStateHealth helth = new ClusterStateHealth(state);
        if (helth.getStatus() == ClusterHealthStatus.RED) {
            return;
        }
        // ClusterHealthStatusがYELLOW以上になった場合、初期化処理を行う

        DiscoveryNodes nodes = state.nodes();
        if (!initializing.getAndSet(true)) {
            this.init(new ActionListener<Void>() {
                @Override
                public void onResponse(Void response) {
                    initializing.set(false);
                }

                @Override
                public void onFailure(Exception e) {
                    initializing.set(false);
                    logger.warn("Failed to reload FastCompatibleService.", e);
                }
            });
        }
    }
    
    /**
     * Aggregation定義のパース処理を行う
     */
    public List<AggregationBuilder> parseAggregation( NamedXContentRegistry content_registory, String aggrigstion ) throws IOException {
        List<AggregationBuilder> agg_list = null ;
        XContentParser parser = XContentType.JSON.xContent().createParser(content_registory, aggrigstion ) ;
        
        XContentParser.Token token = parser.currentToken();
        String currentFieldName = null;
        if (token != XContentParser.Token.START_OBJECT && (token = parser.nextToken()) != XContentParser.Token.START_OBJECT) {
            throw new ParsingException(parser.getTokenLocation(), "Expected [" + XContentParser.Token.START_OBJECT +
                    "] but found [" + token + "]", parser.getTokenLocation());
        }
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                throw new ParsingException(parser.getTokenLocation(), "Unknown key for a " + token + " in [" + currentFieldName + "].",
                        parser.getTokenLocation());
            } else if (token == XContentParser.Token.START_OBJECT) {
                if (SearchSourceBuilder.AGGREGATIONS_FIELD.match(currentFieldName)
                    || SearchSourceBuilder.AGGS_FIELD.match(currentFieldName)) {
                    AggregatorFactories.Builder aggregations = AggregatorFactories.parseAggregators(parser);
                    agg_list = aggregations.getAggregatorFactories() ;
                }
                else
                {
                    throw new ParsingException(parser.getTokenLocation(), "Unknown key for a " + token + " in [" + currentFieldName + "].",
                            parser.getTokenLocation());
                }
            } else {
                throw new ParsingException(parser.getTokenLocation(), "Unknown key for a " + token + " in [" + currentFieldName + "].",
                        parser.getTokenLocation());
            }
        }
        return agg_list ;
        
    }
    
}
