/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jan 19 17:55:18 JST 2018
 *
 * -----------------------------------------------------------------------------
 */

package org.elasticsearch.search.fql.grammar;

public interface Visitor
{
  public Object visit(Rule_fql_expression rule);
  public Object visit(Rule_operator_expression rule);
  public Object visit(Rule_paren_expression rule);
  public Object visit(Rule_token rule);
  public Object visit(Rule_and rule);
  public Object visit(Rule_andnot rule);
  public Object visit(Rule_any rule);
  public Object visit(Rule_or rule);
  public Object visit(Rule_rank rule);
  public Object visit(Rule_rank_param rule);
  public Object visit(Rule_xrank rule);
  public Object visit(Rule_xrank_param rule);
  public Object visit(Rule_near rule);
  public Object visit(Rule_near_param rule);
  public Object visit(Rule_onear rule);
  public Object visit(Rule_onear_param rule);
  public Object visit(Rule_not rule);
  public Object visit(Rule_count rule);
  public Object visit(Rule_equals rule);
  public Object visit(Rule_starts_with rule);
  public Object visit(Rule_ends_with rule);
  public Object visit(Rule_filter rule);
  public Object visit(Rule_phrase_token rule);
  public Object visit(Rule_phrase_token_param rule);
  public Object visit(Rule_string_token rule);
  public Object visit(Rule_explicit_string_token rule);
  public Object visit(Rule_string_token_param rule);
  public Object visit(Rule_implicit_string_token rule);
  public Object visit(Rule_float_token rule);
  public Object visit(Rule_explicit_float_token rule);
  public Object visit(Rule_implicit_float_token rule);
  public Object visit(Rule_int_token rule);
  public Object visit(Rule_explicit_int_token rule);
  public Object visit(Rule_implicit_int_token rule);
  public Object visit(Rule_datetime_token rule);
  public Object visit(Rule_explicit_datetime_token rule);
  public Object visit(Rule_implicit_datetime_token rule);
  public Object visit(Rule_range_token rule);
  public Object visit(Rule_from_range_token_param rule);
  public Object visit(Rule_to_range_token_param rule);
  public Object visit(Rule_range_value rule);
  public Object visit(Rule_min_range_limit rule);
  public Object visit(Rule_max_range_limit rule);
  public Object visit(Rule_min_range_value rule);
  public Object visit(Rule_max_range_value rule);
  public Object visit(Rule_from_condition rule);
  public Object visit(Rule_unquoted_from_condition rule);
  public Object visit(Rule_to_condition rule);
  public Object visit(Rule_unquoted_to_condition rule);
  public Object visit(Rule_string_value rule);
  public Object visit(Rule_quoted_string_value rule);
  public Object visit(Rule_quoted_escaped_character rule);
  public Object visit(Rule_quoted_escaped_backslash rule);
  public Object visit(Rule_quoted_escaped_newline rule);
  public Object visit(Rule_quoted_escaped_carriage_return rule);
  public Object visit(Rule_quoted_escaped_tab rule);
  public Object visit(Rule_quoted_escaped_backspace rule);
  public Object visit(Rule_quoted_escaped_form_feed rule);
  public Object visit(Rule_quoted_escaped_double_quote rule);
  public Object visit(Rule_quoted_escaped_single_quote rule);
  public Object visit(Rule_unquoted_string_value rule);
  public Object visit(Rule_integer_value rule);
  public Object visit(Rule_unsigned_integer_value rule);
  public Object visit(Rule_float_value rule);
  public Object visit(Rule_datetime_value rule);
  public Object visit(Rule_year rule);
  public Object visit(Rule_month rule);
  public Object visit(Rule_day rule);
  public Object visit(Rule_hour rule);
  public Object visit(Rule_minute rule);
  public Object visit(Rule_second rule);
  public Object visit(Rule_yesno_value rule);
  public Object visit(Rule_quoted_yesno_value rule);
  public Object visit(Rule_unquoted_yesno_value rule);
  public Object visit(Rule_onoff_value rule);
  public Object visit(Rule_quoted_onoff_value rule);
  public Object visit(Rule_unquoted_onoff_value rule);
  public Object visit(Rule_mode_value rule);
  public Object visit(Rule_internal_in_expression rule);
  public Object visit(Rule_in_expression rule);
  public Object visit(Rule_numeric_or_mode rule);
  public Object visit(Rule_token_distance rule);
  public Object visit(Rule_internal_property_name rule);
  public Object visit(Rule_property_name rule);
  public Object visit(Rule_multiple_fql_params rule);
  public Object visit(Rule_ALPHA rule);
  public Object visit(Rule_BIT rule);
  public Object visit(Rule_CHAR rule);
  public Object visit(Rule_CR rule);
  public Object visit(Rule_CRLF rule);
  public Object visit(Rule_CTL rule);
  public Object visit(Rule_DIGIT rule);
  public Object visit(Rule_DQUOTE rule);
  public Object visit(Rule_HEXDIG rule);
  public Object visit(Rule_HTAB rule);
  public Object visit(Rule_LF rule);
  public Object visit(Rule_LWSP rule);
  public Object visit(Rule_OCTET rule);
  public Object visit(Rule_SP rule);
  public Object visit(Rule_VCHAR rule);
  public Object visit(Rule_WSP rule);

  public Object visit(Terminal_StringValue value);
  public Object visit(Terminal_NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
