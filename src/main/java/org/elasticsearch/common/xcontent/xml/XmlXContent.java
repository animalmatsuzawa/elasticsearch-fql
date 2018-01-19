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

package org.elasticsearch.common.xcontent.xml;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.FastStringReader;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentGenerator;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * A YAML based content implementation using Jackson.
 */
public class XmlXContent implements XContent {

    public static XContentBuilder contentBuilder() throws IOException {
        return XContentBuilder.builder(xmlXContent);
    }

    static final XmlFactory xmlFactory;
    public static final XmlXContent xmlXContent;

    static {
//        System.setProperty("javax.xml.stream.XMLInputFactory", WstxInputFactory.class.getName());
//        System.setProperty("javax.xml.stream.XMLOutputFactory", WstxOutputFactory.class.getName());

        XMLInputFactory inputFactory = new WstxInputFactory(); // do not use  XMLInputFactory.newInstance()
        inputFactory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
        inputFactory.setProperty("javax.xml.stream.isValidating", Boolean.FALSE);
        inputFactory.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
        inputFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);

        XMLOutputFactory outputFactory = new WstxOutputFactory(); // do not use  XMLOutputFactory.newInstance()
        outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);

        xmlFactory = new XmlFactory(inputFactory, outputFactory);
        xmlXContent = new XmlXContent();
    }

    public XmlXContent() {
    }

    @Override
    public XContentType type() {
        //return XmlXContentType.XML;
        return null;
    }

    @Override
    public byte streamSeparator() {
        throw new ElasticsearchParseException("xml does not support stream parsing...");
    }

    @Override
    public XContentGenerator createGenerator(OutputStream os, Set<String> includes, Set<String> excludes) throws IOException {
        return new XmlXContentGenerator(xmlFactory.createGenerator(os, JsonEncoding.UTF8), os);
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, String content) throws IOException {
        return new XmlXContentParser(xContentRegistry, xmlFactory.createParser(new FastStringReader(content)));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, InputStream is) throws IOException {
        return new XmlXContentParser(xContentRegistry, xmlFactory.createParser(is));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, byte[] data) throws IOException {
        return new XmlXContentParser(xContentRegistry, xmlFactory.createParser(data));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, byte[] data, int offset, int length) throws IOException {
        return new XmlXContentParser(xContentRegistry, xmlFactory.createParser(data, offset, length));
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, BytesReference bytes) throws IOException {
        return createParser(xContentRegistry, bytes.streamInput());
    }

    @Override
    public XContentParser createParser(NamedXContentRegistry xContentRegistry, Reader reader) throws IOException {
        return new XmlXContentParser(xContentRegistry, xmlFactory.createParser(reader));
    }
}
