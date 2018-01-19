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
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fql.grammar.Parser;
import org.elasticsearch.search.fql.grammar.ParserException;
import org.elasticsearch.search.fql.grammar.Rule;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastQueryLanguageRequestParser {
    /**
     * ロガー
     */
    private static final Logger log = Loggers.getLogger(FastQueryLanguageRequestParser.class);


    /**
     * 検索ロジックの AND OR パラメータを表す列挙型
     *
     * @author kenji
     *
     */
    private enum LOGICAL_OPERATOR_MODE {
        AND, OR, NONE
    }

    /**
     * 検索ロジックの AND OR の状態を管理するためのスタック用のリスト
     */
    private List<LOGICAL_OPERATOR_MODE> modeStack = new ArrayList<LOGICAL_OPERATOR_MODE>();
    // 処理をまとめるために内部クラスにしています。
    

    /**
     * ソート条件をパースします
     *
     * @param sortParam FASTに指定されたソート条件の文字列
     */
    public void parseSort(String sortParam, SearchSourceBuilder searchSourceBuilder) {
        StringBuffer sb = new StringBuffer();
        SortOrder orderOperator = SortOrder.ASC;
        for (int i = 0; i < sortParam.length(); i++) {
            char c = sortParam.charAt(i);
            if (c == '-') {
                if (sb.length() > 0) {
                    searchSourceBuilder.sort(sb.toString(), orderOperator);
                    sb = new StringBuffer();
                }
                orderOperator = SortOrder.DESC;
            } else if (c == '+') {
                if (sb.length() > 0) {
                    searchSourceBuilder.sort(sb.toString(), orderOperator);
                    sb = new StringBuffer();
                }
                orderOperator = SortOrder.ASC;
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            searchSourceBuilder.sort(sb.toString(), orderOperator);
        }
    }
    /**
     * FAST のナビゲーションクエリを解析し、検索条件に変換します。
     *
     * @param navigationParam ナビゲーションクエリの文字列
     * @return ナビゲーション検索条件のクエリのリスト
     */
    public String parseNavigation(String navigationParam) {
        return parseNavigation( navigationParam, false ) ;
    }
    public String parseNavigation(String navigationParam, boolean isPretty) {
        String ret = null ;
        String _crlf = isPretty ? "\n" : "" ;
        List<String> list = Arrays.asList(navigationParam.split("[\\s+-]"));
        FqlVisitor disp = new FqlVisitor( isPretty ) ;
        disp.fqldata.down(FqlVisitor.AND);

        for (String s : list) {
            try {
                if( s.isEmpty()) {
                    continue;
                }
                // 正規表現を取り除かないと、検索結果が取得できない
                s = s.replaceAll("\\^", "");
                s = s.replaceAll("\\$", "");
                
                Rule nav_rule = Parser.parse("token", s );
                
                nav_rule.accept(disp) ;
                disp.fqldata.setToken();
                
            } catch (IllegalArgumentException | ParserException e) {
                throw new RuntimeException(e);
            }
            
        }
        String str = disp.toString() ;
        if( str != null ) {
            ret = "\"post_filter\":{" + _crlf ;
            ret +=  str;
            ret += "}";
        }
        return ret;
    }
    
    /**
     * FAST のクエリ文字列を変換し、Elasticsearchのクエリ文字列にします。
     *
     * @param queryParam FASTのクエリ文字列
     * @return Elasticsearchに変換したクエリ文字列
     */
    
    public String parseQuery(String queryParam) {
        return( this.parseQuery( queryParam, false) );
    }
    public String parseQuery(String queryParam, boolean isPretty) {
        String ret = null ;
        String _crlf = isPretty ? "\n" : "" ;
        String es_query = null ;
        
        try {
            // FQLパース処理
            Rule fql_rule = Parser.parse("fql-expression", queryParam );
            FqlVisitor visitor = new FqlVisitor( isPretty ) ;
            fql_rule.accept(visitor) ;
            es_query = visitor.toString() ;
            
        } catch (IllegalArgumentException | ParserException e) {
            throw new RuntimeException(e);
        }
        if( es_query != null )
        {
            ret = "\"query\":{" + _crlf ;
            ret +=  es_query;
            ret += "}";
        }
        return ret;
        
    }
    
    
}
