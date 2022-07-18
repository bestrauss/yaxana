package br.eng.strauss.yaxana.calculator;

import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eng.strauss.yaxana.Parsable;
import br.eng.strauss.yaxana.Robusts;

/**
 * Expression parser.
 * <p>
 * Parses expressions consisting of literals, variable names, binary operations
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
public class Compiler<P extends Parsable<P>>
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

   /** The pattern used to detect literals. */
   private final Pattern literalPattern;

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

   /** The code generator. */
   private final CodeWriter codeGenerator;

   /**
    * Returns a new instance.
    *
    * @param sample
    *           An instance of the {@link Parsable} class, specifying which type of objects the
    *           compiler is dealing with.
    * @param string
    *           the input string.
    */
   public Compiler(final P sample, final String string)
   {

      this.string = string;
      this.lcString = string.toLowerCase();
      this.literalPattern = Robusts.terminalPattern();
      this.N = string.length();
      this.k0 = 0;
      this.k1 = 0;
      this.number = null;
      @SuppressWarnings("unchecked")
      final Class<? extends Parsable<?>> clasz = (Class<? extends Parsable<?>>) sample.getClass();
      this.codeGenerator = new CodeWriter(clasz);
   }

   /**
    * Compiles.
    * 
    * @param <F>
    *           F-param.
    * @param parsableClass
    *           The class of the expressions.s
    * @return new Java class.
    */
   public <F extends Function<P>> Class<F> compile(final Class<P> parsableClass)
   {

      assignments();
      @SuppressWarnings("unchecked")
      final Class<F> javaClass = (Class<F>) this.codeGenerator.getJavaClass();
      return javaClass;
   }

   private void assignments()
   {

      while (true)
      {
         final char c = look();
         if (c == '$')
         {
            return;
         }
         if (c != 'a')
         {
            throw new NumberFormatException(msgFor("variable name expected"));
         }
         assignment();
      }
   }

   private void assignment()
   {

      final char c = look();
      switch (c)
      {
         case 'a' :
            final String name = number;
            next();
            eat('=');
            expression();
            codeGenerator.assignment(name);
            return;
      }
   }

   /**
    * Reads an expression from the input string, consuming the entire input string, and returns its
    * value.
    * 
    * @return the value.
    */
   private void expression() throws NumberFormatException
   {

      try
      {
         binaryAddSub();
      }
      catch (final UnsupportedOperationException e)
      {
         final NumberFormatException nfe = new NumberFormatException(e.getMessage());
         nfe.initCause(e);
         throw nfe;
      }
   }

   /**
    * Reads and consumes a binary addition or subtraction expression from the input an returns its
    * value.
    * 
    * @return the value.
    */
   private void binaryAddSub()
   {

      binaryMulDiv();
      while (true)
      {
         switch (look())
         {
            default :
               return;
            case '+' :
               next();
               binaryMulDiv();
               codeGenerator.add();
               break;
            case '-' :
               next();
               binaryMulDiv();
               codeGenerator.sub();
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
   private void binaryMulDiv()
   {

      binaryPow();
      while (true)
      {
         switch (look())
         {
            default :
               return;
            case '*' :
               next();
               binaryPow();
               codeGenerator.mul();
               break;
            case '/' :
               next();
               binaryPow();
               codeGenerator.div();
               break;
         }
      }
   }

   /**
    * Reads and consumes a binary power expression from the input an returns its value.
    * 
    * @return the value.
    */
   private void binaryPow()
   {

      unary();
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
            index();
            if (sign)
            {
               codeGenerator.ineg();
            }
            codeGenerator.pow();
            return;
      }
   }

   /**
    * Reads and consumes a unary expression from the input an returns its value.
    * 
    * @return the value.
    */
   private void unary()
   {

      switch (look())
      {
         case '-' :
            next();
            unary();
            codeGenerator.neg();
            return;
         case '\\' :
            next();
            unary();
            codeGenerator.sqrt();
            return;
         default :
            primary();
            return;
      }
   }

   /**
    * Reads and consumes a primary expression from the input an returns its value.
    * 
    * @return the value.
    */
   private void primary()
   {

      final char c = look();
      switch (c)
      {
         case '0' :
         case 'a' :
         {
            literal();
            return;
         }
         case '(' :
         {
            next();
            binaryAddSub();
            eat(')');
            return;
         }
         case '|' :
         {
            next();
            binaryAddSub();
            eat('|');
            codeGenerator.abs();
            return;
         }
         case 'r' :
         {
            next();
            eat('(');
            binaryAddSub();
            eat(',');
            index();
            eat(')');
            codeGenerator.root();
            return;
         }
         case 's' :
         {
            next();
            eat('(');
            binaryAddSub();
            eat(')');
            codeGenerator.sqrt();
            return;
         }
         default :
            throw new NumberFormatException(msgFor("unexpected character `" + c + "'!"));
      }
   }

   /**
    * Reads and consumes a literal from the input an returns its value.
    * 
    * @return the value.
    */
   private void literal()
   {

      final char c = look();
      switch (c)
      {
         case 'a' :
            codeGenerator.lookup(number);
            next();
            return;
         case '0' :
         {
            codeGenerator.literal(number);
            next();
            return;
         }
         default :
            throw new NumberFormatException(msgFor("Unexpected character `" + c + "'!"));
      }
   }

   /**
    * Reads and consumes an integer literal from the input an returns its value.
    * 
    * @return the value.
    */
   private void index()
   {

      literal();
      codeGenerator.index();
   }

   /**
    * Ensures that the next character from the input equals a given character and consumes it.
    * 
    * @param c
    *           the expected character
    * @throws NumberFormatException
    *            if the next character from the input does not equal the expected character.
    */
   private void eat(final char c) throws NumberFormatException
   {

      if (look() != c)
      {
         throw new NumberFormatException(msgFor("Character `" + c + "' expected!"));
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
               final Matcher matcher = literalPattern.matcher(lcString.substring(k0));
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
            throw new NumberFormatException(msgFor("Unexpected character `" + c + "'!"));
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
         case '=' :
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
         f.format("error parsing expressions:\n");
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
      final String[] s = this.string.split("\\n");
      int index = k0;
      int kLine = 0;
      while (index > s[kLine].length() + 1)
      {
         final int N = s[kLine].length() + 1;
         index -= N;
         kLine++;
      }
      final String line = s[kLine];
      for (int k = 0; k < index; k++)
      {
         sb.append('-');
      }
      sb.append('^');
      final String mark = sb.toString();

      try (final Formatter f = new Formatter(Locale.US))
      {
         f.format("  %s\n", line);
         f.format("  %s", mark);
         return f.toString();
      }
   }
}
