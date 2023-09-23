package br.eng.strauss.yaxana.image;

import static br.eng.strauss.yaxana.Type.TERMINAL;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import br.eng.strauss.yaxana.SyntaxTree;

/**
 * Creates images of abstract syntax trees of expressions.
 * <p>
 * This is intended to be used for documentation purposes, producing images of syntax trees of
 * simple expressions. It is easy to misuse this class to your own, your friends', and your
 * customers' detriment.
 * 
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class Imageifier
{

   /**
    * Returns a new instance.
    *
    * @param value
    *           The expression to be imageified.
    */
   public Imageifier(final SyntaxTree<?> value)
   {

      this.value = value;
      this.stack = new Stack<>();
      this.coors = new TreeMap<>();
      visit(this.value, (v, x, cy, leftPath) -> this.coors.put(x, 0));
      int index = 1;
      for (final Double coor : this.coors.keySet())
      {
         this.coors.put(coor, index++);
      }
      final int[] maxX = new int[1];
      final int[] maxY = new int[1];
      visit(this.value, (v, x, cy, leftPath) -> {
         maxX[0] = max(maxX[0], this.coors.get(x));
         maxY[0] = max(maxY[0], cy);
      });
      this.w = maxX[0];
      this.h = maxY[0];
   }

   /**
    * @return an image of the abstract syntax tree of the expression passed to the constructor.
    */
   public Image toImage()
   {

      return toImage(false);
   }

   /**
    * @param ultimatePolynomialMode
    *           highlights subexpressions right of the path to x, and prints x instead of 0.
    * @return an image of the abstract syntax tree of the expression passed to the constructor.
    */
   public Image toImage(final boolean ultimatePolynomialMode)
   {

      this.ultimatePolynomialMode = ultimatePolynomialMode;
      final int w = (this.w + 1) * DIV_SIZE / 2;
      final int h = this.h * DIV_SIZE;
      final Image image = new Image(w, h);
      final Graphics2D graphics2D = image.createGraphics();
      try
      {
         graphics2D.translate(-0.5 * DIV_SIZE, -0.5 * DIV_SIZE);
         visit(this.value, (value, x, cy, leftPath) -> {
            final int cx = this.coors.get(x);
            if (!this.stack.isEmpty())
            {
               final Point cp = this.stack.peek();
               paintArc(graphics2D, cp.x, cp.y, cx, cy);
            }
            this.stack.push(new Point(cx, cy));
         }, () -> this.stack.pop());
         visit(this.value, (value, x, cy, leftPath) -> {
            final int cx = this.coors.get(x);
            paintNode(graphics2D, value, cx, cy, leftPath);
         });

      }
      finally
      {
         graphics2D.dispose();
      }
      return image;
   }

   private void paintArc(final Graphics2D graphics2D, final int cx0, final int cy0, final int cx1,
         final int cy1)
   {

      final double x0 = cx0 * DIV_SIZE * 0.5 + 0.5 * DIV_SIZE;
      final double y0 = cy0 * DIV_SIZE;
      final double x1 = cx1 * DIV_SIZE * 0.5 + 0.5 * DIV_SIZE;
      final double y1 = cy1 * DIV_SIZE;
      final GeneralPath path = new GeneralPath();
      if (x0 == x1)
      {
         path.moveTo(x0, y0);
         path.lineTo(x1, y1);
      }
      else if (abs(x1 - x0) < abs(y1 - y0))
      {
         path.moveTo(x1, y1);
         path.lineTo(x1, y0 + abs(x1 - x0));
         path.lineTo(x0, y0);
      }
      else
      {
         final double xc = x1 + (x1 < x0 ? y1 - y0 : y0 - y1);
         path.moveTo(x1, y1);
         path.lineTo(xc, y0);
         path.lineTo(x0, y0);
      }
      graphics2D.setColor(COLOR);
      graphics2D.setStroke(new BasicStroke(1f, CAP_ROUND, JOIN_ROUND));
      graphics2D.draw(path);
   }

   private void paintNode(final Graphics2D graphics2D, final SyntaxTree<?> value, final int cx,
         final int cy, final boolean leftPath)
   {

      final double x = cx * DIV_SIZE * 0.5 + 0.5 * DIV_SIZE;
      final double y = cy * DIV_SIZE;
      final boolean isTerminal = value.type() == TERMINAL;
      final Shape shape = isTerminal ? new RoundRectangle2D.Double(x - R, y - R, 2 * R, 2 * R, R, R)
            : new Ellipse2D.Double(x - R, y - R, 2 * R, 2 * R);
      final Color color = this.ultimatePolynomialMode && !leftPath
            ? isTerminal ? TERM_FILL_COLOR_R : NODE_FILL_COLOR_R
            : isTerminal ? TERM_FILL_COLOR : NODE_FILL_COLOR;
      graphics2D.setColor(color);
      graphics2D.fill(shape);
      graphics2D.setColor(COLOR);
      graphics2D.setStroke(new BasicStroke(1f, CAP_ROUND, JOIN_ROUND));
      graphics2D.draw(shape);

      final BufferedImage icon = Icons.get(value);
      if (icon == null)
      {
         String text = value.toString();
         if (this.ultimatePolynomialMode)
         {
            text = text.equals("0") ? "x" : text;
         }
         Font font = Font.decode(FONT_NAME);
         font = font.deriveFont((float) (1.4 * R));
         graphics2D.setFont(font);
         final FontRenderContext frc = graphics2D.getFontRenderContext();
         final Rectangle2D bounds = font.getStringBounds(text, frc);
         final double dx = -0.5 * bounds.getWidth();
         final double dy = +0.38 * bounds.getY();
         final float tx = (float) (x + dx);
         final float ty = (float) (y - dy);
         graphics2D.drawString(text, tx, ty);
      }
      else
      {
         final AffineTransform transform = new AffineTransform();
         transform.translate(x - 0.5 * icon.getWidth(), y - 0.5 * icon.getHeight());
         graphics2D.drawRenderedImage(icon, transform);
      }
   }

   private void visit(final SyntaxTree<?> value, final Visitor visitor)
   {

      visit(value, visitor, null);
   }

   private void visit(final SyntaxTree<?> value, final Visitor preVisitor,
         final Runnable postVisitor)
   {

      visit(0d, 1d, 1, true, value, preVisitor, postVisitor);
   }

   private void visit(final double x, final double d, final int cy, final boolean leftPath,
         final SyntaxTree<?> value, final Visitor preVisitor, final Runnable postVisitor)
   {

      final int y = this.h != null && value.type() == TERMINAL ? this.h : cy;
      preVisitor.visit(value, x, y, leftPath);
      switch (value.type())
      {
         case TERMINAL ->
         {
         }
         case ADD, SUB, MUL, DIV ->
         {
            visit(x - d, 0.5 * d, cy + 1, leftPath, value.left(), preVisitor, postVisitor);
            visit(x + d, 0.5 * d, cy + 1, false, value.right(), preVisitor, postVisitor);
         }
         case POW, ROOT, ABS, NEG ->
         {
            visit(x, 0.5 * d, cy + 1, leftPath, value.left(), preVisitor, postVisitor);
         }
      }
      if (postVisitor != null)
      {
         postVisitor.run();
      }
   }

   private abstract interface Visitor
   {

      public abstract void visit(SyntaxTree<?> value, double x, int cy, boolean leftPath);
   }

   /** The name of the font. */
   protected static final String FONT_NAME = "Tahoma";
   private static final int DIV_SIZE = 48;
   private static final double R = 0.33 * DIV_SIZE;
   /** The size of the icons. */
   protected static final double ICON_SIZE = 1.2 * R;
   private static final Color NODE_FILL_COLOR = new Color(240, 255, 255);
   private static final Color TERM_FILL_COLOR = new Color(228, 242, 242);
   private static final Color NODE_FILL_COLOR_R = new Color(255, 255, 240);
   private static final Color TERM_FILL_COLOR_R = new Color(242, 242, 228);
   private static final Color COLOR = Color.BLACK;
   private final SyntaxTree<?> value;
   private final Map<Double, Integer> coors;
   private final Stack<Point> stack;
   private final Integer w;
   private final Integer h;
   private boolean ultimatePolynomialMode;
}
