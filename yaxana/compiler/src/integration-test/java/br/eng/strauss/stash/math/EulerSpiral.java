package br.eng.strauss.stash.math;

import static java.awt.AlphaComposite.SRC_OVER;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Erstellt Bilddateien mit Diagramm mit Eulerspirale und Zeitsignalen.
 * 
 * @author Burkhard Strauss
 * @since 2018
 */
public class EulerSpiral
{

   private final String folder = "docs/site/pages/images";

   private final int w = 512;

   private final int h = 512;

   private final int N;

   private final boolean continuous;

   private final boolean limitedFrequency;

   private final int count;

   private final boolean noMean = false;

   private Point2D[] gauss;

   private Point2D[] euler;

   /**
    * Erstellt Bilddatei mit Diagramm mit Eulerspirale und Zeitsignalen.
    *
    * @param N
    *           Parameter der diskreten Eulerspirale.
    * @param continuous
    * 
    * @param limitedFrequency
    */
   public EulerSpiral(final int N, final boolean continuous, final boolean limitedFrequency)
   {

      this.N = N;
      this.continuous = continuous;
      this.limitedFrequency = limitedFrequency;
      this.count = 2 * N * N / (continuous ? 4 : 1);
      createGaussSignal();
      createEulerSignal();
      final BufferedImage image = new BufferedImage(w + 1, h + 1, TYPE_4BYTE_ABGR);
      final Graphics2D g = image.createGraphics();
      try
      {
         g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
         g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
         paintGrid(g);
         final AffineTransform transform = new AffineTransform();
         transform.translate(0, h);
         transform.scale(w, -h);
         g.setTransform(transform);
         g.setStroke(new BasicStroke(0.002f, CAP_ROUND, JOIN_ROUND));
         g.setComposite(AlphaComposite.getInstance(SRC_OVER, 0.5f));
         if (continuous || N <= 64)
         {
            paintGaussSignal(g);
            paintEulerSignal(g);
         }
         paintSpiral(g);
      }
      finally
      {
         g.dispose();
      }
      try
      {
         final String fileName = String
               .format("%s/%s-%c-N=%d%s.png", folder, EulerSpiral.class.getSimpleName(),
                       continuous ? 'C' : 'D', N, limitedFrequency ? "-L" : "");
         final File file = new File(fileName);
         ImageIO.write(image, "PNG", file);
         // Desktop.getDesktop().edit(file);
      }
      catch (final IOException e)
      {
         throw new IllegalStateException("failded to write file", e);
      }
   }

   /**
    * Malt Spirale in der Gaußschen Ebene.
    */
   private void paintSpiral(final Graphics2D g)
   {

      g.setColor(Color.BLUE);
      final double a = (limitedFrequency ? 2d : 1d) / N / sqrt(2);
      // final double a = 1d / N;
      double re = a * sqrt(2) * ((N & 1) != 0 ? 0.5d * N : 0d);
      double im = a * sqrt(2) * ((N & 1) != 0 ? 0.5d * N : 0d);
      final double max = this.count * (limitedFrequency ? 0.25 : 1.0);
      for (int n = 0; n <= max; n++)
      {
         final double px = re;
         final double py = im;
         final double phi = phi(n);
         if (n == 0)
         {
            re += 0.5 * a * cos(phi);
            im += 0.5 * a * sin(phi);
         }
         else
         {
            re += a * cos(phi);
            im += a * sin(phi);
         }
         if (noMean)
         {
            re -= a * 1d / N / sqrt(2d);
            im -= a * 1d / N / sqrt(2d);
         }
         g.draw(new Line2D.Double(px, py, re, im));
      }
   }

   /**
    * Malt Summe über Tschilp, Realteil und Imaginärteil über der Zeit.
    */
   private void paintEulerSignal(final Graphics2D g)
   {

      final double a = sqrt(0.5) * ((continuous | limitedFrequency ? 1d / 2 : 1d / 4) / N);
      final double re0 = 0.50;
      final double im0 = 0.75;
      paintSignal(g, euler, a, re0, im0);
      if (true)
      {
         return;
      }

      double re = 0;
      double im = 0;
      final double max = this.count * (limitedFrequency ? 0.25 : 1.0);
      for (int n = 0; n < max; n++)
      {
         final double prevRe = re;
         final double prevIm = im;
         final double prevX = (n - 1) / max;
         final double x = n / max;
         final double phi = phi(n) / (continuous ? 16 : 1);
         if (n == 0)
         {
            re += 0.5 * a * cos(phi);
            im += 0.5 * a * sin(phi);
         }
         else
         {
            re += a * cos(phi);
            im += a * sin(phi);
         }
         if (noMean)
         {
            re -= a * 1d / N / sqrt(2d);
            im -= a * 1d / N / sqrt(2d);
         }
         g.setColor(Color.GREEN);
         g.draw(new Line2D.Double(prevX, re0 + prevRe, x, re0 + re));
         g.setColor(Color.RED);
         g.draw(new Line2D.Double(prevX, im0 + prevIm, x, im0 + im));
      }
   }

