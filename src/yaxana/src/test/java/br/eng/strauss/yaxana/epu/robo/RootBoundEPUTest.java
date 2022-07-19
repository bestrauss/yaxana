package br.eng.strauss.yaxana.epu.robo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
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

   @Test
   public void testExponent()
   {

      testExponent("\\1", 2);
      testExponent("1", 1);
      testExponent("\\2-\\2", 2);
      testExponent("\\2-\\3", 4);
      testExponent("\\2-(\\3+\\2)", 4);
      testExponent("(\\3+\\2)-(\\3+\\2)", 4);
      testExponent("\\(\\3+\\2)-\\(\\3+\\2)", 8);
   }

   private void testExponent(final String expression, final int exponent)
   {

      try
      {
         Robusts.setSimplification(false);
         final RootBoundEPU epu = new RootBoundEPU();
         final Robust robust = Robust.valueOf(expression);
         final Algebraic a = (Algebraic) robust.toSyntaxTree();
         {
            a.D = Long.MIN_VALUE;
            epu.clearVisitedMarks(a);
            epu.productOfIndices(a);
            assertEquals(exponent, a.D);
         }
         {
            a.D = Long.MIN_VALUE;
            epu.sufficientPrecision(a);
            assertEquals(exponent, a.D);
         }
      }
      finally
      {
         Robusts.setSimplification(true);
      }
   }
}
