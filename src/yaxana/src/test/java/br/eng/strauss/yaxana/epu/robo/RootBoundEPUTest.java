package br.eng.strauss.yaxana.epu.robo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.tools.SampleAlgebraic;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class RootBoundEPUTest extends YaxanaTest
{

   @Test
   public void test1()
   {

      final int N = 3;
      final Algebraic q = new Algebraic("50000000p-12").sqrt();
      final Algebraic value = SampleAlgebraic.geometricSeries(q, N);
      final RootBoundEPU epu = new RootBoundEPU();
      assertEquals(0, epu.signum(value));
   }

   @Test
   public void test2()
   {

      final int N = 3;
      final Algebraic q = new Algebraic("1237251p-1").sqrt();
      final Algebraic value = SampleAlgebraic.geometricSeries(q, N);
      final RootBoundEPU epu = new RootBoundEPU();
      assertEquals(0, epu.signum(value));
   }

   @Test
   public void testToString()
   {

      final RootBoundEPU epu = new RootBoundEPU();
      assertEquals("RootBoundEPU:\n  -no operand-", epu.toString());
      epu.signum(new Algebraic("\\2"));
      assertEquals("RootBoundEPU:\n  \\2", epu.toString());
      epu.signum(new Algebraic("\\3"));
      assertEquals("RootBoundEPU:\n  \\3", epu.toString());
   }
}
