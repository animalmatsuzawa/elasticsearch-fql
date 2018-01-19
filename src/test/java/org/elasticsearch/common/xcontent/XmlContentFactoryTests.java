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

import com.fasterxml.jackson.dataformat.cbor.CBORConstants;
import com.fasterxml.jackson.dataformat.smile.SmileConstants;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.test.ESTestCase;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;

public class XmlContentFactoryTests extends ESTestCase {
    public void testGuessJson() throws IOException {
        testGuessType(XmlXContentType.JSON);
    }

    public void testGuessSmile() throws IOException {
        testGuessType(XmlXContentType.SMILE);
    }

    public void testGuessYaml() throws IOException {
        testGuessType(XmlXContentType.YAML);
    }

    public void testGuessCbor() throws IOException {
        testGuessType(XmlXContentType.CBOR);
    }

    public void testGuessXml() throws IOException {
        testGuessType(XmlXContentType.XML);
    }
    
    private void testGuessType(XmlXContentType type) throws IOException {
        XContentBuilder builder = XmlXContentFactory.contentBuilder(type);
        builder.startObject();
        builder.field("field1", "value1");
        builder.endObject();

        assertThat(XmlXContentFactory.xContentType(builder.bytes()), equalTo(type));
        assertThat(XmlXContentFactory.xContentType(builder.bytes().streamInput()), equalTo(type));

        // CBOR is binary, cannot use String
        if (type != XmlXContentType.CBOR && type != XmlXContentType.SMILE) {
            assertThat(XmlXContentFactory.xContentType(builder.string()), equalTo(type));
        }
    }

    public void testCBORBasedOnMajorObjectDetection() {
        // for this {"f "=> 5} perl encoder for example generates:
        byte[] bytes = new byte[] {(byte) 0xA1, (byte) 0x43, (byte) 0x66, (byte) 6f, (byte) 6f, (byte) 0x5};
        assertThat(XmlXContentFactory.xContentType(bytes), equalTo(XmlXContentType.CBOR));
        //assertThat(((Number) XContentHelper.convertToMap(bytes, true).v2().get("foo")).intValue(), equalTo(5));

        // this if for {"foo" : 5} in python CBOR
        bytes = new byte[] {(byte) 0xA1, (byte) 0x63, (byte) 0x66, (byte) 0x6f, (byte) 0x6f, (byte) 0x5};
        assertThat(XmlXContentFactory.xContentType(bytes), equalTo(XmlXContentType.CBOR));
        assertThat(((Number) XContentHelper.convertToMap(new BytesArray(bytes), true).v2().get("foo")).intValue(), equalTo(5));

        // also make sure major type check doesn't collide with SMILE and JSON, just in case
        assertThat(CBORConstants.hasMajorType(CBORConstants.MAJOR_TYPE_OBJECT, SmileConstants.HEADER_BYTE_1), equalTo(false));
        assertThat(CBORConstants.hasMajorType(CBORConstants.MAJOR_TYPE_OBJECT, (byte) '{'), equalTo(false));
        assertThat(CBORConstants.hasMajorType(CBORConstants.MAJOR_TYPE_OBJECT, (byte) ' '), equalTo(false));
        assertThat(CBORConstants.hasMajorType(CBORConstants.MAJOR_TYPE_OBJECT, (byte) '-'), equalTo(false));
    }

    public void testCBORBasedOnMagicHeaderDetection() {
        byte[] bytes = new byte[] {(byte) 0xd9, (byte) 0xd9, (byte) 0xf7};
        assertThat(XmlXContentFactory.xContentType(bytes), equalTo(XmlXContentType.CBOR));
    }

    public void testEmptyStream() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
        assertNull(XmlXContentFactory.xContentType(is));

        is = new ByteArrayInputStream(new byte[] {(byte) 1});
        assertNull(XmlXContentFactory.xContentType(is));
    }

    public void testInvalidStream() throws Exception {
        byte[] bytes = new byte[] { (byte) '"' };
        assertNull(XmlXContentFactory.xContentType(bytes));

        bytes = new byte[] { (byte) 'x' };
        assertNull(XmlXContentFactory.xContentType(bytes));
    }

    public void testJsonFromBytesOptionallyPrecededByUtf8Bom() throws Exception {
        byte[] bytes = new byte[] {(byte) '{', (byte) '}'};
        assertThat(XmlXContentFactory.xContentType(bytes), equalTo(XmlXContentType.JSON));

        bytes = new byte[] {(byte) 0x20, (byte) '{', (byte) '}'};
        assertThat(XmlXContentFactory.xContentType(bytes), equalTo(XmlXContentType.JSON));

        bytes = new byte[] {(byte) 0xef, (byte) 0xbb, (byte) 0xbf, (byte) '{', (byte) '}'};
        assertThat(XmlXContentFactory.xContentType(bytes), equalTo(XmlXContentType.JSON));

        bytes = new byte[] {(byte) 0xef, (byte) 0xbb, (byte) 0xbf, (byte) 0x20, (byte) '{', (byte) '}'};
        assertThat(XmlXContentFactory.xContentType(bytes), equalTo(XmlXContentType.JSON));
    }
}
