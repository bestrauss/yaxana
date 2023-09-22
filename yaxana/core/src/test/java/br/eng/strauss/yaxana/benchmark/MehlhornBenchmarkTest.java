package br.eng.strauss.yaxana.benchmark;

import static br.eng.strauss.yaxana.tools.YaxanaSettings.STRESS_LEVEL;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.EPU;
import br.eng.strauss.yaxana.rnd.RandomBigFloat;
import br.eng.strauss.yaxana.tools.anno.Benchmark;

/**
 * {@link EPU} benchmark, comparing EPUs.
 * <p>
 * Mehlhorn's test-expression. ZVAA is typically slightly slower if the value vanishes, but
 * depending on values may be ocasionally even slightly faster.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class MehlhornBenchmarkTest extends BenchmarkTest
{

   @Benchmark(1)
   @Test
   public void test()
   {

      run();
   }

   @Override
   protected Robust[] createNewLeftAndRightExpressions()
   {

      Robust robustL = null;
      Robust robustR = null;
      for (int k = 0; k < 1; k++)
      {
         final BigFloat value = randomBigFloat.next().abs().mulTwoTo(-1000);
         final String stringL = String.format("root(%1$s^16, 16)", value);
         final String stringR = String.format("%1$s", value);
         final Robust l = Robust.valueOf(stringL);
         final Robust r = Robust.valueOf(stringR);
         robustL = robustL != null ? robustL.add(l) : l;
         robustR = robustR != null ? robustR.add(r) : r;
      }
      return new Robust[] { robustL, robustR };
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
