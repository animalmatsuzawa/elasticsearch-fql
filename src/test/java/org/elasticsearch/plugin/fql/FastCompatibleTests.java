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


import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.transport.MockTcpTransportPlugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.elasticsearch.transport.nio.NioTransportPlugin;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;

//@PrepareForTest(System.class)
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST )
public class FastCompatibleTests extends ESIntegTestCase {
    private static String nodeTransportTypeKey;
    private static String nodeHttpTypeKey;
    private static String clientTypeKey;
    

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUpTransport() {
        nodeTransportTypeKey = getTypeKey(randomFrom(getTestTransportPlugin(), Netty4Plugin.class));
        nodeHttpTypeKey = getTypeKey(Netty4Plugin.class);
        clientTypeKey = getTypeKey(randomFrom(getTestTransportPlugin(), Netty4Plugin.class));
        

    }
    
    private static String getTypeKey(Class<? extends Plugin> clazz) {
        if (clazz.equals(MockTcpTransportPlugin.class)) {
            return MockTcpTransportPlugin.MOCK_TCP_TRANSPORT_NAME;
        } else if (clazz.equals(NioTransportPlugin.class)) {
            return NioTransportPlugin.NIO_TRANSPORT_NAME;
        } else {
            assert clazz.equals(Netty4Plugin.class);
            return Netty4Plugin.NETTY_TRANSPORT_NAME;
        }
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.builder()
                .put(super.nodeSettings(nodeOrdinal))
                .put("processors",2)
                .put(NetworkModule.TRANSPORT_TYPE_KEY, nodeTransportTypeKey)
                .put(NetworkModule.HTTP_TYPE_KEY, nodeHttpTypeKey)
                .put(NetworkModule.HTTP_ENABLED.getKey(), true).build();
    }

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Arrays.asList(getTestTransportPlugin(), Netty4Plugin.class, FastCompatiblePlugin.class);
    }

    @Override
    protected Collection<Class<? extends Plugin>> transportClientPlugins() {
        return Arrays.asList(getTestTransportPlugin(), Netty4Plugin.class);
    }

    @Override
    protected Settings transportClientSettings() {
        return Settings.builder()
                .put(super.transportClientSettings())
                .put("processors",2)
                .put(NetworkModule.TRANSPORT_TYPE_KEY, clientTypeKey)
                .build();
    }

    @Override
    protected boolean ignoreExternalCluster() {
        return true;
    }

    public void testSearch() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        // create index
        createIndex("test");

        ensureGreen();

        // add index
        client().prepareIndex("test", "type", "1").setSource("field", "xxx","id",1).execute().actionGet();
        client().prepareIndex("test", "type", "2").setSource("field", "yyy","id",2).execute().actionGet();
        client().prepareIndex("test", "type", "3").setSource("field", "xxx","id",3).execute().actionGet();
        refresh();
        
        // search
        String mappings = "";
        NStringEntity entity = new NStringEntity(mappings, ContentType.APPLICATION_JSON);
        Map<String,String> param = new HashMap<String, String>();
        param.put("query", "and(field:\"xxx\",id:1)");
        Response response = getRestClient().performRequest("GET", "/_fast/test/search", param, entity);
        assertEquals( response.getStatusLine().getStatusCode(), 200) ;
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        
        InputSource source = new InputSource(new StringReader(body));
        
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse( source );
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        String totalhits = xpath.evaluate("//RESULTSET/@TOTALHITS", doc);
        assertEquals( totalhits, "1") ;
        
        NodeList hit = (NodeList) xpath.evaluate( "//HIT", doc, XPathConstants.NODESET );
        assertEquals( hit.getLength(), 1) ;

        NodeList name = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='field']/text()", doc, XPathConstants.NODESET );
        assertEquals( name.item(0).getNodeValue(), "xxx") ;

        NodeList id = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='id']/text()", doc, XPathConstants.NODESET );
        assertEquals( id.item(0).getNodeValue(), "1") ;
        
    }
    
    
    public void testNavi() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        // create index
        createIndex("test");
        
        ensureGreen();

        // add index
        client().prepareIndex("test", "type", "1").setSource("field", "xxx","id",1).execute().actionGet();
        client().prepareIndex("test", "type", "2").setSource("field", "yyy","id",2).execute().actionGet();
        client().prepareIndex("test", "type", "3").setSource("field", "xxx","id",3).execute().actionGet();
        refresh();
        
        // search
        String mappings = "";
        NStringEntity entity = new NStringEntity(mappings, ContentType.APPLICATION_JSON);
        Map<String,String> param = new HashMap<String, String>();
        param.put("query", "field:\"xxx\"");
        param.put("navigation", "+id:^1$");
        Response response = getRestClient().performRequest("GET", "/_fast/test/search", param, entity);
        assertEquals( response.getStatusLine().getStatusCode(), 200) ;
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        
        InputSource source = new InputSource(new StringReader(body));
        
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse( source );
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        String totalhits = xpath.evaluate("//RESULTSET/@TOTALHITS", doc);
        assertEquals( totalhits, "1") ;
        
        NodeList hit = (NodeList) xpath.evaluate( "//HIT", doc, XPathConstants.NODESET );
        assertEquals( hit.getLength(), 1) ;

        NodeList name = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='field']/text()", doc, XPathConstants.NODESET );
        for( int i = 0; i < name.getLength(); i++ ) {
            assertEquals( name.item(i).getNodeValue(), "xxx") ;
        }
        NodeList id = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='id']/text()", doc, XPathConstants.NODESET );
        assertEquals( id.item(0).getNodeValue(), "1") ;
        
    }


    public void testSort_DESC() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        // create index
        createIndex("test");

        ensureGreen();

        // add index
        client().prepareIndex("test", "type", "1").setSource("field", "xxx","id",1).execute().actionGet();
        client().prepareIndex("test", "type", "2").setSource("field", "yyy","id",2).execute().actionGet();
        client().prepareIndex("test", "type", "3").setSource("field", "xxx","id",3).execute().actionGet();
        refresh();
        
        // search
        String mappings = "";
        NStringEntity entity = new NStringEntity(mappings, ContentType.APPLICATION_JSON);
        Map<String,String> param = new HashMap<String, String>();
        param.put("query", "field:\"xxx\"");
        param.put("sortby", "+id");
        Response response = getRestClient().performRequest("GET", "/_fast/test/search", param, entity);
        assertEquals( response.getStatusLine().getStatusCode(), 200) ;
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        
        InputSource source = new InputSource(new StringReader(body));
        
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse( source );
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        String totalhits = xpath.evaluate("//RESULTSET/@TOTALHITS", doc);
        assertEquals( totalhits, "2") ;
        
        NodeList hit = (NodeList) xpath.evaluate( "//HIT", doc, XPathConstants.NODESET );
        assertEquals( hit.getLength(), 2) ;

        NodeList name = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='field']/text()", doc, XPathConstants.NODESET );
        for( int i = 0; i < name.getLength(); i++ ) {
            assertEquals( name.item(i).getNodeValue(), "xxx") ;
        }
        NodeList id = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='id']/text()", doc, XPathConstants.NODESET );
        assertEquals( id.item(0).getNodeValue(), "1") ;
        assertEquals( id.item(1).getNodeValue(), "3") ;
        
    }

    public void testSort_ASC() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        // create index
        createIndex("test");

        ensureGreen();

        // add index
        client().prepareIndex("test", "type", "1").setSource("field", "xxx","id",1).execute().actionGet();
        client().prepareIndex("test", "type", "2").setSource("field", "yyy","id",2).execute().actionGet();
        client().prepareIndex("test", "type", "3").setSource("field", "xxx","id",3).execute().actionGet();
        refresh();
        
        // search
        String mappings = "";
        NStringEntity entity = new NStringEntity(mappings, ContentType.APPLICATION_JSON);
        Map<String,String> param = new HashMap<String, String>();
        param.put("query", "field:\"xxx\"");
        param.put("sortby", "-id");
        Response response = getRestClient().performRequest("GET", "/_fast/test/search", param, entity);
        assertEquals( response.getStatusLine().getStatusCode(), 200) ;
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        
        InputSource source = new InputSource(new StringReader(body));
        
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse( source );
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        String totalhits = xpath.evaluate("//RESULTSET/@TOTALHITS", doc);
        assertEquals( totalhits, "2") ;
        
        NodeList hit = (NodeList) xpath.evaluate( "//HIT", doc, XPathConstants.NODESET );
        assertEquals( hit.getLength(), 2) ;

        NodeList name = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='field']/text()", doc, XPathConstants.NODESET );
        for( int i = 0; i < name.getLength(); i++ ) {
            assertEquals( name.item(i).getNodeValue(), "xxx") ;
        }
        NodeList id = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='id']/text()", doc, XPathConstants.NODESET );
        assertEquals( id.item(0).getNodeValue(), "3") ;
        assertEquals( id.item(1).getNodeValue(), "1") ;
        
    }

    public void testFacetNavigation() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        // create index
