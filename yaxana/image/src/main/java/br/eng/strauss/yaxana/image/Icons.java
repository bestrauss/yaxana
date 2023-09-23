package br.eng.strauss.yaxana.image;

import static br.eng.strauss.yaxana.Type.POW;
import static br.eng.strauss.yaxana.Type.ROOT;
import static br.eng.strauss.yaxana.Type.TERMINAL;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import br.eng.strauss.yaxana.SyntaxTree;
import br.eng.strauss.yaxana.Type;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
final class Icons
{

   public static BufferedImage get(final SyntaxTree<?> value)
   {

      final Type type = value.type();
      if (type == POW || type == ROOT)
      {
         final String key = type.toString() + value.index();
         BufferedImage icon = SCALED_ICONS.get(key);
         if (icon == null)
         {
            SCALED_ICONS.put(key, icon = newRootOrPowIcon(value));
         }
         return icon;
      }
      else
      {
         return SCALED_ICONS.get(type.toString());
      }
   }

   private static BufferedImage newRootOrPowIcon(final SyntaxTree<?> value)
   {

      final Type type = value.type();
      final BufferedImage template = ICONS.get(type);
      final int w = template.getWidth();
      final int h = template.getHeight();
      final BufferedImage icon = new Image(w, h);
      final Graphics2D graphics2D = icon.createGraphics();
      try
      {
         final String text = String.valueOf(value.index());
         graphics2D.drawRenderedImage(template, new AffineTransform());
         Font font = Font.decode(Imageifier.FONT_NAME);
         font = font.deriveFont(90f);
         final float tx = type == ROOT ? 16 : 119;
         final float ty = type == ROOT ? 81 : 65;
         graphics2D.setColor(Color.BLACK);
         graphics2D.setFont(font);
         graphics2D.drawString(text, tx, ty);
      }
      finally
      {
         graphics2D.dispose();
      }
      return scale(icon);
   }

   private static Map<Type, BufferedImage> icons()
   {

      try
      {
         final Map<Type, BufferedImage> icons = new HashMap<>();
         for (final Type type : Type.values())
         {
            if (type != TERMINAL)
            {
               final InputStream stream = Imageifier.class
                     .getResourceAsStream(type.name() + ".png");
               final BufferedImage image = ImageIO.read(stream);
               Icons.makeTransparent(image, Color.WHITE);
               icons.put(type, image);
            }
         }
         return icons;
      }
      catch (final IOException e)
      {
         throw new Error("corrupt resource: " + Imageifier.class.getName(), e);
      }
   }

   private static Map<String, BufferedImage> scaledIcons()
   {

      final Map<String, BufferedImage> icons = new HashMap<>();
      for (final Type type : ICONS.keySet())
      {
         BufferedImage icon = ICONS.get(type);
         icon = scale(icon);
         icons.put(type.toString(), icon);
      }
      return icons;
   }

   private static void makeTransparent(final BufferedImage image, final Color transparentColor)
   {

      final int color = transparentColor.getRGB() & 0x00FFFFFF;
      final int w = image.getWidth();
      final int h = image.getHeight();
      for (int y = 0; y < h; y++)
      {
         for (int x = 0; x < w; x++)
         {
            final int rgb = image.getRGB(x, y);
            if ((rgb & 0xFFFFFF) == color)
            {
               image.setRGB(x, y, rgb & 0xFFFFFF);
            }
         }
      }
   }

   private static BufferedImage scale(final BufferedImage icon)
   {

      double scale = Imageifier.ICON_SIZE / icon.getWidth();
      BufferedImage scaledIcon = icon;
      while (scale < 0.5)
      {
         scaledIcon = scaleInternal(scaledIcon, 0.5);
         scale *= 2;
      }
      if (scale < 1d)
      {
         scaledIcon = scaleInternal(scaledIcon, scale);
      }
      return scaledIcon;
   }

   private static BufferedImage scaleInternal(final BufferedImage icon, final double scale)
   {

      final int w = (int) (icon.getWidth() * scale);
      final int h = (int) (icon.getHeight() * scale);
      final BufferedImage scaledIcon = new BufferedImage(w, h, TYPE_4BYTE_ABGR);
      final Graphics2D graphics2D = scaledIcon.createGraphics();
      try
      {
         graphics2D.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
         graphics2D.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
         graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
         graphics2D.scale(scale, scale);
         graphics2D.drawRenderedImage(icon, new AffineTransform());
      }
      finally
      {
         graphics2D.dispose();
      }
      return scaledIcon;
   }

   private Icons()
   {
   }

   private static Map<Type, BufferedImage> ICONS = icons();

   private static Map<String, BufferedImage> SCALED_ICONS = scaledIcons();
}
