package br.eng.strauss.yaxana.benchmark;

import static br.eng.strauss.yaxana.unittest.YaxanaSettings.STRESS_LEVEL;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.EPU;
import br.eng.strauss.yaxana.rnd.RandomBigFloat;
import br.eng.strauss.yaxana.unittest.Benchmark;

/**
 * {@link EPU} benchmark, comparing EPUs.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class RootOfPowMinusOneBenchmarkTest extends BenchmarkTest
{

   @Benchmark(1)
   @Test
   public void test()
   {

      run();
   }

   @Override
   protected Robust[] createNewLeftAndRightExpressions(final Robust epsilon)
   {

      try
      {
         Robusts.setSimplification(false);
         Robust robustL = null;
         Robust robustR = null;
         for (int k = 0; k < 1; k++)
         {
            final int exponent = 20;
            final BigFloat base = randomBigFloat.next().abs().mulTwoTo(-1000);
            final String format = "root(%s^%s+%s, %s)";
            final String stringL = String.format(format, base, exponent, epsilon, exponent);
            final String stringR = String.format("%s", base);
            final Robust l = Robust.valueOf(stringL);
            final Robust r = Robust.valueOf(stringR);
            robustL = robustL != null ? robustL.add(l) : l;
            robustR = robustR != null ? robustR.add(r) : r;
         }
         return new Robust[] { robustL, robustR };
      }
      finally
      {
         Robusts.setSimplification(true);
      }
   }

   @Override
   protected Robust[] createEpsilons()
   {

      return new Robust[] { Robust.ZERO, Robust.ONE };
   }

   @Override
   protected int getLoopCount()
   {

      return STRESS_LEVEL > 0 ? STRESS_LEVEL * 40 : 20;
   }

   private final int maxDigits = 30;
   private final int maxScale = 0;
   private final RandomBigFloat randomBigFloat = new RandomBigFloat(maxDigits, maxScale);
}
