package br.eng.strauss.yaxana;

/**
 * Interface aggregating capabilities.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
public abstract /* sealed */ interface Expression<E extends Expression<E>>
      extends Parsable<E>, Comparison<E> /* permits Algebraic,Robust */
{

   /**
    * Returns the type of this expression.
    * 
    * @return the type of this expression.
    */
   public abstract Type type();
}
