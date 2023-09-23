package br.eng.strauss.yaxana.tools;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class SampleAlgebraic
{

   public static Algebraic geometricSeries(final Algebraic q, final int N)
   {

      final Algebraic[] array = geometricSeriesArray(q, N);
      return array[0].sub(array[1]);
   }

   /**
    * Returns the expressions {@code 1+q+q^2+...+q^(N-1)} und {@code (1-q^N)/(1-q)}.
    * 
    * @param q
    *           the {@code q}.
    * @param N
    *           the {@code N}.
    * @return the expressions {@code 1+q+q^2+...+q^(N-1)} und {@code (1-q^N)/(1-q)}.
    */
   public static Algebraic[] geometricSeriesArray(final Algebraic q, final int N)
   {

      Algebraic sum = Algebraic.ZERO;
      Algebraic product = Algebraic.ONE;
      for (int k = 0; k < N; k++)
      {
         sum = k == 0 ? product : sum.add(product);
         product = k == 0 ? q : product.mul(q);
      }
      final Algebraic E1 = sum;
      final Algebraic E2 = Algebraic.ONE.sub(product).div(Algebraic.ONE.sub(q));
      return new Algebraic[] { E1, E2 };
   }

   private SampleAlgebraic()
   {
   }
}
