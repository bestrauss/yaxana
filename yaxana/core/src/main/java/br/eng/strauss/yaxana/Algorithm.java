package br.eng.strauss.yaxana;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauss
 * @since 07-2022
 */
public enum Algorithm {

   /** Pion, Sylvain and Yap, Chee: Constructive root bound for k-ary rational input numbers. */
   BFMSS2,
   /** Strauß, Burkhard: Zum Vorzeichentest Algebraischer Ausdrücke. */
   ZVAA,
   /** Unproven experimental algorithm. */
   YAXANA;

   /**
    * Sets the algorithm to be used for {@link Robust} computation.
    */
   public void setCurrent()
   {

      Algebraic.setAlgorithm(this);
   }

   /**
    * Returns the algorithm to be used for {@link Robust} computation.
    * 
    * @return the algorithm to be used for {@link Robust} computation.
    */
   public static Algorithm getCurrent()
   {

      return Algebraic.getAlgorithm();
   }
}
