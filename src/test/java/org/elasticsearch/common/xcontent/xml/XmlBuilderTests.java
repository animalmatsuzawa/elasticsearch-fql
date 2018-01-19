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

import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XmlXContentFactory;
import org.elasticsearch.common.xcontent.XmlXContentType;
import org.elasticsearch.test.ESTestCase;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.elasticsearch.common.xcontent.XmlXContentFactory.xmlBuilder;

public class XmlBuilderTests extends ESTestCase {

    public void testXml() throws Exception {
        XContentBuilder builder = xmlBuilder();
        builder.startObject().field("Hello", "World").endObject();
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\"><Hello>World</Hello></root>",
                builder.string()
        );
    }

    public void testXmlParams() throws Exception {
        XmlXParams params = new XmlXParams();
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;

        builder.startObject().field("Hello", "World").endObject();
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\"><Hello>World</Hello></root>",
                builder.string()

        );
    }

    public void testXmlNamespaces() throws Exception {
        XmlNamespaceContext context = XmlNamespaceContext.getDefaultInstance();
        XmlXParams params = new XmlXParams(context);
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;

        builder.startObject()
                .field("dc:creator", "John Doe")
                .endObject();
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\" xmlns:atom=\"http://www.w3.org/2005/Atom\" " +
                        "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
                        "xmlns:dcterms=\"http://purl.org/dc/terms/\" " +
                        "xmlns:es=\"http://elasticsearch.org/ns/1.0/\" " +
                        "xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" "
                        + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\" "
                        + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                        + "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" "
                        + "xmlns:xalan=\"http://xml.apache.org/xslt\" "
                        + "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><dc:creator>John Doe</dc:creator></root>",
                builder.string()
        );
    }

    public void testXmlCustomNamespaces() throws Exception {
        QName root = new QName("http://elasticsearch.org/ns/1.0/", "result", "");
        XmlNamespaceContext context = XmlNamespaceContext.getDefaultInstance();
        context.addNamespace("abc", "http://localhost");
        XmlXParams params = new XmlXParams(root, context);
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;

        builder.startObject()
                .field("abc:creator", "John Doe")
                .endObject();
//        System.err.println(builder.string());
        assertEquals(
                "<result xmlns=\"http://elasticsearch.org/ns/1.0/\" xmlns:abc=\"http://localhost\" " +
                        "xmlns:atom=\"http://www.w3.org/2005/Atom\" " +
                        "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
                        "xmlns:dcterms=\"http://purl.org/dc/terms/\" " +
                        "xmlns:es=\"http://elasticsearch.org/ns/1.0/\" " +
                        "xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" "
                        + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\" "
                        + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                        + "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" "
                        + "xmlns:xalan=\"http://xml.apache.org/xslt\" "
                        + "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><abc:creator>John Doe</abc:creator></result>",
                builder.string()
                );
    }

    public void testXmlObject() throws Exception {
        XmlXParams params = new XmlXParams();
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;

        builder.startObject()
                .startObject("author")
                .field("creator", "John Doe")
                .field("role", "writer")
                .endObject()
                .startObject("author")
                .field("creator", "Joe Smith")
                .field("role", "illustrator")
                .endObject()
                .endObject();
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\">"
                + "<author><creator>John Doe</creator><role>writer</role></author>"
                + "<author><creator>Joe Smith</creator><role>illustrator</role></author></root>",
                builder.string());
    }

    public void testXmlAttributes() throws Exception {
        XmlNamespaceContext namespaceContext = XmlNamespaceContext.newInstance();
        namespaceContext.addNamespace("es", "http://elasticsearch.org/ns/1.0/");
        XmlXParams params = new XmlXParams(namespaceContext);
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;
 
        builder.startObject()
            .startObject("author")
            .field("@name", "John Doe")
            .field("@id", 1)
            .endObject()
            .endObject();
 
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\" "
                + "xmlns:es=\"http://elasticsearch.org/ns/1.0/\">"
                + "<author es:name=\"John Doe\" es:id=\"1\"/></root>",
                builder.string());
    }

    public void testXmlAttributesValue() throws Exception {
        XmlNamespaceContext namespaceContext = XmlNamespaceContext.newInstance();
        namespaceContext.addNamespace("es", "http://elasticsearch.org/ns/1.0/");
        XmlXParams params = new XmlXParams(namespaceContext);
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;
 
        builder.startObject()
            .startObject("author")
            .field("@name", "John Doe")
            .field("@id", 1)
            .field("#author")
            .value("value")
            .endObject()
            .endObject();
 
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\" "
                + "xmlns:es=\"http://elasticsearch.org/ns/1.0/\">"
                + "<author es:name=\"John Doe\" es:id=\"1\">value</author></root>",
                builder.string());
    }
    
    public void testXmlArrayOfValues() throws Exception {
        XmlXParams params = new XmlXParams();
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;

        builder.startObject()
                .array("author", "John Doe", "Joe Smith")
                .endObject();
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\"><author>John Doe</author><author>Joe Smith</author></root>",
                builder.string()
        );
    }

    public void testXmlArrayOfObjects() throws Exception {
        XmlXParams params = new XmlXParams();
        XContentBuilder builder = xmlBuilder();
        XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
        gen.setParams(params) ;

        builder.startObject()
                .startArray("author")
                .startObject()
                .field("creator", "John Doe")
                .field("role", "writer")
                .endObject()
                .startObject()
                .field("creator", "Joe Smith")
                .field("role", "illustrator")
                .endObject()
                .endArray()
                .endObject();
        assertEquals(
                "<root xmlns=\"http://elasticsearch.org/ns/1.0/\">"
                + "<author><creator>John Doe</creator><role>writer</role></author>"
                + "<author><creator>Joe Smith</creator><role>illustrator</role></author></root>",
                builder.string()
        );
    }

    public void testParseJson() throws Exception {
        XmlNamespaceContext context = XmlNamespaceContext.getDefaultInstance();
        context.addNamespace("bib","info:srw/cql-context-set/1/bib-v1/");
        context.addNamespace("abc", "http://localhost/");
        context.addNamespace("xbib", "http://xbib.org/");
        context.addNamespace("lia", "http://xbib.org/namespaces/lia/");
        XmlXParams params = new XmlXParams(context);
        InputStream in = getClass().getResourceAsStream("/test.json");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Streams.copy(in, out);
        byte[] buf = out.toByteArray();
        String s = convertToXml(params, buf, 0, buf.length, false);
        assertEquals(53194, s.length());
    }

    public void testDynamicNamespaces() throws Exception {
        XmlNamespaceContext context = XmlNamespaceContext.getDefaultInstance();
        XmlXParams params = new XmlXParams(context);
        InputStream in = getClass().getResourceAsStream("/dynamic-namespace.json");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Streams.copy(in, out);
        byte[] buf = out.toByteArray();
        String s = convertToXml(params, buf, 0, buf.length, false);
        assertEquals(
            "<root xmlns=\"http://elasticsearch.org/ns/1.0/\" "
            + "xmlns:atom=\"http://www.w3.org/2005/Atom\" "
            + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
            + "xmlns:dcterms=\"http://purl.org/dc/terms/\" "
            + "xmlns:es=\"http://elasticsearch.org/ns/1.0/\" "
            + "xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" "
            + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\" "
            + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
            + "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" "
            + "xmlns:xalan=\"http://xml.apache.org/xslt\" "
            + "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
            + "<context es:ns=\"http://example.org/\" "
            + "xmlns:ns=\"http://example.org/\"/>"
            + "<wstxns1:foo xmlns:wstxns1=\"http://example.org/\">bar</wstxns1:foo></root>",
            s
        );
    }

    public static String convertToXml(XmlXParams params, byte[] data, int offset, int length) throws IOException {
        return convertToXml(params, data, offset, length, false);
    }

    public static String convertToXml(XmlXParams params, byte[] data, int offset, int length, boolean prettyPrint) throws IOException {
        XmlXContentType xmlXContentType = XmlXContentFactory.xContentType(data, offset, length);
        XContentParser parser = null;
        try {
            parser = XmlXContentFactory.xContent(xmlXContentType).createParser(NamedXContentRegistry.EMPTY, data, offset, length);
            parser.nextToken();
            XContentBuilder builder = XmlXContentFactory.xmlBuilder();
            XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator();
            gen.setParams(params) ;
            if (prettyPrint) {
                builder.prettyPrint();
            }
            builder.copyCurrentStructure(parser);
            return builder.string();
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }


}