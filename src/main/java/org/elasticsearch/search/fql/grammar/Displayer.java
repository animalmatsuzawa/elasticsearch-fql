/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jan 19 17:55:18 JST 2018
 *
 * -----------------------------------------------------------------------------
 */

package org.elasticsearch.search.fql.grammar;

import java.util.ArrayList;

public class Displayer implements Visitor
{

  public Object visit(Rule_fql_expression rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_operator_expression rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_paren_expression rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_and rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_andnot rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_any rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_or rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_rank rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_rank_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xrank rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xrank_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_near rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_near_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_onear rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_onear_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_not rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_count rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_equals rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_starts_with rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ends_with rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_filter rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_phrase_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_phrase_token_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_string_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_explicit_string_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_string_token_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_implicit_string_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_float_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_explicit_float_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_implicit_float_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_int_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_explicit_int_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_implicit_int_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_datetime_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_explicit_datetime_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_implicit_datetime_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_range_token rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_from_range_token_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_to_range_token_param rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_range_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_min_range_limit rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_max_range_limit rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_min_range_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_max_range_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_from_condition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unquoted_from_condition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_to_condition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unquoted_to_condition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_string_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_string_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_character rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_backslash rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_newline rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_carriage_return rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_tab rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_backspace rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_form_feed rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_double_quote rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_escaped_single_quote rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unquoted_string_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_integer_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unsigned_integer_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_float_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_datetime_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_year rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_month rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_day rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_hour rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_minute rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_second rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_yesno_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_yesno_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unquoted_yesno_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_onoff_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quoted_onoff_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unquoted_onoff_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_mode_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_internal_in_expression rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_in_expression rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_numeric_or_mode rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_token_distance rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_internal_property_name rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_property_name rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_multiple_fql_params rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ALPHA rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_BIT rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CHAR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CRLF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CTL rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_DIGIT rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_DQUOTE rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_HEXDIG rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_HTAB rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_LF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_LWSP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_OCTET rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_SP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_VCHAR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_WSP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Terminal_StringValue value)
  {
    System.out.print(value.spelling);
    return null;
  }

  public Object visit(Terminal_NumericValue value)
  {
    System.out.print(value.spelling);
    return null;
  }

  private Object visitRules(ArrayList<Rule> rules)
  {
    for (Rule rule : rules)
      rule.accept(this);
    return null;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
