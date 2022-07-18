package br.eng.strauss.yaxana;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * Abstract syntax tree.
 * 
 * @param <S>
 *           The implementing type.
 * @author Burkhard Strauss
 * @since July 2017
 */
public abstract sealed interface SyntaxTree<S extends SyntaxTree<S>> /**/ permits Algebraic
{

   /**
    * Returns the type of this expression.
    * 
    * @return the type of this expression.
    */
   public abstract Type type();

   /**
    * Returns the terminal as a string in case this expressions type is {@link Type#TERMINAL}.
    * 
    * @return the terminal as a string.
    */
   public abstract String terminal();

   /**
    * Returns the terminal as a double in case this expressions type is {@link Type#TERMINAL}.
    * 
    * @return the terminal as a double.
    */
   public abstract double doubleValue();

   /**
    * Returns the index in case this expressions type is {@link Type#POW} or {@link Type#ROOT}.
    * <p>
    * The index is also the right subexpression.
    * 
    * @return the index.
    */
   public abstract int index();

   /**
    * Returns the left subexpression or {@code null}.
    * 
    * @return the left subexpression or {@code null}.
    */
   public abstract S left();

   /**
    * Returns the right subexpression or {@code null}.
    * 
    * @return the right subexpression or {@code null}.
    */
   public abstract S right();
}
