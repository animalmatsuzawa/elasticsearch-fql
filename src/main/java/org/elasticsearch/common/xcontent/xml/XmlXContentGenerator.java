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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentGenerator;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.XmlXContentFactory ;
import org.elasticsearch.common.xcontent.XmlXContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;

/**
 *
 * Content generator for XML format
 *
 */
public class XmlXContentGenerator implements XContentGenerator {
    final Logger logger = Loggers.getLogger(XmlXContentGenerator.class);

    protected final ToXmlGenerator generator;

    private XmlXParams params;

    private boolean started;

    private boolean context;

    private String prefix;
    
    private final OutputStream os;

    public XmlXContentGenerator(ToXmlGenerator generator, OutputStream os) {
        this.generator = generator;
        this.params = new XmlXParams();
        this.started = false;
        this.context = false;
        this.prefix = null;
        this.os = os;
        generator.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
    }

    public XmlXContentGenerator setParams(XmlXParams params) {
        this.params = params;
        return this;
    }

    public XmlNamespaceContext getNamespaceContext() {
        return params.getNamespaceContext();
    }

    @Override
    public XContentType contentType() {
        //return XmlXContentType.XML;
        return null;
    }

    @Override
    public void usePrettyPrint() {
        generator.useDefaultPrettyPrinter();
    }

    @Override
    public void usePrintLineFeedAtEnd() {
        // nothing here
    }

    @Override
    public void writeStartArray() throws IOException {
        generator.writeStartArray();
    }

    @Override
    public void writeEndArray() throws IOException {
        generator.writeEndArray();
    }

