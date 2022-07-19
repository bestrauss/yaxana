package br.eng.strauss.yaxana.tools;

import static br.eng.strauss.yaxana.Algorithm.ZVAA;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.anno.WithAlgorithms;
import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class ManualTest extends YaxanaTest
{

   @WithAlgorithms(value = { ZVAA })
   @Test
   public void test()
   {

      {
         final Algebraic v0 = new Algebraic("0x0.101^16");
         final Algebraic v1 = new Algebraic("0x1.107A372D2F74E272CF59171E30781001P-64");
         assertTrue(v0.compareTo(v1) == 0);
      }
      {
         final Algebraic v0 = new Algebraic("root(0x1.107A372D2F74E272CF59171E30781001P-64,16)");
         v0.approximation(124);
         System.out.format("v0 = %s\n", v0.approximation().toString());
         final Algebraic v1 = new Algebraic("0x0.101");
         assertTrue(v0.compareTo(v1) == 0);
      }
      try
      {
         // 0x1.05A105D8^16
         Robusts.setSimplification(false);
         final Robust r0 = Robust.valueOf("root(0x0.101^16, 16)");
         final Robust r1 = Robust.valueOf("0x0.101");
         {
            final Algebraic a = new Algebraic("0x0.101^16");
            a.approximation(124);
            System.out.format("a = %s\n", a.approximation().toString());
         }
         {
            final Algebraic a = (Algebraic) r0.sub(r1).toSyntaxTree();
            a.ensureApproximationNonZero();
            System.out.format("a = %s\n", a.approximation().toString());
         }
         assertTrue(r0.isEqualTo(r1));
      }
      finally
      {
         Robusts.setSimplification(true);
      }
   }
}
