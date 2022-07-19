package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.epu.Algebraic.TWO;
import static br.eng.strauss.yaxana.epu.Algebraic.ZERO;
import static br.eng.strauss.yaxana.epu.Algebraic.ceilOfLog2OfAbsOf;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.addIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.divIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.mulIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.rootIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.subIsExact;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.MathContext;
import java.util.Objects;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;
import br.eng.strauss.yaxana.pdc.Scrutinizer;

/**
 * Some basic tests for {@link Algebraic} to make sure that things work in principle.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class AlgebraicTest extends YaxanaTest
{

   @Test
   public void test_toString()
   {

      final Algebraic x = new Algebraic("15");
      final Algebraic y = new Algebraic("37");
      assertTrue(x.add(y).toString().equals("15+37"));
      assertTrue(x.sub(y).toString().equals("15-37"));
      assertTrue(x.mul(y).toString().equals("15*37"));
      assertTrue(x.div(y).toString().equals("15/37"));
      assertTrue(x.neg().toString().equals("-15"));
      assertTrue(x.abs().toString().equals("|15|"));
      assertTrue(x.sqr().toString().equals("15^2"));
      assertTrue(x.sqrt().toString().equals("\\15"));
      assertTrue(x.root(21).toString().equals("root(15, 21)"));
   }

   @Test
   public void testHashCode()
   {

      assertEquals(Objects.hash(BigFloat.ONE, Integer.MAX_VALUE), Algebraic.ONE.hashCode());
      assertEquals(Objects.hash(new BigFloat(-42), Integer.MAX_VALUE),
                   new Algebraic(-42d).hashCode());
   }

   @Test
   public void testAdd()
   {

      format("add:\n");
      {
         final Algebraic a = new Algebraic("123.0");
         final Algebraic b = new Algebraic("321P-20");
         final Algebraic c = a.add(b);
         c.approximation(300);
         format("%s\n", c);
         assertTrue(c.doubleValue() == 123.0 + 321 * Math.pow(2, -20));
         assertTrue(c.precision() == Integer.MAX_VALUE);
      }
      final Algebraic a = new Algebraic("123.0");
      final Algebraic b = new Algebraic("321P-200");
      final Algebraic c = a.add(b);
      format("%s + %s = %s\n", a, b, c);
      assertTrue(c.compareTo(c) == 0);
      c.approximation(300);
      format("%s + %s = %s\n", a, b, c);
      format("precision: %s\n", c.precision());
      assertTrue(addIsExact(a.approximation(), b.approximation(), c.approximation()));
      assertTrue(c.precision() == Integer.MAX_VALUE);
      format("\n");
   }

   @Test
   public void testSub()
   {

      format("sub:\n");
      {
         final Algebraic a = new Algebraic("123.0");
         final Algebraic b = new Algebraic("321P-20");
         final Algebraic c = a.sub(b);
         format("%s\n", c);
         c.approximation(300);
         assertTrue(c.doubleValue() == 123.0 - 321 * Math.pow(2, -20));
         assertTrue(c.precision() == Integer.MAX_VALUE);
      }
      final Algebraic a = new Algebraic("123.0");
      final Algebraic b = new Algebraic("321P-200");
      final Algebraic c = a.sub(b);
      assertTrue(c.compareTo(c) == 0);
      c.approximation(300);
      assertTrue(subIsExact(a.approximation(), b.approximation(), c.approximation()));
      format("%s\n", c);
      format("precision: %s\n", c.precision());
      assertTrue(c.precision() == Integer.MAX_VALUE);
      format("\n");
   }

   @Test
   public void testMul()
   {

      format("mul:\n");
      {
         final Algebraic a = new Algebraic("2222222.0");
         final Algebraic b = new Algebraic("2222222.0");
         final Algebraic c = a.mul(b);
         c.approximation(300);
         format("%s\n", c);
         assertTrue(c.doubleValue() == 2222222.0 * 2222222.0);
         assertTrue(c.precision() == Integer.MAX_VALUE);
      }
      final Algebraic a = new Algebraic("22222222222.0");
      final Algebraic b = new Algebraic("22222222222.0");
      final Algebraic c = a.mul(b);
      assertTrue(c.compareTo(c) == 0);
      c.approximation(300);
      assertTrue(mulIsExact(a.approximation(), b.approximation(), c.approximation()));
      format("%s\n", c);
      format("precision: %s\n", c.precision());
      assertTrue(c.precision() == Integer.MAX_VALUE);
      format("\n");
   }

   @Test
   public void testDiv()
   {

      format("div:\n");
      {
         final Algebraic a = new Algebraic("999999999999.0");
         final Algebraic b = new Algebraic("333333333333.0");
         final Algebraic c = a.div(b);
         c.approximation(300);
         format("%s\n", c);
         assertTrue(c.doubleValue() == 999999999999.0 / 333333333333.0);
         assertTrue(c.precision() == Integer.MAX_VALUE);
      }
      final Algebraic a = new Algebraic("999999999999999999999999999999.0");
      final Algebraic b = new Algebraic("33.0");
      final Algebraic c = a.div(b);
      assertTrue(c.compareTo(c) == 0);
      c.approximation(300);
      assertTrue(divIsExact(a.approximation(), b.approximation(), c.approximation()));
      format("%s\n", c);
      format("precision: %s\n", c.precision());
      format("\n");
      assertTrue(c.precision() == Integer.MAX_VALUE);
      format("\n");
   }

   @Test
   public void testSqrt()
   {

      format("%s\n", new BigFloat(1234567));
      format("%s\n", new BigFloat(1234567).pow(2));
      format("%s\n", new BigFloat(1234567).pow(2).sqrt(Rounder.SINGLE));
      assertTrue(new BigFloat(1234567).pow(2).sqrt(new Rounder(100)).equals(new BigFloat(1234567)));

      format("sqrt:\n");
      {
         final Algebraic a = new Algebraic("1234567.0");
         final Algebraic b = a.sqr();
         final Algebraic c = b.sqrt();
         c.approximation(300);
         format("%s ~= %s\n", b, b.approximation().toString(new MathContext(20)));
         format("%s ~= %s\n", c, c.approximation().toString(new MathContext(20)));
         format("%s\n", c.doubleValue());
         assertTrue(c.doubleValue() == Math.sqrt(1234567.0 * 1234567.0));
         assertTrue(c.precision() == Integer.MAX_VALUE);
      }
      final Algebraic a = new Algebraic("1234567890123456789.0");
      final Algebraic b = a.sqr();
      final Algebraic c = b.sqrt();
      assertTrue(c.compareTo(c) == 0);
      c.approximation(300);
      format("%s\n", c);
      assertTrue(a.compareTo(c) == 0);
      assertTrue(rootIsExact(b.approximation(), 2, c.approximation()));
      format("precision: %s\n", c.precision());
      format("\n");
      assertTrue(c.precision() == Integer.MAX_VALUE);
      format("\n");

      Scrutinizer.powIsExact(BigFloat.ZERO, 0, BigFloat.ONE);
   }

   @Test
   public void testSqrtOfBigFloat()
   {

      {
         final Algebraic a = Algebraic.TWO.sqrt();
         assertTrue(a.compareTo(a) == 0);
         assertFalse(rootIsExact(BigFloat.TWO, 2, a.approximation()));
      }
      {
         final Algebraic SEVENS = new Algebraic("777777777777777");
         final Algebraic SEVENS2 = SEVENS.mul(SEVENS);
         final Algebraic a = SEVENS2.sqrt();
         assertTrue(a.compareTo(a) == 0);
         assertTrue(rootIsExact(SEVENS2.approximation(), 2, a.approximation()));
      }
   }

   @Test
   public void test2ndRootOfBigFloat()
   {

      {
         final Algebraic a = Algebraic.TWO.root(2);
         assertTrue(a.compareTo(a) == 0);
         assertFalse(rootIsExact(BigFloat.TWO, 2, a.approximation()));
      }
      {
         final Algebraic SEVENS = new Algebraic("777777777777777");
         final Algebraic SEVENS2 = SEVENS.mul(SEVENS);
         final Algebraic a = SEVENS2.root(2);
         assertTrue(a.compareTo(a) == 0);
         assertTrue(rootIsExact(SEVENS2.approximation(), 2, a.approximation()));
      }
   }

   @Test
   public void testSampleFromImprovedImplementationOfLedaReals()
   {

      format("sample expressions:\n");
      final Algebraic x = new Algebraic("15");
      final Algebraic y = new Algebraic("37");
      assertTrue(x.mul(y).toString().equals("15*37"));
      final Algebraic s0 = x.sqrt().add(y.sqrt());
      final Algebraic s1 = x.add(y).add(x.mul(y).sqrt().mul(Algebraic.TWO)).sqrt();
      final Algebraic a = s0.sub(s1);
      assertTrue(s0.toString().equals("\\15+\\37"));
      assertTrue(s1.toString().equals("\\(15+37+\\(15*37)*2)"));
      assertTrue(a.toString().equals("\\15+\\37-\\(15+37+\\(15*37)*2)"));
      format("s0: %s\n", s0);
      format("s1: %s\n", s1);
      format("a:  %s\n", a);
      format("precision: %d\n", a.precision());
      format("\n");
      assertTrue(a.signum() == 0);
      assertTrue(a.compareTo(a) == 0);
   }

   @Test
   public void testSampleFromImprovedImplementationOfLedaReals2()
   {

      format("sample expressions:\n");
      final Algebraic x = new Algebraic("2");
      final Algebraic y = new Algebraic("3");
      assertTrue(x.mul(y).toString().equals("2*3"));
      final Algebraic s0 = x.sqrt().add(y.sqrt());
      final Algebraic s1 = x.add(y).add(x.mul(y).sqrt().mul(Algebraic.TWO)).sqrt();
      final Algebraic a = s0.sub(s1);
      assertTrue(s0.toString().equals("\\2+\\3"));
      assertTrue(s1.toString().equals("\\(2+3+\\(2*3)*2)"));
      assertTrue(a.toString().equals("\\2+\\3-\\(2+3+\\(2*3)*2)"));
      format("s0: %s\n", s0);
      format("s1: %s\n", s1);
      assertTrue(a.compareTo(a) == 0);
      format("a:  %s\n", a);
      format("precision: %d\n", a.precision());
      format("\n");
   }

   @Test
   public void testCeilOfLog2OfAbsOf()
   {

      assertEquals(0, ceilOfLog2OfAbsOf(0));
      assertEquals(0, ceilOfLog2OfAbsOf(1));
      assertEquals(1, ceilOfLog2OfAbsOf(2));
      assertEquals(2, ceilOfLog2OfAbsOf(3));
      assertEquals(2, ceilOfLog2OfAbsOf(4));
      assertEquals(3, ceilOfLog2OfAbsOf(5));
      assertEquals(3, ceilOfLog2OfAbsOf(6));
      assertEquals(3, ceilOfLog2OfAbsOf(7));
      assertEquals(3, ceilOfLog2OfAbsOf(8));
      assertEquals(4, ceilOfLog2OfAbsOf(9));

      assertEquals(4, ceilOfLog2OfAbsOf(15));
      assertEquals(4, ceilOfLog2OfAbsOf(16));
      assertEquals(5, ceilOfLog2OfAbsOf(17));

      assertEquals(0, ceilOfLog2OfAbsOf(-0));
      assertEquals(0, ceilOfLog2OfAbsOf(-1));
      assertEquals(1, ceilOfLog2OfAbsOf(-2));
      assertEquals(2, ceilOfLog2OfAbsOf(-3));
      assertEquals(2, ceilOfLog2OfAbsOf(-4));
      assertEquals(3, ceilOfLog2OfAbsOf(-5));
      assertEquals(3, ceilOfLog2OfAbsOf(-6));
      assertEquals(3, ceilOfLog2OfAbsOf(-7));
      assertEquals(3, ceilOfLog2OfAbsOf(-8));
      assertEquals(4, ceilOfLog2OfAbsOf(-9));
   }

   @Test
   public void testCalculatorBug20220526()
   {

      {
         final Algebraic epsilon = new Algebraic("1P-100");
         final Algebraic a = new Algebraic("2");
         final Algebraic b = new Algebraic("3");
         final Algebraic c = a.add(b.add(epsilon));
         final Algebraic d = a.mul(b);
         final Algebraic f = ZERO.add(a.sqrt().add(b.sqrt())).sqr().sub(c).div(TWO).sqr().sub(d);
         assertFalse(f.isZero());
      }
      {
         final Algebraic epsilon = new Algebraic("0P-100");
         final Algebraic a = new Algebraic("2");
         final Algebraic b = new Algebraic("3");
         final Algebraic c = a.add(b.add(epsilon));
         final Algebraic d = a.mul(b);
         final Algebraic f = ZERO.add(a.sqrt().add(b.sqrt())).sqr().sub(c).div(TWO).sqr().sub(d);
         assertTrue(f.isZero());
      }
   }

   @Test
   public void testEquals()
   {

      {
         final Algebraic a = new Algebraic("2+\\3");
         final Algebraic b = new Algebraic("2+\\3");
         assertEquals(a, b);
      }
      {
         final Algebraic a = new Algebraic("2+\\3");
         final Algebraic b = new Algebraic("2+\\4");
         assertNotEquals(a, b);
      }
   }
}
