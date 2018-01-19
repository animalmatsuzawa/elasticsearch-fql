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

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fql.grammar.ParserException;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.test.ESTestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class FastQueryLanguageRequestParserTests extends ESTestCase {

    public void checkGrammerError( String str ) {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();
        try{
            fast_parser.parseQuery(str);
            fail("No Error!!");
        } 
        catch (RuntimeException ex )
        {
            if(!(ex.getCause() instanceof ParserException))
            {
                fail(ex.getMessage());
            }
        }
    }
    
    public void testPaseQuery_syntax() throws IOException {

        /* 正常 */
        try{
            checkGrammerError("text:equals(\"16\")") ;
        } catch (AssertionError ex )
        {
            if( !ex.getMessage().equals("No Error!!"))
            {
                fail("Why?????????");
            }
        }
        /* space */
        checkGrammerError(" text:equals(\"16\")") ;
        checkGrammerError("text: equals(\"16\")") ;
        checkGrammerError("text:equals( \"16\")") ;

        /* カッコがあわない */
        checkGrammerError("text:equals(\"16\"))") ;
        checkGrammerError("text:equals(\"16\"))") ;
        checkGrammerError("and(text:equals(\"16\")") ;

        /* typo */
        checkGrammerError("anf(text:equals(\"16\"))") ;
        checkGrammerError("and(text;equals(\"16\"))") ;
        checkGrammerError("and{text:equals(\"16\")}") ;

    }

    public void testPaseQuery_token() throws IOException {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();

        String fql = "" ;
        String answer = "" ;
        
        /* 文字列 */
        fql = "text:equals(\"16\")" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"text\": \"16\"}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "text:\"16\"" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"text\": \"16\"}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "text:string(\"ほげほげ\",mode=\"AND\")" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"text\": \"ほげほげ\"}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

        
        /* flag */
        fql = "flag:0" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"flag\": 0}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "flag:1" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"flag\": 1}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        /* 数値 */
        fql = "num:10" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"num\": 10}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "num:equals(10)" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"num\": 10}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "num:[1;]" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"num\": {\"gte\":1}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "num:[;10]" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"num\": {\"lt\":10}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "num:[1;10]" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"num\": {\"gte\":1,\"lt\":10}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "num:range(1,10)" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"num\": {\"gte\":1,\"lt\":10}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "num:10.0" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"num\": 10.0}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "num:equals(10.0)" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"num\": 10.0}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        /* 日時 */
        fql = "date:2017-01-31" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"date\": 2017-01-31T00:00}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "date:2017-01-31T01:10:10" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"date\": 2017-01-31T01:10:10}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        
        fql = "date:[2017-01-31;]" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"date\": {\"gte\":\"2017-01-31T00:00\"}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "date:[;2018-01-31]" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"date\": {\"lt\":\"2018-01-31T00:00\"}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "date:range(2017-01-31,2018-01-31)" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"date\": {\"gte\":\"2017-01-31T00:00\",\"lt\":\"2018-01-31T00:00\"}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "date:[2017-01-31;2018-01-31]" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"range\": { \"date\": {\"gte\":\"2017-01-31T00:00\",\"lt\":\"2018-01-31T00:00\"}}}]}}";
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
    }
    public void testPaseQuery_and() throws IOException {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();

        String fql = "" ;
        String answer = "" ;
        
        fql = "and(field1:equals(\"16\"),field2:0)" ;
        answer = "\"query\":{\"bool\": {\"must\": [{\"match\": { \"field1\": \"16\"}},{\"match\": { \"field2\": 0}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "and(field1:equals(\"16\"),field2:0,field3:100)" ;
        answer = "\"query\":{\"bool\": {\"must\": ["
                + "{\"match\": { \"field1\": \"16\"}},"
                + "{\"match\": { \"field2\": 0}},"
                + "{\"match\": { \"field3\": 100}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

    }
    public void testPaseQuery_andnot() throws IOException {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();

        String fql = "" ;
        String answer = "" ;
        
        fql = "andnot(field1:equals(\"16\"),field2:0)" ;
        answer = "\"query\":{\"bool\": {\"must_not\": [{\"match\": { \"field1\": \"16\"}},{\"match\": { \"field2\": 0}}]}}" ;
//        String str = fast_parser.parseQuery(fql,true);
//        String esq = fast_parser.parseQuery(fql);
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "andnot(field1:equals(\"16\"),field2:0,field3:100)" ;
        answer = "\"query\":{\"bool\": {\"must_not\": ["
                + "{\"match\": { \"field1\": \"16\"}},"
                + "{\"match\": { \"field2\": 0}},"
                + "{\"match\": { \"field3\": 100}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

    }
    public void testPaseQuery_or() throws IOException {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();

        String fql = "" ;
        String answer = "" ;
        
        fql = "or(field1:equals(\"16\"),field2:0)" ;
        answer = "\"query\":{\"bool\": {\"should\": [{\"match\": { \"field1\": \"16\"}},{\"match\": { \"field2\": 0}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "or(field1:equals(\"16\"),field2:0,field3:100)" ;
        answer = "\"query\":{\"bool\": {\"should\": ["
                + "{\"match\": { \"field1\": \"16\"}},"
                + "{\"match\": { \"field2\": 0}},"
                + "{\"match\": { \"field3\": 100}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

    }
    public void testPaseQuery_and_or_andnot() throws IOException {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();

        String fql = "" ;
        String answer = "" ;
        
        fql = "and(field1:equals(\"16\"),and(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"must\": ["
                + "{\"match\": { \"field1\": \"16\"}},"
                + "{\"bool\": {\"must\": ["
                + "{\"match\": { \"field2\": \"16\"}},"
                + "{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "and(field1:equals(\"16\"),or(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"must\": ["
                + "{\"match\": { \"field1\": \"16\"}},"
                + "{\"bool\": {\"should\": ["
                + "{\"match\": { \"field2\": \"16\"}},"
                + "{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "and(field1:equals(\"16\"),andnot(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"must\": ["
                + "{\"match\": { \"field1\": \"16\"}},"
                + "{\"bool\": {\"must_not\": ["
                + "{\"match\": { \"field2\": \"16\"}},"
                + "{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

   
        
        fql = "or(field1:equals(\"16\"),and(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"should\": ["
                + "{\"match\": { \"field1\": \"16\"}},{\"bool\": {\"must\": ["
                + "{\"match\": { \"field2\": \"16\"}},{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "or(field1:equals(\"16\"),or(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"should\": ["
                + "{\"match\": { \"field1\": \"16\"}},{\"bool\": {\"should\": ["
                + "{\"match\": { \"field2\": \"16\"}},{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "or(field1:equals(\"16\"),andnot(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"should\": ["
                + "{\"match\": { \"field1\": \"16\"}},{\"bool\": {\"must_not\": ["
                + "{\"match\": { \"field2\": \"16\"}},{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

        
        
        fql = "andnot(field1:equals(\"16\"),and(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"must_not\": ["
                + "{\"match\": { \"field1\": \"16\"}},{\"bool\": {\"must\": ["
                + "{\"match\": { \"field2\": \"16\"}},{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );
        
        fql = "andnot(field1:equals(\"16\"),or(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"must_not\": ["
                + "{\"match\": { \"field1\": \"16\"}},{\"bool\": {\"should\": ["
                + "{\"match\": { \"field2\": \"16\"}},{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

        fql = "andnot(field1:equals(\"16\"),andnot(field2:equals(\"16\"),field3:0))" ;
        answer = "\"query\":{\"bool\": {\"must_not\": ["
                + "{\"match\": { \"field1\": \"16\"}},{\"bool\": {\"must_not\": ["
                + "{\"match\": { \"field2\": \"16\"}},{\"match\": { \"field3\": 0}}]}}]}}" ;
        assertEquals( answer, fast_parser.parseQuery(fql) );

    }
    
    
    public class MyEntry<K, V> implements Entry<K, V> {
        private final K key;
        private V value;
        public MyEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        public K getKey() {
            return key;
        }
        public V getValue() {
            return value;
        }
        public V setValue(final V value) {
            final V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
    
    public void checkSort( String sortParam, List<Entry<String,SortOrder>> result )
    {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        fast_parser.parseSort(sortParam, searchSourceBuilder);
        List<SortBuilder<?>> lists = searchSourceBuilder.sorts();
        assertEquals( lists.size(), result.size() );
        for( int i = 0 ; i < lists.size() ; i++) {
            FieldSortBuilder s = (FieldSortBuilder)lists.get(i);
            assertEquals( s.getFieldName(), result.get(i).getKey() );
            assertEquals( s.order(), result.get(i).getValue() ) ;
        }
    }

    @SuppressWarnings("serial")
    public void testParseSort() throws IOException {
        String sortParam = "" ;
        List<Entry<String,SortOrder>> result = null ;
        
        sortParam = "+field1";
        result = new ArrayList<Entry<String,SortOrder>>(){{
            add(new MyEntry<String, SortOrder>("field1",SortOrder.ASC));
        }};
        checkSort(sortParam, result) ;

        sortParam = "-field1";
        result = new ArrayList<Entry<String,SortOrder>>(){{
            add(new MyEntry<String, SortOrder>("field1",SortOrder.DESC));
        }};
        checkSort(sortParam, result) ;
        
        sortParam = "+field1-field2";
        result = new ArrayList<Entry<String,SortOrder>>(){{
            add(new MyEntry<String, SortOrder>("field1",SortOrder.ASC));
            add(new MyEntry<String, SortOrder>("field2",SortOrder.DESC));
        }};
        checkSort(sortParam, result) ;
        
        sortParam = "-field1+field2";
        result = new ArrayList<Entry<String,SortOrder>>(){{
            add(new MyEntry<String, SortOrder>("field1",SortOrder.DESC));
            add(new MyEntry<String, SortOrder>("field2",SortOrder.ASC));
        }};
        checkSort(sortParam, result) ;
        
        sortParam = "+field1-field2+field3";
        result = new ArrayList<Entry<String,SortOrder>>(){{
            add(new MyEntry<String, SortOrder>("field1",SortOrder.ASC));
            add(new MyEntry<String, SortOrder>("field2",SortOrder.DESC));
            add(new MyEntry<String, SortOrder>("field3",SortOrder.ASC));
        }};
        checkSort(sortParam, result) ;
    }
    
    public void testParseNavigation() throws IOException {
        FastQueryLanguageRequestParser fast_parser = new FastQueryLanguageRequestParser();
        String navigationParam = "";
        String esq = "" ;
        String answer = "" ;
     
        navigationParam = "+code1:^\"1004@@@2059\"$" ;
        answer = "\"post_filter\":{\"bool\": {\"must\": [{\"match\": { \"code1\": \"1004@@@2059\"}}]}}" ;
        assertEquals( answer, fast_parser.parseNavigation(navigationParam) );
        
        navigationParam = "+code1:^\"1004@@@2059\"$ +code2:^\"1004@@@2059\"$" ;
        esq = fast_parser.parseNavigation(navigationParam) ;
        answer = "\"post_filter\":{\"bool\": {\"must\": ["
                + "{\"match\": { \"code1\": \"1004@@@2059\"}},"
                + "{\"match\": { \"code2\": \"1004@@@2059\"}}]}}" ;
        assertEquals( answer, fast_parser.parseNavigation(navigationParam) );

    }

}
