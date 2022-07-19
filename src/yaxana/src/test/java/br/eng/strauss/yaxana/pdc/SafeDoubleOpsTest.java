package br.eng.strauss.yaxana.pdc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class SafeDoubleOpsTest extends YaxanaTest
{

   @Test
   public void test()
   {

      {
         final String desired = "\\2";
         final String actual = Robust.valueOf(desired).toString();
         assertEquals(desired, actual);
      }
      {
         final String desired = "\\2+\\3-\\(5+2*\\7)";
         final String actual = Robust.valueOf(desired).toString();
         assertEquals(desired, actual);
      }
   }
}
