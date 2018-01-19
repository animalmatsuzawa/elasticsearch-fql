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

import org.apache.logging.log4j.Logger;
import org.elasticsearch.ExceptionsHelper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.io.stream.OutputStreamStreamOutput;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.StatusToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.xml.XmlXContent;
import org.elasticsearch.common.xcontent.xml.XmlXContentGenerator;
import org.elasticsearch.common.xcontent.xml.XmlXParams;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.action.RestResponseListener;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import javax.xml.namespace.QName;

/**
 * Content listener that extracts that {@link RestStatus} from the response.
 */
public class FastResponseListener<Response extends StatusToXContentObject> implements ActionListener<Response> {
    private final Function<Response, String> extractLocation;

    private static Logger logger = Loggers.getLogger(RestResponseListener.class);

    protected final RestChannel channel;
    
    SearchRequest searchRequest;
    RestRequest request ;

    /**
     * Build an instance that doesn't support responses with the status {@code 201 CREATED}.
     */
    public FastResponseListener(RestChannel channel, RestRequest request, SearchRequest searchRequest) {
        this(channel, request, searchRequest, r -> {
            assert false: "Returned a 201 CREATED but not set up to support a Location header";
            return null;
        });
    }

    /**
     * Build an instance that does support responses with the status {@code 201 CREATED}.
     */
    public FastResponseListener(
            RestChannel channel, 
            RestRequest request, 
            SearchRequest searchRequest, 
            Function<Response, 
            String> extractLocation) {
        this.channel = channel;
        this.extractLocation = extractLocation;
        this.searchRequest = searchRequest ;
        this.request = request ;
    }
    
    
    @Override
    public final void onResponse(Response response) {
        try {
            processResponse(response);
        } catch (Exception e) {
            logger.error("ERROR?????",e);
            onFailure(e);
        }
    }
    
    protected final void processResponse(Response response) throws Exception {
        channel.sendResponse(buildResponse(response));
    }
    
    public RestResponse buildResponse(Response response) throws Exception {
        XContentBuilder builder = this.newBuilder() ;        
        return buildResponse(response, builder);
    }

    public RestResponse buildResponse(Response response, XContentBuilder builder) throws Exception {
        assert response.isFragment() == false; //would be nice if we could make default methods final
        SearchResponse search = null ;
        SearchHits hits = null ;
        Suggest suggests = null ;
        Aggregations aggregations = null ;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.getDefault());
        
        int size = this.searchRequest.source().size() ;
        if( size == -1 ) {
            size = 10 ;
        }
        int from = this.searchRequest.source().from() ;
        if( from == -1 ) {
            from = 0 ;
        }
        
