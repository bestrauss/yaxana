package br.eng.strauss.yaxana.epu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.anno.WithAlgorithms;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.rnd.RandomBigFloat;

/**
 * Comparison tests of {@link Algebraic} expressions with equals values using all available
 * {@link EPU}s.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class BinomialTest extends YaxanaTest
{

   @WithAlgorithms
   @Test
   public void test()
   {

      long comparisonTimeNs = 0;
      comparisonTimeNs += testBinomial("2", "3");
      comparisonTimeNs += testBinomial("2", "1");
      comparisonTimeNs += testBinomial("2222222222", "3333333333");
      comparisonTimeNs += testBinomial("2", "9");

      final int maxDigits = 20;
      final int maxScale = 0;
      final RandomBigFloat randomBigFloat = new RandomBigFloat(maxDigits, maxScale);
      final int loopCount = STRESS_LEVEL > 0 ? STRESS_LEVEL * 10000 : 5000;
      for (int k = 0, N = loopCount; k < N; k++)
      {
         final BigFloat a = randomBigFloat.next().abs();
         final BigFloat b = randomBigFloat.next().abs();
         comparisonTimeNs += testBinomial(a, b);
      }
      format("%-12s comparison time: %7.2fms\n", Algebraic.getAlgorithm(), 1E-6 * comparisonTimeNs);
   }

   private static long testBinomial(final String sx, final String sy)
   {

      final BigFloat x = new BigFloat(sx);
      final BigFloat y = new BigFloat(sy);
      return testBinomial(x, y);
   }

   private static long testBinomial(final BigFloat x, final BigFloat y)
   {

      long comparisonTimeNs = 0;
      {
         final String e1 = String.format("sqrt(%1$s)+sqrt(%2$s)", x, y);
         final String e2 = String.format("sqrt(%1$s+2*sqrt(%1$s*%2$s)+%2$s)", x, y);
         final Algebraic E1 = new Algebraic(e1);
         final Algebraic E2 = new Algebraic(e2);
         try
         {
            final long now = System.nanoTime();
            assertTrue(E1.compareTo(E2) == 0);
            comparisonTimeNs += System.nanoTime() - now;
         }
         catch (final AssertionError e)
         {
            format("E1 = %s\n", E1);
            format("E2 = %s\n", E2);
            E1.compareTo(E2);
            throw e;
         }
      }
      {
         final BigFloat ys = y.add(y.mul(BigFloat.twoTo(-132)));
         // format("ys = %s\n", ys);
         final String e1 = String.format("\\%1$s+\\%2$s", x, y);
         final String e2 = String.format("\\(%1$s+2*\\(%1$s*%2$s)+%3$s)", x, y, ys);
         Algebraic E1 = new Algebraic(e1);
         Algebraic E2 = new Algebraic(e2);
         try
         {
            final long now = System.nanoTime();
            assertTrue(E1.compareTo(E2) < 0);
            comparisonTimeNs += System.nanoTime() - now;
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
      {
         final BigFloat ys = y.add(y.mul(BigFloat.twoTo(-132)));
         // format("ys = %s\n", ys);
         final String e1 = String.format("\\%1$s+\\%2$s", x, y);
         final String e2 = String.format("\\(%1$s+2*\\(%1$s*%2$s))-%3$s", x, y, ys);
         Algebraic E1 = new Algebraic(e1);
         Algebraic E2 = new Algebraic(e2);
         try
         {
            final long now = System.nanoTime();
            assertTrue(E1.compareTo(E2) > 0);
            comparisonTimeNs += System.nanoTime() - now;
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
      return comparisonTimeNs;
   }
}
