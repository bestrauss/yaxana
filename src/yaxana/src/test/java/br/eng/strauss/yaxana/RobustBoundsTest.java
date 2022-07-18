package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Robust.ONE;
import static br.eng.strauss.yaxana.Robust.ZERO;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.doubleToLongBits;
import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.nextDown;
import static java.lang.Math.nextUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.IllegalExponentException;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class RobustBoundsTest extends YaxanaTest
{

   @Test
   public void testZERO()
   {

      {
         final double x = Robust.ZERO.lowerBound();
         assertEquals(doubleToLongBits(x), doubleToLongBits(-0d));
         assertNotEquals(doubleToLongBits(x), doubleToLongBits(0d));
      }
      {
         final double x = Robust.ZERO.upperBound();
         assertEquals(doubleToLongBits(x), doubleToLongBits(0d));
         assertNotEquals(doubleToLongBits(x), doubleToLongBits(-0d));
      }
   }

   @Test
   public void testTerminal()
   {

      final double[] v = { 0d, 1d, PI, -E };
      final Robust[] r = { Robust.ZERO, Robust.ONE, Robust.valueOf(PI), Robust.valueOf(-E) };
      int index = 0;
      for (final Robust a : r)
      {
         assertEqual(v[index++], a);
      }
   }

   @Test
   public void testAdd()
   {

      {
         final Robust a = ZERO;
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.add(b);
         assertTrue(c == b);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = ZERO;
         final Robust c = a.add(b);
         assertTrue(c == a);
      }
      {
         final Robust a = Robust.valueOf(-PI);
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.add(b);
         assertBounds(0d, c);
      }
      {
         final Robust a = Robust.valueOf(-PI);
         final Robust b = Robust.valueOf(1d + PI);
         final Robust c = a.add(b);
         assertBounds(1d, c);
      }
      {
         final Robust a = Robust.valueOf(2d);
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.add(b);
         assertBounds(2d + PI, c);
      }
      {
         final Robust a = ONE.add(ONE);
         assertEquals(2d, a.doubleValue(), 0d);
         assertEquals(nextDown(2d), a.lowerBound(), 0d);
         assertEquals(nextUp(2d), a.upperBound(), 0d);
         final Robust b = ONE.neg().add(ONE.neg());
         final Robust c = a.add(b);
         assertBounds(0d, c);
      }
   }

   @Test
   public void testSub()
   {

      {
         final Robust a = ZERO;
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.sub(b);
         assertEquals(Robust.valueOf(PI).neg(), c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = ZERO;
         final Robust c = a.sub(b);
         assertTrue(c == a);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.sub(b);
         assertBounds(0d, c);
      }
      {
         final Robust a = Robust.valueOf(2d);
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.sub(b);
         assertEquals(2d - PI, c.doubleValue(), 0d);
         assertEquals(nextDown(2d - PI), c.lowerBound(), 0d);
         assertEquals(nextUp(2d - PI), c.upperBound(), 0d);
      }
      {
         final Robust a = ONE.add(ONE);
         final Robust b = ONE.add(ONE);
         final Robust c = a.sub(b);
         assertBounds(0d, c);
      }
   }

   @Test
   public void testMul()
   {

      {
         final Robust a = ZERO;
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.mul(b);
         assertTrue(c == ZERO);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = ZERO;
         final Robust c = a.mul(b);
         assertTrue(c == ZERO);
      }
      {
         final Robust a = Robust.valueOf(2d);
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.mul(b);
         assertBounds(2d * PI, c);
      }
      {
         final Robust a = Robust.valueOf(-2d);
         final Robust b = Robust.valueOf(-PI);
         final Robust c = a.mul(b);
         assertBounds(2d * PI, c);
      }
      {
         final Robust a = Robust.valueOf(-2d);
         final Robust b = Robust.valueOf(PI);
         final Robust c = a.mul(b);
         assertBounds(-2d * PI, c);
      }
      {
         final Robust a = ONE.add(ONE);
         final Robust b = ONE.add(ONE);
         final Robust c = a.mul(b);
         assertBounds(4d, c);
      }
      {
         final Robust a = ONE.add(ONE);
         final Robust b = ONE.neg().add(ONE.neg());
         final Robust c = a.mul(b);
         assertBounds(-4d, c);
      }
      {
         final Robust a = ONE.sub(ONE);
         final Robust b = ONE.neg();
         final Robust c = a.mul(b);
         assertBounds(0d, c);
      }
      {
         final Robust a = Robust.valueOf(nextUp(0d)).mul(Robust.valueOf(2d))
               .sub(Robust.valueOf(4.9E-324));
         final Robust b = ONE;
         final Robust c = a.mul(b);
         assertBounds(4.9E-324, c);
      }
      {
         final Robust a = Robust.valueOf(nextUp(0d)).mul(Robust.valueOf(2d))
               .sub(Robust.valueOf(nextUp(0d))).neg();
         final Robust b = ONE;
         final Robust c = a.mul(b);
         assertBounds(nextDown(0d), c);
      }
      {
         final Robust a = Robust.valueOf(1E-200);
         final Robust b = Robust.valueOf(1E-200);
         final Robust c = a.mul(b);
         assertBounds(nextUp(0d), c);
      }
      {
         final Robust a = Robust.valueOf(-1E-200);
         final Robust b = Robust.valueOf(1E-200);
         final Robust c = a.mul(b);
         assertBounds(nextDown(0d), c);
      }
   }

   @Test
   public void testDiv()
   {

      try
      {
         final Robust a = ONE;
         final Robust b = ZERO;
         a.div(b);
         fail();
      }
      catch (final DivisionByZeroException e)
      {
      }
      {
         final Robust a = ZERO;
         final Robust b = ONE;
         final Robust c = a.div(b);
         assertEquals(ZERO, c);
      }
      {
         final Robust a = ONE;
         final Robust b = ONE;
         final Robust c = a.div(b);
         assertBounds(1d, c);
      }
      {
         final Robust a = ONE.neg();
         final Robust b = ONE;
         final Robust c = a.div(b);
         assertBounds(-1d, c);
      }
      {
         final Robust a = ONE;
         final Robust b = ONE.neg();
         final Robust c = a.div(b);
         assertBounds(-1d, c);
      }
      {
         final Robust a = Robust.valueOf(nextUp(0d)).mul(Robust.valueOf(2d))
               .sub(Robust.valueOf(4.9E-324));
         final Robust b = ONE;
         final Robust c = a.div(b);
         assertBounds(4.9E-324, c);
      }
      {
         final Robust a = Robust.valueOf(nextUp(0d)).mul(Robust.valueOf(2d))
               .sub(Robust.valueOf(4.9E-324)).neg();
         final Robust b = ONE;
         final Robust c = a.div(b);
         assertBounds(-4.9E-324, c);
      }
      {
         final Robust a = Robust.valueOf(1E-200);
         final Robust b = Robust.valueOf(1E200);
         final Robust c = a.div(b);
         assertBounds(nextUp(0d), c);
      }
      {
         final Robust a = Robust.valueOf(1E200);
         final Robust b = Robust.valueOf(1E-200);
         final Robust c = a.div(b);
         assertBounds(POSITIVE_INFINITY, c);
      }
   }

   @Test
   public void testNeg()
   {

      {
         final Robust a = ZERO;
         final Robust c = a.neg();
         assertEquals(ZERO, c);
      }
      {
         final Robust a = ONE;
         final Robust c = a.neg();
         assertBounds(-1d, c);
      }
   }

   @Test
   public void testAbs()
   {

      {
         final Robust a = ZERO;
         final Robust c = a.abs();
         assertEquals(ZERO, c);
      }
      {
         final Robust a = ONE;
         final Robust c = a.abs();
         assertEquals(ONE, c);
      }
      {
         final Robust a = ONE.neg();
         final Robust c = a.abs();
         assertBounds(1d, c);
      }
   }

   @Test
   public void testPow()
   {

      {
         final Robust a = ZERO;
         final Robust c = a.pow(0);
         assertBounds(1d, c);
      }
      {
         final Robust a = ZERO;
         final Robust c = a.pow(1);
         assertEquals(ZERO, c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.pow(0);
         assertEquals(ONE, c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.pow(1);
         assertBounds(PI, c);
      }
      try
      {
         final Robust a = Robust.valueOf(PI);
         a.pow(Robust.MAX_EXPONENT + 1);
         fail();
      }
      catch (final IllegalExponentException e)
      {
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.pow(2);
         assertBounds(PI * PI, c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.pow(-2);
         assertBounds(1d / (PI * PI), c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.pow(3);
         assertBounds(PI * PI * PI, c);
      }
      {
         final Robust a = Robust.valueOf(-PI);
         final Robust c = a.pow(3);
         assertBounds(-PI * PI * PI, c);
      }
      {
         final Robust a = Robust.valueOf(nextUp(0d)).mul(Robust.valueOf(2d))
               .sub(Robust.valueOf(4.9E-324));
         final Robust c = a.pow(2);
         assertBounds(nextUp(0d), c);
      }
      {
         final Robust a = Robust.valueOf(nextUp(0d)).mul(Robust.valueOf(2d))
               .sub(Robust.valueOf(4.9E-324)).neg();
         final Robust b = ONE;
         final Robust c = a.mul(b);
         assertBounds(-4.9E-324, c);
      }
   }

   @Test
   public void testRoot()
   {

      try
      {
         final Robust a = ONE;
         a.root(0);
         fail();
      }
      catch (final DivisionByZeroException e)
      {
      }
      {
         final Robust a = ZERO;
         final Robust c = a.sqrt();
         assertEquals(ZERO, c);
      }
      {
         final Robust a = ONE;
         final Robust c = a.root(1);
         assertEquals(ONE, c);
      }
      try
      {
         final Robust a = ONE;
         a.root(Robust.MAX_EXPONENT + 1);
         fail();
      }
      catch (final IllegalExponentException e)
      {
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.root(-1);
         assertBounds(1d / PI, c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.root(-2);
         assertBounds(1d / Math.sqrt(PI), c);
      }
      {
         final Robust a = ONE;
         final Robust c = a.sqrt();
         assertBounds(1d, c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.root(3);
         assertBounds(Math.pow(PI, 1d / 3), c);
      }
      {
         final Robust a = Robust.valueOf(-PI);
         final Robust c = a.root(3);
         assertBounds(-Math.pow(PI, 1d / 3), c);
      }
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.root(4);
         assertBounds(Math.pow(PI, 1d / 4), c);
      }
      {
         final Robust a = Robust.valueOf(nextUp(0d)).mul(Robust.valueOf(2d))
               .sub(Robust.valueOf(4.9E-324));
         final Robust c = a.root(2);
         assertBounds(Math.sqrt(4.9E-324), c);
      }
   }

   private static void assertEqual(final double value, final Robust robust)
   {

      assertEquals(value, robust.doubleValue(), 0d);
      assertEquals(value, robust.lowerBound(), 0d);
      assertEquals(value, robust.upperBound(), 0d);
   }

   private static void assertBounds(final double value, final Robust robust)
   {

      assertEquals(value, robust.doubleValue(), 0d);
      if (!(robust.doubleValue() >= robust.lowerBound()))
      {
         format("%s: %s\n", value, robust);
      }
      assertTrue(robust.doubleValue() >= robust.lowerBound());
      if (!(robust.doubleValue() <= robust.upperBound()))
      {
         format("%s: %s\n", value, robust);
      }
      assertTrue(robust.doubleValue() <= robust.upperBound());
      final double eps = 4E-15;
      {
         final boolean condition = Double.isInfinite(value) || value - eps <= robust.lowerBound();
         if (!condition)
         {
            format("%s: %s\n", value, robust);
         }
         assertTrue(condition);
      }
      if (!(robust.lowerBound() <= value))
      {
         format("%s: %s\n", value, robust);
      }
      assertTrue(robust.lowerBound() <= value);
      if (!(value <= robust.upperBound()))
      {
         format("%s: %s\n", value, robust);
      }
      assertTrue(value <= robust.upperBound());
      if (!(robust.upperBound() <= value + eps))
      {
         format("%s: %s\n", value, robust);
      }
      assertTrue(robust.upperBound() <= value + eps);
   }
}