        if( !(response instanceof SearchResponse) ) {
            throw new IllegalStateException("Unexpected result.");
        }
        else {
            search = (SearchResponse)response ;
            hits = search.getHits() ;
            suggests = search.getSuggest() ;
            aggregations = search.getAggregations() ;

            long totalcount = ((hits != null) ? hits.totalHits : 0);
            long firstHit =  from + (totalcount > 0 ? 1 : 0);
            long lastHit = from + ((totalcount-from)>size ? size : (totalcount-from)) ;

            // 次のページが存在するか
            boolean hasNextPage = true;
            if (lastHit >= totalcount) {
                hasNextPage = false;
                lastHit = totalcount;
            }
            // 前のページが存在するか
            boolean hasPreviousPage = false;
            if (firstHit > size) {
                hasPreviousPage = true;
            }
            
            builder.startObject();
            builder.startObject("SEGMENT");
            builder.field("@NAME").value("webcluster") ;
            builder.startObject("RESULTPAGE");
            builder.startObject("QUERYTRANSFORMS");
    
            builder.startObject("QUERYTRANSFORM");
            builder.field("@NAME").value("Original query") ;
            builder.field("@ACTION").value("NOP") ;
            builder.field("@CUSTOM").value("") ;
            builder.field("@MESSAGE").value("Original query") ;
            builder.field("@MESSAGEID").value(1) ;

            builder.field("@QUERT", request.param("query")) ;
        
            builder.endObject();    // </QUERYTRANSFORM>
            
            builder.startObject("QUERYTRANSFORM");
            builder.field("@NAME").value("FastQT Kansuji") ;
            builder.field("@ACTION").value("nop") ;
            builder.field("@CUSTOM").value("") ;
            builder.field("@MESSAGE").value("Query was not modified.") ;
            builder.field("@MESSAGEID").value(2) ;
            builder.field("@INSTANCE").value("kansuji") ;
            builder.field("@QUERY").value("Trere were no CJK numerals in the query.") ;
            builder.endObject();    // </QUERYTRANSFORM
    
            builder.startObject("QUERYTRANSFORM");
            builder.field("@NAME").value("FastQT_Lemmatizer") ;
            builder.field("@ACTION").value("nop") ;
            builder.field("@CUSTOM").value("No change to query") ;
            builder.field("@MESSAGE").value("Lemmatization turned off for current query") ;
            builder.field("@MESSAGEID").value(16) ;
            builder.field("@INSTANCE").value("lemmatizer") ;
            builder.field("@QUERY").value("") ;
            builder.endObject();    // </QUERYTRANSFORM
    
            builder.startObject("QUERYTRANSFORM");
            builder.field("@NAME").value("Final query") ;
            builder.field("@ACTION").value("NOP") ;
            builder.field("@CUSTOM").value("") ;
            builder.field("@MESSAGE").value("Final query") ;
            builder.field("@MESSAGEID").value(1) ;
            builder.field("@QUERY").value("") ;
    
            builder.endObject();    // </QUERYTRANSFORM
            
            builder.endObject();    // </QUERYTRANSFORMS>
    
            builder.startObject("NAVIGATION");
            // gggregation -> navigation
            if( aggregations != null) {
                builder.field("@ENTRIES").value(aggregations.asList().size()) ;
    
                for( Aggregation agg : aggregations) {
                    builder.startObject("NAVIGATIONENTRY");
                    builder.field("@NAME").value(agg.getName()) ;
                    builder.field("@DISPAYNAME").value(agg.getName()) ;
                    builder.field("@MODIFIER").value(agg.getName()) ;
                    
                    if( agg instanceof MultiBucketsAggregation ) {
                        MultiBucketsAggregation m_agg = (MultiBucketsAggregation)agg ;
                        builder.startObject("NAVIGATIONELEMENTS");
                        builder.field("@COUNT").value(m_agg.getBuckets().size()) ;
                        for( MultiBucketsAggregation.Bucket bucket : m_agg.getBuckets()){
                            String name = null ;
                            String modefier = null ;
                            Long count = null ;
                            if( bucket instanceof Terms.Bucket ) {
                                Terms.Bucket t_bucket = (Terms.Bucket)bucket ;
                                name = t_bucket.getKeyAsString() ;
                                modefier = "^\"" + name + "\"$" ;
                                count = t_bucket.getDocCount() ;
                            }
                            else if( bucket instanceof Range.Bucket ) {
                                Range.Bucket r_bucket = (Range.Bucket)bucket ;
                                // name = r_bucket.getKeyAsString() ;
                                String t = null ;
                                String f = null ;
                                if( r_bucket.getTo() instanceof DateTime ) {
                                    t = dateFormat.format(r_bucket.getTo()) ;
                                } else {
                                    t = r_bucket.getToAsString() ;
                                }
                                if( r_bucket.getFrom() instanceof DateTime ) {
                                    f = dateFormat.format(r_bucket.getFrom()) ;
                                } else {
                                    f = r_bucket.getFromAsString() ;
                                }
                                if( f == null ){
                                    f = "" ;
                                }
                                if( t == null ){
                                    t = "" ;
                                }
                                modefier = "["+f+";"+t+"]" ;
                                if (t == null || t.isEmpty()) {
                                    name = f + " and above" ;
                                } else if ( f == null || f.isEmpty()) {
                                    name = "Less than " + t ;
                                } else  {
                                    name = "From " + f + " to " + t ;
                                }
                                count = r_bucket.getDocCount() ;
                            }
                            else {
                                name = "" ;
                                modefier = "" ;
                                count = 0L ;
                            }
                            builder.startObject("NAVIGATIONELEMENT");
                            builder.field("@NAME").value(name) ;
                            builder.field("@MODIFIER").value(modefier) ;
                            builder.field("@COUNT").value(count) ;
                            
                            builder.endObject();    // </NAVIGATIONELEMENT>
                        }
                        builder.endObject();    // </NAVIGATIONELEMENTS>
                    }
                    else
                    {
                        builder.startObject("NAVIGATIONELEMENTS");
                        builder.field("@COUNT").value(0) ;
                        builder.endObject();    // </NAVIGATIONELEMENTS>
                    }
                    builder.endObject();    // </NAVIGATIONENTRY>
                }
            }
            else {
                builder.field("@ENTRIES").value(0) ;
            }
            builder.endObject();    // </NAVIGATION>
            
            builder.startObject("CLUSTERS");
            builder.endObject();    // </CLUSTERS>
            
            builder.startObject("RESULTSET");        
    
            if( hits != null ) {
                // Hits -> resultset
                builder.field("@TOTALHITS").value(hits.totalHits);
                builder.field("@FIRSTHIT").value(hits.getHits().length > 0 ? firstHit : 0);
                builder.field("@HITS").value(hits.getHits().length);
            } 
            else {
                builder.field("@TOTALHITS").value(0);
                builder.field("@FIRSTHIT").value(0);
                builder.field("@HITS").value(0);
            }
            builder.field("@LASTHIT").value(lastHit);
            builder.field("@MAXRANK").value(0);// ダミー
            builder.field("@TIME").value(search.getTook());
            
            if( hits == null || hits.getHits().length == 0) {
                // EMPTYRESULTSET
                builder.startObject("EMPTYRESULTSET");
                builder.endObject();    // </EMPTYRESULTSET>
            }
            else
            {
                int hitcount = 0 ;
                for( SearchHit hit : hits.getHits()) {
                    builder.startObject("HIT");
                    builder.field("@NO").value(firstHit+hitcount);
                    builder.field("@FCOCOUNT").value(0);// ダミー
                    builder.field("@MOREHITS").value(0);// ダミー
                    builder.field("@RANK").value(0);// ダミー
                    builder.field("@SITEID").value(0);// ダミー
                    
                    for( Map.Entry<String, Object> source : hit.getSourceAsMap().entrySet()){
                        builder.startObject("FIELD");
                        builder.field("@name").value(source.getKey());
                        builder.field("#FIELD") ;
                        if( source.getValue() instanceof List) {
                            StringBuilder sb = new StringBuilder();
                            for( Object n: (List<?>)source.getValue()) {
                                String val = null ;
                                if( n instanceof DateTime ) {
                                    val = dateFormat.format(n) ;
                                } else {
                                    val = n.toString() ;
                                }
                                if( sb.length() > 0 ) {
                                    sb.append("@|@") ;
                                }
                                sb.append(val) ;
                            }
                            builder.value(sb.toString());
                        } else if( source.getValue() instanceof DateTime ) {
                            builder.value(dateFormat.format(source.getValue())) ;
                        } else {
                            builder.value(source.getValue().toString()) ;
                        }
                        builder.endObject();    // </FIELD>
    
                    }
                    hitcount ++ ;
                    builder.endObject();    // </HIT>
                }
            }
            
            builder.endObject();    // </RESULTSET>
            
            
            builder.startObject("PAGENAVIGATION");
            if ( hasNextPage ){
                builder.startObject("NEXTPAGE");
                long nextFirst = lastHit + 1;
                long nextLast = nextFirst + size - 1;
                builder.field("@FIRSTHIT").value(nextFirst);
                builder.field("@LASTHIT").value(nextLast);
                builder.field("#NEXTPAGE") ;
                String path = this.request.path() ;
                boolean fast = false ;
                if(this.request.param("query") != null ) {
                    path = path + (fast?"&":"?") + "query=" + this.request.param("query") ; 
                    fast = true ;
                }
                if(this.request.param("navigation") != null ) {
                    path = path + (fast?"&":"?") + "navigation=" + this.request.param("navigation") ; 
                    fast = true ;
                }
                if(this.request.param("sortby") != null ) {
                    path = path + (fast?"&":"?") + "sortby=" + this.request.param("sortby") ; 
                    fast = true ;
                }
                path = path + (fast?"&":"?") + "offset=" + (size + from) + "&hits=" + size ;                
                
                builder.value(path);
                
                builder.endObject();    // </NEXTPAGE>
            }
            if ( hasPreviousPage ) {
                builder.startObject("PREVPAGE");
                long prevFirst = firstHit - size;
                long prevLast = firstHit - 1;
                builder.field("@FIRSTHIT").value(prevFirst);
                builder.field("@LASTHIT").value(prevLast);
                builder.field("#PREVPAGE") ;
                String path = this.request.path() ;
                boolean fast = false ;
                if(this.request.param("query") != null ) {
                    path = path + (fast?"&":"?") + "query=" + this.request.param("query") ; 
                    fast = true ;
                }
                if(this.request.param("navigation") != null ) {
                    path = path + (fast?"&":"?") + "navigation=" + this.request.param("navigation") ; 
                    fast = true ;
                }
                if(this.request.param("sortby") != null ) {
                    path = path + (fast?"&":"?") + "sortby=" + this.request.param("sortby") ; 
                    fast = true ;
                }
                path = path + (fast?"&":"?") + "offset=" + (from - size) + "&hits=" + size ;                
                
                builder.value(path);
                
                builder.endObject();    // </PREVPAGE>
                
            }
            builder.endObject();    // </PAGENAVIGATION>
            
            builder.endObject();    // </RESULTPAGE>
            builder.endObject();    // </SEGMENT>
            builder.endObject();    // </SEGMENTS>

        }

