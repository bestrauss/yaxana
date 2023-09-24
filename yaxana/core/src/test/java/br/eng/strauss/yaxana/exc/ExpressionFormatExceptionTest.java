package br.eng.strauss.yaxana.exc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class ExpressionFormatExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      assertTrue(new ExpressionFormatException("") instanceof NumberFormatException);
      assertEquals("\\123", new ExpressionFormatException("\\123").getMessage());
   }
}
