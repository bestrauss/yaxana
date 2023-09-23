package br.eng.strauss.yaxana.exc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class DivisionByZeroExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      assertTrue(new DivisionByZeroException() instanceof ArithmeticException);
      assertEquals("division by zero", new DivisionByZeroException().getMessage());
   }
}
