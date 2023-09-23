package br.eng.strauss.yaxana.big;

import static br.eng.strauss.yaxana.big.BigFloat.MINUSONE;
import static br.eng.strauss.yaxana.big.BigFloat.ONE;
import static br.eng.strauss.yaxana.big.BigFloat.TWO;
import static br.eng.strauss.yaxana.big.BigFloat.ZERO;
import static br.eng.strauss.yaxana.big.BigFloat.twoTo;
import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.NegativeRadicandException;
import br.eng.strauss.yaxana.exc.NonTerminatingBinaryExpansionException;
import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since August 2017
 */
public class BigFloatTest extends YaxanaTest
{

   private final Random random = new Random();

   private final int loopCount = 1000;

   @Test
   public void test_BigFloat_long()
   {

      assertTrue(new BigFloat(0).signum() == 0);
      assertTrue(new BigFloat(1).equals(new BigFloat(1d)));
      assertTrue(new BigFloat(-1).equals(new BigFloat(-1d)));
      for (int k = 0; k < loopCount; k++)
      {
         long l = random.nextLong();
         final double d = l;
         l = (long) d;
         if (d == l)
         {
            assertTrue(new BigFloat(l).equals(new BigFloat(d)));
         }
      }
   }

   @Test
   public void test_BigFloat_BigInteger()
   {

      assertTrue(new BigFloat(BigInteger.ZERO).signum() == 0);
      assertTrue(new BigFloat(BigInteger.ONE).equals(new BigFloat(1d)));
      assertTrue(new BigFloat(BigInteger.ONE.negate()).equals(new BigFloat(-1d)));
      for (int k = 0; k < loopCount; k++)
      {
         final long l = random.nextLong();
         final BigInteger b = BigInteger.valueOf(l);
         assertTrue(new BigFloat(b).equals(new BigFloat(l)));
      }
   }

   @Test
   public void test_BigFloat_signum()
   {

      assertTrue(new BigFloat(BigInteger.ZERO).signum() == 0);
      assertTrue(new BigFloat(BigInteger.ONE).signum() == 1);
      assertTrue(new BigFloat(BigInteger.ONE.negate()).signum() == -1);
      for (int k = 0; k < loopCount; k++)
      {
         final double d = 2E10 * random.nextDouble() - 1E10;
         assertTrue(new BigFloat(d).signum() == Math.signum(d));
      }
   }

   @Test
   public void test_unscaledValue()
   {

      assertEquals(BigInteger.valueOf(0), new BigFloat(BigInteger.ZERO).unscaledValue());
      assertEquals(BigInteger.valueOf(1), new BigFloat(BigInteger.ONE).unscaledValue());
      assertEquals(BigInteger.valueOf(5), new BigFloat(BigInteger.TEN).unscaledValue());
      assertEquals(BigInteger.valueOf(-15), new BigFloat(-15).unscaledValue());
   }

   @Test
   public void test_scale()
   {

      assertEquals(0, new BigFloat(BigInteger.ZERO).scale());
      assertEquals(0, new BigFloat(BigInteger.ONE).scale());
      assertEquals(1, new BigFloat(BigInteger.TEN).scale());
      assertEquals(0, new BigFloat(-15).scale());
      assertEquals(-200, new BigFloat(0x1P-200).scale());
   }

   @Test
   public void test_max()
   {

      assertEquals(new BigFloat(23.1), new BigFloat(23.0).max(new BigFloat(23.1)));
      assertEquals(new BigFloat(23.2), new BigFloat(23.2).max(new BigFloat(23.1)));
   }

   @Test
   public void test_min()
   {

      assertEquals(new BigFloat(23.0), new BigFloat(23.0).min(new BigFloat(23.1)));
      assertEquals(new BigFloat(23.1), new BigFloat(23.2).min(new BigFloat(23.1)));
   }

