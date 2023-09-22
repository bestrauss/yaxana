package br.eng.strauss.yaxana.calculator;

import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.compiler.Compiler;
import br.eng.strauss.yaxana.compiler.Function;

/**
 * Java application: Pocket Calculator with Graphic Display.
 * 
 * @author Burkhard Strauss
 * @since September 2017
 */
public class Calculator extends JFrame
{

   /** Serial version identifier. */
   private static final long serialVersionUID = 1L;

   /** The display. */
   private final Display display;

   /** The editor. */
   private final Editor editor;

   /** The result field. */
   private final Result result;

   /** The source code. */
   private String sourceCode;

   /**
    * Returns a new instance.
    */
   public Calculator()
   {

      super();
      setTitle(format("Yaxana Algebraic Calculator"));
      setBounds(new Rectangle(50, 50, 500, 400));
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.display = new Display();
      this.editor = new Editor(this);
      this.result = new Result();
      final JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      mainSplit.add(display);
      final JSplitPane textSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      mainSplit.add(textSplit);
      textSplit.add(new JScrollPane(editor));
      textSplit.add(new JScrollPane(result));
      mainSplit.setResizeWeight(0.5);
      textSplit.setResizeWeight(0.5);
      getContentPane().add(mainSplit, BorderLayout.CENTER);
      pack();
      this.sourceCode = "";
      {
         final String text = """
               epsilon = 0x2P-100
               a = 2
               b = 3
               c = a+(b+epsilon)
               d = a*b
               f = (((x + (\\a + \\b))^2 - c) / 2)^2 - d
               """;
         EventQueue.invokeLater(() -> editor.setText(text));
      }
   }

   /**
    * Called by the {@link Editor} every time the source code changes.
    */
   public void onModification()
   {

      final String sourceCode = this.editor.getText();
      if (!this.sourceCode.equals(sourceCode))
      {
         this.sourceCode = sourceCode;
         try
         {
            final Compiler<Robust> compiler = new Compiler<>(Robust.ZERO, this.sourceCode);
            final Class<Function<Robust>> clasz = compiler.compile(Robust.class);
            final Function<Robust> function = clasz.getConstructor().newInstance();
            display.setFunction(function);
            final Map<String, Robust> map = new TreeMap<>(new Comparator<String>()
            {
               @Override
               public int compare(final String s1, final String s2)
               {
                  final int cmp = s1.toLowerCase().compareTo(s2.toLowerCase());
                  return cmp != 0 ? cmp : s1.compareTo(s2);
               }
            });
            map.put("x", Robust.ZERO);
            function.value(map);
            result.setText(map);
         }
         catch (final NumberFormatException e)
         {
            result.setText(e.toString());
         }
         catch (final Throwable t)
         {
            result.setText(t.toString());
         }
      }
   }

   /**
    * Application entry.
    * 
    * @param args
    *           unused.
    */
   public static void main(final String... args)
   {

      final Calculator calculator = new Calculator();
      calculator.setVisible(true);
      while (calculator.isVisible())
      {
         try
         {
            Thread.sleep(100);
         }
         catch (final InterruptedException e)
         {
         }
      }
   }
}
