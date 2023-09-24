package br.eng.strauss.yaxana;

import java.lang.reflect.Method;

import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.exc.UnreachedException;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;

/**
 * @author Burkhard Strauss
 * @since 07-2022
 */
public enum Algorithm {

   /** Pion, Sylvain and Yap, Chee: Constructive root bound for k-ary rational input numbers. */
   BFMSS2,
   /** Strauß, Burkhard: Zum Vorzeichentest Algebraischer Ausdrücke. */
   ZVAA,
   /** Strauß, Burkhard: More on Sign Computation of Algebraic Expressions. */
   MOSC;

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

   /**
    * Returns the algorithms to used in unit tests per default.
    * 
    * @return see above.
    */
   @WithAlgorithms
   public static Algorithm[] getValuesForTest()
   {

      try
      {
         final Method method = Algorithm.class.getMethod("getValuesForTest");
         final WithAlgorithms withAlgorithms = method.getDeclaredAnnotation(WithAlgorithms.class);
         return withAlgorithms.value();
      }
      catch (final Exception e)
      {
         throw new UnreachedException(e);
      }
   }
}
