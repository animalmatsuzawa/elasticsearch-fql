/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at    XML(3) {
        @Override
        public String restContentType() {
            return "application/xml";
        }

        @Override
        public String shortName() {
            return "xml";
        }

        @Override
        public XContent xContent() {
            return XmlXContent.xmlXContent();
        }
    };
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

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.cbor.CborXContent;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.common.xcontent.smile.SmileXContent;
import org.elasticsearch.common.xcontent.yaml.YamlXContent;
import org.elasticsearch.common.xcontent.xml.XmlXContent;


import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * The content type of {@link org.elasticsearch.common.xcontent.XContent}.
 */
public enum XmlXContentType implements Writeable {

    /**
     * A JSON based content type.
     */
    JSON(0) {
        @Override
        public String mediaTypeWithoutParameters() {
            return "application/json";
        }

        @Override
        public String mediaType() {
            return "application/json; charset=UTF-8";
        }

        @Override
        public String shortName() {
            return "json";
        }

        @Override
        public XContent xContent() {
            return JsonXContent.jsonXContent;
        }
    },
    /**
     * The jackson based smile binary format. Fast and compact binary format.
     */
    SMILE(1) {
        @Override
        public String mediaTypeWithoutParameters() {
            return "application/smile";
        }

        @Override
        public String shortName() {
            return "smile";
        }

        @Override
        public XContent xContent() {
            return SmileXContent.smileXContent;
        }
    },
    /**
     * A YAML based content type.
     */
    YAML(2) {
        @Override
        public String mediaTypeWithoutParameters() {
            return "application/yaml";
        }

        @Override
        public String shortName() {
            return "yaml";
        }

        @Override
        public XContent xContent() {
            return YamlXContent.yamlXContent;
        }
    },
    /**
     * A CBOR based content type.
     */
    CBOR(3) {
        @Override
        public String mediaTypeWithoutParameters() {
            return "application/cbor";
        }

        @Override
        public String shortName() {
            return "cbor";
        }

        @Override
        public XContent xContent() {
            return CborXContent.cborXContent;
        }
    },
    XML(4) {
        @Override
        public String mediaTypeWithoutParameters() {
            return "application/xml";
        }

        @Override
        public String shortName() {
            return "xml";
        }

        @Override
        public XContent xContent() {
            return XmlXContent.xmlXContent;
        }
    };
    
    /**
     * Accepts either a format string, which is equivalent to {@link XContentType#shortName()} or a media type that optionally has
     * parameters and attempts to match the value to an {@link XContentType}. The comparisons are done in lower case format and this method
     * also supports a wildcard accept for {@code application/*}. This method can be used to parse the {@code Accept} HTTP header or a
     * format query string parameter. This method will return {@code null} if no match is found
     */
    public static XmlXContentType fromMediaTypeOrFormat(String mediaType) {
        if (mediaType == null) {
            return null;
        }
        for (XmlXContentType type : values()) {
            if (isSameMediaTypeOrFormatAs(mediaType, type)) {
                return type;
            }
        }
        final String lowercaseMediaType = mediaType.toLowerCase(Locale.ROOT);
        if (lowercaseMediaType.startsWith("application/*")) {
            return JSON;
        }

        return null;
    }

    /**
     * Attempts to match the given media type with the known {@link XContentType} values. This match is done in a case-insensitive manner.
     * The provided media type should not include any parameters. This method is suitable for parsing part of the {@code Content-Type}
     * HTTP header. This method will return {@code null} if no match is found
     */
    public static XmlXContentType fromMediaType(String mediaType) {
        final String lowercaseMediaType = Objects.requireNonNull(mediaType, "mediaType cannot be null").toLowerCase(Locale.ROOT);
        for (XmlXContentType type : values()) {
            if (type.mediaTypeWithoutParameters().equals(lowercaseMediaType)) {
                return type;
            }
        }

        return null;
    }

    private static boolean isSameMediaTypeOrFormatAs(String stringType, XmlXContentType type) {
        return type.mediaTypeWithoutParameters().equalsIgnoreCase(stringType) ||
                stringType.toLowerCase(Locale.ROOT).startsWith(type.mediaTypeWithoutParameters().toLowerCase(Locale.ROOT) + ";") ||
                type.shortName().equalsIgnoreCase(stringType);
    }

    private int index;

    XmlXContentType(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    public String mediaType() {
        return mediaTypeWithoutParameters();
    }
    public static XContentType toXContentType( XmlXContentType type)
    {
        if (type == XmlXContentType.JSON) {
            return XContentType.JSON;
        } else if (type == XmlXContentType.SMILE) {
            return XContentType.SMILE;
        } else if (type == XmlXContentType.YAML) {
            return XContentType.YAML;
        } else if (type == XmlXContentType.CBOR) {
            return XContentType.CBOR;
        } else if (type == XmlXContentType.XML) {
            return null;
        } else {
            return null;
        }
    }
    public static XmlXContentType toXmlXContentType( XContentType type)
    {
        if (type == XContentType.JSON) {
            return XmlXContentType.JSON;
        } else if (type == XContentType.SMILE) {
            return XmlXContentType.SMILE;
        } else if (type == XContentType.YAML) {
            return XmlXContentType.YAML;
        } else if (type == XContentType.CBOR) {
            return XmlXContentType.CBOR;
        } else {
            return null;
        }
    }


    public abstract String shortName();

    public abstract XContent xContent();

    public abstract String mediaTypeWithoutParameters();

    public static XmlXContentType readFrom(StreamInput in) throws IOException {
        int index = in.readVInt();
        for (XmlXContentType contentType : values()) {
            if (index == contentType.index) {
                return contentType;
            }
        }
        throw new IllegalStateException("Unknown XContentType with index [" + index + "]");
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeVInt(index);
    }
}