   /**
    * Malt Tschilp, Realteil und Imaginärteil über der Zeit.
    */
   private void paintGaussSignal(final Graphics2D g)
   {

      final double a = (continuous ? 0.50 : 0.25) * 0.125;
      final double re0 = 0.125;
      final double im0 = 0.375;
      paintSignal(g, gauss, a, re0, im0);
   }

   /**
    * Malt Signal, Realteil und Imaginärteil über der Zeit.
    */
   private void paintSignal(final Graphics2D g, final Point2D[] signal, final double a,
         final double re0, final double im0)
   {

      double re = 0d;
      double im = 0d;
      final int count = signal.length;
      for (int n = 0; n < count; n++)
      {
         final double prevRe = re;
         final double prevIm = im;
         final double prevX = (n - 1) / (double) count;
         final double x = n / (double) count;
         re = a * signal[n].getX();
         im = a * signal[n].getY();
         g.setColor(Color.GREEN);
         g.draw(new Line2D.Double(prevX, re0 + prevRe, x, re0 + re));
         g.setColor(Color.RED);
         g.draw(new Line2D.Double(prevX, im0 + prevIm, x, im0 + im));
      }
   }

   /**
    * Malt Gitternetz des Diagramms.
    */
   private void paintGrid(final Graphics2D g)
   {

      g.setColor(new Color(0xF0, 0xF0, 0xF0, 0x00));
      g.fillRect(0, 0, w, h);
      g.setStroke(new BasicStroke(0.0005f, CAP_ROUND, JOIN_ROUND));
      g.setColor(Color.BLACK);
      final double Nx = 4;
      final double dx = w / Nx;
      for (int kx = 0; kx <= Nx; kx++)
      {
         final double x = kx * dx;
         g.draw(new Line2D.Double(x, 0, x, h));
      }
      final double Ny = 4;
      final double dy = w / Ny;
      for (int ky = 0; ky <= Ny; ky++)
      {
         final double y = ky * dy;
         g.draw(new Line2D.Double(0, y, w, y));
      }
      final Font font = g.getFont();
      final FontRenderContext frc = g.getFontRenderContext();
      {
         final String string = "1/div";
         final Rectangle2D r = font.getStringBounds(string, frc);
         final double tw = r.getWidth();
         final double th = r.getHeight();
         final double x = 0.5 * (0.1f * w - tw);
         final double y = h - 0.5 * (0.1f * h - th);
         g.drawString(string, (float) x, (float) y);
      }
      {
         final String string = String.format("N=%d", N);
         final Rectangle2D r = font.getStringBounds(string, frc);
         final double tw = r.getWidth();
         final double th = r.getHeight();
         final double x = 0.5 * (0.1f * w - tw);
         final double y = 0.5 * (0.1f * h - 0 * th);
         g.drawString(string, (float) x, (float) y);
      }
   }

   /**
    * Erstellt Euler-Spirale {@link #euler}.
    */
   private void createEulerSignal()
   {

      final int max = gauss.length;
      euler = new Point2D[max];
      double re = 0;
      double im = 0;
      for (int n = 0; n < max; n++)
      {
         re += gauss[n].getX();
         im += gauss[n].getY();
         euler[n] = new Point2D.Double(re, im);
      }
   }

   /**
    * Erstellt Gauß-Tschilp {@link #gauss}.
    */
   private void createGaussSignal()
   {

      double re = 0;
      double im = 0;
      double count = this.count * (limitedFrequency ? 0.25 : 1.0);
      count = count * (continuous ? 0.25 * sqrt(0.5) : 1d);
      gauss = new Point2D[(int) count];
      for (int n = 0; n < gauss.length; n++)
      {
         final double phi = phi(n);
         if (n == 0)
         {
            re = 0.5 * cos(phi);
            im = 0.5 * sin(phi);
         }
         else
         {
            re = cos(phi);
            im = sin(phi);
         }
         if (noMean)
         {
            re -= sqrt(0.5);
            im -= sqrt(0.5);
         }
         gauss[n] = new Point2D.Double(re, im);
      }
   }

   /**
    * Liefert imaginäres Ärgument für e-Funktion.
    */
   private double phi(final int n)
   {

      final double phi;
      if (limitedFrequency && n > 0.25 * N * N)
      {
         phi = PI / 2 * n;
      }
      else
      {
         phi = PI / N / N * n * n;
      }
      return phi;
   }

   /**
    * Erstellt Bilder.
    */
   public static void main(final String... args)
   {

      new EulerSpiral(7, false, false);
      new EulerSpiral(16, false, false);
      new EulerSpiral(15, false, false);
      new EulerSpiral(32, false, true);
      new EulerSpiral(31, false, false);
      new EulerSpiral(64, true, false);
      new EulerSpiral(256, false, false);
      new EulerSpiral(256, true, false);
   }
}
