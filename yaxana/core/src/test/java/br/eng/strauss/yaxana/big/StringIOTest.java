package br.eng.strauss.yaxana.big;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.tools.YaxanaTest;

/**
 * Tests for {@link StringIO}.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public class StringIOTest extends YaxanaTest
{

   @Test
   public void test_stringIO()
   {

      test("0xFP-1", "0x1.EP+2");

      test("0");
      test("-0", "0");
      test("0");
      test("1");
      test("0x1", "1");
      test("0x2", "2");
      test("-0x3", "-3");

      test("999999");
      test("1000000");
      test("100000000000000000000000000000000000002");
      test("100000000000000000000000000000000000010");
      test("0x7a121P1", "1000002");
      test("123p-123", "0x1.ECP-117");
      test("0x1.ECP-117");
      test("-0x123P-123", "-0x1.23P-115");
      test("800p-1", "400");
      test("-0xF", "-15");

      test("0.5p-70", "1P-71");
   }

   @Test
   public void test_decimalStringIO()
   {

      final MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
      test("0", mc, "0.00");
      test("1", mc, "1.00");
      test("123", mc, "1.23E+02");
      test("12345", mc, "1.23E+04");
      test("12350", mc, "1.24E+04");
      test("12350", new MathContext(3, RoundingMode.DOWN), "1.23E+04");
      test("2.5", mc, "2.50");
      test("16400.00P-6", mc, "2.56E+02");
      test("16400.00P-6", new MathContext(4), "2.563E+02");
      test("16400.00P-6", new MathContext(5), "2.5625E+02");
      test("16400.00P-6", new MathContext(6), "2.56250E+02");
   }

   @Test
   public void test_hexStringValueOf()
   {

      assertEquals("0x0P+0", StringIO.hexStringValueOf(BigFloat.ZERO));
      assertEquals("-0x1P+0", StringIO.hexStringValueOf(BigFloat.MINUSONE));
      assertEquals("0x1P-1", StringIO.hexStringValueOf(BigFloat.HALF));
   }

   @Test
   public void test_bigFloatValueOf()
   {

      try
      {
         StringIO.bigFloatValueOf("ABC", null);
         fail();
      }
      catch (final NumberFormatException e)
      {
      }
      assertEquals(BigFloat.ZERO, StringIO.bigFloatValueOf("0", null));
      assertEquals(BigFloat.ZERO, StringIO.bigFloatValueOf("0p0", null));
      assertEquals(BigFloat.ZERO, StringIO.bigFloatValueOf("0P0", null));
      assertEquals(BigFloat.TEN, StringIO.bigFloatValueOf("1E1", null));
      assertEquals(new BigFloat(500), StringIO.bigFloatValueOf("0.5E3", null));
      assertEquals(new BigFloat(1.0), StringIO.bigFloatValueOf("1000.0E-3", null));
      assertEquals(new BigFloat(1000.0), StringIO.bigFloatValueOf("1000.0E-0", null));
   }

   private static void test(final String number)
   {

      test(number, number);
   }

   private static void test(final String number, final String desired)
   {

      try
      {
         assertTrue(new BigFloat(number).toString().compareTo(desired) == 0);
      }
      catch (final AssertionError e)
      {
         format("number:  %s\n", number);
         format("desired: %s\n", desired);
         format("actual:  %s\n", new BigFloat(number).toString());
         throw e;
      }
      try
      {
         assertTrue(new BigFloat(number).compareTo(new BigFloat(desired)) == 0);
      }
      catch (final AssertionError e)
      {
         format("number:  %s\n", number);
         format("desired: %s\n", desired);
         format("actual: %s\n", new BigFloat(desired).toString());
         throw e;
      }
   }

   private static void test(final String number, final MathContext mc, final String desired)
   {

      try
      {
         assertTrue(new BigFloat(number).toString(mc).compareTo(desired) == 0);
      }
      catch (final AssertionError e)
      {
         format("number:  %s\n", number);
         format("desired: %s\n", desired);
         format("actual: %s\n", new BigFloat(number).toString(mc));
         throw e;
      }
   }
}
