package br.eng.strauss.yaxana.pdc;

import static br.eng.strauss.yaxana.pdc.Scrutinizer.addIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.divIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.mulIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.rootIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.subIsExact;
import static br.eng.strauss.yaxana.unittesttools.YaxanaSettings.STRESS_LEVEL;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.rnd.RandomAlgebraic;
import br.eng.strauss.yaxana.rnd.RandomBigFloat;
import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * Tests for {@link Approximable} bzw. {@link PDC}.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class ApproximableTest extends YaxanaTest
{

   @Test
   public void test_numbers()
   {

      {
         final BigFloat a = new BigFloat("-0x1.EB560679052D43242B7DEE1D8P-2");
         final BigFloat b = new BigFloat("-106169686486635788906113091812658860519325696");
         testEnsurePrecisionDiv(a.mul(b), b);
      }

      final int loopCount = STRESS_LEVEL > 0 ? STRESS_LEVEL * 10000 : 1000;
      final RandomBigFloat random = new RandomBigFloat(PRECISION, 100);
      for (int k = 0, N = loopCount; k < N; k++)
      {
         final BigFloat a = random.next();
         final BigFloat b = random.next();
         testEnsurePrecisionAdd(a, b);
         testEnsurePrecisionSub(a, b);
         testEnsurePrecisionNeg(a);
         testEnsurePrecisionMul(a, b);
         testEnsurePrecisionDiv(a.mul(b), b);
         testEnsurePrecisionAbs(a);
         testEnsurePrecisionSqrt(a.mul(a));

         testEnsurePrecisionAdd(BigFloat.ZERO, b);
         testEnsurePrecisionSub(BigFloat.ZERO, b);
         testEnsurePrecisionMul(BigFloat.ZERO, b);
         testEnsurePrecisionDiv(BigFloat.ZERO, b);

         testEnsurePrecisionAdd(a, BigFloat.ZERO);
         testEnsurePrecisionSub(a, BigFloat.ZERO);
         testEnsurePrecisionMul(a, BigFloat.ZERO);
      }
      testEnsurePrecisionNeg(BigFloat.ZERO);
      testEnsurePrecisionAbs(BigFloat.ZERO);
      testEnsurePrecisionSqrt(BigFloat.ZERO);
   }

   @Test
   public void test_expressions()
   {

      final int loopCount = STRESS_LEVEL > 0 ? STRESS_LEVEL * 10000 : 1000;
      final RandomAlgebraic random = new RandomAlgebraic(PRECISION, PRECISION, MAX_DEPTH);
      for (int k = 0, N = loopCount; k < N; k++)
      {
         final Algebraic expression = random.next();
         testEnsurePrecision(expression);
      }
   }

   @Test
   public void test_expressionsHighExponent()
   {

      testEnsurePrecision(new Algebraic("-1*0x1.1B4F819C2FF817FF7B6C29CF6708AP-76"));
      testEnsurePrecision(new Algebraic("-1*\\\\0x1.8P-304"));

      testEnsurePrecision(new Algebraic("\\\\0x1.8P-304"));
      testEnsurePrecision(new Algebraic("-\\\\0x1.8P-304"));
      testEnsurePrecision(new Algebraic("-1*\\\\0x1.8P-304"));

      testEnsurePrecision(new Algebraic("|\\(\\0x1.8P-304)|"));
      testEnsurePrecision(new Algebraic("-|\\(\\0x1.8P-304)|"));

      testEnsurePrecision(new Algebraic("|\\(\\0x1.AAAAAP-304)|"));
      testEnsurePrecision(new Algebraic("-|\\(\\0x1.AAAAAP-304)|"));

      testEnsurePrecision(new Algebraic(
            "\\226884673016738511081154451529593575097121243660383079132867374543256772992303104"));

      testEnsurePrecision(new Algebraic(
            "\\0x1.82AEF893B2E33716326B7005A00000000000000000000000000000000000000086E596AF4C3DF9F599AB9937A926D6BE17E98EB6B930AD5FDA2A47FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFAC308B97574A50F2F5A3020CAP+486"));
      testEnsurePrecision(new Algebraic(
            "\\0x1.259350A6DC71E3A04608C24AA240DC4A2F34D6833241AA3161D480A506A92D0C53A563C08A9DEB771057CE88C19FE2C52AF6105D2F17B485E0D7E4E096E9FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA3791AC09B8096D38C3AF986F46F7EB3AA2A72A354D3E2AF2000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000197FD47B59D802BC343D7FA7A5CAD84B9A927CA9812281CFE2P+890"));
      testEnsurePrecision(new Algebraic(
            "(\\226884673016738511081154451529593575097121243660383079132867374543256772992303104+\\0x1.82AEF893B2E33716326B7005A00000000000000000000000000000000000000086E596AF4C3DF9F599AB9937A926D6BE17E98EB6B930AD5FDA2A47FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFAC308B97574A50F2F5A3020CAP+486)*\\0x1.259350A6DC71E3A04608C24AA240DC4A2F34D6833241AA3161D480A506A92D0C53A563C08A9DEB771057CE88C19FE2C52AF6105D2F17B485E0D7E4E096E9FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA3791AC09B8096D38C3AF986F46F7EB3AA2A72A354D3E2AF2000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000197FD47B59D802BC343D7FA7A5CAD84B9A927CA9812281CFE2P+890"));

      testEnsurePrecision(new Algebraic("\\(\\0x1.92FA0E9F5D7C4C36D38964158P-304)"));
      testEnsurePrecision(new Algebraic("\\1P-304"));
      testEnsurePrecision(new Algebraic("\\0x1.92FA0E9F5D7C4C36D38964158P-304"));
      testEnsurePrecision(new Algebraic("\\(\\0x1.92FA0E9F5D7C4C36D38964158P-304)"));
      testEnsurePrecision(new Algebraic("|\\(\\0x1.92FA0E9F5D7C4C36D38964158P-304)|"));
      testEnsurePrecision(new Algebraic("-|\\(\\0x1.92FA0E9F5D7C4C36D38964158P-304)|"));
      testEnsurePrecision(new Algebraic("0x1.68417670A3B3A8F597267B7CP-246"));
      testEnsurePrecision(new Algebraic(
            "0x1.68417670A3B3A8F597267B7CP-246-|\\(\\0x1.92FA0E9F5D7C4C36D38964158P-304)|"));
      testEnsurePrecision(new Algebraic(
            "-1*(0x1.68417670A3B3A8F597267B7CP-246-|\\(\\0x1.92FA0E9F5D7C4C36D38964158P-304)|)"));

      final int loopCount = STRESS_LEVEL > 0 ? STRESS_LEVEL * 10000 : 1000;
      final RandomAlgebraic random = new RandomAlgebraic(PRECISION, 500, MAX_DEPTH);
      for (int k = 0, N = loopCount; k < N; k++)
      {
         final Algebraic expression = random.next();
         testEnsurePrecision(expression);
      }
   }

   @Test
   public void test_example()
   {

      final String lhs = "1P+101";
      final String rhs = "1/(((1-\\\\1P-101*\\\\1P-101*\\\\1P-101*\\\\1P-101)/(1+\\\\1P-101+\\\\1P-101*\\\\1P-101+\\\\1P-101*\\\\1P-101*\\\\1P-101)-1)*((1-\\\\1P-101*\\\\1P-101*\\\\1P-101*\\\\1P-101)/(1+\\\\1P-101+\\\\1P-101*\\\\1P-101+\\\\1P-101*\\\\1P-101*\\\\1P-101)-1)*((1-\\\\1P-101*\\\\1P-101*\\\\1P-101*\\\\1P-101)/(1+\\\\1P-101+\\\\1P-101*\\\\1P-101+\\\\1P-101*\\\\1P-101*\\\\1P-101)-1)*((1-\\\\1P-101*\\\\1P-101*\\\\1P-101*\\\\1P-101)/(1+\\\\1P-101+\\\\1P-101*\\\\1P-101+\\\\1P-101*\\\\1P-101*\\\\1P-101)-1))";
      final Algebraic s = new Algebraic(rhs);
      BigFloat x = s.approximation(102);
      // format("%s\n", x.toHexString());
      x = x.round(new Rounder(101));
      // format("%s\n", x.round(new Rounder(101)));
      assertTrue(x.equals(new BigFloat(lhs)));
   }

   private void testEnsurePrecisionAdd(final BigFloat a, final BigFloat b)
   {

      final BigFloat c = a.add(b);
      assertTrue(addIsExact(a, b, c));
      final Algebraic x = new Algebraic(a);
      final Algebraic y = new Algebraic(b);
      final Algebraic z = x.add(y);
      testEnsurePrecision(c, z);
   }

   private void testEnsurePrecisionSub(final BigFloat a, final BigFloat b)
   {

      final BigFloat c = a.sub(b);
      assertTrue(subIsExact(a, b, c));
      final Algebraic x = new Algebraic(a);
      final Algebraic y = new Algebraic(b);
      final Algebraic z = x.sub(y);
      testEnsurePrecision(c, z);
   }

   private void testEnsurePrecisionNeg(final BigFloat a)
   {

      final BigFloat c = a.neg();
      final Algebraic x = new Algebraic(a);
      final Algebraic z = x.neg();
      testEnsurePrecision(c, z);
   }

   private void testEnsurePrecisionMul(final BigFloat a, final BigFloat b)
   {

      final BigFloat c = a.mul(b);
      assertTrue(mulIsExact(a, b, c));
      final Algebraic x = new Algebraic(a);
      final Algebraic y = new Algebraic(b);
      final Algebraic z = x.mul(y);
      testEnsurePrecision(c, z);
   }

   private void testEnsurePrecisionDiv(final BigFloat a, final BigFloat b)
   {

      final BigFloat c = a.div(b);
      assertTrue(divIsExact(a, b, c));
      final Algebraic x = new Algebraic(a);
      final Algebraic y = new Algebraic(b);
      final Algebraic z = x.div(y);
      testEnsurePrecision(c, z);
   }

   private void testEnsurePrecisionAbs(final BigFloat a)
   {

      final BigFloat c = a.abs();
      final Algebraic x = new Algebraic(a);
      final Algebraic z = x.abs();
      testEnsurePrecision(c, z);
   }

   private void testEnsurePrecisionSqrt(final BigFloat a)
   {

      final BigFloat c = a.sqrt(ROUNDER);
      assertTrue(rootIsExact(a, 2, c));
      final Algebraic x = new Algebraic(a);
      final Algebraic z = x.sqrt();
      testEnsurePrecision(c, z);
   }

   private void testEnsurePrecision(final BigFloat c, final Algebraic z)
   {

      for (int relativePrecision = 1; relativePrecision < PRECISION; relativePrecision++)
      {
         final BigFloat d0 = c;
         final BigFloat d1 = z.approximation(relativePrecision);
         final BigFloat eps = BigFloat.twoTo(-(relativePrecision - 1));
         final BigFloat diff = d0.sub(d1).abs();
         try
         {
            assertTrue(diff.compareTo(eps.mul(c).abs()) <= 0);
         }
         catch (final AssertionError e)
         {
            format("%s\n", eps);
            format("%s\n", c);
            format("%s\n", z.approximation());
            format("%s\n", eps.mul(c).abs());
            throw e;
         }
      }
   }

   private void testEnsurePrecision(final Algebraic z)
   {

      final Algebraic z0 = z;
      final Algebraic z1 = new Algebraic(z);
      final BigFloat d1 = z1.approximation(PRECISION);
      final BigFloat d0 = z0.approximation(3 * PRECISION);
      try
      {
         assertTrue(equal(d0, d1, PRECISION));
      }
      catch (final AssertionError e)
      {
         format("%s\n", z);
         throw e;
      }
   }

   private static boolean equal(final BigFloat d0, final BigFloat d1, final int precision)
   {

      final BigFloat eps = BigFloat.twoTo(-(precision - 1));
      final BigFloat diff = d0.sub(d1).abs();
      if (diff.isZero())
      {
         return true;
      }
      final BigFloat denom = d0.abs();
      if (diff.compareTo(eps.mul(denom)) <= 0)
      {
         return true;
      }
      format("eps:      %s\n", eps);
      format("d0:       %s\n", d0);
      format("d1:       %s\n", d1);
      format("diff:     %s\n", diff);
      format("eps*|d0|: %s\n", eps.mul(denom));
      return false;
   }

   private static final int PRECISION = 100;

   private static final Rounder ROUNDER = new Rounder(3 * PRECISION);

   private static final int MAX_DEPTH = 7;
}
