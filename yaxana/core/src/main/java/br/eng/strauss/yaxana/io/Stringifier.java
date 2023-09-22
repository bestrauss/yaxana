package br.eng.strauss.yaxana.io;

import br.eng.strauss.yaxana.SyntaxTree;
import br.eng.strauss.yaxana.Type;

/**
 * Expression stringifier.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class Stringifier<S extends SyntaxTree<S>>
{

   /**
    * Returns a new instance.
    */
   public Stringifier()
   {

   }

   /**
    * Returns the string-representation of a given stringifiable object.
    * <p>
    * The expression syntax generated is described on the project documentation pages.
    * </p>
    * 
    * @param stringifiable
    *           the stringifiable object.
    * @return the string-representation of the stringifiable object.
    */
   public String stringify(final SyntaxTree<S> stringifiable)
   {

      final StringBuffer sb = new StringBuffer();
      append(sb, stringifiable, null);
      return sb.toString();
   }

   /**
    * Helper method for {@link #stringify(Stringifiable)}.
    * 
    * @param sb
    *           ...
    * @param a
    *           ...
    * @param parent
    *           ...
    */
   private void append(final StringBuffer sb, final SyntaxTree<?> a, final SyntaxTree<?> parent)
   {

      switch (a.type())
      {
         case TERMINAL :
            sb.append(a.terminal());
            break;
         case ADD :
            append(sb, a.left(), a);
            sb.append("+");
            append(sb, a.right(), a, a.right().type() == Type.ADD || a.right().type() == Type.SUB);
            break;
         case SUB :
         {
            append(sb, a.left(), a);
            sb.append("-");
            append(sb, a.right(), a, a.right().type() == Type.ADD || a.right().type() == Type.SUB);
            break;
         }
         case MUL :
         {
            append(sb, a.left(), a, a.left().type() == Type.ADD || a.left().type() == Type.SUB);
            sb.append("*");
            append(sb, a.right(), a, a.right().type() == Type.ADD || a.right().type() == Type.SUB);
            break;
         }
         case DIV :
         {
            append(sb, a.left(), a, a.left().type() == Type.ADD || a.left().type() == Type.SUB);
            sb.append("/");
            append(sb, a.right(), a, a.right().type() == Type.ADD || a.right().type() == Type.SUB
                  || a.right().type() == Type.MUL || a.right().type() == Type.DIV);
            break;
         }
         case POW :
         {
            final boolean parens = a.left().type() != Type.TERMINAL && a.left().type() != Type.ABS
                  && a.left().type() != Type.ROOT
                  || a.left().type() == Type.TERMINAL && a.left().terminal().startsWith("-");
            append(sb, a.left(), a, parens);
            sb.append("^");
            sb.append(a.index());
            break;
         }
         case ROOT :
            if (a.index() == 2)
            {
               sb.append("\\");
               append(sb, a.left(), a,
                      a.left().type() != Type.TERMINAL && a.left().type() != Type.ROOT
                            && a.left().type() != Type.POW && a.left().type() != Type.ABS);
            }
            else
            {
               sb.append("root(");
               append(sb, a.left(), a);
               sb.append(", ");
               append(sb, a.right(), a);
               sb.append(")");
            }
            break;
         case ABS :
            sb.append("|");
            append(sb, a.left(), a);
            sb.append("|");
            break;
         case NEG :
            sb.append("-");
            append(sb, a.left(), a, a.left().type() == Type.ADD || a.left().type() == Type.SUB
                  || a.left().type() == Type.MUL || a.left().type() == Type.DIV);
            break;
      }
   }

   /**
    * Helper method for {@link #append(StringBuffer, Stringifiable, Stringifiable)}.
    * 
    * @param sb
    *           ...
    * @param a
    *           ...
    * @param parent
    *           ...
    * @param parentheses
    *           ...
    */
   private void append(final StringBuffer sb, final SyntaxTree<?> a, final SyntaxTree<?> parent,
         final boolean parentheses)
   {

      if (parentheses)
      {
         sb.append("(");
      }
      append(sb, a, parent);
      if (parentheses)
      {
         sb.append(")");
      }
   }
}
