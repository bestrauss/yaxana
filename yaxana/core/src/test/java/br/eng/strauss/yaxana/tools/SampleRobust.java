package br.eng.strauss.yaxana.tools;

import br.eng.strauss.yaxana.Robust;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class SampleRobust
{

   public static Robust geometricSeries(final Robust q, final int N)
   {

      final Robust[] array = geometricSeriesArray(q, N);
      return array[0].sub(array[1]);
   }

   /**
    * Returns the expressions {@code 1+q+q^2+...+q^(N-1)} and {@code (1-q^N)/(1-q)}.
    * 
    * @param q
    *           the {@code q}.
    * @param N
    *           the {@code N}.
    * @return the expressions {@code 1+q+q^2+...+q^(N-1)} and {@code (1-q^N)/(1-q)}.
    */
   public static Robust[] geometricSeriesArray(final Robust q, final int N)
   {

      Robust sum = Robust.ZERO;
      Robust product = Robust.ONE;
      for (int k = 0; k < N; k++)
      {
         sum = k == 0 ? product : sum.add(product);
         product = k == 0 ? q : product.mul(q);
      }
      final Robust E1 = sum;
      final Robust E2 = Robust.ONE.sub(product).div(Robust.ONE.sub(q));
      return new Robust[] { E1, E2 };
   }

   private SampleRobust()
   {
   }
}
