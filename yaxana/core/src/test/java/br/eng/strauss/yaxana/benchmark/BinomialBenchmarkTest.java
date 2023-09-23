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
 * ZVAA is shown to be massively faster than BMFSS[2] on expressions with high degree whose values
 * vanish. If the values do not vanish, ZVAA is about 20% slower.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class BinomialBenchmarkTest extends BenchmarkTest
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
      for (int k = 0; k < 2; k++)
      {
         final BigFloat left = randomBigFloat.next().abs();
         final BigFloat rite = randomBigFloat.next().abs();
         final String stringL = String.format("sqrt(%1$s)+sqrt(%2$s)", left, rite);
         final String stringR = String.format("sqrt(%1$s+2*sqrt(%1$s*%2$s)+%2$s)", left, rite);
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

      return STRESS_LEVEL > 0 ? STRESS_LEVEL * 2000 : 1000;
   }

   private final int maxDigits = 20;
   private final int maxScale = 3;
   private final RandomBigFloat randomBigFloat = new RandomBigFloat(maxDigits, maxScale);
}
