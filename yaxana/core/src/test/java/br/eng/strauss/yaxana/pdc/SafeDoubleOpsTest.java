package br.eng.strauss.yaxana.pdc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

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
