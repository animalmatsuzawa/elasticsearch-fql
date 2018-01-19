/* -----------------------------------------------------------------------------
 * XmlDisplayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jan 19 17:55:18 JST 2018
 *
 * -----------------------------------------------------------------------------
 */

package org.elasticsearch.search.fql.grammar;

import java.util.ArrayList;

public class XmlDisplayer implements Visitor
{
  private boolean terminal = true;

  public Object visit(Rule_fql_expression rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<fql-expression>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</fql-expression>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_operator_expression rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<operator-expression>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</operator-expression>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_paren_expression rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<paren-expression>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</paren-expression>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_and rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<and>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</and>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_andnot rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<andnot>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</andnot>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_any rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<any>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</any>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_or rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<or>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</or>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_rank rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<rank>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</rank>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_rank_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<rank-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</rank-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xrank rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xrank>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xrank>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xrank_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xrank-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xrank-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_near rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<near>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</near>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_near_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<near-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</near-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_onear rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<onear>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</onear>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_onear_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<onear-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</onear-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_not rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<not>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</not>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_count rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<count>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</count>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_equals rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<equals>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</equals>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_starts_with rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<starts-with>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</starts-with>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ends_with rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ends-with>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ends-with>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_filter rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<filter>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</filter>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_phrase_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<phrase-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</phrase-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_phrase_token_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<phrase-token-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</phrase-token-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_string_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<string-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</string-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_explicit_string_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<explicit-string-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</explicit-string-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_string_token_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<string-token-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</string-token-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_implicit_string_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<implicit-string-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</implicit-string-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_float_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<float-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</float-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_explicit_float_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<explicit-float-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</explicit-float-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_implicit_float_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<implicit-float-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</implicit-float-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_int_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<int-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</int-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_explicit_int_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<explicit-int-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</explicit-int-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_implicit_int_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<implicit-int-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</implicit-int-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_datetime_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<datetime-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</datetime-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_explicit_datetime_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<explicit-datetime-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</explicit-datetime-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_implicit_datetime_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<implicit-datetime-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</implicit-datetime-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_range_token rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<range-token>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</range-token>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_from_range_token_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<from-range-token-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</from-range-token-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_to_range_token_param rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<to-range-token-param>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</to-range-token-param>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_range_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<range-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</range-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_min_range_limit rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<min-range-limit>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</min-range-limit>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_max_range_limit rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<max-range-limit>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</max-range-limit>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_min_range_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<min-range-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</min-range-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_max_range_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<max-range-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</max-range-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_from_condition rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<from-condition>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</from-condition>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unquoted_from_condition rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unquoted-from-condition>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unquoted-from-condition>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_to_condition rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<to-condition>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</to-condition>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unquoted_to_condition rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unquoted-to-condition>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unquoted-to-condition>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_string_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<string-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</string-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_string_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-string-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-string-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_character rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-character>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-character>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_backslash rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-backslash>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-backslash>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_newline rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-newline>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-newline>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_carriage_return rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-carriage-return>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-carriage-return>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_tab rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-tab>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-tab>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_backspace rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-backspace>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-backspace>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_form_feed rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-form-feed>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-form-feed>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_double_quote rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-double-quote>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-double-quote>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_escaped_single_quote rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-escaped-single-quote>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-escaped-single-quote>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unquoted_string_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unquoted-string-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unquoted-string-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_integer_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<integer-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</integer-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unsigned_integer_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unsigned-integer-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unsigned-integer-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_float_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<float-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</float-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_datetime_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<datetime-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</datetime-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_year rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<year>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</year>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_month rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<month>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</month>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_day rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<day>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</day>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_hour rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<hour>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</hour>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_minute rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<minute>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</minute>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_second rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<second>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</second>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_yesno_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<yesno-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</yesno-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_yesno_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-yesno-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-yesno-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unquoted_yesno_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unquoted-yesno-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unquoted-yesno-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_onoff_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<onoff-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</onoff-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quoted_onoff_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quoted-onoff-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quoted-onoff-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unquoted_onoff_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unquoted-onoff-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unquoted-onoff-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_mode_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<mode-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</mode-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_internal_in_expression rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<internal-in-expression>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</internal-in-expression>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_in_expression rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<in-expression>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</in-expression>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_numeric_or_mode rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<numeric-or-mode>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</numeric-or-mode>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_token_distance rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<token-distance>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</token-distance>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_internal_property_name rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<internal-property-name>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</internal-property-name>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_property_name rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<property-name>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</property-name>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_multiple_fql_params rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<multiple-fql-params>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</multiple-fql-params>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ALPHA rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ALPHA>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ALPHA>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_BIT rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<BIT>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</BIT>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_CHAR rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CHAR>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CHAR>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_CR rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CR>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CR>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_CRLF rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CRLF>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CRLF>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_CTL rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CTL>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CTL>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_DIGIT rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<DIGIT>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</DIGIT>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_DQUOTE rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<DQUOTE>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</DQUOTE>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_HEXDIG rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<HEXDIG>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</HEXDIG>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_HTAB rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<HTAB>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</HTAB>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_LF rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<LF>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</LF>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_LWSP rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<LWSP>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</LWSP>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_OCTET rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<OCTET>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</OCTET>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_SP rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<SP>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</SP>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_VCHAR rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<VCHAR>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</VCHAR>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_WSP rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<WSP>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</WSP>");
    terminal = false;
    return null;
  }

  public Object visit(Terminal_StringValue value)
  {
    System.out.print(value.spelling);
    terminal = true;
    return null;
  }

  public Object visit(Terminal_NumericValue value)
  {
    System.out.print(value.spelling);
    terminal = true;
    return null;
  }

  private Boolean visitRules(ArrayList<Rule> rules)
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
