package br.eng.strauss.yaxana.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class ExampleTest extends YaxanaTest
{

   @Test
   public void test()
   {
      {
         final String expression = "\\2+\\3-\\(5+2*\\6)";
         final Robust value = Robust.valueOf(expression);
         format(value);
         assertEquals(0, value.signum());
      }
      try
      {
         final String expression = "\\2+\\3-\\(5+2*\\(6+0x1P-10000))";
         format("%s: %s is illegal, ", Robust.class.getSimpleName(), expression);
         Robust.valueOf(expression);
         fail();
      }
      catch (final ArithmeticException e)
      {
         final String msg = "terminal `0x1p-10000' cannot be represented as a double";
         format("%s\n", msg);
         assertEquals(msg, e.getMessage());
      }
      final Robust two = Robust.valueOf(2);
      final Robust three = Robust.valueOf(3);
      final Robust five = Robust.valueOf(5);
      final Robust six = Robust.valueOf(6);
      {
         final Robust value = two.sqrt().add(three.sqrt())
               .sub(five.add(two.mul(six.sqrt())).sqrt());
         format(value);
         assertEquals(0, value.signum());
      }
      final Robust eps = Robust.valueOf("0x1P-1000");
      {
         final Robust value = two.sqrt().add(three.sqrt())
               .sub(five.add(two.mul(six.add(eps).sqrt())).sqrt());
         format(value);
         assertEquals(-1, value.signum());
      }
   }

   private void format(final Robust value)
   {

      format("%s: %s is in [%s;%s], is ~ %s, sgn: %d\n", Robust.class.getSimpleName(), value,
             value.lowerBound(), value.upperBound(), value.doubleValue(), value.signum());
   }
}
