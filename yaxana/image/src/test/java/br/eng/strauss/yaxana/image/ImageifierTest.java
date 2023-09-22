package br.eng.strauss.yaxana.image;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.test.GenerateTestResource;
import br.eng.strauss.yaxana.test.ImageTestResource;
import br.eng.strauss.yaxana.test.YaxanaImageTest;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class ImageifierTest extends YaxanaImageTest implements ImageTestResource
{

   @GenerateTestResource(0)
   @Test
   public void test() throws IOException
   {

      format("hello, world\n");
      try
      {
         Robusts.setSimplification(false);
         final Robust value = Robust.valueOf("\\2+\\3-\\(2+3+2*\\(2*3))");
         assertImageEquals(file(), new Imageifier(value.toSyntaxTree()).toImage());
      }
      finally
      {
         Robusts.setSimplification(true);
      }
   }
}
