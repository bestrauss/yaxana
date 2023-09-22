package br.eng.strauss.yaxana.image;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
import static java.lang.String.format;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

/**
 * A {@link BufferedImage} with additional methods to store and open the image.
 * 
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class Image extends BufferedImage
{

   /**
    * Returns a new instance
    *
    * @param w
    *           the width of the image in pixels.
    * @param h
    *           the height of the image in pixels.
    */
   public Image(final int w, final int h)
   {

      super(w, h, TYPE_4BYTE_ABGR);
   }

   @Override
   public Graphics2D createGraphics()
   {

      final Graphics2D graphics2D = super.createGraphics();
      graphics2D.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
      graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
      graphics2D.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
      graphics2D.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_GASP);
      return graphics2D;
   }

   /**
    * Stores this image in a file, and opens the file in the system's image editor.
    * <p>
    * The name of the file is {@code images/Yaxana-<id>.png} where {@code <id>} is the current value
    * of {@link #IMAGE_ID}.
    * <p>
    * The MIME type of the file is {@code image/png}.
    * 
    * @throws IOException
    *            In case of an I/O-Error.
    */
   public void open() throws IOException
   {

      open(new File(format("images/Yaxana-%d.png", IMAGE_ID.getAndIncrement())));
   }

   /**
    * Stores this image in a given file, and opens the file in the system's image editor.
    * <p>
    * The MIME type of the file is {@code image/png}.
    * 
    * @param file
    *           The file.
    * @throws IOException
    *            In case of an I/O-Error.
    */
   public void open(final File file) throws IOException
   {

      try
      {
         store(file);
      }
      catch (final IOException e)
      {
         final String msg = format("failed to write image to file `%s'", file.toString());
         throw new IllegalStateException(msg);
      }
      try
      {
         Desktop.getDesktop().edit(file);
      }
      catch (final IOException e)
      {
         final String msg = format("failed to open image file `%s' in the system's image editor\n%s",
                                   file.toString(), e.toString());
         throw new IllegalStateException(msg);
      }
   }

   /**
    * Stores this image in a given file.
    * <p>
    * The MIME type of the file is {@code image/png}.
    * 
    * @param file
    *           The file.
    * @throws IOException
    *            In case of an I/O-Error.
    */
   public void store(final File file) throws IOException
   {

      file.getParentFile().mkdirs();
      ImageIO.write(this, "PNG", file);
   }

   /** Identifier for automatically generated image file names. */
   public static final AtomicInteger IMAGE_ID = new AtomicInteger(0);
}
