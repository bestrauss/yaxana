package br.eng.strauss.yaxana.big;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * Tests for {@link Rounder}.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public class RounderTest extends YaxanaTest
{

   @Test
   public void test_ALL_DIGITS_HALF_EVEN()
   {

      final RoundingMode rm = RoundingMode.HALF_EVEN;

      test("123", new Rounder(9, rm), "123"); // 01111011
      test("123", new Rounder(8, rm), "123");
      test("123", new Rounder(7, rm), "123");
      test("123", new Rounder(6, rm), "124");
      test("123", new Rounder(5, rm), "124");
      test("123", new Rounder(4, rm), "120");
      test("123", new Rounder(3, rm), "128");
      test("123", new Rounder(2, rm), "128");
      test("123", new Rounder(1, rm), "128");
      test("123", new Rounder(0, rm), "123");

      test("-123", new Rounder(9, rm), "-123");
      test("-123", new Rounder(8, rm), "-123");
      test("-123", new Rounder(7, rm), "-123");
      test("-123", new Rounder(6, rm), "-124");
      test("-123", new Rounder(5, rm), "-124");
      test("-123", new Rounder(4, rm), "-120");
      test("-123", new Rounder(3, rm), "-128");
      test("-123", new Rounder(2, rm), "-128");
      test("-123", new Rounder(1, rm), "-128");
      test("-123", new Rounder(0, rm), "-123");

      test("122", new Rounder(5, rm), "120");
      test("127", new Rounder(5, rm), "128");
      test("126", new Rounder(5, rm), "128");
   }

   @Test
   public void test_ALL_DIGITS_HALF_UP()
   {

      final RoundingMode rm = RoundingMode.HALF_UP;

      test("123", new Rounder(9, rm), "123"); // 01111011
      test("123", new Rounder(8, rm), "123");
      test("123", new Rounder(7, rm), "123");
      test("123", new Rounder(6, rm), "124");
      test("123", new Rounder(5, rm), "124");
      test("123", new Rounder(4, rm), "120");
      test("123", new Rounder(3, rm), "128");
      test("123", new Rounder(2, rm), "128");
      test("123", new Rounder(1, rm), "128");
      test("123", new Rounder(0, rm), "123");

      test("-123", new Rounder(9, rm), "-123");
      test("-123", new Rounder(8, rm), "-123");
      test("-123", new Rounder(7, rm), "-123");
      test("-123", new Rounder(6, rm), "-124");
      test("-123", new Rounder(5, rm), "-124");
      test("-123", new Rounder(4, rm), "-120");
      test("-123", new Rounder(3, rm), "-128");
      test("-123", new Rounder(2, rm), "-128");
      test("-123", new Rounder(1, rm), "-128");
      test("-123", new Rounder(0, rm), "-123");

      test("122", new Rounder(5, rm), "124");
      test("127", new Rounder(5, rm), "128");
      test("126", new Rounder(5, rm), "128");
   }

   @Test
   public void test_ALL_DIGITS_HALF_DOWN()
   {

      final RoundingMode rm = RoundingMode.HALF_DOWN;

      test("123", new Rounder(9, rm), "123"); // 01111011
      test("123", new Rounder(8, rm), "123");
      test("123", new Rounder(7, rm), "123");
      test("123", new Rounder(6, rm), "122");
      test("123", new Rounder(5, rm), "124");
      test("123", new Rounder(4, rm), "120");
      test("123", new Rounder(3, rm), "128");
      test("123", new Rounder(2, rm), "128");
      test("123", new Rounder(1, rm), "128");
      test("123", new Rounder(0, rm), "123");

      test("-123", new Rounder(9, rm), "-123");
      test("-123", new Rounder(8, rm), "-123");
      test("-123", new Rounder(7, rm), "-123");
      test("-123", new Rounder(6, rm), "-122");
      test("-123", new Rounder(5, rm), "-124");
      test("-123", new Rounder(4, rm), "-120");
      test("-123", new Rounder(3, rm), "-128");
      test("-123", new Rounder(2, rm), "-128");
      test("-123", new Rounder(1, rm), "-128");
      test("-123", new Rounder(0, rm), "-123");
   }

   @Test
   public void test_ALL_DIGITS_UP()
   {

      final RoundingMode rm = RoundingMode.UP;

      test("123", new Rounder(9, rm), "123"); // 01111011
      test("123", new Rounder(8, rm), "123");
      test("123", new Rounder(7, rm), "123");
      test("123", new Rounder(6, rm), "124");
      test("123", new Rounder(5, rm), "124");
      test("123", new Rounder(4, rm), "128");
      test("123", new Rounder(3, rm), "128");
      test("123", new Rounder(2, rm), "128");
      test("123", new Rounder(1, rm), "128");
      test("123", new Rounder(0, rm), "123");

      test("-123", new Rounder(9, rm), "-123");
      test("-123", new Rounder(8, rm), "-123");
      test("-123", new Rounder(7, rm), "-123");
      test("-123", new Rounder(6, rm), "-124");
      test("-123", new Rounder(5, rm), "-124");
      test("-123", new Rounder(4, rm), "-128");
      test("-123", new Rounder(3, rm), "-128");
      test("-123", new Rounder(2, rm), "-128");
      test("-123", new Rounder(1, rm), "-128");
      test("-123", new Rounder(0, rm), "-123");
   }

   @Test
   public void test_ALL_DIGITS_DOWN()
   {

      final RoundingMode rm = RoundingMode.DOWN;

      test("123", new Rounder(9, rm), "123"); // 01111011
      test("123", new Rounder(8, rm), "123");
      test("123", new Rounder(7, rm), "123");
      test("123", new Rounder(6, rm), "122");
      test("123", new Rounder(5, rm), "120");
      test("123", new Rounder(4, rm), "120");
      test("123", new Rounder(3, rm), "112");
      test("123", new Rounder(2, rm), "96");
      test("123", new Rounder(1, rm), "64");
      test("123", new Rounder(0, rm), "123");

      test("-123", new Rounder(9, rm), "-123");
      test("-123", new Rounder(8, rm), "-123");
      test("-123", new Rounder(7, rm), "-123");
      test("-123", new Rounder(6, rm), "-122");
      test("-123", new Rounder(5, rm), "-120");
      test("-123", new Rounder(4, rm), "-120");
      test("-123", new Rounder(3, rm), "-112");
      test("-123", new Rounder(2, rm), "-96");
      test("-123", new Rounder(1, rm), "-64");
      test("-123", new Rounder(0, rm), "-123");
   }

   @Test
   public void test_ALL_DIGITS_CEILING()
   {

      final RoundingMode rm = RoundingMode.CEILING;

      test("123", new Rounder(9, rm), "123"); // 01111011
      test("123", new Rounder(8, rm), "123");
      test("123", new Rounder(7, rm), "123");
      test("123", new Rounder(6, rm), "124");
      test("123", new Rounder(5, rm), "124");
      test("123", new Rounder(4, rm), "128");
      test("123", new Rounder(3, rm), "128");
      test("123", new Rounder(2, rm), "128");
      test("123", new Rounder(1, rm), "128");
      test("123", new Rounder(0, rm), "123");

      test("-123", new Rounder(9, rm), "-123");
      test("-123", new Rounder(8, rm), "-123");
      test("-123", new Rounder(7, rm), "-123");
      test("-123", new Rounder(6, rm), "-122");
      test("-123", new Rounder(5, rm), "-120");
      test("-123", new Rounder(4, rm), "-120");
      test("-123", new Rounder(3, rm), "-112");
      test("-123", new Rounder(2, rm), "-96");
      test("-123", new Rounder(1, rm), "-64");
      test("-123", new Rounder(0, rm), "-123");
   }

   @Test
   public void test_ALL_DIGITS_FLOOR()
   {

      final RoundingMode rm = RoundingMode.FLOOR;

      test("123", new Rounder(9, rm), "123"); // 01111011
      test("123", new Rounder(8, rm), "123");
      test("123", new Rounder(7, rm), "123");
      test("123", new Rounder(6, rm), "122");
      test("123", new Rounder(5, rm), "120");
      test("123", new Rounder(4, rm), "120");
      test("123", new Rounder(3, rm), "112");
      test("123", new Rounder(2, rm), "96");
      test("123", new Rounder(1, rm), "64");
      test("123", new Rounder(0, rm), "123");

      test("-123", new Rounder(9, rm), "-123");
      test("-123", new Rounder(8, rm), "-123");
      test("-123", new Rounder(7, rm), "-123");
      test("-123", new Rounder(6, rm), "-124");
      test("-123", new Rounder(5, rm), "-124");
      test("-123", new Rounder(4, rm), "-128");
      test("-123", new Rounder(3, rm), "-128");
      test("-123", new Rounder(2, rm), "-128");
      test("-123", new Rounder(1, rm), "-128");
      test("-123", new Rounder(0, rm), "-123");
   }

   private static void test(final String number, final Rounder rounder, final String desired)
   {

      try
      {
         assertTrue(new BigFloat(number).round(rounder).equals(new BigFloat(desired)));
      }
      catch (final AssertionError e)
      {
         format("given:   %s\n", number);
         format("desired: %s\n", desired);
         format("actual:  %s\n", new BigFloat(number).round(rounder));
         throw e;
      }
   }
}
