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
package org.elasticsearch.common.xcontent;

import org.elasticsearch.test.ESTestCase;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class XmlContentTypeTests extends ESTestCase {
    
    
    public void testFromXml() throws Exception {
        String mediaType = "application/xml";
        XmlXContentType expectedXContentType = XmlXContentType.XML;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + "; charset=UTF-8"), equalTo(expectedXContentType));
    }
    
    public void testFromJson() throws Exception {
        String mediaType = "application/json";
        XmlXContentType expectedXContentType = XmlXContentType.JSON;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + "; charset=UTF-8"), equalTo(expectedXContentType));
    }

    public void testFromJsonUppercase() throws Exception {
        String mediaType = "application/json".toUpperCase(Locale.ROOT);
        XmlXContentType expectedXContentType = XmlXContentType.JSON;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + "; charset=UTF-8"), equalTo(expectedXContentType));
    }

    public void testFromYaml() throws Exception {
        String mediaType = "application/yaml";
        XmlXContentType expectedXContentType = XmlXContentType.YAML;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + "; charset=UTF-8"), equalTo(expectedXContentType));
    }

    public void testFromSmile() throws Exception {
        String mediaType = "application/smile";
        XmlXContentType expectedXContentType = XmlXContentType.SMILE;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
    }

    public void testFromCbor() throws Exception {
        String mediaType = "application/cbor";
        XmlXContentType expectedXContentType = XmlXContentType.CBOR;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
    }

    public void testFromWildcard() throws Exception {
        String mediaType = "application/*";
        XmlXContentType expectedXContentType = XmlXContentType.JSON;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
    }

    public void testFromWildcardUppercase() throws Exception {
        String mediaType = "APPLICATION/*";
        XmlXContentType expectedXContentType = XmlXContentType.JSON;
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType), equalTo(expectedXContentType));
        assertThat(XmlXContentType.fromMediaTypeOrFormat(mediaType + ";"), equalTo(expectedXContentType));
    }

    public void testFromRubbish() throws Exception {
        assertThat(XmlXContentType.fromMediaTypeOrFormat(null), nullValue());
        assertThat(XmlXContentType.fromMediaTypeOrFormat(""), nullValue());
        assertThat(XmlXContentType.fromMediaTypeOrFormat("text/plain"), nullValue());
        assertThat(XmlXContentType.fromMediaTypeOrFormat("gobbly;goop"), nullValue());
    }
}
