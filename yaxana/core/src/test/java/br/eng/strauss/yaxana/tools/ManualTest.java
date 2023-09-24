package br.eng.strauss.yaxana.tools;

import static br.eng.strauss.yaxana.Algorithm.MOSC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class ManualTest extends YaxanaTest
{

   /** Test for transient experiments. */
   @WithAlgorithms({ MOSC })
   @Test
   public void test()
   {

      final int exponent = 46;
      final BigFloat base = BigFloat.TWO;
      final String format = "root(%s^%s-1, %s)-%s";
      final String string = String.format(format, base, exponent, exponent, base);
      final Robust value = Robust.valueOf(string);
      assertEquals(-1, value.signum());
      Robusts.printCache("", null);
   }

   @BeforeAll
   public static void beforeAll()
   {
   }
}