   @Test
   public void test_msb()
   {

      assertTrue(new BigFloat(1).msb() == 0);
      assertTrue(new BigFloat(0.5).msb() == -1);
      assertTrue(new BigFloat(0.25).msb() == -2);
      assertTrue(new BigFloat(2).msb() == 1);
      assertTrue(new BigFloat(4).msb() == 2);

      assertTrue(new BigFloat("9p-3").msb() == 0);
      assertTrue(new BigFloat("0x81p-7").msb() == 0);
      assertTrue(new BigFloat("0x81p-8").msb() == -1);

      assertTrue(new BigFloat(1.75).msb() == 0);
      assertTrue(new BigFloat(2.75).msb() == 1);

      assertTrue(new BigFloat("1p-70").msb() == -70);
      assertTrue(new BigFloat("1p+70").msb() == 70);
      assertTrue(new BigFloat("0.5p-70").msb() == -71);
      assertTrue(new BigFloat("0.5p+70").msb() == 69);
      assertTrue(new BigFloat("0.25p-70").msb() == -72);
      assertTrue(new BigFloat("0.25p+70").msb() == 68);

      try
      {
         new BigFloat("0").msb();
         fail();
      }
      catch (final ArithmeticException e)
      {
      }
      try
      {
         new BigFloat("-1").msb();
         fail();
      }
      catch (final ArithmeticException e)
      {
      }
      try
      {
         new BigFloat("-1p-10").msb();
         fail();
      }
      catch (final ArithmeticException e)
      {
      }
   }

   @Test
   public void test_add()
   {

      {
         test(BigFloat.ZERO.add(BigFloat.ZERO), 0d);
      }
      {
         final double d0 = -8.746833419016434E9;
         final double d1 = -9.883514548968908E9;
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
      }
      {
         final double d0 = -8.746833419016434E3;
         final double d1 = -9.883514548968908E3;
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
      }
      test(new BigFloat(123).add(new BigFloat(0)), 123);
      test(new BigFloat(0).add(new BigFloat(123)), 123);
      test(new BigFloat(1).add(new BigFloat(1)), 2);
      test(new BigFloat(1).add(new BigFloat(2)), 3);
      test(new BigFloat(1).add(new BigFloat(-1)), 0);
      {
         final Rounder rounder = new Rounder(8);
         final BigFloat value = new BigFloat(0xF).add(new BigFloat(0xFP-4), rounder);
         assertEquals(new BigFloat(0x1.FEP+3), value);
      }
      {
         final Rounder rounder = new Rounder(8);
         final BigFloat value = new BigFloat(0xFP-4).add(new BigFloat(0xF), rounder);
         assertEquals(new BigFloat(0x1.FEP+3), value);
      }
   }

   @Test
   public void test_sub()
   {

      test(new BigFloat(123).sub(new BigFloat(0)), 123);
      test(new BigFloat(0).sub(new BigFloat(123)), -123);
      test(new BigFloat(1).sub(new BigFloat(1)), 0);
      test(new BigFloat(1).sub(new BigFloat(2)), -1);
   }

   @Test
   public void test_mul()
   {

      test(new BigFloat(123).mul(new BigFloat(0)), 0);
      test(new BigFloat(0).mul(new BigFloat(123)), 0);
      test(new BigFloat(123).mul(new BigFloat(1)), 123);
      test(new BigFloat(1).mul(new BigFloat(123)), 123);
      test(new BigFloat(1).mul(new BigFloat(1)), 1);
      test(new BigFloat(1).mul(new BigFloat(-1)), -1);
      test(new BigFloat(-1).mul(new BigFloat(1)), -1);
      test(new BigFloat(-1).mul(new BigFloat(-1)), 1);
      test(new BigFloat(1).mul(new BigFloat(2)), 2);
   }

   @Test
   public void test_div()
   {

      try
      {
         new BigFloat(1).div(new BigFloat(10));
         fail();
      }
      catch (final NonTerminatingBinaryExpansionException e)
      {
      }

      assertTrue(ONE.div(ONE).compareTo(ONE) == 0);
      assertTrue(new BigFloat(124d).div(new BigFloat(32d)).doubleValue() == 3.875);
      assertTrue(new BigFloat(-124d).div(new BigFloat(32d)).doubleValue() == -3.875);

      final BigFloat f1 = new BigFloat(0.5d);
      final BigFloat f2 = new BigFloat(1d).mul(BigFloat.twoTo(-5));
      final BigFloat f5 = f1.div(f2);
      final double d5 = f5.unscaledValue.longValue() * Math.pow(2d, f5.scale);
      assertTrue(d5 == 16.0);
   }

