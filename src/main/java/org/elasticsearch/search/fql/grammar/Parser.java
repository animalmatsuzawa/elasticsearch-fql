/* -----------------------------------------------------------------------------
 * Parser.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jan 19 17:55:18 JST 2018
 *
 * -----------------------------------------------------------------------------
 */

package org.elasticsearch.search.fql.grammar;

import java.util.Stack;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

public class Parser
{
  private Parser() {}

  static public void main(String[] args)
  {
    Properties arguments = new Properties();
    String error = "";
    boolean ok = args.length > 0;

    if (ok)
    {
      arguments.setProperty("Trace", "Off");
      arguments.setProperty("Rule", "fql-expression");

      for (int i = 0; i < args.length; i++)
      {
        if (args[i].equals("-trace"))
          arguments.setProperty("Trace", "On");
        else if (args[i].equals("-visitor"))
          arguments.setProperty("Visitor", args[++i]);
        else if (args[i].equals("-file"))
          arguments.setProperty("File", args[++i]);
        else if (args[i].equals("-string"))
          arguments.setProperty("String", args[++i]);
        else if (args[i].equals("-rule"))
          arguments.setProperty("Rule", args[++i]);
        else
        {
          error = "unknown argument: " + args[i];
          ok = false;
        }
      }
    }

    if (ok)
    {
      if (arguments.getProperty("File") == null &&
          arguments.getProperty("String") == null)
      {
        error = "insufficient arguments: -file or -string required";
        ok = false;
      }
    }

    if (!ok)
    {
      System.out.println("error: " + error);
      System.out.println("usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]");
    }
    else
    {
      try
      {
        Rule rule = null;

        if (arguments.getProperty("File") != null)
        {
          rule = 
            parse(
              arguments.getProperty("Rule"), 
              new File(arguments.getProperty("File")), 
              arguments.getProperty("Trace").equals("On"));
        }
        else if (arguments.getProperty("String") != null)
        {
          rule = 
            parse(
              arguments.getProperty("Rule"), 
              arguments.getProperty("String"), 
              arguments.getProperty("Trace").equals("On"));
        }

        if (arguments.getProperty("Visitor") != null)
        {
          Visitor visitor = 
            (Visitor)Class.forName(arguments.getProperty("Visitor")).newInstance();
          rule.accept(visitor);
        }
      }
      catch (IllegalArgumentException e)
      {
        System.out.println("argument error: " + e.getMessage());
      }
      catch (IOException e)
      {
        System.out.println("io error: " + e.getMessage());
      }
      catch (ParserException e)
      {
        System.out.println("parser error: " + e.getMessage());
      }
      catch (ClassNotFoundException e)
      {
        System.out.println("visitor error: class not found - " + e.getMessage());
      }
      catch (IllegalAccessException e)
      {
        System.out.println("visitor error: illegal access - " + e.getMessage());
      }
      catch (InstantiationException e)
      {
        System.out.println("visitor error: instantiation failure - " + e.getMessage());
      }
    }
  }

  static public Rule parse(String rulename, String string)
  throws IllegalArgumentException,
         ParserException
  {
    return parse(rulename, string, false);
  }

