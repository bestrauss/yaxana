package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Robust.ONE;
import static br.eng.strauss.yaxana.Robust.ZERO;
import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.rnd.RandomAlgebraic;
import br.eng.strauss.yaxana.tools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class RobustTest extends YaxanaTest
{

   @Test
   public void testValueOfString()
   {

      {
         final Robust value = Robust.valueOf("0");
         assertEquals(ZERO, value);
         assertEquals(0, value.signum());
         assertEquals(0, value.compareTo(ZERO));
         assertEquals(-1, value.compareTo(ONE));
      }
      {
         final Robust value = Robust.valueOf("0+0");
         assertEquals(ZERO, value);
         assertEquals(0, value.signum());
         assertEquals(0, value.compareTo(ZERO));
         assertEquals(-1, value.compareTo(ONE));
      }
      {
         final Robust value = Robust.valueOf("1+2");
         assertEquals(1, value.signum());
         assertEquals(1, value.compareTo(ZERO));
         assertEquals(3d, value.doubleValue(), 0d);
      }
   }

   @Disabled // TODO ignorierte Testmethode
   @Test
   public void testValueOfSyntaxTree()
   {

      final RandomAlgebraic randomAlgebraic = new RandomAlgebraic(10, 10, 10);
      for (int k = 0; k < 100; k++)
      {
         final Algebraic a = randomAlgebraic.next();
         final Robust value = Robust.valueOf(a);
         final Algebraic b = (Algebraic) value.toSyntaxTree();
         assertEquals(a.signum(), b.signum());
         assertTrue(a.approximation().sub(b.approximation()).abs()
               .compareTo(new BigFloat(1E-15)) < 0);
      }
   }

   @Test
   public void testLowerUpperBound()
   {

      final double doubleValue = Math.sqrt(2d);
      {
         final Robust value = Robust.valueOf(doubleValue);
         assertEquals(doubleValue, value.lowerBound(), 0d);
         assertEquals(doubleValue, value.upperBound(), 0d);
      }
      {
         final Robust value = Robust.valueOf("\\2");
         assertEquals(Math.nextDown(doubleValue), value.lowerBound(), 0d);
         assertEquals(Math.nextUp(doubleValue), value.upperBound(), 0d);
      }
   }

   @Test
   public void testSignum()
   {

      assertEquals(0, Robust.valueOf(0d).signum());
      assertEquals(1, Robust.valueOf(2d).signum());
      assertEquals(-1, Robust.valueOf(-3d).signum());
   }

   @Test
   public void testCompareTo()
   {

      assertEquals(0, ZERO.compareTo(ZERO));
      assertEquals(0, ONE.compareTo(ONE));
      assertEquals(1, ONE.compareTo(ZERO));
      assertEquals(-1, ZERO.compareTo(ONE));
   }

   @Test
   public void testAdd()
   {

      final double v = PI;
      final Robust a = Robust.valueOf(v);
      final Robust b = Robust.valueOf(0x1P-100);
      final Robust c = a.add(b);
      assertEquals(Math.nextDown(v), c.lowerBound(), 0d);
      assertEquals(Math.nextUp(v), c.upperBound(), 0d);
      assertEquals(v, c.doubleValue(), 0d);
      assertTrue(c.lowerBound() <= c.doubleValue());
      assertTrue(c.doubleValue() <= c.upperBound());
   }

   @Test
   public void testSub()
   {

      final double v = PI;
      final Robust a = Robust.valueOf(v);
      final Robust b = Robust.valueOf(0x1P-100);
      final Robust c = a.sub(b);
      assertEquals(Math.nextDown(v), c.lowerBound(), 0d);
      assertEquals(Math.nextUp(v), c.upperBound(), 0d);
      assertEquals(v, c.doubleValue(), 0d);
      assertTrue(c.lowerBound() <= c.doubleValue());
      assertTrue(c.doubleValue() <= c.upperBound());
   }

   @Test
   public void testMul()
   {

      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = Robust.valueOf(0x1P-100);
         final Robust c = a.mul(b);
         final double v = PI * Math.pow(2d, -100);
         assertEquals(v, c.lowerBound(), 0d);
         assertEquals(v, c.upperBound(), 0d);
         assertEquals(v, c.doubleValue(), 0d);
         assertTrue(c.lowerBound() <= c.doubleValue());
         assertTrue(c.doubleValue() <= c.upperBound());
      }
      if (false) // TODO Zahlenwerte, so dass nextDown/nextUp benötigt wird, wie oben
      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = Robust.valueOf(0x1P-100);
         final Robust c = a.mul(b);
         final double v = PI * Math.pow(2d, -100);
         assertEquals(Math.nextDown(v), c.lowerBound(), 0d);
         assertEquals(Math.nextUp(v), c.upperBound(), 0d);
         assertEquals(v, c.doubleValue(), 0d);
         assertTrue(c.lowerBound() <= c.doubleValue());
         assertTrue(c.doubleValue() <= c.upperBound());
      }
   }

   @Test
   public void testDiv()
   {

      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = Robust.valueOf(0x1P100);
         final Robust c = a.div(b);
         final double v = PI * Math.pow(2d, -100);
         assertEquals(v, c.lowerBound(), 0d);
         assertEquals(v, c.upperBound(), 0d);
         assertEquals(v, c.doubleValue(), 0d);
         assertTrue(c.lowerBound() <= c.doubleValue());
         assertTrue(c.doubleValue() <= c.upperBound());
      }
      if (false) // TODO Zahlenwerte, so dass nextDown/nextUp benötigt wird, wie oben
      {
         final Robust a = Robust.valueOf(PI);
         final Robust b = Robust.valueOf(0x1P100);
         final Robust c = a.div(b);
         final double v = PI * Math.pow(2d, -100);
         assertEquals(Math.nextDown(v), c.lowerBound(), 0d);
         assertEquals(Math.nextUp(v), c.upperBound(), 0d);
         assertEquals(v, c.doubleValue(), 0d);
         assertTrue(c.lowerBound() <= c.doubleValue());
         assertTrue(c.doubleValue() <= c.upperBound());
      }
   }

   @Test
   public void testNeg()
   {

      final Robust a = Robust.valueOf(PI);
      final Robust c = a.neg();
      final double v = -PI;
      assertEquals(v, c.doubleValue(), 0d);
      assertTrue(c.lowerBound() <= c.doubleValue());
      assertTrue(c.doubleValue() <= c.upperBound());
   }

   @Test
   public void testAbs()
   {

      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.abs();
         final double v = PI;
         assertEquals(v, c.doubleValue(), 0d);
         assertTrue(c.lowerBound() <= c.doubleValue());
         assertTrue(c.doubleValue() <= c.upperBound());
      }
      {
         final Robust a = Robust.valueOf(-PI);
         final Robust c = a.abs();
         final double v = PI;
         assertEquals(v, c.doubleValue(), 0d);
         assertTrue(c.lowerBound() <= c.doubleValue());
         assertTrue(c.doubleValue() <= c.upperBound());
      }
   }

   @Test
   public void testPow()
   {

      assertEquals(ONE, Robust.valueOf(PI).pow(0));
      assertEquals(Robust.valueOf(PI), Robust.valueOf(PI).pow(1));
      assertEquals(ONE.div(Robust.valueOf(PI)), Robust.valueOf(PI).pow(-1));
      assertEquals(Robust.valueOf(125), Robust.valueOf(5).pow(3));
      assertEquals(ONE.div(Robust.valueOf(125)), Robust.valueOf(5).pow(-3));
      assertEquals(Robust.valueOf("2^2047"), Robust.valueOf(2).pow(Robust.MAX_EXPONENT));
   }

   @Test
   public void testRoot()
   {

      try
      {
         Robust.valueOf(PI).root(0);
         fail();
      }
      catch (final DivisionByZeroException e)
      {
      }
      assertEquals(Robust.valueOf(PI), Robust.valueOf(PI).root(1));
      assertEquals(ONE.div(Robust.valueOf(PI)), Robust.valueOf(PI).root(-1));
      {
         final Robust a = Robust.valueOf(PI);
         final Robust c = a.root(2);
         final double v = Math.sqrt(PI);
         assertEquals(Math.nextDown(v), c.lowerBound(), 0d);
         assertEquals(Math.nextUp(v), c.upperBound(), 0d);
         assertEquals(v, c.doubleValue(), 0d);
         assertTrue(c.lowerBound() <= c.doubleValue());
         assertTrue(c.doubleValue() <= c.upperBound());
      }
   }

   @Test
   public void testNewTerminal()
   {

      final Robust value = ZERO.newTerminal(Double.toHexString(PI));
      assertEquals(Robust.valueOf(PI), value);
   }

   @Test
   public void testIndex()
   {

      assertEquals(1, Robust.valueOf(1d).index());
      assertEquals(4, Robust.valueOf(2d).pow(2).index());
      assertEquals(2, Robust.valueOf(PI).pow(2).index());
      assertEquals(2, Robust.valueOf(2d).root(2).index());
   }

   @Test
   public void testToString()
   {

      {
         final String desired = "\\2+\\3+\\(5+2*\\6)";
         final String actual = Robust.valueOf(desired).toString();
         assertEquals(desired, actual);
      }
      {
         final String input = "|-2|^2-3/4";
         final String desired = "0x1.AP+1";
         final String actual = Robust.valueOf(input).toString();
         assertEquals(desired, actual);
      }
   }

   @Test
   public void testIntValue()
   {

      final String value = "\\2+\\3+\\(5+2*\\6)";
      assertEquals(6, Robust.valueOf(value).intValue());
   }

   @Test
   public void testLongValue()
   {

      final String value = "\\2+\\3+\\(5+2*\\6)";
      assertEquals(6L, Robust.valueOf(value).longValue());
   }

   @Test
   public void testFloatValue()
   {

      final String value = "\\2+\\3";
      assertEquals((float) (Math.sqrt(2d) + Math.sqrt(3d)), Robust.valueOf(value).floatValue(), 0d);
   }

   @Test
   public void testDoubleValue()
   {

      final String value = "\\2+\\3";
      assertEquals(Math.sqrt(2d) + Math.sqrt(3d), Robust.valueOf(value).doubleValue(), 0d);
   }

   @Test
   public void testOne()
   {

      assertEquals(ONE, ONE.one());
   }

   @Test
   public void testToAlgebraic()
   {

      {
         final String input = "(-|-2|)^2";
         final String desired = "4";
         final String actual = Robust.valueOf(input).toString();
         assertEquals(desired, actual);
      }
      {
         final String desired = "\\2+\\3-\\(5+2*\\7)";
         final String actual = Robust.valueOf(desired).toString();
         assertEquals(desired, actual);
      }
      {
         final String desired = "\\2+\\3-\\(5+2*\\6)";
         final String actual = Robust.valueOf(desired).toString();
         assertEquals("0", actual);
      }
      {
         final String input = "(-|-2|)^2-3/4-((-|-2|)^2-3/5)";
         final String desired = "0x1.AP+1-(4-3/5)";
         final String actual = Robust.valueOf(input).toString();
         assertEquals(desired, actual);
      }
   }

   @Test
   public void testZERO()
   {

      final Robust value = Robust.valueOf(0d);
      assertEquals(ZERO, value);
      assertEquals(0, value.signum());
      assertEquals(0, value.compareTo(ZERO));
      assertEquals(-1, value.compareTo(ONE));
   }

   @Test
   public void testONE()
   {

      final Robust value = Robust.valueOf(1d);
      assertEquals(ONE, value);
      assertEquals(1, value.signum());
      assertEquals(0, value.compareTo(ONE));
      assertEquals(1, value.compareTo(ZERO));
   }

   @Test
   public void testNoOfOperationsAndOperands()
   {

      final Robust value = Robust.valueOf("1/3+2");
      assertEquals(3, value.noOfOperands());
      assertEquals(2, value.noOfOperations());
   }
}