   @Test
   public void test_arith()
   {

      final Rounder rounder = new Rounder(53);
      for (int k = 0; k < loopCount; k++)
      {
         final double d0 = random.nextDouble();
         final double d1 = random.nextDouble();
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
         test(new BigFloat(d0).sub(new BigFloat(d1)), d0 - d1);
         test(new BigFloat(d0).mul(new BigFloat(d1)), d0 * d1);
         test(new BigFloat(d0).div(new BigFloat(d1), rounder), d0 / d1);
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d0 = 2E10 * random.nextDouble() - 1E10;
         final double d1 = 2E10 * random.nextDouble() - 1E10;
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
         test(new BigFloat(d0).sub(new BigFloat(d1)), d0 - d1);
         test(new BigFloat(d0).mul(new BigFloat(d1)), d0 * d1);
         test(new BigFloat(d0).div(new BigFloat(d1), rounder), d0 / d1);
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d0 = 2E-10 * random.nextDouble() - 1E-10;
         final double d1 = 2E-10 * random.nextDouble() - 1E-10;
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
         test(new BigFloat(d0).sub(new BigFloat(d1)), d0 - d1);
         test(new BigFloat(d0).mul(new BigFloat(d1)), d0 * d1);
         test(new BigFloat(d0).div(new BigFloat(d1), rounder), d0 / d1);
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d0 = 2E149 * random.nextDouble() - 1E149;
         final double d1 = 2E149 * random.nextDouble() - 1E149;
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
         test(new BigFloat(d0).sub(new BigFloat(d1)), d0 - d1);
         test(new BigFloat(d0).mul(new BigFloat(d1)), d0 * d1);
         test(new BigFloat(d0).div(new BigFloat(d1), rounder), d0 / d1);
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d0 = 2E-149 * random.nextDouble() - 1E-149;
         final double d1 = 2E-149 * random.nextDouble() - 1E-149;
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
         test(new BigFloat(d0).sub(new BigFloat(d1)), d0 - d1);
         test(new BigFloat(d0).mul(new BigFloat(d1)), d0 * d1);
         test(new BigFloat(d0).div(new BigFloat(d1), rounder), d0 / d1);
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d0 = 2E149 * random.nextDouble() - 1E149;
         final double d1 = 2E-149 * random.nextDouble() - 1E-149;
         test(new BigFloat(d0).add(new BigFloat(d1)), d0 + d1);
         test(new BigFloat(d0).sub(new BigFloat(d1)), d0 - d1);
         test(new BigFloat(d0).mul(new BigFloat(d1)), d0 * d1);
         test(new BigFloat(d0).div(new BigFloat(d1), rounder), d0 / d1);
      }
   }

   @Test
   public void test_neg_abs_sqrt()
   {

      final Rounder rounder = new Rounder(53);
      assertTrue(ZERO.neg().equals(ZERO));
      assertTrue(ONE.neg().equals(MINUSONE));
      assertTrue(MINUSONE.neg().equals(ONE));
      assertTrue(ZERO.abs().equals(ZERO));
      assertTrue(ONE.abs().equals(ONE));
      assertTrue(MINUSONE.abs().equals(ONE));
      assertTrue(ZERO.sqrt(rounder).equals(ZERO));
      assertTrue(ONE.sqrt(rounder).equals(ONE));
      for (int k = 0; k < loopCount; k++)
      {
         final double d = random.nextDouble();
         test(new BigFloat(d).neg(), -d);
         test(new BigFloat(d).abs(), Math.abs(d));
         test(new BigFloat(d).abs().sqrt(rounder), Math.sqrt(Math.abs(d)));
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d = 2E10 * random.nextDouble() - 1E10;
         test(new BigFloat(d).neg(), -d);
         test(new BigFloat(d).abs(), Math.abs(d));
         test(new BigFloat(d).abs().sqrt(rounder), Math.sqrt(Math.abs(d)));
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d = 2E-10 * random.nextDouble() - 1E-10;
         test(new BigFloat(d).neg(), -d);
         test(new BigFloat(d).abs(), Math.abs(d));
         test(new BigFloat(d).abs().sqrt(rounder), Math.sqrt(Math.abs(d)));
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d = 2E200 * random.nextDouble() - 1E200;
         test(new BigFloat(d).neg(), -d);
         test(new BigFloat(d).abs(), Math.abs(d));
         test(new BigFloat(d).abs().sqrt(rounder), Math.sqrt(Math.abs(d)));
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d = 2E-200 * random.nextDouble() - 1E-200;
         test(new BigFloat(d).neg(), -d);
         test(new BigFloat(d).abs(), Math.abs(d));
         test(new BigFloat(d).abs().sqrt(rounder), Math.sqrt(Math.abs(d)));
      }
   }

