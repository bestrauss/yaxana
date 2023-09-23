package br.eng.strauss.yaxana.exc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class NonPositiveLogarithmArgumentExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      assertTrue(new NonPositiveLogarithmArgumentException() instanceof ArithmeticException);
      assertEquals("log(x) is not defined for x <= 0",
                   new NonPositiveLogarithmArgumentException().getMessage());
   }
}
