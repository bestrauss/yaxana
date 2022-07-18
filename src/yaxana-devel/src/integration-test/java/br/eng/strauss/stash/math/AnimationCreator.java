package br.eng.strauss.stash.math;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Burkhard Strauss
 * @since 25. March 2018
 */
public class AnimationCreator
{

   private final String fileName = "docs/site/pages/images/CircularMotion.gif";

   private final int delay = 20;

   private final int noOfFrames = 340;

   private final int w = 1024;

   private final int h = 768;

   private final double tickLength = 0.05;

   private final Stroke stroke = new BasicStroke(0.02f, BasicStroke.CAP_ROUND,
         BasicStroke.JOIN_ROUND);

   private final Stroke thinStroke = new BasicStroke(0.01f, BasicStroke.CAP_ROUND,
         BasicStroke.JOIN_ROUND);

   private final List<Point2D> list = new ArrayList<>();

   private final List<Point2D> otherList = new ArrayList<>();

   private final boolean otherWheelResting = false;

   private final boolean alwaysDrawCycloid = true;

   public AnimationCreator()
   {

      final GIFEncoder gifenc = new GIFEncoder();
      gifenc.setTransparent(Color.WHITE);
      gifenc.setQuality(20);
      gifenc.setRepeat(0);
      gifenc.start(fileName);
      for (int kFrame = 0; kFrame < noOfFrames; kFrame++)
      {
         System.out.format("frame %3d/%3d\n", kFrame, noOfFrames);
         gifenc.setDelay(kFrame == 0 ? 0 : kFrame == noOfFrames - 1 ? 2000 : delay);
         gifenc.addFrame(createImage(kFrame, noOfFrames));
      }
      gifenc.finish();
      System.out.format("done\n");
   }

   private BufferedImage createImage(final int kFrame, final int noOfFrames)
   {

      double t = kFrame / (double) (noOfFrames - 1);
      t = -0.35 + 1.7 * t;
      return createImage(t);
   }

   private BufferedImage createImage(final double t)
   {

      final BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
      final Graphics2D g = image.createGraphics();
      try
      {
         g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g.setColor(Color.WHITE);
         g.fillRect(0, 0, w, h);
         g.setColor(Color.RED);
         g.fillRect(0, h - 12, w, 4);
         g.setColor(Color.BLUE);
         g.fillRect(0, h - 8, w, 4);
         g.setColor(Color.GREEN);
         g.fillRect(0, h - 4, w, 4);
         final AffineTransform transform = new AffineTransform();
         transform.scale(120, 120);
         transform.translate(1.1, 2.5);
         transform.scale(1, -1);
         g.setTransform(transform);
         {
            final Shape clip = g.getClip();
            g.setClip(new Rectangle2D.Double(-1.2, -0.6, 2 * Math.PI + 2.4, 3.2));
            final Graphics2D g2 = (Graphics2D) g.create();
            try
            {
               paintFrame(g2, t, false, list);
            }
            finally
            {
               g2.dispose();
               g.setClip(clip);
            }
         }
         {
            final Shape clip = g.getClip();
            g.setClip(new Rectangle2D.Double(-1.2, -3.6, 2 * Math.PI + 2.4, 3.2));
            final Graphics2D g2 = (Graphics2D) g.create();
            try
            {
               final double r = 1;
               final double x = 2 * Math.PI * t;
               final double alpha = 2 * Math.PI * t;
               final AffineTransform tx = new AffineTransform();
               if (otherWheelResting)
               { // ungeklärt
                  tx.scale(0.5, 0.5);
                  tx.translate(0, -2.0);
               }
               tx.translate(-x + 2 * Math.PI * r, -3);
               if (otherWheelResting)
               {
                  tx.translate(x, 1);
                  tx.rotate(alpha);
                  tx.translate(-x, -1);
               }
               g2.transform(tx);
               paintFrame(g2, t, true, otherList);
            }
            finally
            {
               g2.dispose();
               g.setClip(clip);
            }
         }
      }
      finally
      {
         g.dispose();
      }
      return image;
   }

