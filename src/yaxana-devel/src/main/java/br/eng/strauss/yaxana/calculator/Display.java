package br.eng.strauss.yaxana.calculator;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;

import br.eng.strauss.yaxana.Robust;

/**
 * Function display.
 * 
 * @author Burkhard Strauss
 * @since September 2017
 */
class Display extends JComponent implements KeyListener, MouseListener, MouseMotionListener,
      MouseWheelListener, ComponentListener
{

   /** Serial version identifier. */
   private static final long serialVersionUID = 1L;

   /** Base color. */
   private static final Color BASECOLOR = new Color(0x22, 0xAA, 0xAA);

   /** Background color. */
   private Color bgColor = Color.WHITE;

   /** Grid color. */
   private Color gridColor = Color.BLACK;

   /** Cathode ray color. */
   private Color rayColor = Color.BLACK;

   private int noOfDivsX = 10;

   private int noOfDivsY = 8;

   private final int wDiv = 40;

   private final int hDiv = 40;

   /** Grid coordinate. */
   private double cx = 0;

   /** Grid coordinate. */
   private double cy = 0;

   /** Grid coordinate. */
   private PerDiv perDivX = new PerDiv();

   /** Grid coordinate. */
   private PerDiv perDivY = new PerDiv();

   private Point mousePressed = null;

   private double pressedCx;

   private double pressedCy;

   /** The function to be draw. */
   private Function<Robust> function;

   /**
    * Returns a new instance.
    */
   public Display()
   {

      super();
      setMinimumSize(new Dimension(wDiv * noOfDivsX, hDiv * noOfDivsY));
      setPreferredSize(new Dimension(wDiv * noOfDivsX, hDiv * noOfDivsY));
      addKeyListener(this);
      addMouseListener(this);
      addMouseMotionListener(this);
      addMouseWheelListener(this);
      addComponentListener(this);
      setFont(new Font("Georgia", Font.BOLD, 12));
      this.function = Function.ZERO;
      toggleColoring();
   }

   @Override
   public void paintComponent(final Graphics graphics)
   {

      final Graphics2D graphics2D = (Graphics2D) graphics;
      graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
      paint(graphics2D);
   }

   private void paint(final Graphics2D graphics2D)
   {

      final Dimension size = getSize();
      final int w = size.width;
      final int h = size.height;
      graphics2D.setColor(bgColor);
      graphics2D.fillRect(0, 0, w, h);
      final AffineTransform originalTransform = graphics2D.getTransform();
      try
      {
         final double dx = perDivX.value();
         final double dy = perDivY.value();
         final double x0 = dx * (int) (cx - noOfDivsX / 2d - 0d);
         final double x1 = dx * (int) (cx + noOfDivsX / 2d + 0d);
         final double y0 = dy * (int) (cy - noOfDivsY / 2d - 0d);
         final double y1 = dy * (int) (cy + noOfDivsY / 2d + 0d);
         final AffineTransform transform = new AffineTransform();
         final double sx = w / (x1 - x0);
         final double sy = h / (y1 - y0);
         transform.scale(sx, -sy);
         transform.translate(-x0, -y1);
         graphics2D.transform(transform);
         paintGrid(graphics2D);
         graphics2D.setTransform(originalTransform);
         paintLabels(graphics2D);
         graphics2D.transform(transform);
         paintRay(graphics2D);
      }
      finally
      {
         graphics2D.setTransform(originalTransform);
      }
   }

   private void paintGrid(final Graphics2D graphics2D)
   {

      final float pw = (float) (0.01 * perDivX.value());
      graphics2D.setStroke(new BasicStroke(pw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      graphics2D.setColor(gridColor);
      final Composite originalComposite = graphics2D.getComposite();
      try
      {
         graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
         final double dx = perDivX.value();
         final double dy = perDivY.value();
         final double x0 = dx * (int) (cx - noOfDivsX / 2d - 0d);
         final double x1 = dx * (int) (cx + noOfDivsX / 2d + 0d);
         final double y0 = dy * (int) (cy - noOfDivsY / 2d - 0d);
         final double y1 = dy * (int) (cy + noOfDivsY / 2d + 0d);
         {
            final int N = (int) ((x1 - x0) / dx + 0.5);
            for (int k = 0; k <= N; k++)
            {
               final double x = x0 + k * dx;
               graphics2D.draw(new Line2D.Double(x, y0, x, y1));
            }
         }
         {
            final int N = (int) ((y1 - y0) / dy + 0.5);
            for (int k = 0; k <= N; k++)
            {
               final double y = y0 + k * dy;
               graphics2D.draw(new Line2D.Double(x0, y, x1, y));
            }
         }
         if (x0 <= 0 && 0 <= x1)
         {
            final double xt = 0.1 * dx;
            final double yt = 0.2 * dy;
            final int N = (int) ((y1 - y0) / yt + 0.5);
            for (int k = 0; k <= N; k++)
            {
               final double y = y0 + k * yt;
               graphics2D.draw(new Line2D.Double(-xt, y, xt, y));
            }
         }
         if (y0 <= 0 && 0 <= y1)
         {
            final double xt = 0.2 * dx;
            final double yt = 0.1 * dy;
            final int N = (int) ((x1 - x0) / xt + 0.5);
            for (int k = 0; k <= N; k++)
            {
               final double x = x0 + k * xt;
               graphics2D.draw(new Line2D.Double(x, -yt, x, yt));
            }
         }
      }
      finally
      {
         graphics2D.setComposite(originalComposite);
      }
   }

   private void paintLabels(final Graphics2D graphics2D)
   {

      final Dimension size = getSize();
      final int w = size.width;
      final int h = size.height;
      final float pw = 1f;
      graphics2D.setStroke(new BasicStroke(pw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      graphics2D.setColor(gridColor);
      final Composite originalComposite = graphics2D.getComposite();
      try
      {
         graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
         final float fontSize = (float) (0.05 * h);
         Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
         // graphics2D.getFont();
         font = font.deriveFont(fontSize);
         font = font.deriveFont(Font.BOLD);
         graphics2D.setFont(font);
         final FontRenderContext frc = graphics2D.getFontRenderContext();
         {
            final String str = "x: " + perDivX.toString();
            final Rectangle2D tr = font.getStringBounds(str, frc);
            final double th = tr.getHeight();
            final double tw = tr.getWidth();
            final float x = (float) (w - tw - 0.3 * th);
            final float y = (float) (h - 0.7 * th);
            graphics2D.drawString(str, x, y);
         }
         {
            final String str = "y: " + perDivY.toString();
            final Rectangle2D tr = font.getStringBounds(str, frc);
            final double th = tr.getHeight();
            final float x = (float) (0.3 * th);
            final float y = (float) (1.3 * th);
            graphics2D.drawString(str, x, y);
         }
      }
      finally
      {
         graphics2D.setComposite(originalComposite);
      }
   }

   private void paintRay(final Graphics2D graphics2D)
   {

      final double dx = perDivX.value();
      final double x0 = dx * (int) (cx - noOfDivsX / 2 - 1d);
      final double x1 = dx * (int) (cx + noOfDivsX / 2 + 1d);
      final float pw = (float) (0.03 * dx);
      graphics2D.setStroke(new BasicStroke(pw, CAP_ROUND, JOIN_ROUND));
      graphics2D.setColor(rayColor);
      final Map<String, Robust> map = new HashMap<>();
      Double px = null;
      Double py = null;
      for (int k = 0, N = 100 * noOfDivsX; k <= N; k++)
      {
         final double x = x0 + k * (x1 - x0) / N;
         map.put("x", Robust.valueOf(x));
         try
         {
            function.value(map);
            final Robust f = map.get("f");
            if (f != null)
            {
               final double y = f.doubleValue();
               if (px != null)
               {
                  graphics2D.draw(new Line2D.Double(px, py, x, y));
               }
               px = x;
               py = y;
            }
            else
            {
               px = null;
               py = null;
            }
         }
         catch (final Exception e)
         {
            px = null;
            py = null;
         }
      }
   }

   public void setFunction(final Function<Robust> function)
   {

      this.function = function;
      repaint();
   }

   @Override
   public void keyPressed(final KeyEvent e)
   {

   }

   @Override
   public void keyReleased(final KeyEvent e)
   {

   }

   @Override
   public void keyTyped(final KeyEvent e)
   {

   }

   @Override
   public void mouseClicked(final MouseEvent event)
   {

      if (event.getClickCount() == 2)
      {
         final Point p = new Point(event.getPoint());
         if (p.x < 48 && p.y < 48)
         {
            toggleColoring();
         }
         else
         {
            perDivX = new PerDiv();
            perDivY = new PerDiv();
            cx = 0;
            cy = 0;
         }
         repaint();
      }
   }

   @Override
   public void mousePressed(final MouseEvent event)
   {

      this.mousePressed = new Point(event.getPoint());
      this.pressedCx = cx;
      this.pressedCy = cy;
   }

   @Override
   public void mouseReleased(final MouseEvent event)
   {

      this.mousePressed = null;
   }

   @Override
   public void mouseEntered(final MouseEvent event)
   {

   }

   @Override
   public void mouseExited(final MouseEvent event)
   {

   }

   @Override
   public void mouseDragged(final MouseEvent event)
   {

      if (mousePressed != null)
      {
         final Dimension size = getSize();
         final double w = size.width;
         final double h = size.height;
         final int wDivs = this.noOfDivsX;
         final int hDivs = this.noOfDivsY;
         final Point p = new Point(event.getPoint());
         final double nx = (p.x - mousePressed.x) / (w / wDivs);
         final double ny = (p.y - mousePressed.y) / (h / hDivs);
         // System.out.format(US, "%f/%f\n", w, h);
         // System.out.format(US, "%d/%d\n", wDivs, hDivs);
         // System.out.format(US, "%f/%f\n", w / wDivs, h / hDivs);
         cx = pressedCx - nx;
         cy = pressedCy + ny;
         repaint();
      }
   }

   @Override
   public void mouseMoved(final MouseEvent event)
   {

   }

   @Override
   public void mouseWheelMoved(final MouseWheelEvent event)
   {

      final int rotation = event.getWheelRotation();
      if (rotation > 0)
      {
         perDivX = perDivX.increment();
         perDivY = perDivY.increment();
      }
      else
      {
         perDivX = perDivX.decrement();
         perDivY = perDivY.decrement();
      }
      repaint();
   }

   private static class PerDiv
   {

      public final int unscaledValue;
      public final int scale;

      public PerDiv()
      {

         unscaledValue = 1;
         scale = 0;
      }

      private PerDiv(final int unscaledValue, final int scale)
      {

         this.unscaledValue = unscaledValue;
         this.scale = scale;
      }

      public double value()
      {

         return unscaledValue * Math.pow(10.0, scale);
      }

      public PerDiv increment()
      {

         switch (unscaledValue)
         {
            default :
               return new PerDiv();
            case 1 :
               return new PerDiv(2, scale);
            case 2 :
               return new PerDiv(5, scale);
            case 5 :
               if (scale < 12)
               {
                  return new PerDiv(1, scale + 1);
               }
               return this;
         }
      }

      public PerDiv decrement()
      {

         switch (unscaledValue)
         {
            default :
               return new PerDiv();
            case 1 :
               if (scale > -12)
               {
                  return new PerDiv(5, scale - 1);
               }
               return this;
            case 2 :
               return new PerDiv(1, scale);
            case 5 :
               return new PerDiv(2, scale);
         }
      }

      @Override
      public String toString()
      {

         if (scale >= 0)
         {
            if (scale < 3)
            {
               return String.format(Locale.US, "%.0f/div", value());
            }
            if (scale < 6)
            {
               return String.format(Locale.US, "%.0fk/div",
                                    unscaledValue * Math.pow(10.0, scale - 3));
            }
            if (scale < 9)
            {
               return String.format(Locale.US, "%.0fM/div",
                                    unscaledValue * Math.pow(10.0, scale - 6));
            }
            if (scale < 12)
            {
               return String.format(Locale.US, "%.0fG/div",
                                    unscaledValue * Math.pow(10.0, scale - 9));
            }
            return String.format(Locale.US, "%.0fT/div",
                                 unscaledValue * Math.pow(10.0, scale - 12));
         }
         else
         {
            if (scale >= -3)
            {
               return String.format(Locale.US, "%.0fm/div",
                                    unscaledValue * Math.pow(10.0, scale + 3));
            }
            if (scale >= -6)
            {
               return String.format(Locale.US, "%.0f\u03BC/div",
                                    unscaledValue * Math.pow(10.0, scale + 6));
            }
            if (scale >= -9)
            {
               return String.format(Locale.US, "%.0fn/div",
                                    unscaledValue * Math.pow(10.0, scale + 9));
            }
            return String.format(Locale.US, "%.0fp/div",
                                 unscaledValue * Math.pow(10.0, scale + 12));
         }
      }
   }

   @Override
   public void componentResized(final ComponentEvent e)
   {

      final Dimension size = getSize();
      final int w = size.width;
      final int h = size.height;
      noOfDivsX = (int) (w / wDiv + 0.5);
      noOfDivsY = (int) (h / hDiv + 0.5);
      repaint();
   }

   @Override
   public void componentMoved(final ComponentEvent e)
   {

   }

   @Override
   public void componentShown(final ComponentEvent e)
   {

   }

   @Override
   public void componentHidden(final ComponentEvent e)
   {

   }

   private void toggleColoring()
   {

      if (bgColor == Color.WHITE)
      {
         bgColor = BASECOLOR.darker();
         gridColor = BASECOLOR.brighter();
         rayColor = gridColor.brighter();
      }
      else
      {
         bgColor = Color.WHITE;
         gridColor = Color.BLACK;
         rayColor = Color.BLACK;
      }
   }
}