  static public Rule parse(String rulename, InputStream in)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    return parse(rulename, in, false);
  }

  static public Rule parse(String rulename, File file)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    return parse(rulename, file, false);
  }

  static private Rule parse(String rulename, String string, boolean trace)
  throws IllegalArgumentException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (string == null)
      throw new IllegalArgumentException("null string");

    ParserContext context = new ParserContext(string, trace);

    Rule rule = null;
    if (rulename.equalsIgnoreCase("fql-expression")) rule = Rule_fql_expression.parse(context);
    else if (rulename.equalsIgnoreCase("operator-expression")) rule = Rule_operator_expression.parse(context);
    else if (rulename.equalsIgnoreCase("paren-expression")) rule = Rule_paren_expression.parse(context);
    else if (rulename.equalsIgnoreCase("token")) rule = Rule_token.parse(context);
    else if (rulename.equalsIgnoreCase("and")) rule = Rule_and.parse(context);
    else if (rulename.equalsIgnoreCase("andnot")) rule = Rule_andnot.parse(context);
    else if (rulename.equalsIgnoreCase("any")) rule = Rule_any.parse(context);
    else if (rulename.equalsIgnoreCase("or")) rule = Rule_or.parse(context);
    else if (rulename.equalsIgnoreCase("rank")) rule = Rule_rank.parse(context);
    else if (rulename.equalsIgnoreCase("rank-param")) rule = Rule_rank_param.parse(context);
    else if (rulename.equalsIgnoreCase("xrank")) rule = Rule_xrank.parse(context);
    else if (rulename.equalsIgnoreCase("xrank-param")) rule = Rule_xrank_param.parse(context);
    else if (rulename.equalsIgnoreCase("near")) rule = Rule_near.parse(context);
    else if (rulename.equalsIgnoreCase("near-param")) rule = Rule_near_param.parse(context);
    else if (rulename.equalsIgnoreCase("onear")) rule = Rule_onear.parse(context);
    else if (rulename.equalsIgnoreCase("onear-param")) rule = Rule_onear_param.parse(context);
    else if (rulename.equalsIgnoreCase("not")) rule = Rule_not.parse(context);
    else if (rulename.equalsIgnoreCase("count")) rule = Rule_count.parse(context);
    else if (rulename.equalsIgnoreCase("equals")) rule = Rule_equals.parse(context);
    else if (rulename.equalsIgnoreCase("starts-with")) rule = Rule_starts_with.parse(context);
    else if (rulename.equalsIgnoreCase("ends-with")) rule = Rule_ends_with.parse(context);
    else if (rulename.equalsIgnoreCase("filter")) rule = Rule_filter.parse(context);
    else if (rulename.equalsIgnoreCase("phrase-token")) rule = Rule_phrase_token.parse(context);
    else if (rulename.equalsIgnoreCase("phrase-token-param")) rule = Rule_phrase_token_param.parse(context);
    else if (rulename.equalsIgnoreCase("string-token")) rule = Rule_string_token.parse(context);
    else if (rulename.equalsIgnoreCase("explicit-string-token")) rule = Rule_explicit_string_token.parse(context);
    else if (rulename.equalsIgnoreCase("string-token-param")) rule = Rule_string_token_param.parse(context);
    else if (rulename.equalsIgnoreCase("implicit-string-token")) rule = Rule_implicit_string_token.parse(context);
    else if (rulename.equalsIgnoreCase("float-token")) rule = Rule_float_token.parse(context);
    else if (rulename.equalsIgnoreCase("explicit-float-token")) rule = Rule_explicit_float_token.parse(context);
    else if (rulename.equalsIgnoreCase("implicit-float-token")) rule = Rule_implicit_float_token.parse(context);
    else if (rulename.equalsIgnoreCase("int-token")) rule = Rule_int_token.parse(context);
    else if (rulename.equalsIgnoreCase("explicit-int-token")) rule = Rule_explicit_int_token.parse(context);
    else if (rulename.equalsIgnoreCase("implicit-int-token")) rule = Rule_implicit_int_token.parse(context);
    else if (rulename.equalsIgnoreCase("datetime-token")) rule = Rule_datetime_token.parse(context);
    else if (rulename.equalsIgnoreCase("explicit-datetime-token")) rule = Rule_explicit_datetime_token.parse(context);
    else if (rulename.equalsIgnoreCase("implicit-datetime-token")) rule = Rule_implicit_datetime_token.parse(context);
    else if (rulename.equalsIgnoreCase("range-token")) rule = Rule_range_token.parse(context);
    else if (rulename.equalsIgnoreCase("from-range-token-param")) rule = Rule_from_range_token_param.parse(context);
    else if (rulename.equalsIgnoreCase("to-range-token-param")) rule = Rule_to_range_token_param.parse(context);
    else if (rulename.equalsIgnoreCase("range-value")) rule = Rule_range_value.parse(context);
    else if (rulename.equalsIgnoreCase("min-range-limit")) rule = Rule_min_range_limit.parse(context);
    else if (rulename.equalsIgnoreCase("max-range-limit")) rule = Rule_max_range_limit.parse(context);
    else if (rulename.equalsIgnoreCase("min-range-value")) rule = Rule_min_range_value.parse(context);
    else if (rulename.equalsIgnoreCase("max-range-value")) rule = Rule_max_range_value.parse(context);
    else if (rulename.equalsIgnoreCase("from-condition")) rule = Rule_from_condition.parse(context);
    else if (rulename.equalsIgnoreCase("unquoted-from-condition")) rule = Rule_unquoted_from_condition.parse(context);
    else if (rulename.equalsIgnoreCase("to-condition")) rule = Rule_to_condition.parse(context);
    else if (rulename.equalsIgnoreCase("unquoted-to-condition")) rule = Rule_unquoted_to_condition.parse(context);
    else if (rulename.equalsIgnoreCase("string-value")) rule = Rule_string_value.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-string-value")) rule = Rule_quoted_string_value.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-character")) rule = Rule_quoted_escaped_character.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-backslash")) rule = Rule_quoted_escaped_backslash.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-newline")) rule = Rule_quoted_escaped_newline.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-carriage-return")) rule = Rule_quoted_escaped_carriage_return.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-tab")) rule = Rule_quoted_escaped_tab.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-backspace")) rule = Rule_quoted_escaped_backspace.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-form-feed")) rule = Rule_quoted_escaped_form_feed.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-double-quote")) rule = Rule_quoted_escaped_double_quote.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-escaped-single-quote")) rule = Rule_quoted_escaped_single_quote.parse(context);
    else if (rulename.equalsIgnoreCase("unquoted-string-value")) rule = Rule_unquoted_string_value.parse(context);
    else if (rulename.equalsIgnoreCase("integer-value")) rule = Rule_integer_value.parse(context);
    else if (rulename.equalsIgnoreCase("unsigned-integer-value")) rule = Rule_unsigned_integer_value.parse(context);
    else if (rulename.equalsIgnoreCase("float-value")) rule = Rule_float_value.parse(context);
    else if (rulename.equalsIgnoreCase("datetime-value")) rule = Rule_datetime_value.parse(context);
    else if (rulename.equalsIgnoreCase("year")) rule = Rule_year.parse(context);
    else if (rulename.equalsIgnoreCase("month")) rule = Rule_month.parse(context);
    else if (rulename.equalsIgnoreCase("day")) rule = Rule_day.parse(context);
    else if (rulename.equalsIgnoreCase("hour")) rule = Rule_hour.parse(context);
    else if (rulename.equalsIgnoreCase("minute")) rule = Rule_minute.parse(context);
    else if (rulename.equalsIgnoreCase("second")) rule = Rule_second.parse(context);
    else if (rulename.equalsIgnoreCase("yesno-value")) rule = Rule_yesno_value.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-yesno-value")) rule = Rule_quoted_yesno_value.parse(context);
    else if (rulename.equalsIgnoreCase("unquoted-yesno-value")) rule = Rule_unquoted_yesno_value.parse(context);
    else if (rulename.equalsIgnoreCase("onoff-value")) rule = Rule_onoff_value.parse(context);
    else if (rulename.equalsIgnoreCase("quoted-onoff-value")) rule = Rule_quoted_onoff_value.parse(context);
    else if (rulename.equalsIgnoreCase("unquoted-onoff-value")) rule = Rule_unquoted_onoff_value.parse(context);
    else if (rulename.equalsIgnoreCase("mode-value")) rule = Rule_mode_value.parse(context);
    else if (rulename.equalsIgnoreCase("internal-in-expression")) rule = Rule_internal_in_expression.parse(context);
    else if (rulename.equalsIgnoreCase("in-expression")) rule = Rule_in_expression.parse(context);
    else if (rulename.equalsIgnoreCase("numeric-or-mode")) rule = Rule_numeric_or_mode.parse(context);
    else if (rulename.equalsIgnoreCase("token-distance")) rule = Rule_token_distance.parse(context);
    else if (rulename.equalsIgnoreCase("internal-property-name")) rule = Rule_internal_property_name.parse(context);
    else if (rulename.equalsIgnoreCase("property-name")) rule = Rule_property_name.parse(context);
    else if (rulename.equalsIgnoreCase("multiple-fql-params")) rule = Rule_multiple_fql_params.parse(context);
    else if (rulename.equalsIgnoreCase("ALPHA")) rule = Rule_ALPHA.parse(context);
    else if (rulename.equalsIgnoreCase("BIT")) rule = Rule_BIT.parse(context);
    else if (rulename.equalsIgnoreCase("CHAR")) rule = Rule_CHAR.parse(context);
    else if (rulename.equalsIgnoreCase("CR")) rule = Rule_CR.parse(context);
    else if (rulename.equalsIgnoreCase("CRLF")) rule = Rule_CRLF.parse(context);
    else if (rulename.equalsIgnoreCase("CTL")) rule = Rule_CTL.parse(context);
    else if (rulename.equalsIgnoreCase("DIGIT")) rule = Rule_DIGIT.parse(context);
    else if (rulename.equalsIgnoreCase("DQUOTE")) rule = Rule_DQUOTE.parse(context);
    else if (rulename.equalsIgnoreCase("HEXDIG")) rule = Rule_HEXDIG.parse(context);
    else if (rulename.equalsIgnoreCase("HTAB")) rule = Rule_HTAB.parse(context);
    else if (rulename.equalsIgnoreCase("LF")) rule = Rule_LF.parse(context);
    else if (rulename.equalsIgnoreCase("LWSP")) rule = Rule_LWSP.parse(context);
    else if (rulename.equalsIgnoreCase("OCTET")) rule = Rule_OCTET.parse(context);
    else if (rulename.equalsIgnoreCase("SP")) rule = Rule_SP.parse(context);
    else if (rulename.equalsIgnoreCase("VCHAR")) rule = Rule_VCHAR.parse(context);
    else if (rulename.equalsIgnoreCase("WSP")) rule = Rule_WSP.parse(context);
    else throw new IllegalArgumentException("unknown rule");

    if (rule == null)
    {
      throw new ParserException(
        "rule \"" + (String)context.getErrorStack().peek() + "\" failed",
        context.text,
        context.getErrorIndex(),
        context.getErrorStack());
    }

    if (context.text.length() > context.index)
    {
      ParserException primaryError = 
        new ParserException(
          "extra data found",
          context.text,
          context.index,
          new Stack<String>());

      if (context.getErrorIndex() > context.index)
      {
        ParserException secondaryError = 
          new ParserException(
            "rule \"" + (String)context.getErrorStack().peek() + "\" failed",
            context.text,
            context.getErrorIndex(),
            context.getErrorStack());

        primaryError.initCause(secondaryError);
      }

      throw primaryError;
    }

    return rule;
  }

  static private Rule parse(String rulename, InputStream in, boolean trace)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (in == null)
      throw new IllegalArgumentException("null input stream");

    int ch = 0;
    StringBuffer out = new StringBuffer();
    while ((ch = in.read()) != -1)
      out.append((char)ch);

    return parse(rulename, out.toString(), trace);
  }

  static private Rule parse(String rulename, File file, boolean trace)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (file == null)
      throw new IllegalArgumentException("null file");

    BufferedReader in = new BufferedReader(new FileReader(file));
    int ch = 0;
    StringBuffer out = new StringBuffer();
    while ((ch = in.read()) != -1)
      out.append((char)ch);

    in.close();

    return parse(rulename, out.toString(), trace);
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
