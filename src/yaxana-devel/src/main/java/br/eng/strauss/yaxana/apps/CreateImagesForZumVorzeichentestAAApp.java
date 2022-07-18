package br.eng.strauss.yaxana.apps;

import java.io.File;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.SyntaxTree;
import br.eng.strauss.yaxana.image.Imageifier;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class CreateImagesForZumVorzeichentestAAApp
{

   /**
    * Creates images.
    * 
    * @param args
    *           ignored.
    */
   public static void main(final String... args)
   {

      try
      {
         final String folder = "../zum-vorzeichentest-algebraischer-ausdruecke/images/";
         final String fileName0 = folder + "E.png";
         final SyntaxTree<?> syntaxTree = Robust.valueOf("\\2+\\3-\\(5+2*\\6)").toSyntaxTree();
         new Imageifier(syntaxTree).toImage().open(new File(fileName0));
      }
      catch (final Exception e)
      {
         e.printStackTrace();
      }
   }
}