    @Override
    public void writeStartObject() throws IOException {
        try {
            if (!started) {
                generator.getStaxWriter().setDefaultNamespace(params.getQName().getNamespaceURI());
                generator.startWrappedValue(null, params.getQName());
            }
            generator.writeStartObject();
            if (!started ) {
                if (getNamespaceContext() != null &&  getNamespaceContext().getNamespaces() != null) {
                    for (String prefix : getNamespaceContext().getNamespaces().keySet()) {
                        generator.getStaxWriter().writeNamespace(prefix, getNamespaceContext().getNamespaceURI(prefix));
                    }
                }
                started = true;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public void writeEndObject() throws IOException {
        generator.writeEndObject();
        context = false;
    }

    @Override
    public void writeFieldName(String name) throws IOException {
        writeFieldNameXml(name);
    }


    @Override
    public void writeString(String text) throws IOException {
        try {
            generator.writeString(text);
            if (context && prefix != null) {
                params.getNamespaceContext().addNamespace(prefix, text);
                generator.getStaxWriter().writeNamespace(prefix, text);
                prefix = null;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + ": " + text, e);
        }
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException {
        String s = new String(text, offset, len);
        try {
            generator.writeString(s);
            if (context && prefix != null) {
                params.getNamespaceContext().addNamespace(prefix, s);
                generator.getStaxWriter().writeNamespace(prefix, s);
                prefix = null;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + ": " + s, e);
        }
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        String s = new String(text, offset, length, StandardCharsets.UTF_8);
        try {
            generator.writeUTF8String(text, offset, length);
            if (context && prefix != null) {
                params.getNamespaceContext().addNamespace(prefix, s);
                generator.getStaxWriter().writeNamespace(prefix, s);
                prefix = null;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + ": " + s, e);
        }
    }

    @Override
    public void writeBinary(byte[] data, int offset, int len) throws IOException {
        generator.writeBinary(data, offset, len);
    }

    @Override
    public void writeBinary(byte[] data) throws IOException {
        generator.writeBinary(data);
    }

    @Override
    public void writeNumber(short v) throws IOException {
        generator.writeNumber(v);
    }

    @Override
    public void writeNumber(int v) throws IOException {
        generator.writeNumber(v);
    }

    @Override
    public void writeNumber(long v) throws IOException {
        generator.writeNumber(v);
    }

    @Override
    public void writeNumber(double d) throws IOException {
        generator.writeNumber(d);
    }

    @Override
    public void writeNumber(float f) throws IOException {
        generator.writeNumber(f);
    }

    @Override
    public void writeBoolean(boolean state) throws IOException {
        generator.writeBoolean(state);
    }

    @Override
    public void writeNull() throws IOException {
        generator.writeNull();
    }


    @Override
    public void writeStringField(String fieldName, String value) throws IOException {
        try {
            this.writeFieldName(fieldName);
            this.writeString(value);
//            generator.writeStringField(fieldName, value);
            if (context && value != null) {
                params.getNamespaceContext().addNamespace(fieldName, value);
                generator.getStaxWriter().writeNamespace(fieldName, value);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage() + ": " + fieldName + "=" + value, e);
        }
    }


    @Override
    public void writeBooleanField(String fieldName, boolean value) throws IOException {
        this.writeFieldName(fieldName);
        this.writeBoolean(value);
//        generator.writeBooleanField(fieldName, value);
    }

    @Override
    public void writeNullField(String fieldName) throws IOException {
        this.writeFieldName(fieldName);
        this.writeNull();
//        generator.writeNullField(fieldName);
    }

    @Override
    public void writeNumberField(String fieldName, int value) throws IOException {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
//        generator.writeNumberField(fieldName, value);
    }


    @Override
    public void writeNumberField(String fieldName, long value) throws IOException {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
//        generator.writeNumberField(fieldName, value);
    }


    @Override
    public void writeNumberField(String fieldName, double value) throws IOException {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
//        generator.writeNumberField(fieldName, value);
    }

    @Override
    public void writeNumberField(String fieldName, float value) throws IOException {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
//        generator.writeNumberField(fieldName, value);
    }

    @Override
    public void writeBinaryField(String fieldName, byte[] data) throws IOException {
        this.writeFieldName(fieldName);
        this.writeBinary(data);
//        generator.writeBinaryField(fieldName, data);
    }


    public void writeArrayFieldStart(String fieldName) throws IOException {
        generator.writeArrayFieldStart(fieldName);
    }

    public void writeObjectFieldStart(String fieldName) throws IOException {
        generator.writeObjectFieldStart(fieldName);
    }


    @Override
    public void writeRawField(String fieldName, InputStream content) throws IOException {
        writeFieldNameXml(fieldName);
        try (JsonParser parser = XmlXContent.xmlFactory.createParser(content)) {
            parser.nextToken();
            generator.copyCurrentStructure(parser);
        }
    }

    @Override
    public void writeRawField(String fieldName, BytesReference content) throws IOException {
        XmlXContentType contentType = XmlXContentFactory.xContentType(BytesReference.toBytes(content));
        if( contentType == XmlXContentType.XML ) {
            writeFieldNameXml(fieldName);
            try (JsonParser parser = XmlXContent.xmlFactory.createParser(BytesReference.toBytes(content))) {
                parser.nextToken();
                generator.copyCurrentStructure(parser);
            }
        }
        else if( contentType == null ) {
            throw new IllegalArgumentException("Can't write raw bytes whose xcontent-type can't be guessed");
        }
        else {
            XContentType type = XmlXContentType.toXContentType(contentType) ;
            writeRawField(fieldName, content, type);
        }
    }

    @Override
    public void writeRawValue(BytesReference content) throws IOException {
        generator.writeRawValue(content.utf8ToString());
    }

    @Override
    public void copyCurrentStructure(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser instanceof XmlXContentParser) {
            generator.copyCurrentStructure(((XmlXContentParser) parser).parser);
        } else {
            copyCurrentStructure(this, parser);
        }
    }

    public static void copyCurrentStructure(XContentGenerator generator, XContentParser parser) throws IOException {
        XContentParser.Token t = parser.currentToken();

        // Let's handle field-name separately first
        if (t == XContentParser.Token.FIELD_NAME) {
            generator.writeFieldName(parser.currentName());
            t = parser.nextToken();
            // fall-through to copy the associated value
        }

        switch (t) {
            case START_ARRAY:
                generator.writeStartArray();
                while (parser.nextToken() != XContentParser.Token.END_ARRAY) {
                    copyCurrentStructure(generator, parser);
                }
                generator.writeEndArray();
                break;
            case START_OBJECT:
                generator.writeStartObject();
                while (parser.nextToken() != XContentParser.Token.END_OBJECT) {
                    copyCurrentStructure(generator, parser);
                }
                generator.writeEndObject();
                break;
            default: // others are simple:
                copyCurrentEvent(generator, parser);
        }
    }

    public static void copyCurrentEvent(XContentGenerator generator, XContentParser parser) throws IOException {
        switch (parser.currentToken()) {
            case START_OBJECT:
                generator.writeStartObject();
                break;
            case END_OBJECT:
                generator.writeEndObject();
                break;
            case START_ARRAY:
                generator.writeStartArray();
                break;
            case END_ARRAY:
                generator.writeEndArray();
                break;
            case FIELD_NAME:
                generator.writeFieldName(parser.currentName());
                break;
            case VALUE_STRING:
                if (parser.hasTextCharacters()) {
                    generator.writeString(parser.textCharacters(), parser.textOffset(), parser.textLength());
                } else {
                    generator.writeString(parser.text());
                }
                break;
            case VALUE_NUMBER:
                switch (parser.numberType()) {
                    case INT:
                        generator.writeNumber(parser.intValue());
                        break;
                    case LONG:
                        generator.writeNumber(parser.longValue());
                        break;
                    case FLOAT:
                        generator.writeNumber(parser.floatValue());
                        break;
                    case DOUBLE:
                        generator.writeNumber(parser.doubleValue());
                        break;
                }
                break;
            case VALUE_BOOLEAN:
                generator.writeBoolean(parser.booleanValue());
                break;
            case VALUE_NULL:
                generator.writeNull();
                break;
            case VALUE_EMBEDDED_OBJECT:
                generator.writeBinary(parser.binaryValue());
        }
    }

    @Override
    public void flush() throws IOException {
        generator.flush();
    }

    @Override
    public void close() throws IOException {
        generator.close();
    }

    private void writeFieldNameXml(String name) throws IOException {
        if (!context) {
            this.context = "@context".equals(name);
            this.prefix = null;
        }
        if (name.startsWith("@")) {
            // setting to attribute is simple but tricky, it allows to declare namespaces in StaX
            generator.setNextIsAttribute(true);
        } else if (name.startsWith("#")) {
            // reset field name for value
            generator.setNextIsAttribute(false);
            // 
            generator.setNextIsUnwrapped(true);
        } else if (context) {
            prefix = name;
        }
        else {
            generator.setNextIsAttribute(false);
        }
        QName qname = toQName(name);
        generator.setNextName(qname);
        generator.writeFieldName(qname.getLocalPart());
    }

    private QName toQName(String name) throws IOException {
        QName root = params.getQName();
        XmlNamespaceContext context = params.getNamespaceContext();
        String nsPrefix = root.getPrefix();
        String nsURI = root.getNamespaceURI();
        if (name.startsWith("_") || name.startsWith("@")|| name.startsWith("#")) {
            name = name.substring(1);
        }
        name = ISO9075.encode(name);
        int pos = name.indexOf(':');
        if (pos > 0) {
            nsPrefix = name.substring(0, pos);
            nsURI = context != null ? context.getNamespaceURI(nsPrefix) : XmlXParams.DEFAULT_ROOT.getNamespaceURI();
            if (nsURI == null) {
                throw new IOException("unknown namespace prefix: " + nsPrefix);
            }
            name = name.substring(pos + 1);
        }
        return new QName(nsURI, name, nsPrefix);
    }

    @Override
    public boolean isPrettyPrint() {
        return false;
    }

    @Override
    public void writeRawField(String name, InputStream value, XContentType xContentType) throws IOException {
    }
    
    private void writeStartRaw(String name) throws IOException {
        writeFieldName(name);
        generator.writeRaw(':');
    }

    public void writeEndRaw() {
    }
    
    protected void copyRawValue(BytesReference content, XContent xContent) throws IOException {
        // EMPTY is safe here because we never call namedObject
        try (StreamInput input = content.streamInput();
             XContentParser parser = xContent.createParser(NamedXContentRegistry.EMPTY, input)) {
            copyCurrentStructure(parser);
        }
    }
    
    protected boolean supportsRawWrites() {
        return true;
    }
    private boolean isFiltered() {
        return false;
    }
    
    
    private boolean mayWriteRawData(XContentType contentType) {
        // When the current generator is filtered (ie filter != null)
        // or the content is in a different format than the current generator,
        // we need to copy the whole structure so that it will be correctly
        // filtered or converted
        return supportsRawWrites()
                && isFiltered() == false
                && contentType == contentType()
                && isPrettyPrint() == false;
    }

    @Override
    public void writeRawField(String name, BytesReference content, XContentType contentType) throws IOException {
        if (mayWriteRawData(contentType) == false) {
            writeFieldName(name);
            copyRawValue(content, contentType.xContent());
        } else {
            writeStartRaw(name);
            flush();
            content.writeTo(os);
            writeEndRaw();
        }
    }

    @Override
    public void writeRawValue(BytesReference value, XContentType xContentType) throws IOException {
    }

    @Override
    public boolean isClosed() {
        return false;
    }
    
    public ToXmlGenerator generator() {
        return this.generator;
    }

}