   @Test
   public void test_pow()
   {

      assertTrue(ZERO.pow(0).equals(ONE));
      assertTrue(ONE.pow(0).equals(ONE));
      assertTrue(TWO.pow(0).equals(ONE));
      assertTrue(MINUSONE.pow(0).equals(ONE));
      assertTrue(new BigFloat(1.25d).pow(0).equals(ONE));
      assertTrue(new BigFloat(-1.25d).pow(0).equals(ONE));
      assertTrue(new BigFloat("123p-8").pow(0).equals(ONE));
      assertTrue(new BigFloat("123p+8").pow(0).equals(ONE));

      assertTrue(ZERO.pow(1).equals(ZERO));
      assertTrue(ONE.pow(1).equals(ONE));
      assertTrue(TWO.pow(1).equals(TWO));
      assertTrue(MINUSONE.pow(1).equals(MINUSONE));
      assertTrue(new BigFloat(1.25d).pow(1).equals(new BigFloat(1.25d)));
      assertTrue(new BigFloat(-1.25d).pow(1).equals(new BigFloat(-1.25d)));
      assertTrue(new BigFloat("123p-8").pow(1).equals(new BigFloat("123p-8")));
      assertTrue(new BigFloat("123p+8").pow(1).equals(new BigFloat("123p+8")));

      assertTrue(ZERO.pow(2).equals(ZERO));
      assertTrue(ONE.pow(2).equals(ONE));
      assertTrue(TWO.pow(2).equals(new BigFloat(4)));
      assertTrue(MINUSONE.pow(2).equals(ONE));
      assertTrue(new BigFloat(1.25d).pow(2).equals(new BigFloat(1.5625d)));
      assertTrue(new BigFloat(-1.25d).pow(2).equals(new BigFloat(1.5625d)));
      assertTrue(new BigFloat("123p-8").pow(2).equals(new BigFloat("0.2308502197265625")));
      assertTrue(new BigFloat("123p+8").pow(2).equals(new BigFloat("991494144")));

      for (int k = 0; k < loopCount; k++)
      {
         final double d = random.nextDouble();
         final int n = random.nextInt(5);
         test(new BigFloat(d).pow(n), Math.pow(d, n));
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d = 2E10 * random.nextDouble() - 1E10;
         final int n = random.nextInt(5);
         test(new BigFloat(d).pow(n), Math.pow(d, n));
      }
      for (int k = 0; k < loopCount; k++)
      {
         final double d = 2E-10 * random.nextDouble() - 1E-10;
         final int n = random.nextInt(5);
         test(new BigFloat(d).pow(n), Math.pow(d, n));
      }
   }

   @Test
   public void test_Root()
   {

      final Rounder r = new Rounder(1000);
      final BigFloat eps = BigFloat.twoTo(-1000);
      try
      {
         new BigFloat(3.14).root(0, r);
         fail();
      }
      catch (final DivisionByZeroException e)
      {
      }
      try
      {
         new BigFloat(-4).root(2, r);
         fail();
      }
      catch (final NegativeRadicandException e)
      {
      }
      assertEquals(new BigFloat(0.5), new BigFloat(2).root(-1, r));
      assertEquals(new BigFloat(2), new BigFloat(2).root(1, r));
      assertEquals(new BigFloat(2), new BigFloat(4).root(2, r));
      assertEquals(new BigFloat(0), new BigFloat(0).root(3, r));
      assertEquals(new BigFloat(4).root(3, r).neg(), new BigFloat(-4).root(3, r));
      assertEquals(new BigFloat(1), new BigFloat(1).root(17, r));

      for (final BigFloat value : new BigFloat[] { new BigFloat(2d), new BigFloat("0x0.101") })
      {
         for (final int exponent : new int[] { 2, 16, 3, 17 })
         {
            {
               final BigFloat result = value.root(exponent, r).pow(exponent);
               assertTrue(result.sub(value).compareTo(eps) < 0);
            }
            {
               final BigFloat result = value.pow(exponent, r).root(exponent, r);
               if (result.sub(value, r).compareTo(eps) >= 0)
               {
                  System.out.format(US, "%s\n", value.pow(exponent, r).toString());
                  String.format("");
               }
               assertTrue(result.sub(value, r).compareTo(eps) < 0);
            }
         }
      }
   }

