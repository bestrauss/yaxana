package br.eng.strauss.yaxana.exc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class ExponentUnderflowExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {
      assertTrue(new ExponentUnderflowException() instanceof ArithmeticException);
      assertEquals("exponent underflow", new ExponentUnderflowException().getMessage());
   }
}
