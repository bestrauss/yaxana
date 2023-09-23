package br.eng.strauss.yaxana.calculator;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.SyntaxTree;
import br.eng.strauss.yaxana.Type;

/**
 * Result display.
 * 
 * @author Burkhard Strauss
 * @since September 2017
 */
class Result extends JTextArea
{

   /** Serial version identifier. */
   private static final long serialVersionUID = 1L;

   /** The font. */
   private final static Font FONT = new Font("Courier New", Font.PLAIN, 13);

   /**
    * Returns a new instance.
    */
   public Result()
   {

      super();
      setBackground(new Color(0xFFF0F0F0));
      setMinimumSize(new Dimension(200, 80));
      setPreferredSize(new Dimension(400, 180));
      setFont(FONT);
      setBorder(BorderFactory.createTitledBorder("Results:"));
      setEditable(false);
   }

   public void setText(final Map<String, Robust> map)
   {

      try (Formatter f = new Formatter(Locale.US))
      {
         int w = 0;
         for (final String key : map.keySet())
         {
            if (w < key.length())
            {
               w = key.length();
            }
         }
         for (final String key : map.keySet())
         {
            if (!"x".equals(key))
            {
               final Robust robust = map.get(key);
               final SyntaxTree<?> syntaxTree = robust.toSyntaxTree();
               f.format(format("%%-%ds = %%s\n", w), key, robust);
               if (syntaxTree.type() != Type.TERMINAL)
               {
                  f.format(format("%%-%ds \u2248 %%s\n", w), " ", robust.doubleValue());
               }
            }
         }
         setText(f.toString());
      }
   }
}
