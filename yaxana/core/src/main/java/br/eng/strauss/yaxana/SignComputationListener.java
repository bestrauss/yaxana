package br.eng.strauss.yaxana;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Listener to be registered
 * 
 * @author Burkhard Strau�
 * @since 2023-10
 */
public abstract interface SignComputationListener
{

   /**
    * 
    * 
    * @param value
    * @param estimatedPlaces
    * @param actualPlaces
    */
   public abstract void signComputed(Robust value, int estimatedPlaces, int actualPlaces);

   public static class Default implements SignComputationListener
   {

      @Override
      public void signComputed(final Robust value, final int estimatedPlaces,
            final int actualPlaces)
      {
         // TODO Auto-generated method stub

      }
   }

   public static final AtomicReference<SignComputationListener> INSTANCE = new AtomicReference<>(
         new Default());

}