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

package org.elasticsearch.search.fql;


import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;

public class FastRestResponse extends RestResponse {
    
    
    private final RestStatus status;
    private final BytesReference content;
    private final String contentType;
    /**
     * Creates a new plain text response.
     */
    public FastRestResponse(RestStatus status, String content) {
        this(status, new BytesArray(content));
    }
    public FastRestResponse( Exception e, XContentBuilder builder) throws IOException {
        this( ExceptionsHelper.status(e), builder.bytes());
    }
    public FastRestResponse(RestStatus status, XContentBuilder builder) {
        this(status, builder.bytes());
    }
    
    public FastRestResponse(RestStatus status, BytesReference content) {
        this.status = status;
        this.content = content;
        this.contentType = "aplication/xml; charset=UTF-8";
    }

    @Override
    public String contentType() {
        return this.contentType;
    }

    @Override
    public BytesReference content() {
        return this.content;
    }

    @Override
    public RestStatus status() {
        return this.status;
    }

}
