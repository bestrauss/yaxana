package br.eng.strauss.yaxana.pdc;

import static br.eng.strauss.yaxana.pdc.LdNOverHalfN.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class LdNOverHalfNTest
{

   @Test
   public void test()
   {
      assertEquals(2, valueOf(2));
      assertEquals(3, valueOf(3));
      assertEquals(3, valueOf(4));
      assertEquals(5, valueOf(6));
      assertEquals(7, valueOf(7));
      assertEquals(7, valueOf(8));
      assertEquals(8, valueOf(9));
      assertEquals(8, valueOf(10));
      assertEquals(10, valueOf(11));
      assertEquals(10, valueOf(12));
   }

   @Test
   public void testExceptions()
   {

      try
      {
         LdNOverHalfN.valueOf(0);
         fail();
      }
      catch (final UnreachedException e)
      {
      }
      try
      {
         LdNOverHalfN.valueOf(Robust.MAX_EXPONENT + 1);
         fail();
      }
      catch (final UnreachedException e)
      {
      }
   }
}