//        createIndex("test");        
       assertAcked(prepareCreate("test")
        .addMapping("type", jsonBuilder().startObject().startObject("type").startObject("properties")
            .startObject("id")
                .field("type", "long")
            .endObject()
            .startObject("field")
                .field("type", "text")
                .field("fielddata", true)
            .endObject()
            .endObject().endObject().endObject()
        ));
        
        ensureGreen();
        
        // regist aggregation
        String navijson = "{ \"aggs\" : { \"fields\" : { \"terms\" : { \"field\" : \"field\" , \"size\":2} } } }";
        Response navi_response = getRestClient().performRequest(
                "PUT", 
                "/_fast/test/type/_navigation", 
                new HashMap<String, 
                String>(), new NStringEntity(navijson, ContentType.APPLICATION_JSON));
        assertEquals( navi_response.getStatusLine().getStatusCode(), 201) ;

        // add index
        client().prepareIndex("test", "type", "1").setSource("field", "xxx","id",1).execute().actionGet();
        client().prepareIndex("test", "type", "2").setSource("field", "yyy","id",2).execute().actionGet();
        client().prepareIndex("test", "type", "3").setSource("field", "xxx","id",3).execute().actionGet();
        refresh();

//        Response map_response = getRestClient().performRequest(
//        "GET", "/test/_mapping/type", 
//        Collections.singletonMap("pretty", "true"), 
//        new NStringEntity("", ContentType.APPLICATION_JSON));
//        String map_body = EntityUtils.toString(map_response.getEntity(), "UTF-8");
        
        // search
        Map<String,String> param = new HashMap<String, String>();
        param.put("query", "field:\"xxx\"");
        Response response = getRestClient().performRequest(
                "GET", 
                "/_fast/test/type/search", 
                param, 
                new NStringEntity("", ContentType.APPLICATION_JSON));
        assertEquals( response.getStatusLine().getStatusCode(), 200) ;
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        
        InputSource source = new InputSource(new StringReader(body));
        
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse( source );
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        String totalhits = xpath.evaluate("//RESULTSET/@TOTALHITS", doc);
        assertEquals( totalhits, "2") ;
        
        NodeList hit = (NodeList) xpath.evaluate( "//HIT", doc, XPathConstants.NODESET );
        assertEquals( hit.getLength(), 2) ;

        NodeList name = (NodeList) xpath.evaluate( "//HIT/FIELD[@name='field']/text()", doc, XPathConstants.NODESET );
        for( int i = 0; i < name.getLength(); i++ ) {
            assertEquals( name.item(i).getNodeValue(), "xxx") ;
        }

        NodeList nvai_name = (NodeList) xpath.evaluate( "//NAVIGATION/NAVIGATIONENTRY/@NAME", doc, XPathConstants.NODESET );
        assertEquals( nvai_name.item(0).getNodeValue(), "fields") ;

        NodeList nvai_count = (NodeList) xpath.evaluate( 
                "//NAVIGATION/NAVIGATIONENTRY/NAVIGATIONELEMENTS/@COUNT", 
                doc, XPathConstants.NODESET );
        assertEquals( nvai_count.item(0).getNodeValue(), "1") ;

        NodeList nvai_elem_name = (NodeList) xpath.evaluate( 
                "//NAVIGATION/NAVIGATIONENTRY/NAVIGATIONELEMENTS/NAVIGATIONELEMENT/@NAME", 
                doc, XPathConstants.NODESET );
        assertEquals( nvai_elem_name.item(0).getNodeValue(), "xxx") ;

        NodeList nvai_elem_count = (NodeList) xpath.evaluate( 
                "//NAVIGATION/NAVIGATIONENTRY/NAVIGATIONELEMENTS/NAVIGATIONELEMENT/@COUNT", 
                doc, XPathConstants.NODESET );
        assertEquals( nvai_elem_count.item(0).getNodeValue(), "2") ;

    }
    
    
    
}
