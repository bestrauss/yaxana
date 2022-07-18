package br.eng.strauss.yaxana.exc;

import static org.junit.Assert.fail;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.big.BigFloat;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class NonTerminatingBinaryExpansionExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      final BigFloat a = new BigFloat(1);
      final BigFloat b = new BigFloat(11);
      try
      {
         a.div(b);
         fail();
      }
      catch (final NonTerminatingBinaryExpansionException e)
      {
      }
   }
}