   private void paintFrame(final Graphics2D g, final double t, final boolean otherReferenceFrame,
         final List<Point2D> list)
   {

      final double alpha = 2 * Math.PI * t;
      final double r = 1;
      final double x = 2 * Math.PI * t;
      final double y = 1;
      final double xc = x - r * Math.sin(alpha);
      final double yc = y - r * Math.cos(alpha);
      final Shape clip = g.getClip();
      try
      {
         g.setColor(Color.BLACK);
         { // Fahrbahn mit Ticks
            g.setStroke(stroke);
            g.draw(new Line2D.Double(-2.5, 0, 2 * Math.PI + 2.5, 0));
            final double d = 2 * Math.PI / (12 * r);
            final double x0 = -(int) (2 / d + 1) * d;
            g.setStroke(stroke);
            for (double xx = x0; xx <= 2 * Math.PI + 2.5; xx += d)
            {
               final double tx = xx;
               final double ty0 = 0;
               final double ty1 = ty0 - tickLength;
               g.draw(new Line2D.Double(tx, ty0, tx, ty1));
            }
            g.setStroke(thinStroke);
            for (double xx = x0 - 0.6 * d; xx <= 2 * Math.PI + 2.5; xx += 0.2 * d)
            {
               final double tx = xx;
               final double ty0 = 0;
               final double ty1 = ty0 - tickLength;
               g.draw(new Line2D.Double(tx, ty0, tx, ty1));
            }
         }
         { // Rad mit Ticks
            g.setStroke(stroke);
            g.draw(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
            if (!otherReferenceFrame)
            {
               g.draw(new Line2D.Double(x, y, xc, yc)); // Radius
            }
            else
            {
               g.draw(new Line2D.Double(x, y, xc, yc)); // Radius
               // g.draw(new Line2D.Double(x, y, x, 0)); // Radius
            }
            // Radius 1 soll 12 Ticks ergeben
            for (double a = -alpha; a < -alpha + 2 * Math.PI; a += 2 * Math.PI / (12 * r))
            {
               final double tx0 = x + r * Math.cos(a);
               final double ty0 = y + r * Math.sin(a);
               final double tx1 = x + (r - tickLength) * Math.cos(a);
               final double ty1 = y + (r - tickLength) * Math.sin(a);
               g.draw(new Line2D.Double(tx0, ty0, tx1, ty1));
            }
            g.setStroke(thinStroke);
            for (double a = -alpha; a < -alpha + 2 * Math.PI; a += 2 * Math.PI / (60 * r))
            {
               final double tx0 = x + r * Math.cos(a);
               final double ty0 = y + r * Math.sin(a);
               final double tx1 = x + (r - 0.5 * tickLength) * Math.cos(a);
               final double ty1 = y + (r - 0.5 * tickLength) * Math.sin(a);
               g.draw(new Line2D.Double(tx0, ty0, tx1, ty1));
            }
         }
         if (alwaysDrawCycloid || !otherReferenceFrame)
         {
            if (!otherReferenceFrame)
            {
               list.add(new Point2D.Double(xc, yc));
            }
            else
            {
               list.add(new Point2D.Double(xc, yc));
               // list.add(new Point2D.Double(x, 0));
            }
            Point2D q = null;
            for (final Point2D p : list)
            {
               if (q != null)
               {
                  g.setStroke(thinStroke);
                  g.setColor(Color.RED);
                  g.draw(new Line2D.Double(q.getX(), q.getY(), p.getX(), p.getY()));
               }
               q = p;
               final double rr = 0.3 * tickLength;
               g.setColor(Color.RED);
               g.fill(new Ellipse2D.Double(p.getX() - rr, p.getY() - rr, 2 * rr, 2 * rr));
            }
         }
         { // Mikro-Diskus
            double rr = 0.7 * tickLength;
            g.setColor(Color.BLUE);
            g.fill(new Ellipse2D.Double(x - rr, 0 - rr, 2 * rr, 2 * rr));
            final int tProzent = (int) (100 * t + 0.5);
            System.out.format("%d\n", tProzent);
            if (Math.abs(tProzent) <= 1 || Math.abs(tProzent - 100) <= 1)
            {
               rr = 2.5 * tickLength;
               final Area area = new Area(new Ellipse2D.Double(x - rr, 0 - rr, 2 * rr, 2 * rr));
               rr = 1.5 * tickLength;
               area.subtract(new Area(new Ellipse2D.Double(x - rr, 0 - rr, 2 * rr, 2 * rr)));
               g.setColor(Color.GREEN);
               g.fill(area);
            }
         }
         { // Nullpunkt
            final double rr = 0.7 * tickLength;
            g.setColor(Color.GREEN);
            g.fill(new Ellipse2D.Double(0 - rr, 0 - rr, 2 * rr, 2 * rr));
            g.fill(new Ellipse2D.Double(2 * Math.PI * r - rr, 0 - rr, 2 * rr, 2 * rr));
         }
         { // Zahn
            final double rr = 0.7 * tickLength;
            g.setColor(Color.RED);
            g.fill(new Ellipse2D.Double(xc - rr, yc - rr, 2 * rr, 2 * rr));
         }
      }
      finally
      {
         g.setClip(clip);
      }
   }

   public static void main(final String... args)
   {

      try
      {
         new AnimationCreator();
      }
      catch (final Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }
}
