package br.eng.strauss.yaxana.big;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.exc.DivisionByZeroException;

/**
 * @author Burkhard Strauß
 * @since 2023-10
 */
public final class DivisionTest
{

   @Test
   public void testDiv()
   {

      final DivisionByZeroException thrown = assertThrows(DivisionByZeroException.class, () -> {
         final BigFloat thiz = BigFloat.ONE;
         final BigFloat that = BigFloat.ZERO;
         final Rounder rounder = null;
         Divison.div(thiz, that, rounder);
      });
      assertEquals("division by zero", thrown.getMessage());
   }
}
