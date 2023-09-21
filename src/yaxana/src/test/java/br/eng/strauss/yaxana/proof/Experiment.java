package br.eng.strauss.yaxana.proof;

import static br.eng.strauss.yaxana.epu.Algebraic.ONE;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * Kleinster relativer Unterschied zwischen zwei Gitterpunkten.
 * 
 * @author Burkhard Strauss
 * @since 07-2022
 */
public final class Experiment extends YaxanaTest
{

   @Test
   public void experiment()
   {

      final Algebraic p = new Algebraic("1p-256");
      // final Algebraic a = new Algebraic("12345678901234567890").mul(p).add(ONE);
      // final Algebraic b = new Algebraic("12345678901234567891").mul(p).add(ONE);
      final Algebraic a = new Algebraic("1").mul(p).add(ONE);
      final Algebraic b = new Algebraic("2").mul(p).add(ONE);
      final Algebraic v0 = a.div(b);
      final Algebraic v1 = b.div(a);
      v0.approximation(1);
      v1.approximation(1);
      final int yp0 = v0.yaxanaPrecision();
      final int yp1 = v1.yaxanaPrecision();
      v0.approximation(yp0);
      v1.approximation(yp1);
      format("v0: %s\n", v0.approximation(1));
      format("v1: %s\n", v1.approximation(1));
      format("yp: %s\n", yp0);
      format("yp: %s\n", yp1);
   }
}
