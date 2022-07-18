package br.eng.strauss.yaxana.io;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * Tests for {@link Parser}.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class ParserTest extends YaxanaTest
{

   @Test
   public void test()
   {

      test("100", "100");

      test("|2^2+3^2|", "|2^2+3^2|");
      test("(sqrt(2)+3)^-2", "1/(\\2+3)^2");
      test("-1.5", "-0x1.8P+0");

      test("15");
      test("15+37");
      test("15-37");
      test("15*37");
      test("15/37");
      test("|15|");
      test("sqrt(15)", "\\15");
      // test("root(15, 21)");
      test("\\15+\\37");
      test("\\(15+37+\\(15*37)*2)");
      test("\\15+\\37-\\(15+37+\\(15*37)*2)");
      test("-15+-15+-15");
      test("root(1*(2+3), 2)", "\\(1*(2+3))");
      // test("root(1*(2+3), 99)");
      failParsing("-1.5+-1.5++1.5");
      failParsing("Mist");
      failParsing("sqrY");
      failParsing("rAot");
      failParsing("300x");
      test("300 ", "300");
      test(" 300", "300");
      test(" 300 ", "300");
      failParsing("300 .");
      test("1*(2+3)");
      test("1*(2*2*2*2+3)");
      test("(1*2*2)*(2*2+3)", "1*2*2*(2*2+3)");

      failParsing("|sqrt(7)*sqrt(7)*sqrt(7)*sqrt(7)*sqrt(5)");
   }

   private void test(final String expression, final String e2)
   {

      try
      {
         assertTrue(new Parser<Algebraic>(Algebraic.ZERO, expression).expression().toString()
               .equals(e2));
      }
      catch (final AssertionError e)
      {
         format("soll: %s\n", e2);
         format("ist:  %s\n", new Parser<Algebraic>(Algebraic.ZERO, expression).expression());
         throw e;
      }
      catch (final NumberFormatException e)
      {
         format("%s\n", e.getMessage());
         throw e;
      }
   }

   private void test(final String expression)
   {

      try
      {
         assertTrue(new Parser<Algebraic>(Algebraic.ZERO, expression).expression().toString()
               .equals(expression));
      }
      catch (final AssertionError e)
      {
         format("soll: %s\n", expression);
         format("ist:  %s\n", new Parser<Algebraic>(Algebraic.ZERO, expression).expression());
         throw e;
      }
      catch (final NumberFormatException e)
      {
         format("%s\n", e.getMessage());
         throw e;
      }
   }

   private void failParsing(final String expression)
   {

      try
      {
         new Parser<Algebraic>(Algebraic.ZERO, expression).expression();
         fail();
      }
      catch (final NumberFormatException e)
      {
         format("%s\n", e.getMessage());
      }
   }
}
