package br.eng.strauss.yaxana.big;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;
import java.math.MathContext;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.YaxanaTest;

/**
 * Tests for {@link DoubleIO}.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public class DoubleIOTest extends YaxanaTest
{

   @Test
   public void test_1()
   {

      for (int k = 0; k < 100; k++)
      {
         test_doubleValue(k);
      }
      test_doubleValue(PI);
      test_doubleValue(E);
      assertTrue(new BigFloat(Double.MAX_VALUE).doubleValue() == Double.MAX_VALUE);
      assertTrue(new BigFloat(Double.MIN_VALUE).doubleValue() == Double.MIN_VALUE);
      assertTrue(new BigFloat(Double.MIN_NORMAL).doubleValue() == Double.MIN_NORMAL);
      for (int k = 1; k < 100; k++)
      {
         assertTrue(new BigFloat(BigInteger.valueOf(k), -1074).doubleValue() == k
               * Double.MIN_VALUE);
      }
      for (long k = 0x000FFFFFFFFFFFFFL, count = 0; count < 100; k--, count++)
      {
         assertTrue(new BigFloat(BigInteger.valueOf(k), -1074).doubleValue() == k
               * Double.MIN_VALUE);
      }
      assertTrue(new BigFloat(BigInteger.valueOf(1), 1024)
            .doubleValue() == Double.POSITIVE_INFINITY);
      assertTrue(new BigFloat(BigInteger.valueOf(-1), 1024)
            .doubleValue() == Double.NEGATIVE_INFINITY);
   }

   @Test
   public void test_2()
   {

      assertTrue(new BigFloat(0d).equals(BigFloat.ZERO));
      assertTrue(new BigFloat(1d).equals(BigFloat.ONE));
      assertTrue(new BigFloat(2d).equals(BigFloat.TWO));
      assertTrue(new BigFloat(0.5d).equals(BigFloat.HALF));
      try
      {
         new BigFloat(Double.POSITIVE_INFINITY);
         fail();
      }
      catch (final NumberFormatException e)
      {
      }
      try
      {
         new BigFloat(Double.NEGATIVE_INFINITY);
         fail();
      }
      catch (final NumberFormatException e)
      {
      }
      try
      {
         new BigFloat(Double.NaN);
         fail();
      }
      catch (final NumberFormatException e)
      {
      }
      {
         final BigFloat f = new BigFloat(3d);
         assertTrue(3d == f.unscaledValue.longValue() * Math.pow(2d, f.scale));
         assertTrue(3d == f.doubleValue());
      }
      {
         final BigFloat f = new BigFloat(Math.PI);
         assertTrue(Math.PI == f.unscaledValue.longValue() * Math.pow(2d, f.scale));
         assertTrue(Math.PI == f.doubleValue());
      }
      {
         final BigFloat f = new BigFloat(-Math.E);
         assertTrue(-Math.E == f.unscaledValue.longValue() * Math.pow(2d, f.scale));
         assertTrue(-Math.E == f.doubleValue());
      }
      {
         final BigFloat f = new BigFloat(Double.MAX_VALUE);
         assertTrue(Double.MAX_VALUE == f.unscaledValue.longValue() * Math.pow(2d, f.scale));
         assertTrue(Double.MAX_VALUE == f.doubleValue());
      }
      {
         final BigFloat f = new BigFloat(Double.MIN_VALUE);
         assertTrue(f.unscaledValue.longValue() == 1L);
         assertTrue(f.scale == -1074);
         assertTrue(Double.MIN_VALUE == f.doubleValue());
      }
      {
         final BigFloat f = new BigFloat(Double.MIN_NORMAL);
         assertTrue(Double.MIN_NORMAL == f.unscaledValue.longValue() * Math.pow(2d, f.scale));
         assertTrue(Double.MIN_NORMAL == f.doubleValue());
      }
   }

   @Test
   public void test_problems()
   {

      final BigFloat bigFloat = new BigFloat(new BigInteger("-9767667875439099"), -19);
      final double d0 = Double.valueOf(bigFloat.toString(new MathContext(16)));
      final double d1 = bigFloat.doubleValue();
      assertTrue(d0 == d1);
   }

   private static void test_doubleValue(final double value)
   {

      assertTrue(new BigFloat(value).doubleValue() == value);
      assertTrue(new BigFloat(-value).doubleValue() == -value);
      assertTrue(new BigFloat(1E99 * value).doubleValue() == 1E99 * value);
      assertTrue(new BigFloat(1E99 * -value).doubleValue() == 1E99 * -value);
      assertTrue(new BigFloat(1E-99 * value).doubleValue() == 1E-99 * value);
      assertTrue(new BigFloat(1E-99 * -value).doubleValue() == 1E-99 * -value);
   }
}
