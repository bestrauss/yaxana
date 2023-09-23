package br.eng.strauss.yaxana.test;

import static br.eng.strauss.yaxana.test.TestTools.getAnnotationOnTestClassOrMethod;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * @author Burkhard Strauߟ
 * @since 04-2022
 */
public abstract interface ImageTestResource extends ResourceBase
{

   public static final boolean MAKE_ALL = false;

   public default void assertImageEquals(final File desiredFile, final BufferedImage actualImage)
         throws IOException
   {

      final GenerateTestResource annotation = getAnnotationOnTestClassOrMethod(GenerateTestResource.class);
      if (MAKE_ALL || annotation != null && annotation.value() != 0)
      {
         Private.make(desiredFile, actualImage);
      }
      else
      {
         Private.test(desiredFile, actualImage);
      }
   }

   @Override
   public default String extension()
   {

      return "png";
   }

   public static class Private
   {
      private static void make(final File desiredFile, final BufferedImage actualImage)
            throws IOException
      {

         ImageIO.write(actualImage, FORMAT, desiredFile);
         Desktop.getDesktop().edit(desiredFile);
      }

      private static void test(final File desiredFile, final BufferedImage actualImage)
            throws IOException
      {

         final BufferedImage desiredImage = ImageIO.read(desiredFile);
         final byte[] desiredByteArray = toByteArray(desiredImage);
         final byte[] actualByteArray = toByteArray(actualImage);
         assert Arrays.equals(desiredByteArray, actualByteArray);
      }

      private static byte[] toByteArray(final BufferedImage image) throws IOException
      {

         final ByteArrayOutputStream stream = new ByteArrayOutputStream();
         ImageIO.write(image, FORMAT, stream);
         return stream.toByteArray();
      }

      private static final String FORMAT = "PNG";
   }
}
