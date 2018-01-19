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

import org.elasticsearch.search.fql.grammar.Displayer;
import org.elasticsearch.search.fql.grammar.Rule;
import org.elasticsearch.search.fql.grammar.Rule_and;
import org.elasticsearch.search.fql.grammar.Rule_andnot;
import org.elasticsearch.search.fql.grammar.Rule_datetime_value;
import org.elasticsearch.search.fql.grammar.Rule_float_token;
import org.elasticsearch.search.fql.grammar.Rule_fql_expression;
import org.elasticsearch.search.fql.grammar.Rule_integer_value;
import org.elasticsearch.search.fql.grammar.Rule_internal_in_expression;
import org.elasticsearch.search.fql.grammar.Rule_max_range_value;
import org.elasticsearch.search.fql.grammar.Rule_min_range_value;
import org.elasticsearch.search.fql.grammar.Rule_or;
import org.elasticsearch.search.fql.grammar.Rule_phrase_token;
import org.elasticsearch.search.fql.grammar.Rule_range_token;
import org.elasticsearch.search.fql.grammar.Rule_string_value;
import org.elasticsearch.search.fql.grammar.Terminal_NumericValue;
import org.elasticsearch.search.fql.grammar.Terminal_StringValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FqlVisitor extends Displayer {
    FqlData fqldata = null;
    public static final String AND = "and";
    public static final String ANDNOT = "andnot";
    public static final String OR = "or";
    public static final String DEFAULT_OPERATER = AND;

    boolean isPretty = false;

    public FqlVisitor(boolean isPretty) {
        super();
        this.isPretty = isPretty;
        this.fqldata = new FqlData(this.isPretty);
    }

    public String toString() {
        return this.fqldata.toString();
    }

    class PhraseValue extends Object {
        String str;

        PhraseValue(String str) {
            this.str = str;
        }

        public String toString() {
            return this.str;
        }
    }

    class RangeValue {
        Object min;
        Object max;

        public String toString() {
            String _max = max instanceof LocalDateTime ? "\"" + max + "\"" : "" + max;
            String _min = min instanceof LocalDateTime ? "\"" + min + "\"" : "" + min;
            String str = "{";
            if (min != null) {
                str += "\"gte\":" + _min;
            }
            if (max != null) {
                if (min != null) {
                    str += ",";
                }
                str += "\"lt\":" + _max;
            }
            str += "}";
            return str;
        }
    }

    public class ModeStack {
        public ModeStack parent = null;
        public String name; // "and"/"or"
        public List<Map.Entry<String, ?>> list;
        boolean isPretty = false;

        ModeStack(String name, boolean isPretty) {
            this(name, null, isPretty);
        }

        ModeStack(String name, ModeStack parent, boolean isPretty) {
            this.parent = parent;
            this.name = name;
            list = new ArrayList<Map.Entry<String, ?>>();
            this.isPretty = isPretty;
        }

        String indent() {
            String ret = "  ";
            int count = 0;
            ModeStack now = this.parent;
            while (now != null) {
                ret += "    ";
                now = now.parent;
            }
            return ret;
        }

        public String toString() {
            String ret = "";

            String _indent = this.isPretty ? this.indent() : "";
            String _one_indent = this.isPretty ? "  " : "";
            String _crlf = this.isPretty ? "\n" : "";
            if (this.parent == null) {
                // ret += this.name + "\n" ;
                if (FqlVisitor.AND.equals(this.name)) {
                    ret += "\"bool\": {" + _crlf;
                    ret += "\"must\": [" + _crlf;
                } else if (FqlVisitor.OR.equals(this.name)) {
                    ret += "\"bool\": {" + _crlf;
                    ret += "\"should\": [" + _crlf;
                } else if (FqlVisitor.ANDNOT.equals(this.name)) {
                    ret += "\"bool\": {" + _crlf;
                    ret += "\"must_not\": [" + _crlf;

                    // // TODO
                    // //
                    // https://stackoverflow.com/questions/38640784/combining-must-not-in-elasticsearch-query
                    // ret += "{" + _crlf;
                    // ret += "\"query\": {" + _crlf;
                    // ret += "\"bool\": {" + _crlf;
                    // ret += "\"must\": [" + _crlf;

                } else {
                }
            }
            int len = this.list.size();
            int i = 0;
            for (Map.Entry<String, ?> entry : this.list) {
                i++;
                if (entry.getValue() instanceof ModeStack) {
                    if (FqlVisitor.AND.equals(entry.getKey())) {
                        ret += _indent + "{\"bool\": {" + _crlf;
                        ret += _indent + _one_indent + "\"must\": [" + _crlf;
                        ret += entry.getValue().toString();
                        ret += _indent + _one_indent + "]" + _crlf;
                        ret += _indent + "}}";
                    } else if (FqlVisitor.OR.equals(entry.getKey())) {
                        ret += _indent + "{\"bool\": {" + _crlf;
                        ret += _indent + _one_indent + "\"should\": [" + _crlf;
                        ret += entry.getValue().toString();
                        ret += _indent + _one_indent + "]" + _crlf;
                        ret += _indent + "}}";
                    } else if (FqlVisitor.ANDNOT.equals(entry.getKey())) {
                        ret += _indent + "{\"bool\": {" + _crlf;
                        ret += _indent + _one_indent + "\"must_not\": [" + _crlf;

                        // // TODO
                        // //
                        // https://stackoverflow.com/questions/38640784/combining-must-not-in-elasticsearch-query
                        // ret += "{" + _crlf;
                        // ret += "\"query\": {" + _crlf;
                        // ret += "\"bool\": {" + _crlf;
                        // ret += "\"must\": [" + _crlf;

                        ret += entry.getValue().toString();

                        // // TODO
                        // //
                        // https://stackoverflow.com/questions/38640784/combining-must-not-in-elasticsearch-query
                        // ret += "]" + _crlf; // must
                        // ret += "}" + _crlf; // bool
                        // ret += "}" + _crlf; // query
                        // ret += "}" + _crlf; //

                        ret += _indent + _one_indent + "]";
                        ret += _indent + "}}";
                    } else {
                    }
                } else {
                    if (entry.getValue() instanceof RangeValue) {
                        ret += _indent + "{\"range\": { \"" + entry.getKey() + "\": " + entry.getValue() + "}}";
                    } else if (entry.getValue() instanceof PhraseValue) {
                        ret += _indent + "{\"match_phrase\": { \"" + (entry.getKey() == null ? "_all" : entry.getKey().toString()) + "\": "
                                + entry.getValue() + "}}";
                    } else {
                        ret += _indent + "{\"match\": { \"" + (entry.getKey() == null ? "_all" : entry.getKey().toString()) + "\": "
                                + entry.getValue() + "}}";
                    }
                }
                if (i < len) {
                    ret += "," + _crlf;
                } else {
                    ret += _crlf;
                }
            }
            if (this.parent == null) {
                if (FqlVisitor.ANDNOT.equals(this.name)) {

                    // // TODO
                    // //
                    // https://stackoverflow.com/questions/38640784/combining-must-not-in-elasticsearch-query
                    // ret += "]" + _crlf; // must
                    // ret += "}" + _crlf; // bool
                    // ret += "}" + _crlf; // query
                    // ret += "}" + _crlf; //

                    ret += "]" + _crlf;
                    ret += "}" + _crlf;
                } else {
                    ret += "]" + _crlf;
                    ret += "}" + _crlf;
                }
            }
            return ret;
        }
    }

    class FqlData {
        int depth;

        public Deque<Object> queue = null;

        ModeStack top = null;
        ModeStack now = null;
        boolean isPretty = false;

        FqlData(boolean isPretty) {
            this.depth = 0;
            this.queue = new ArrayDeque<Object>();
            this.isPretty = isPretty;
        }

        public String toString() {
            return this.top.toString();
        }

        Object ProcessValue = null;

        String inexpression = null;
        Boolean phrase = false;

        public void setInExpression(String name) {
            this.inexpression = name;
        }

        public void setProcessValue(Object value) {
            this.ProcessValue = value;
        }

        public void setToken() {
            if (this.ProcessValue != null) {
                if (this.now == null) {
                    this.top = new ModeStack(DEFAULT_OPERATER, this.isPretty);
                    this.now = this.top;
                }

                this.now.list.add(new AbstractMap.SimpleEntry<String, Object>(this.inexpression, this.ProcessValue));
                this.inexpression = null;
                this.ProcessValue = null;
            }
        }

        public void down(String name) {
            if (this.now == null) {
                this.top = new ModeStack(name, this.isPretty);
                this.now = this.top;
            } else {
                ModeStack down = new ModeStack(name, this.now, this.isPretty);
                this.now.list.add(new AbstractMap.SimpleEntry<String, ModeStack>(name, down));
                this.now = down;
            }
        }

        public void up() {
            this.now = this.now.parent;
        }
    }

    @Override
    public Object visit(Rule_fql_expression rule) {

        this.fqldata.depth++;

        Object ret = visitRules(rule.rules);

        this.fqldata.depth--;

        this.fqldata.setToken();

        return ret;
    }

    @Override
    public Object visit(Rule_and rule) {
        this.fqldata.down(FqlVisitor.AND);
        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.up();
        this.fqldata.depth--;

        return ret;
    }

    @Override
    public Object visit(Rule_andnot rule) {
        this.fqldata.down(FqlVisitor.ANDNOT);
        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.up();
        this.fqldata.depth--;

        return ret;
    }

    @Override
    public Object visit(Rule_or rule) {
        this.fqldata.down(FqlVisitor.OR);
        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.up();
        this.fqldata.depth--;

        return ret;
    }

    @Override
    public Object visit(Rule_phrase_token rule) {
        this.fqldata.depth++;
        this.fqldata.phrase = true;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.phrase = false;
        this.fqldata.depth--;
        return ret;
    }

    @Override
    public Object visit(Rule_float_token rule) {
        String str = rule.spelling;
        Object obj = Float.parseFloat(str);

        this.fqldata.setProcessValue(obj);
        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.depth--;
        return ret;
    }

    @Override
    public Object visit(Rule_range_token rule) {
        this.fqldata.queue.addFirst(new RangeValue());

        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        RangeValue range = (RangeValue) this.fqldata.queue.removeFirst();
        this.fqldata.setProcessValue(range);
        this.fqldata.depth--;

        return ret;
    }

    @Override
    public Object visit(Rule_min_range_value rule) {

        this.fqldata.depth++;

        Object ret = visitRules(rule.rules);

        RangeValue range = (RangeValue) this.fqldata.queue.peekFirst();
        range.min = this.fqldata.ProcessValue;
        this.fqldata.depth--;

        return ret;
    }

    @Override
    public Object visit(Rule_max_range_value rule) {

        this.fqldata.depth++;

        Object ret = visitRules(rule.rules);

        RangeValue range = (RangeValue) this.fqldata.queue.peekFirst();
        range.max = this.fqldata.ProcessValue;
        this.fqldata.depth--;

        return ret;
    }

    @Override
    public Object visit(Rule_string_value rule) {

        String str = rule.spelling;

        // Object obj = StringEscapeUtils.unescapeHtml4(str) ;
        Object obj = this.fqldata.phrase ? new PhraseValue(str) : str;

        this.fqldata.setProcessValue(obj);
        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.depth--;
        return ret;
    }

    @Override
    public Object visit(Rule_integer_value rule) {

        String str = rule.spelling;
        Object obj = Integer.parseInt(str);

        this.fqldata.setProcessValue(obj);
        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.depth--;
        return ret;
    }

    @Override
    public Object visit(Rule_datetime_value rule) {

        String str = rule.spelling;
        Pattern p = Pattern.compile("^.+T.+$");
        Matcher m = p.matcher(str);
        Object obj = m.find() ? LocalDateTime.parse(str)
                : LocalDateTime.ofInstant(Date.from(LocalDate.parse(str).atStartOfDay(ZoneId.systemDefault()).toInstant()).toInstant(),
                        ZoneId.systemDefault());

        this.fqldata.setProcessValue(obj);
        this.fqldata.depth++;

        Object ret = this.visitRules(rule.rules);

        this.fqldata.depth--;
        return ret;
    }

    @Override
    public Object visit(Rule_internal_in_expression rule) {
        this.fqldata.depth++;
        this.fqldata.setInExpression(rule.spelling);

        Object ret = this.visitRules(rule.rules);

        this.fqldata.depth--;

        return ret;
    }

    @Override
    public Object visit(Terminal_StringValue value) {
        return null;
    }

    @Override
    public Object visit(Terminal_NumericValue value) {
        return null;
    }

    private Object visitRules(ArrayList<Rule> rules) {
        for (Rule rule : rules)
            rule.accept(this);
        return null;
    }

}

/*
 * -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
