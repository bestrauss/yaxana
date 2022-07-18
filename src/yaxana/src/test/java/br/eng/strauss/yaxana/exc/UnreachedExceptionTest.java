package br.eng.strauss.yaxana.exc;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class UnreachedExceptionTest extends YaxanaTest
{

   @Test
   public void test()
   {

      try
      {
         throw new UnreachedException();
      }
      catch (final UnreachedException e)
      {
      }
      try
      {
         throw new UnreachedException("message");
      }
      catch (final UnreachedException e)
      {
      }
      try
      {
         throw new UnreachedException(new Exception("cause"));
      }
      catch (final UnreachedException e)
      {
      }
      try
      {
         throw new UnreachedException("message", new Exception("cause"));
      }
      catch (final UnreachedException e)
      {
      }
   }
}
