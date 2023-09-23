package br.eng.strauss.yaxana;

/**
 * Interface declaring and documenting the supported Operations.
 * 
 * @param <O>
 *           The implementing type.
 * @author Burkhard Strauss
 * @since September 2017
 */
public abstract sealed interface Operation<O extends Operation<O>> permits Parsable
{

   /**
    * Returns a new instance with the value {@code this+that}.
    * 
    * @param that
    *           the other number.
    * @return a new instance.
    */
   public abstract O add(O that);

   /**
    * Returns a new instance with the value {@code this-that}.
    * 
    * @param that
    *           the other number.
    * @return a new instance.
    */
   public abstract O sub(O that);

   /**
    * Returns a new instance with the value {@code this*that}.
    * 
    * @param that
    *           the other number.
    * @return a new instance.
    */
   public abstract O mul(O that);

   /**
    * Returns a new instance with the value {@code this/that}.
    * 
    * @param that
    *           the other number.
    * @return a new instance.
    */
   public abstract O div(O that);

   /**
    * Returns a new instance with the value {@code -this}.
    * 
    * @return a new instance.
    */
   public abstract O neg();

   /**
    * Returns a new instance with the value {@code |this|}.
    * 
    * @return a new instance.
    */
   public abstract O abs();

   /**
    * Returns a new instance with the value {@code this^n}.
    * 
    * @param n
    *           the index.
    * @return a new instance.
    */
   public abstract O pow(int n);

   /**
    * Returns a new instance with the value {@code this^(1/n)}.
    * 
    * @param n
    *           the index.
    * @return a new instance.
    */
   public abstract O root(int n);

   /**
    * Returns {@code this*this}.
    * 
    * @return {@code this*this}.
    */
   public default O sqr()
   {

      return this.pow(2);
   }

   /**
    * Returns {@code sqrt(this)}
    * 
    * @return {@code sqrt(this)}
    */
   public default O sqrt()
   {

      return this.root(2);
   }

   /**
    * Returns the reciprocal value {@code 1 / this}.
    * 
    * @return the reciprocal value {@code 1 / this}.
    */
   public default O inv()
   {

      @SuppressWarnings("unchecked")
      final O s = (O) this;
      return one().div(s);
   }

   /**
    * Returns a new instance with value {@code 1}.
    * 
    * @return a new instance with value {@code 1}.
    */
   public abstract O one();
}
