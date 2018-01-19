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

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestStatusToXContentListener;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.rest.RestRequest.Method.PUT;

/**
 * FAST query時にAggregationを行う条件を保存する。
 * 保存先はFastCompatibleServiceにて作成されたindex
 * 
 * curl -XPUT -H 'Content-Type:application/json' 'http://localhost:9200/_fast/.work/_navigation?pretty' -d '{
 * "aggs" : {
 *         "genres" : {
 *             "terms" : { "field" : "keyword" , "size":5}
 *         },
 *         "price_ranges" : {
 *             "range" : {
 *                 "field" : "frequency",
 *                 "ranges" : [
 *                     { "to" : 10 },
 *                     { "from" : 10, "to" : 100 },
 *                     { "from" : 100, "to" : 1000 }, { "from" : 1000}
 *                 ]
 *             }
 *         }
 * }
 * }
 */
public class FastSetNavigationAction extends BaseRestHandler {
    
    FastCompatibleService fastService;
    public FastSetNavigationAction(Settings settings, RestController controller, FastCompatibleService fastService) {
        super(settings);
        this.fastService = fastService ;
        controller.registerHandler(PUT, "/_fast/{index}/_navigation/", this);
        controller.registerHandler(PUT, "/_fast/{index}/{type}/_navigation", this);
        controller.registerHandler(PUT, "/_fast/{index}/_navigation/{type}", this);
//        controller.registerHandler(PUT, "/_fast/_navigation/{type}", this);

        controller.registerHandler(POST, "/_fast/{index}/_navigation/", this);
        controller.registerHandler(POST, "/_fast/{index}/{type}/_navigation", this);
        controller.registerHandler(POST, "/_fast/{index}/_navigation/{type}", this);
//        controller.registerHandler(POST, "/_fast/_navigation/{type}", this);
    }

    @Override
    public String getName() {
        return "fast_setnavigation_action";
    }

    
    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
//        PutMappingRequest putMappingRequest = putMappingRequest(Strings.splitStringByCommaToArray(request.param("index")));

        String _index = request.param("index");
        String _type = request.param("type") ;
        if( _type == null)
        {
            _type = FastCompatibleService.DEFAULT_CONSTRAINT_TYPE ;
        }

        String source = null ;
        Objects.requireNonNull(request.getXContentType());
        try {
//            source = XContentHelper.convertToJson(request.requiredContent(), false, false, request.getXContentType());
            source = XContentHelper.convertToJson(request.content(), false, false, request.getXContentType());
        } catch (IOException e) {
                throw new UncheckedIOException("failed to convert source to json", e);
        }
        final Map<String, Object> sourceMap = new HashMap<String, Object>();
        sourceMap.put("source", source);
        sourceMap.put("lastModified", new Date());
        
        _index = Base64.getEncoder().encodeToString(_index.getBytes(StandardCharsets.UTF_8)) ;
        _type = Base64.getEncoder().encodeToString(_type.getBytes(StandardCharsets.UTF_8)) ;

        IndexRequest indexRequest = new IndexRequest(this.fastService.constraintIndex, _index, _type);
        
        // sourceのチェック　aggregationとして有効か？
        this.fastService.parseAggregation(request.getXContentRegistry(), source) ;
        
        indexRequest.source(sourceMap);

        return channel ->
                client.index(indexRequest, new RestStatusToXContentListener<>(channel, r -> r.getLocation(indexRequest.routing())));
    }
}

