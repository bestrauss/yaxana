package br.eng.strauss.yaxana.pdc;

import static br.eng.strauss.yaxana.big.BigFloat.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class PrecisionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      { // fE0
         final String formula = "(0+(1-\\a*\\a*\\a*\\a)/(1-\\a)-\\a*\\a*\\a-\\a*\\a-1)^2-a";
         final String expression = formula.replace("a", "1P-1000");
         final Algebraic value = new Algebraic(expression);
         assertEquals(ZERO, value.approximation(1_000_000));
      }
      { // N0
         final String formula = "1+\\a+\\a*\\a+\\a*\\a*\\a-(1-\\a*\\a*\\a*\\a)/(1-\\a)";
         final String expression = formula.replace("a", "1P-1000");
         final Algebraic value = new Algebraic(expression);
         assertEquals(ZERO, value.approximation(1_000_000));
      }
      { // N1
         final String formula = "1-\\a+\\a*\\a+\\a*\\a*\\a-(1-\\a*\\a*\\a*\\a)/(1-\\a)";
         final String expression = formula.replace("a", "1P-1000");
         final Algebraic value = new Algebraic(expression);
         value.approximation(1000);
         assertEquals(1000, value.precision());
         assertEquals(new BigFloat("-1P-499"), value.approximation());
      }
      { // N1
         final String formula = "1-\\a+\\a*\\a+\\a*\\a*\\a-(1-\\a*\\a*\\a*\\a)/(1-\\a)";
         final String expression = formula.replace("a", "1P-1000");
         final Algebraic value = new Algebraic(expression);
         value.approximation(1000);
         assertEquals(1000, value.precision());
         assertEquals(new BigFloat("-1P-499"), value.approximation());
      }
   }

   @Test
   public void testHarder()
   {

      String expression = """
             (0+(1-\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a)/(1-\\a)
             -\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a
             -\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a
             -\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a
             -\\a*\\a*\\a*\\a*\\a*\\a*\\a*\\a
             -\\a*\\a*\\a*\\a*\\a*\\a*\\a
             -\\a*\\a*\\a*\\a*\\a*\\a
             -\\a*\\a*\\a*\\a*\\a
             -\\a*\\a*\\a*\\a
             -\\a*\\a*\\a
             -\\a*\\a
             -1)^2-a
            """;
      expression = expression.replace("a", "0x1.2E103P-33");
      final Algebraic value = new Algebraic(expression);
      value.approximation(256);
      assertEquals(256, value.precision());
      assertTrue(value.approximation().abs().compareTo(BigFloat.twoTo(256)) < 0);
   }
}
