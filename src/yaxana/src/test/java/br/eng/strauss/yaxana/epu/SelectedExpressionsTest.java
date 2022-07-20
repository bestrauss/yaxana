package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.epu.Algebraic.ONE;
import static br.eng.strauss.yaxana.epu.Algebraic.TWO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.MathContext;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.anno.WithAlgorithms;
import br.eng.strauss.yaxana.big.BigFloat;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
@WithAlgorithms
public final class SelectedExpressionsTest extends YaxanaTest
{

   @Test
   public void testFancyRoots()
   {

      {
         // aus NoOfEqualDigitsDouble
         final Algebraic value = new Algebraic("(\\2190-46)-(\\58466-241)");
         assertEquals(-1, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("(\\2+\\3)-\\(\\5*\\5+\\4*\\6)");
         assertEquals(0, value.signum());
         log(value);
      }
   }

   @Test
   public void testOverflows()
   {

      {
         Algebraic value = new Algebraic("0x0.FFFF+0x0.1");
         value = value.sub(value);
         assertEquals(0, value.signum());
         log(value);
      }
   }

   @Test
   public void testShifts()
   {

      {
         final Algebraic value = new Algebraic("0x1FFFFp-16-0x1FFFFp-16");
         assertEquals(0, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("0x1FFFFp-16-0x1FFFEp-16");
         assertEquals(1, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("(0xFFFFp-16+0xFFFFp-16)-0x1FFFFp-16");
         assertEquals(-1, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("1/9-(1-8/9)");
         assertEquals(0, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("0x8001p-32");
         assertEquals(0, value.sub(value).signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("0x8001p-32+0x8001p-64");
         assertEquals(0, value.sub(value).signum());
         log(value);
      }
   }

   @Test
   public void testRootFractions()
   {

      {
         final Algebraic value = new Algebraic("\\262145-512");
         assertEquals(1, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("\\262145-\\262145");
         assertEquals(0, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("\\262145-0");
         assertEquals(1, value.signum());
         log(value);
      }
   }

   @Test
   public void testComposition()
   {

      final Algebraic a998 = new Algebraic("9+(9*9+9)+(9*9*9+2*9*9)+8");
      final Algebraic a999 = new Algebraic("9+(9*9+9)+(9*9*9+2*9*9)+9");
      format("%s = %s\n", a999, a999.approximation(1));
      final Algebraic value = a999.div(a999).sub(a998.div(a999));
      assertEquals(1, value.signum());
   }

   @Test
   public void testOffsets()
   {

      {
         final Algebraic value = new Algebraic(
               "(999_999_999+0.5*\\\\\\2)-(999_999_999+0.5*\\\\\\\\2)");
         assertEquals(1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("(999_999+\\2)-(999_999+\\\\2)");
         assertEquals(1, value.signum());
      }
   }

   @Test
   public void testPowers()
   {

      {
         final Algebraic value = new Algebraic(
               "((\\2_000+\\3_000)^2)^2-((\\(5_000+2*\\6_000_001))^2)^2");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("(\\2-1p-20)-\\2");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("(\\2-(1p-10)*(1p-10))-\\2");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("(\\2-(1p-10)^2)-\\2");
         assertEquals(-1, value.signum());
      }
   }

   @Test
   public void testRationals()
   {

      {
         final Algebraic value = new Algebraic("(1p+100+\\2*1p-100)-1p+100");
         assertEquals(1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("10*(1/10)-1");
         assertEquals(0, value.signum());
      }
      {
         final Algebraic value = new Algebraic("1-10*(1/10)");
         assertEquals(0, value.signum());
      }
      {
         final Algebraic value = new Algebraic("3*(1/3)-1");
         assertEquals(0, value.signum());
      }
      {
         final Algebraic value = new Algebraic("1-3*(1/3)");
         assertEquals(0, value.signum());
      }
   }

   @Test
   public void testBFMSExample()
   {

      {
         final Algebraic value = new Algebraic("root(1p256 + 1, 256)-2");
         assertEquals(1, value.signum());
      }
   }

   @Test
   public void testSmallValues()
   {

      {
         final Algebraic value = new Algebraic("(\\999-\\998)-(\\998-\\997)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "((\\999-\\998)-(\\998-\\997))-((\\999-\\998)-(\\997-\\996))");
         assertEquals(1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "((\\999-\\998)-(\\998-\\997))-((\\999-\\998)-(\\998-\\997))");
         assertEquals(0, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "((\\999p-100-\\998p-100)-(\\998p-100-\\997p-100))-((\\999p-100-\\998p-100)-(\\998p-100-\\997p-100))");
         assertEquals(0, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "((\\999p+100-\\998p+100)-(\\998p+100-\\997p+100))-((\\999p+100-\\998p+100)-(\\998p+100-\\997p+100))");
         assertEquals(0, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "((\\99999-\\99998)-(\\99998-\\99997))-((\\99999-\\99998)-(\\99998-\\99997))");
         assertEquals(0, value.signum());
      }
   }

   @Test
   public void testExtremeDifferences()
   {

      {
         final Algebraic value = new Algebraic(
               "(\\9999999999999999999999-\\9999999999999999999998)-(\\9999999999999999999998-\\9999999999999999999997)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "(\\999999999999999999999999999999999999999999-\\999999999999999999999999999999999999999998)-(\\999999999999999999999999999999999999999998-\\999999999999999999999999999999999999999997)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "((\\9999999999999999999999-\\9999999999999999999998)-(\\9999999999999999999998-\\9999999999999999999997))-((\\9999999999999999999999-\\9999999999999999999998)-(\\9999999999999999999998-\\9999999999999999999996))");
         assertEquals(1, value.signum());
      }
   }

   @Test
   public void testSmallValuesUsingRoots()
   {

      {
         final Algebraic value = TWO.root(1000).sub(ONE);
         assertEquals(1, value.signum());
         log(value);
      }
      {
         final Algebraic value = TWO.sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt()
               .sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt()
               .sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sqrt().sub(ONE);
         assertEquals(1, value.signum());
         log(value);
      }
      {
         final Algebraic value = new Algebraic("(\\8-\\7)-(\\9-\\8)");
         assertEquals(1, value.signum());
         log(value);
      }
   }

   @Test
   public void yaxanasRescue()
   {

      final BigFloat x = new BigFloat(2);
      final BigFloat y = new BigFloat(3);
      final BigFloat ys = y.add(y.mul(BigFloat.twoTo(-132)));
      // format("ys = %s\n", ys);
      final String e1 = String.format("\\%1$s+\\%2$s", x, y);
      final String e2 = String.format("\\(%1$s+2*\\(%1$s*%2$s)+%3$s)", x, y, ys);
      Algebraic E1 = new Algebraic(e1);
      Algebraic E2 = new Algebraic(e2);
      try
      {
         assertTrue(E1.compareTo(E2) < 0);
      }
      catch (final AssertionError e)
      {
         format("EPU failure %s\n", Algebraic.getAlgorithm());
         format("E1 = %s\n", E1);
         format("E2 = %s\n", E2);
         E1 = new Algebraic(e1);
         E2 = new Algebraic(e2);
         E1.compareTo(E2);
         throw e;
      }
   }

   @Test
   public void test1()
   {

      {
         final Algebraic value = new Algebraic(
               "(((\\2p-10+\\3p-10)^2-2p-10-3p-10)/2)^2/2p-10-0x1.2P-9");
         assertEquals(1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "\\(1_000_000_000_000_000_000_000_000p-100000^2)-\\(1_000_000_000_000_000_000_000_001p-100000^2)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "\\(1_000_000_000_000_000_000_000_000p-100000^2)*1p-100000-\\(1_000_000_000_000_000_000_000_001p-100000^2)*1p-100000");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("(\\2+\\3-\\(2+2*\\(3*3)+3))*1p-10000-1p-100000");
         value.approximation(1);
         log(value);
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "(\\20+\\30-\\(20+2*\\(21*30)+30))*1p-10000-1p-100000");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic(
               "(\\200+\\300-\\(200+2*\\(201*300)+300))*1p-10000-1p-100000");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("\\200+\\300-\\(200+2*\\(60001)+300)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("(\\2+\\3-\\(2+2*\\(3*3)+3))/1p+10000-1p-100000");
         assertEquals(-1, value.signum());
      }
   }

   @Test
   public void test2()
   {

      if (false)
      {
         final Algebraic value = new Algebraic("-1*1");
         assertEquals(-1, value.signum());
      }
      if (false)
      {
         final Algebraic value = new Algebraic("-1*0");
         assertEquals(0, value.signum());
      }
      {
         {
            final Algebraic value = new Algebraic("\\(1p-12345*1p-12345*1p-12345)-\\(1p-37034)");
            assertEquals(-1, value.signum());
            format("approx: %s\n", value.approximation().toString(MathContext.DECIMAL64));
            format("msb: %s\n", value.approximation().abs().msb());
         }
         {
            final Algebraic value = new Algebraic(
                  "\\(1p-12345*1p-12345*1p-12345)-\\(1p-12345*1p-12345*1p-12344)");
            assertEquals(-1, value.signum());
            format("approx: %s\n", value.approximation().toString(MathContext.DECIMAL64));
            format("msb: %s\n", value.approximation().abs().msb());
         }
      }
      {
         final Algebraic value = new Algebraic("\\(1p-12345*\\1p-12345)-\\(1p-12345*\\1p-12344)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("\\(1p-12345*1p-12345)-\\(1p-12345*1p-12344)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("\\\\(1p-12345*1p-12345)-\\\\(1p-12345*1p-12344)");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("1p-12345*1p-12345-1p-12345*1p-12344");
         assertEquals(-1, value.signum());
      }
      {
         final Algebraic value = new Algebraic("1p-12345*1p-12345-1p-12345*1p-12345");
         assertEquals(0, value.signum());
      }
   }

   private void log(final Algebraic value)
   {

      format("value     : %s\n", value);
      format("approx dec: %s\n", value.approximation().toString(MathContext.DECIMAL64));
      format("approx hex: %s\n", Double.toHexString(value.doubleValue()).toUpperCase());
      if (value.approximation() != null)
      {
         format("yaxanaPrecision: %s\n", value.yaxanaPrecision());
      }
      format("\n");
   }
}