   @Test
   public void test_other()
   {

      assertTrue(twoTo(0).equals(ONE));
      assertTrue(twoTo(1).equals(new BigFloat(2)));
      assertTrue(twoTo(-1).equals(new BigFloat(0.5)));
      assertTrue(twoTo(10).equals(new BigFloat(1024)));
      assertTrue(twoTo(-10).equals(new BigFloat(1d / 1024d)));

      assertTrue(ZERO.bigIntegerValue().equals(BigInteger.ZERO));
      assertTrue(ONE.bigIntegerValue().equals(BigInteger.ONE));
      assertTrue(MINUSONE.bigIntegerValue().equals(BigInteger.ONE.negate()));
      assertTrue(new BigFloat("12p8").bigIntegerValue().equals(BigInteger.valueOf(3072)));

      assertTrue(ZERO.doubleValue() == 0d);
      assertTrue(ONE.doubleValue() == 1d);
      assertTrue(MINUSONE.doubleValue() == -1d);
      assertTrue(new BigFloat("12p8").doubleValue() == 3072d);
      assertTrue(new BigFloat("12p-8").doubleValue() == 0.046875d);

      assertTrue(ZERO.isInteger());
      assertTrue(ZERO.isZero());
      assertTrue(!ZERO.isOne());
      assertTrue(!ZERO.isMinusOne());

      assertTrue(ONE.isInteger());
      assertTrue(!ONE.isZero());
      assertTrue(ONE.isOne());
      assertTrue(!ONE.isMinusOne());

      assertTrue(MINUSONE.isInteger());
      assertTrue(!MINUSONE.isZero());
      assertTrue(!MINUSONE.isOne());
      assertTrue(MINUSONE.isMinusOne());
   }

   @Test
   public void test_compareTo()
   {

      assertTrue(ZERO.compareTo(ZERO) == 0);
      assertTrue(ONE.compareTo(ZERO) > 0);
      assertTrue(TWO.compareTo(ZERO) > 0);
      assertTrue(MINUSONE.compareTo(ZERO) < 0);

      assertTrue(ZERO.compareTo(ONE) < 0);
      assertTrue(ONE.compareTo(ONE) == 0);
      assertTrue(TWO.compareTo(ONE) > 0);
      assertTrue(MINUSONE.compareTo(ONE) < 0);

      assertTrue(ZERO.compareTo(TWO) < 0);
      assertTrue(ONE.compareTo(TWO) < 0);
      assertTrue(TWO.compareTo(TWO) == 0);
      assertTrue(MINUSONE.compareTo(TWO) < 0);

      assertTrue(ZERO.compareTo(MINUSONE) > 0);
      assertTrue(ONE.compareTo(MINUSONE) > 0);
      assertTrue(TWO.compareTo(MINUSONE) > 0);
      assertTrue(MINUSONE.compareTo(MINUSONE) == 0);

      assertTrue(MINUSONE.compareTo(TWO.neg()) == 1);
      assertTrue(TWO.neg().compareTo(MINUSONE) == -1);
   }

   @SuppressWarnings("unlikely-arg-type")
   @Test
   public void test_equals_hashCode()
   {

      assertTrue(ONE.equals(ONE));
      assertFalse(ONE.equals("hello, world"));
      assertTrue(ONE.equals(ONE));
      for (int k = 0; k < loopCount; k++)
      {
         final double d = random.nextDouble();
         assertTrue(new BigFloat(d).equals(new BigFloat(d)));
         assertFalse(new BigFloat(d).equals(new BigFloat(2 * d)));
         assertFalse(new BigFloat(d).equals(new BigFloat(d / 2)));
         assertFalse(new BigFloat(d).equals(new BigFloat(1.1 * d)));
         assertTrue(new BigFloat(d).equals(new BigFloat(d)));
      }
   }

   @Test
   public void test_intValue()
   {

      assertEquals(17, new BigFloat(17.7).intValue());
   }

