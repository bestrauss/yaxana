package br.eng.strauss.yaxana.exc;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.pdc.PDCTools;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class PrecisionOverflowExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      final Algebraic a = Algebraic.ONE;
      final int precision = (int) PrecisionOverflowException.MAX_PRECISION;
      try
      {
         PDCTools.increment(a, precision);
         fail();
      }
      catch (final PrecisionOverflowException e)
      {
      }
   }
}
