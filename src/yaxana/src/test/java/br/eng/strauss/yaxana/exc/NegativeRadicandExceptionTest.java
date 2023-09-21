package br.eng.strauss.yaxana.exc;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class NegativeRadicandExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      final BigFloat a = new BigFloat(-2);
      try
      {
         a.root(2, Rounder.DOUBLE);
         fail();
      }
      catch (final NegativeRadicandException e)
      {
      }
   }
}
