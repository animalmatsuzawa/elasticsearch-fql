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

import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.node.Node;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FastCompatiblePlugin extends Plugin implements ActionPlugin {

    public FastCompatiblePlugin(Settings settings) {
        this.settings = settings;
//        Environment environment = new Environment(this.settings);
    }    
    private FastCompatibleService fastService;
    private final Settings settings;

    @Override
    public Settings additionalSettings() {
        return Settings.EMPTY;
    }
    
    @Override
    public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool,
            ResourceWatcherService resourceWatcherService, ScriptService scriptService,
            NamedXContentRegistry xContentRegistry, Environment environment,
            NodeEnvironment nodeEnvironment, NamedWriteableRegistry namedWriteableRegistry) {


        fastService = new FastCompatibleService(settings, client, nodeEnvironment, clusterService, namedWriteableRegistry,
                nodeBuilder(environment.configFile()));
        return Collections.singleton(fastService);
    }
    
    protected Function<Settings, Node> nodeBuilder(Path configPath) {
        return settings -> new Node(new Environment(settings, configPath));
    }
    
    @Override
    public List<RestHandler> getRestHandlers(Settings settings, RestController restController, ClusterSettings clusterSettings,
            IndexScopedSettings indexScopedSettings, SettingsFilter settingsFilter, IndexNameExpressionResolver indexNameExpressionResolver,
            Supplier<DiscoveryNodes> nodesInCluster) {
        List<RestHandler> list = new ArrayList<RestHandler>();
        list.add(new FastSetNavigationAction(settings, restController,fastService)) ;
        list.add(new FastQueryLanguageAction(settings, restController,fastService)) ;
        
        return list;
    }
    
//    @Override
//    public List<ActionFilter> getActionFilters() {
//        return Arrays.asList(new FastCompatibleActionFilter(settings));
//    }
}
