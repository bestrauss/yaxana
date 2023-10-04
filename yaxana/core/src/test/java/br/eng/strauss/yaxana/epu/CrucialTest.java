package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauß
 * @since 2023-09
 */
public final class CrucialTest extends YaxanaTest
{

   @WithAlgorithms({ Algorithm.BFMSS2, Algorithm.YAXANA, Algorithm.BFMSS2, Algorithm.YAXANA })
   @Test
   public void testRootOfPow()
   {

      final int exp = 64;
      {
         final String expression = String.format("root(2^%s+1P-100, %s)-2", exp, exp);
         final Algebraic algebraic = new Algebraic(expression);
         final int[] precision = new int[] { -1 };
         final int signum = algebraic.signum(prec -> precision[0] = prec);
         format("%s: sign: %s: prec: %s\n", algebraic, signum, precision[0]);
         assertEquals(1, signum);
      }
      {
         final String expression = String.format("root(2^%s-1P-100, %s)-2", exp, exp);
         final Algebraic algebraic = new Algebraic(expression);
         final int[] precision = new int[] { -1 };
         final int signum = algebraic.signum(prec -> precision[0] = prec);
         format("%s: sign: %s: prec: %s\n", algebraic, signum, precision[0]);
         assertEquals(-1, signum);
      }
      {
         final String expression = String.format("root(2^%s, %s)-2", exp, exp);
         final Algebraic algebraic = new Algebraic(expression);
         final int[] precision = new int[] { -1 };
         final int signum = algebraic.signum(prec -> precision[0] = prec);
         format("%s: sign: %s: prec: %s\n", algebraic, signum, precision[0]);
         assertEquals(0, signum);
      }
   }

   @WithAlgorithms
   @Test
   public void testBinomial()
   {

      // BFMSS: prec = 4041
      // MOSC: prec = 507
      // benötigt: prec = 833
      {
         final String string = "\\2+\\3-\\(5+2*\\(6+1P-1000))";
         final Robust value = Robust.valueOf(string);
         assertEquals(-1, value.signum());
      }
      {
         final String string = "\\(5+2*\\(6+1P-1000))-(\\2+\\3)";
         final Robust value = Robust.valueOf(string);
         assertEquals(1, value.signum());
      }
      {
         final String string = "\\(5+2*\\6)-(\\2+\\3)";
         final Robust value = Robust.valueOf(string);
         assertEquals(0, value.signum());
      }
   }
}
