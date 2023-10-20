package br.eng.strauss.yaxana.io;

import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eng.strauss.yaxana.Parsable;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.exc.ExpressionFormatException;

/**
 * Expression parser.
 * <p>
 * Parses expressions consisting of terminals, variable names, binary operations
 * {@code +, -, *, /, ^}, unary operations {@code -x, |x|, \x}, function calls {@code sqrt(x)},
 * {@code root(x, n)} and {@code pow(x, n)}, and parentheses to control precedence.
 * </p>
 * <p>
 * The expression syntax parsed is described on the project documentation pages.
 * </p>
 * 
 * @param <P>
 *           The type of object to be produced.
 * @author Burkhard Strauss
 * @since July 2017
 */
public class Parser<P extends Parsable<P>>
{

   /** Regular expression to match identifiers. */
   private static final String IDENTIFIER_REGEXP = "[a-z_][a-z_0-9]*";

   /** Pattern to match identifiers. */
   private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(IDENTIFIER_REGEXP,
                                                                     Pattern.CASE_INSENSITIVE);
   /** Reserved keywords (allowed function names) */
   private static final Set<String> KEYWORDS = new HashSet<>();

   static
   {
      KEYWORDS.add("pow");
      KEYWORDS.add("root");
      KEYWORDS.add("sqrt");
   }

   /** The pattern used to detect terminal. */
   private final Pattern terminalPattern;

   /** The sample value. */
   private final P sample;

   /** The input string. */
   private final String string;

   /** The input string in lower case. */
   private final String lcString;

   /** The length of input string. */
   private final int N;

   /** The position of the first character of the current token. */
   private int k0;

   /** The position of the first character after the current token. */
   private int k1;

   /** The token type, which is the token itself or a code for multi0character tokens. */
   private char token;

   /** The token in case the current token is a literal number or identifier. */
   private String number;

   /**
    * Returns a new instance.
    *
    * @param sample
    *           An instance of the {@link Parsable} class, specifying which type of objects the
    *           parser is to produce from the input string.
    * @param string
    *           the input string.
    */
   public Parser(final P sample, final String string)
   {

      this.sample = sample;
      this.string = string;
      this.lcString = string.toLowerCase();
      this.terminalPattern = BigFloat.terminalPattern();
      this.N = string.length();
      this.k0 = 0;
      this.k1 = 0;
      this.number = null;
   }

   /**
    * Reads an expression from the input string, consuming the entire input string, and returns its
    * value.
    * 
    * @return the value.
    * @throws ExpressionFormatException
    *            In case of an illegal expression in the input.
    */
   public P expression() throws ExpressionFormatException
   {

      try
      {
         final P t = binaryAddSub();
         if (look() != '$')
         {
            throw new ExpressionFormatException(msgFor("end of input expected"));
         }
         return t;
      }
      catch (final UnsupportedOperationException e)
      {
         throw new ExpressionFormatException(e.getMessage(), e);
      }
   }

   /**
    * Reads and consumes a binary addition or subtraction expression from the input an returns its
    * value.
    * 
    * @return the value.
    */
   private P binaryAddSub()
   {

      P a = binaryMulDiv();
      while (true)
      {
         switch (look())
         {
            default :
               return a;
            case '+' :
               next();
               a = a.add(binaryMulDiv());
               break;
            case '-' :
               next();
               a = a.sub(binaryMulDiv());
               break;
         }
      }
   }

   /**
    * Reads and consumes a binary multiplication or division expression from the input an returns
    * its value.
    * 
    * @return the value.
    */
   private P binaryMulDiv()
   {

      P a = binaryPow();
      while (true)
      {
         switch (look())
         {
            default :
               return a;
            case '*' :
               next();
               a = a.mul(binaryPow());
               break;
            case '/' :
               next();
               a = a.div(binaryPow());
               break;
         }
      }
   }

   /**
    * Reads and consumes a binary power expression from the input an returns its value.
    * 
    * @return the value.
    */
   private P binaryPow()
   {

      final P a = unary();
      switch (look())
      {
         case '^' :
            next();
            boolean sign = false;
            if (look() == '-')
            {
               next();
               sign = true;
            }
            final int n = index();
            return a.pow(sign ? -n : n);
      }
      return a;
   }

   /**
    * Reads and consumes a unary expression from the input an returns its value.
    * 
    * @return the value.
    */
   private P unary()
   {

      switch (look())
      {
         case '-' :
            next();
            return unary().neg();
         case '\\' :
            next();
            return unary().root(2);
         default :
            return primary();
      }
   }

