package br.eng.strauss.yaxana.image;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.test.GenerateTestResource;
import br.eng.strauss.yaxana.test.ImageTestResource;
import br.eng.strauss.yaxana.test.YaxanaDevelTest;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class ImageifierTest extends YaxanaDevelTest implements ImageTestResource
{

   @GenerateTestResource(0)
   @Test
   public void test() throws IOException
   {

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
