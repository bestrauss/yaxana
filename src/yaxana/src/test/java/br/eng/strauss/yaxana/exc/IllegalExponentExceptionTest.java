package br.eng.strauss.yaxana.exc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class IllegalExponentExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      final Exception e = new IllegalExponentException(Robust.MAX_EXPONENT + 1);
      assertTrue(e instanceof ArithmeticException);
      assertEquals("illegal exponent (-2047 <= 2048 <= 2047 is false)", e.getMessage());
   }
}