   /**
    * Reads and consumes a primary expression from the input an returns its value.
    * 
    * @return the value.
    */
   private P primary()
   {

      final char c = look();
      switch (c)
      {
         case '0' :
         case 'a' :
         {
            return terminal();
         }
         case '(' :
         {
            next();
            final P a = binaryAddSub();
            eat(')');
            return a;
         }
         case '|' :
         {
            next();
            final P a = binaryAddSub();
            eat('|');
            return a.abs();
         }
         case 'r' :
         {
            next();
            eat('(');
            final P x = binaryAddSub();
            eat(',');
            final int n = index();
            eat(')');
            return x.root(n);
         }
         case 's' :
         {
            next();
            eat('(');
            final P a = binaryAddSub();
            eat(')');
            return a.root(2);
         }
         default :
            throw new ExpressionFormatException(msgFor("Unexpected character `" + c + "'!"));
      }
   }

   /**
    * Reads and consumes a literal from the input an returns its value.
    * 
    * @return the value.
    */
   private P terminal()
   {

      final char c = look();
      switch (c)
      {
         case '0' :
         {

            try
            {
               final P a = sample.newTerminal(number);
               next();
               return a;
            }
            catch (final NumberFormatException e)
            {
               throw e;
            }
         }
         case 'a' :
         default :
            throw new ExpressionFormatException(msgFor("Unexpected token!"));
      }
   }

   /**
    * Reads and consumes an integer literal from the input an returns its value.
    * 
    * @return the value.
    */
   private int index()
   {

      try
      {
         return terminal().index();
      }
      catch (final Exception e)
      {
         throw new ExpressionFormatException(msgFor("non-integer pow or root exponent"));
      }
   }

   /**
    * Ensures that the next character from the input equals a given character and consumes it.
    * 
    * @param c
    *           the expected character
    * @throws NumberFormatException
    *            if the next character from the input does not equal the expected character.
    */
   private void eat(final char c) throws ExpressionFormatException
   {

      if (look() != c)
      {
         throw new ExpressionFormatException(msgFor("Character `" + c + "' expected!"));
      }
      next();
   }

   /**
    * Returns the next character from the input but does not consume it.
    * 
    * @return the next character from the input.
    */
   private char look()
   {

      return k0 == k1 ? next() : token;
   }

   /**
    * Consumes and returns the next character from the input.
    * 
    * @return the next character from the input.
    */
   private char next()
   {

      k0 = k1;
      if (k0 >= N)
      {
         return token = '$';
      }
      this.number = null;
      k1 = k0 + 1;
      char c;
      while (true)
      {
         c = lcString.charAt(k0);
         if (!Character.isWhitespace(c))
         {
            break;
         }
         k0++;
         k1++;
         if (k0 >= N)
         {
            return token = '$';
         }
      }
      switch (c)
      {
         default :
            if ('0' <= c && c <= '9')
            {
               final Matcher matcher = terminalPattern.matcher(lcString.substring(k0));
               if (matcher.lookingAt())
               {
                  k1 = k0 + matcher.end();
                  number = matcher.group();
                  return token = '0';
               }
            }
            else if ('a' <= c && c <= 'z' || c == '_')
            {
               final Matcher matcher = IDENTIFIER_PATTERN.matcher(lcString.substring(k0));
               if (matcher.lookingAt())
               {
                  k1 = k0 + matcher.end();
                  final String s = matcher.group();
                  if ("pow".equals(s))
                  {
                     return token = 'p';
                  }
                  if ("root".equals(s))
                  {
                     return token = 'r';
                  }
                  if ("sqrt".equals(s))
                  {
                     return token = 's';
                  }
                  number = this.string.substring(k0 + matcher.start(), k0 + matcher.end());
                  return token = 'a';
               }
            }
            throw new ExpressionFormatException(msgFor("Unexpected character `" + c + "'!"));
         case '+' :
         case '-' :
         case '*' :
         case '/' :
         case '^' :
         case '\\' :
         case '(' :
         case ')' :
         case ',' :
         case '|' :
            return token = c;
      }
   }

   /**
    * Returns the full multiline error message, containing the expression and a line with a marker
    * showing up to where characters have been read.
    * 
    * @param msg
    *           the short message text.
    * @return the full multiline error message.
    */
   private String msgFor(final String msg)
   {

      try (final Formatter f = new Formatter(Locale.US))
      {
         f.format("error parsing expression:\n");
         f.format("\n");
         f.format("%s\n", toString());
         f.format("%s", msg);
         return f.toString();
      }
   }

   /**
    * Returns the string to be parsed plus a second line with a marker up to where characters have
    * been read.
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {

      final StringBuffer sb = new StringBuffer();
      for (int k = 0; k < k0; k++)
      {
         sb.append('-');
      }
      sb.append('^');
      final String mark = sb.toString();

      try (final Formatter f = new Formatter(Locale.US))
      {
         f.format("  %s\n", string);
         f.format("  %s", mark);
         return f.toString();
      }
   }
}