        RestResponse restResponse = new FastRestResponse(response.status(), builder);
        if (RestStatus.CREATED == restResponse.status()) {
            final String location = extractLocation.apply(response);
            if (location != null) {
                restResponse.addHeader("Location", location);
            }
        }
        return restResponse;
    }
    
    
    XContentBuilder newBuilder() throws IOException{
        OutputStream unclosableOutputStream = Streams.flushOnCloseStream(channel.bytesOutput());
//        OutputStream unclosableOutputStream = new OutputStreamStreamOutput(channel.bytesOutput());
        XContentBuilder builder =
            new XContentBuilder(new XmlXContent(), unclosableOutputStream );
            builder.prettyPrint().lfAtEnd();
            
        if( builder.generator() instanceof XmlXContentGenerator ) {
            XmlXContentGenerator gen = (XmlXContentGenerator)builder.generator() ;
            XmlXParams param = new XmlXParams( new QName("SEGMENTS"), null)  ;
            gen.setParams(param) ;
        }
        return builder ;
    }
    
    @Override
    public final void onFailure(Exception e) {
        try {
            
            XContentBuilder builder = this.newBuilder() ;        
            builder.startObject();
            builder.startObject("SEGMENT");
            builder.field("@NAME", "webcluster") ;
            builder.startObject("RESULTPAGE");
            builder.startObject("ERROR");
            builder.field("@CODE", ExceptionsHelper.status(e).getStatus()) ;
            builder.field("@CONTENT", e.toString()) ;
            builder.endObject();
            builder.endObject();
            builder.endObject();
            builder.endObject();
            
            channel.sendResponse(new FastRestResponse(e, builder));
        } catch (Exception inner) {
            inner.addSuppressed(e);
            logger.error("failed to send failure response", inner);
        }
    }
}
