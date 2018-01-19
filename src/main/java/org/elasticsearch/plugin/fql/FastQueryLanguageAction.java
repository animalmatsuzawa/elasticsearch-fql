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

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fql.FastQueryLanguageRequestParser;
import org.elasticsearch.search.fql.FastResponseListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;

public class FastQueryLanguageAction extends BaseRestHandler{

    FastCompatibleService fastService;
    protected FastQueryLanguageAction(Settings settings, RestController restController, FastCompatibleService fastService) {
        super(settings);
        this.fastService = fastService ;
        
        restController.registerHandler(GET, "/_fast/search", this);
        restController.registerHandler(POST, "/_fast/search", this);
        restController.registerHandler(GET, "/_fast/{index}/search", this);
        restController.registerHandler(POST, "/_fast/{index}/search", this);
        restController.registerHandler(GET, "/_fast/{index}/{type}/search", this);
        restController.registerHandler(POST, "/_fast/{index}/{type}/search", this);
       
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        
        SearchRequest searchRequest = new SearchRequest();
        
        request.withContentOrSourceParamParserOrNull(parser ->
            parseSearchRequest(searchRequest, request, parser, client, this.fastService));
        
        
        
        return channel -> client.search(searchRequest, new FastResponseListener<SearchResponse>(channel, request, searchRequest));
    }
    
    
    /**
     * Parses the rest request on top of the SearchRequest, preserving values that are not overridden by the rest request.
     *
     * @param requestContentParser body of the request to read. This method does not attempt to read the body from the {@code request}
     *        parameter
     */
    public static void parseSearchRequest(SearchRequest searchRequest, 
                                          RestRequest request,
                                          XContentParser requestContentParser, 
                                          NodeClient client, 
                                          FastCompatibleService fastService ) throws IOException {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser() ;
        if (searchRequest.source() == null) {
            searchRequest.source(new SearchSourceBuilder());
        }

//        if (requestContentParser != null) {
//            searchRequest.source().parseXContent(requestContentParser);
//        }
        
        // fast fql -> query
        String queryString = request.param("query");
        if( queryString != null ) {
            queryString = fast_parser.parseQuery(queryString);
        }
        // query指定ない場合は全件
        else {
            queryString = "\"query\":{\"match_all\":{}}" ;
        }

        // fast navigation -> post_filter
        String filterString = request.param("navigation");
        if( filterString != null ) {
            filterString = fast_parser.parseNavigation(filterString) ;
        }
        // esのquery作成
        if( queryString != null ) {
            String query = "{\n" + queryString ;
            if( filterString != null ) {
                query += ",\n" + filterString ;
            }
            query += "\n}\n" ;
            
            XContentParser parser = XContentType.JSON.xContent().createParser(request.getXContentRegistry(),query ) ;
            searchRequest.source().parseXContent(parser);
        }
        
        MySync<GetResponse> sync = new MySync<>();
        // fql navigation -> aggregation
        String _index = request.param("index");
        String _type = request.param("type") ;
        if( _type == null)
        {
            _type = FastCompatibleService.DEFAULT_CONSTRAINT_TYPE ;
        }
        String agg_index = Base64.getEncoder().encodeToString(_index.getBytes(StandardCharsets.UTF_8)) ;
        String agg_type = Base64.getEncoder().encodeToString(_type.getBytes(StandardCharsets.UTF_8)) ;

        // indexから取得        
        GetRequestBuilder reqbuilder = client.prepareGet(fastService.constraintIndex, agg_index, agg_type);
        reqbuilder.execute( new ActionListener<GetResponse>(){
            @Override
            public void onResponse(GetResponse response) {
                sync.set(response) ;
            }

            @Override
            public void onFailure(Exception e) {
                sync.setException(e) ;
            }
        } );
        
        try {
            GetResponse response = sync.get();
            Map<String, Object> navi_map = response.getSourceAsMap();
            if( navi_map != null )
            {
                String agg = navi_map.get("source").toString();
                if( agg != null && !agg.isEmpty()) {
                    List<AggregationBuilder> list;
                    try {
                        list = fastService.parseAggregation(request.getXContentRegistry(), agg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if( list != null ){
                        for(AggregationBuilder builder : list ) {
                            searchRequest.source().aggregation(builder) ;
                        }
                    }
                }
            }
        } catch (CancellationException | ExecutionException | InterruptedException e1) {
            throw new RuntimeException(e1);
        }
        // fast sortby -> sort
        String sSorts = request.param("sortby");
        if (sSorts != null) {        
            fast_parser.parseSort( sSorts.trim(), searchRequest.source()) ;
        }
        
        searchRequest.indices(Strings.splitStringByCommaToArray(request.param("index")));
//        if (requestContentParser != null) {
//            QueryParseContext context = new QueryParseContext(requestContentParser);
//            searchRequest.source().parseXContent(context);
//        }
//    
//        final int batchedReduceSize = request.paramAsInt("batched_reduce_size", searchRequest.getBatchedReduceSize());
//        searchRequest.setBatchedReduceSize(batchedReduceSize);
//    
//        // do not allow 'query_and_fetch' or 'dfs_query_and_fetch' search types
//        // from the REST layer. these modes are an internal optimization and should
//        // not be specified explicitly by the user.
//        String searchType = request.param("search_type");
//        if ("query_and_fetch".equals(searchType) ||
//                "dfs_query_and_fetch".equals(searchType)) {
//            throw new IllegalArgumentException("Unsupported search type [" + searchType + "]");
//        } else {
//            searchRequest.searchType(searchType);
//        }
//        parseSearchSource(searchRequest.source(), request);
//        searchRequest.requestCache(request.paramAsBoolean("request_cache", null));
//    
//        String scroll = request.param("scroll");
//        if (scroll != null) {
//            searchRequest.scroll(new Scroll(parseTimeValue(scroll, null, "scroll")));
//        }
//    
        searchRequest.types(Strings.splitStringByCommaToArray(request.param("type")));
//        searchRequest.routing(request.param("routing"));
//        searchRequest.preference(request.param("preference"));
//        searchRequest.indicesOptions(IndicesOptions.fromRequest(request, searchRequest.indicesOptions()));
      // fast offset -> from
      int from = request.paramAsInt("offset", -1);
      if (from != -1) {
          searchRequest.source().from(from);
      }
      // fast hits -> size
      int size = request.paramAsInt("hits", -1);
      if (size != -1) {
          searchRequest.source().size(size);
      }
    }
    
    /**
     * <p>Following the contract of {@link AbstractQueuedSynchronizer} we create a
     * private subclass to hold the synchronizer.  This synchronizer is used to
     * implement the blocking and waiting calls as well as to handle state changes
     * in a thread-safe manner.  The current state of the future is held in the
     * Sync state, and the lock is released whenever the state changes to either
     * {@link #COMPLETED} or {@link #CANCELLED}.
     * <p>
     * To avoid races between threads doing release and acquire, we transition
     * to the final state in two steps.  One thread will successfully CAS from
     * RUNNING to COMPLETING, that thread will then set the result of the
     * computation, and only then transition to COMPLETED or CANCELLED.
     * <p>
     * We don't use the integer argument passed between acquire methods so we
     * pass around a -1 everywhere.
     * 
     * @see org.elasticsearch.common.util.concurrent.BaseFuture
     * BaseFutureだとassertにより実効スレッドのチェックが入る。チェック回避の為に自前で同期する
     */
    static final class MySync<V> extends AbstractQueuedSynchronizer {
        /* Valid states. */
        static final int RUNNING = 0;
        static final int COMPLETING = 1;
        static final int COMPLETED = 2;
        static final int CANCELLED = 4;

        private V value;
        private Throwable exception;

        /*
        * Acquisition succeeds if the future is done, otherwise it fails.
        */
        @Override
        protected int tryAcquireShared(int ignored) {
            if (isDone()) {
                return 1;
            }
            return -1;
        }

        /*
        * We always allow a release to go through, this means the state has been
        * successfully changed and the result is available.
        */
        @Override
        protected boolean tryReleaseShared(int finalState) {
            setState(finalState);
            return true;
        }

        /**
         * Blocks until the task is complete or the timeout expires.  Throws a
         * {@link TimeoutException} if the timer expires, otherwise behaves like
         * {@link #get()}.
         */
        V get(long nanos) throws TimeoutException, CancellationException,
                ExecutionException, InterruptedException {

            // Attempt to acquire the shared lock with a timeout.
            if (!tryAcquireSharedNanos(-1, nanos)) {
                throw new TimeoutException("Timeout waiting for task.");
            }

            return getValue();
        }

        /**
         * Blocks until {@link #complete(Object, Throwable, int)} has been
         * successfully called.  Throws a {@link CancellationException} if the task
         * was cancelled, or a {@link ExecutionException} if the task completed with
         * an error.
         */
        V get() throws CancellationException, ExecutionException,
                InterruptedException {

            // Acquire the shared lock allowing interruption.
            acquireSharedInterruptibly(-1);
            return getValue();
        }

        /**
         * Implementation of the actual value retrieval.  Will return the value
         * on success, an exception on failure, a cancellation on cancellation, or
         * an illegal state if the synchronizer is in an invalid state.
         */
        private V getValue() throws CancellationException, ExecutionException {
            int state = getState();
            switch (state) {
                case COMPLETED:
                    if (exception != null) {
                        throw new ExecutionException(exception);
                    } else {
                        return value;
                    }

                case CANCELLED:
                    throw new CancellationException("Task was cancelled.");

                default:
                    throw new IllegalStateException(
                            "Error, synchronizer in invalid state: " + state);
            }
        }

        /**
         * Checks if the state is {@link #COMPLETED} or {@link #CANCELLED}.
         */
        boolean isDone() {
            return (getState() & (COMPLETED | CANCELLED)) != 0;
        }

        /**
         * Checks if the state is {@link #CANCELLED}.
         */
        boolean isCancelled() {
            return getState() == CANCELLED;
        }

        /**
         * Transition to the COMPLETED state and set the value.
         */
        boolean set(@Nullable V v) {
            return complete(v, null, COMPLETED);
        }

        /**
         * Transition to the COMPLETED state and set the exception.
         */
        boolean setException(Throwable t) {
            return complete(null, t, COMPLETED);
        }

        /**
         * Transition to the CANCELLED state.
         */
        boolean cancel() {
            return complete(null, null, CANCELLED);
        }

        /**
         * Implementation of completing a task.  Either {@code v} or {@code t} will
         * be set but not both.  The {@code finalState} is the state to change to
         * from {@link #RUNNING}.  If the state is not in the RUNNING state we
         * return {@code false} after waiting for the state to be set to a valid
         * final state ({@link #COMPLETED} or {@link #CANCELLED}).
         *
         * @param v          the value to set as the result of the computation.
         * @param t          the exception to set as the result of the computation.
         * @param finalState the state to transition to.
         */
        private boolean complete(@Nullable V v, @Nullable Throwable t,
                                 int finalState) {
            boolean doCompletion = compareAndSetState(RUNNING, COMPLETING);
            if (doCompletion) {
                // If this thread successfully transitioned to COMPLETING, set the value
                // and exception and then release to the final state.
                this.value = v;
                this.exception = t;
                releaseShared(finalState);
            } else if (getState() == COMPLETING) {
                // If some other thread is currently completing the future, block until
                // they are done so we can guarantee completion.
                acquireShared(-1);
            }
            return doCompletion;
        }
    }

    @Override
    public String getName() {
        return "fql_action";
    }
    
//    /**
//     * Parses the rest request on top of the SearchSourceBuilder, preserving
//     * values that are not overridden by the rest request.
//     */
//    private static void parseSearchSource(final SearchSourceBuilder searchSourceBuilder, RestRequest request) {
//        QueryBuilder queryBuilder = RestActions.urlParamsToQueryBuilder(request);
//        if (queryBuilder != null) {
//            searchSourceBuilder.query(queryBuilder);
//        }
//    
//        int from = request.paramAsInt("from", -1);
//        if (from != -1) {
//            searchSourceBuilder.from(from);
//        }
//        int size = request.paramAsInt("size", -1);
//        if (size != -1) {
//            searchSourceBuilder.size(size);
//        }
//    
//        if (request.hasParam("explain")) {
//            searchSourceBuilder.explain(request.paramAsBoolean("explain", null));
//        }
//        if (request.hasParam("version")) {
//            searchSourceBuilder.version(request.paramAsBoolean("version", null));
//        }
//        if (request.hasParam("timeout")) {
//            searchSourceBuilder.timeout(request.paramAsTime("timeout", null));
//        }
//        if (request.hasParam("terminate_after")) {
//            int terminateAfter = request.paramAsInt("terminate_after",
//                    SearchContext.DEFAULT_TERMINATE_AFTER);
//            if (terminateAfter < 0) {
//                throw new IllegalArgumentException("terminateAfter must be > 0");
//            } else if (terminateAfter > 0) {
//                searchSourceBuilder.terminateAfter(terminateAfter);
//            }
//        }
//    
//        if (request.param("fields") != null) {
//            throw new IllegalArgumentException("The parameter [" +
//                SearchSourceBuilder.FIELDS_FIELD + "] is no longer supported, please use [" +
//                SearchSourceBuilder.STORED_FIELDS_FIELD + "] to retrieve stored fields or _source filtering " +
//                "if the field is not stored");
//        }
//    
//    
//        StoredFieldsContext storedFieldsContext =
//            StoredFieldsContext.fromRestRequest(SearchSourceBuilder.STORED_FIELDS_FIELD.getPreferredName(), request);
//        if (storedFieldsContext != null) {
//            searchSourceBuilder.storedFields(storedFieldsContext);
//        }
//        String sDocValueFields = request.param("docvalue_fields");
//        if (sDocValueFields == null) {
//            sDocValueFields = request.param("fielddata_fields");
//        }
//        if (sDocValueFields != null) {
//            if (Strings.hasText(sDocValueFields)) {
//                String[] sFields = Strings.splitStringByCommaToArray(sDocValueFields);
//                for (String field : sFields) {
//                    searchSourceBuilder.docValueField(field);
//                }
//            }
//        }
//        FetchSourceContext fetchSourceContext = FetchSourceContext.parseFromRestRequest(request);
//        if (fetchSourceContext != null) {
//            searchSourceBuilder.fetchSource(fetchSourceContext);
//        }
//    
//        if (request.hasParam("track_scores")) {
//            searchSourceBuilder.trackScores(request.paramAsBoolean("track_scores", false));
//        }
//    
//        String sSorts = request.param("sort");
//        if (sSorts != null) {
//            String[] sorts = Strings.splitStringByCommaToArray(sSorts);
//            for (String sort : sorts) {
//                int delimiter = sort.lastIndexOf(":");
//                if (delimiter != -1) {
//                    String sortField = sort.substring(0, delimiter);
//                    String reverse = sort.substring(delimiter + 1);
//                    if ("asc".equals(reverse)) {
//                        searchSourceBuilder.sort(sortField, SortOrder.ASC);
//                    } else if ("desc".equals(reverse)) {
//                        searchSourceBuilder.sort(sortField, SortOrder.DESC);
//                    }
//                } else {
//                    searchSourceBuilder.sort(sort);
//                }
//            }
//        }
//    
//        String sStats = request.param("stats");
//        if (sStats != null) {
//            searchSourceBuilder.stats(Arrays.asList(Strings.splitStringByCommaToArray(sStats)));
//        }
//    
//        String suggestField = request.param("suggest_field");
//        if (suggestField != null) {
//            String suggestText = request.param("suggest_text", request.param("q"));
//            int suggestSize = request.paramAsInt("suggest_size", 5);
//            String suggestMode = request.param("suggest_mode");
//            searchSourceBuilder.suggest(new SuggestBuilder().addSuggestion(suggestField,
//                    termSuggestion(suggestField)
//                        .text(suggestText).size(suggestSize)
//                        .suggestMode(SuggestMode.resolve(suggestMode))));
//        }
//    }

}
