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
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.action.support.ActionFilterChain;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.tasks.Task;

public class FastCompatibleActionFilter extends AbstractComponent implements ActionFilter{
//    NodeClient client ;
    protected final Logger logger = Loggers.getLogger(FastCompatibleActionFilter.class);
    
    @Inject
    public FastCompatibleActionFilter(Settings settings) {
        super(settings);
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void apply(Task task, String action, Request request,
            ActionListener<Response> listener, ActionFilterChain<Request, Response> chain) {
        logger.debug("ActionFilter######## :"+action);

        chain.proceed(task, action, request, new ActionListener<Response>() {
            @Override
            public void onResponse(Response response) {
                listener.onResponse(response);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
        
    }

}
