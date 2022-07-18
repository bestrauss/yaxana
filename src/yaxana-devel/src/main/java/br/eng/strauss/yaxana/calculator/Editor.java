package br.eng.strauss.yaxana.calculator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Text editor.
 * 
 * @author Burkhard Strauss
 * @since September 2017
 */
class Editor extends JTextArea implements DocumentListener
{

   /**
    * Returns a new instance.
    */
   public Editor(final Calculator calculator)
   {

      super();
      this.calculator = calculator;
      setBackground(new Color(0xFFF0F0F0));
      setMinimumSize(new Dimension(200, 80));
      setPreferredSize(new Dimension(400, 120));
      setFont(FONT);
      setBorder(BorderFactory.createTitledBorder("Algebraic expression:"));
      getDocument().addDocumentListener(this);
   }

   @Override
   public void insertUpdate(final DocumentEvent event)
   {

      onModification();
      repaint(0);
   }

   @Override
   public void removeUpdate(final DocumentEvent event)
   {

      onModification();
      repaint(0);
   }

   @Override
   public void changedUpdate(final DocumentEvent event)
   {

      onModification();
      repaint(0);
   }

   private void onModification()
   {
      EventQueue.invokeLater(() -> this.calculator.onModification());
   }

   /** Serial version identifier. */
   private static final long serialVersionUID = 1L;

   /** The font. */
   private final static Font FONT = new Font("Courier New", Font.PLAIN, 13);

   /** The calculator */
   private final Calculator calculator;
}
