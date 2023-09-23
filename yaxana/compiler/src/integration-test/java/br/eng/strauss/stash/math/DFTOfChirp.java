package br.eng.strauss.stash.math;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import org.junit.Test;

import br.eng.strauss.yaxana.test.YaxanaDevelTest;

/**
 * Zur Quadratur der Zykloide.
 * 
 * @author Burkhard Strauss
 * @since Montag 16 April, A.D. 2018
 */
public class DFTOfChirp extends YaxanaDevelTest
{

   @Test
   public void test()
   {

      // for (int L = 2; L <= 30; L += 2)
      final int L = 3;
      {
         final int N = 1 << L;
         format("N = 2^%2d = %10d:\n", L, N);
         final int N2 = N * N;
         for (int nu = 0; nu < N2; nu++)
         {
            double sumX = 0;
            double sumY = 0;
            for (int n = 0; n < N2; n++)
            {
               final double phi = 2 * PI / N2 * (2 * nu - n) * n;
               final double x = cos(phi);
               final double y = sin(phi);
               sumX += x;
               sumY += y;
            }
            sumX /= N;
            sumY /= N;
            sumX = Math.abs(sumX) < 1E-12 ? 0 : sumX;
            sumY = Math.abs(sumY) < 1E-12 ? 0 : sumY;
            final double r = sqrt(sumX * sumX + sumY * sumY);
            // r = Math.abs(r) < 1E-12 ? 0 : r;
            double a = 180 / PI * atan2(sumY, sumX);
            a = r == 0 ? 0 : a;
            if (nu == N2 / 2)
            {
               format("\n");
            }
            format("%8.5f +j%8.5f", sumX, sumY);
            format("     ");
            format("%7.5f +e^j%9.4f\n", r, a);
         }
      }
   }
}
