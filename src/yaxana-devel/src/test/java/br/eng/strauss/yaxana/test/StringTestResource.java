package br.eng.strauss.yaxana.test;

import static br.eng.strauss.yaxana.test.TestTools.getAnnotationOnTestClassOrMethod;
import static org.junit.Assert.assertEquals;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * @author Burkhard Strauߟ
 * @since 04-2022
 */
public abstract interface StringTestResource extends ResourceBase
{

   public static final boolean MAKE_ALL = false;

   public default void assertImageEquals(final File desiredFile, final String actualString)
         throws IOException
   {

      final GenerateTestResource annotation = getAnnotationOnTestClassOrMethod(GenerateTestResource.class);
      if (MAKE_ALL || annotation != null && annotation.value() != 0)
      {
         Private.make(desiredFile, actualString);
      }
      else
      {
         Private.test(desiredFile, actualString);
      }
   }

   @Override
   public default String extension()
   {

      return "txt";
   }

   public static class Private
   {

      private static void make(final File desiredFile, final String actualString) throws IOException
      {

         Files.writeString(desiredFile.toPath(), actualString, StandardOpenOption.CREATE);
         Desktop.getDesktop().open(desiredFile);
      }

      private static void test(final File desiredFile, final String actualString) throws IOException
      {

         final String desired = Files.readString(desiredFile.toPath());
         assertEquals(desired, actualString);
      }
   }
}
