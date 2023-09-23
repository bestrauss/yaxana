package br.eng.strauss.yaxana.devel;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.test.GenerateTestResource;
import br.eng.strauss.yaxana.test.StringTestResource;
import br.eng.strauss.yaxana.test.YaxanaResourceTest;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class SampleTest extends YaxanaResourceTest implements StringTestResource
{

   @GenerateTestResource(0)
   @Test
   public void test() throws IOException
   {

      try
      {
         Robusts.setSimplification(false);
         final Robust value = Robust.valueOf("\\2+\\3-\\(2+3+2*\\(2*3))");
         assertStringEquals(file(), value.toString());
      }
      finally
      {
         Robusts.setSimplification(true);
      }
   }
}