   @Test
   public void test_longValue()
   {

      assertEquals(Long.MAX_VALUE, new BigFloat(Long.MAX_VALUE).longValue());
      assertEquals(Long.MIN_VALUE, new BigFloat(Long.MIN_VALUE).longValue());
   }

   @Test
   public void test_floatValue()
   {

      final float value = 17.7f;
      final double diff = value - new BigFloat(value).floatValue();
      assertTrue(Math.abs(diff) <= Math.nextUp(0f));
   }

   @Test
   public void test_doubleValue()
   {

      assertEquals(17.7, new BigFloat(17.7).doubleValue());
   }

   @Test
   public void test_isInteger()
   {

      assertTrue(new BigFloat(17).isInteger());
      assertFalse(new BigFloat(17.7).isInteger());
   }

   @Test
   public void test_terminalPattern()
   {

      final Pattern p = BigFloat.terminalPattern();
      final String s = "((-?0x[_0-9a-f]+([.][_0-9a-f]*)?|-?0x[_0-9a-f]*[.]([_0-9a-f]+)?)(([ep])([+-]?[0-9]+))?|(-?[_0-9]+([.][_0-9]*)?|-?[_0-9]*[.]([_0-9]+)?)(([ep])([+-]?[0-9]+))?)";
      assertEquals(s, p.pattern());
   }

   @Test
   public void test_toString()
   {

      assertEquals("1P-17", new BigFloat(0x1P-17).toString());
      assertEquals("0x1P-17", new BigFloat(0x1P-17).toHexString());
      assertEquals("7.629394531250000E-06", new BigFloat(0x1P-17).toString(MathContext.DECIMAL64));
   }

   @Test
   public void test_precision()
   {

      assertEquals(1, new BigFloat(0).precision());
      assertEquals(3, new BigFloat(7).precision());
   }

   @Test
   public void test_overflow()
   {

      assertTrue(BigFloat.overflow(0) == 0);
      assertTrue(BigFloat.overflow(1) == 1);
      assertTrue(BigFloat.overflow(-1) == -1);
      assertTrue(BigFloat.overflow(Integer.MAX_VALUE) == Integer.MAX_VALUE);
      assertTrue(BigFloat.overflow(Integer.MIN_VALUE) == Integer.MIN_VALUE);
      try
      {
         BigFloat.overflow(1L + Integer.MAX_VALUE);
         fail();
      }
      catch (final ArithmeticException e)
      {
      }
      try
      {
         BigFloat.overflow(-1L + Integer.MIN_VALUE);
         fail();
      }
      catch (final ArithmeticException e)
      {
      }
   }

   @Test
   public void test_for_coverage()
   {

      assertEquals(TWO.neg(), TWO.mul(MINUSONE));
      assertEquals(TWO, TWO.divOrNull(ONE));
      assertEquals(TWO.mul(TWO), TWO.sqr());
      assertEquals(TWO.mul(TWO), TWO.sqr(Rounder.DOUBLE));
      try
      {
         TWO.root(0, null);
         fail();
      }
      catch (final DivisionByZeroException e)
      {
      }
      final Rounder r = Rounder.DOUBLE;
      assertEquals(ONE.div(TWO.root(2, r), r), TWO.root(-2, r));
      assertEquals(TWO, TWO.root(1, r));
      assertEquals(ZERO, ZERO.root(3, r));
      try
      {
         MINUSONE.root(4, r);
         fail();
      }
      catch (final NegativeRadicandException e)
      {
      }
      assertEquals(ONE, ONE.root(5, r));
      assertEquals(new BigFloat(Math.nextDown(Math.pow(2d, 1d / 3d))), TWO.root(3, r));
      {
         final double val = -Math.pow(2d, 1d / 3d);
         assertEquals(new BigFloat(Math.nextUp(val)), TWO.neg().root(3, r));
      }
   }

   private static void test(final BigFloat bigFloat, final double desired)
   {

      if (desired == 0)
      {
         assertTrue(bigFloat.isZero());
      }
      else
      {
         final double error = Math.abs((bigFloat.doubleValue() - desired) / desired);
         try
         {
            assertTrue(error < 1E-15);
         }
         catch (final AssertionError e)
         {
            format("desired: %s\n", desired);
            format("actual:  %s\n", bigFloat.toString(new MathContext(16)));
            format("actual:  %s\n", bigFloat.doubleValue());
            throw e;
         }
      }
   }
}
