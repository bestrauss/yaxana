package br.eng.strauss.yaxana.proof;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * Kleinster relativer Unterschied zwischen zwei Gitterpunkten.
 * 
 * @author Burkhard Strauss
 * @since 07-2022
 */
public final class Grid extends YaxanaTest
{

   @Disabled
   @Test
   public void test()
   {

      format("maxN  minDiff n0    n1    \n");
      format("--------------------------\n");
      for (int maxN = 1; maxN <= 32768; maxN <<= 1)
      {
         double minDiff = Double.POSITIVE_INFINITY;
         int minN0 = -1;
         int minN1 = -1;
         for (int n0 = 1; n0 <= maxN; n0++)
         {
            for (int n1 = n0; n1 <= maxN; n1++)
            {
               final double a = Math.sqrt(n0 * n0 + n1 * n1);
               for (final boolean hv : new boolean[] { true, false })
               {
                  final double b = Math.sqrt(n0 * (hv ? n0 : n0 - 1) + n1 * (hv ? n1 - 1 : n1));
                  final double diff = maxN * maxN * Math.abs(1 - b / a);
                  if (diff == 0d)
                  {
                     throw new UnreachedException();
                  }
                  else
                  {
                     if (minDiff > diff)
                     {
                        minDiff = diff;
                        minN0 = n0;
                        minN1 = n1;
                     }
                  }
               }
            }
         }
         if (minN0 >= 0 && minN0 >= 0)
         {
            format("%5d", maxN);
            format(" %7.5f", minDiff);
            format(" %5d", minN0);
            format(" %5d", minN1);
            format("\n");
         }
      }
   }
}
