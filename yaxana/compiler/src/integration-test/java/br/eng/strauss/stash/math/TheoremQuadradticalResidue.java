package br.eng.strauss.stash.math;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.eng.strauss.yaxana.test.YaxanaDevelTest;

/**
 * Zur Quadratur der Zykloide.
 * 
 * @author Burkhard Strauss
 * @since Montag 16 April, A.D. 2018
 */
public class TheoremQuadradticalResidue extends YaxanaDevelTest
{

   @Test
   public void sum()
   {

      for (int N = 2; N < 256; N++)
      {
         format("N = %2d: ", N);
         double sumX = 0;
         double sumY = 0;
         // for (int n = 0, p = 2 * N * N + (N & 1); n < p; n++)
         for (int n = 0, p = N * N; n < p; n++)
         {
            final double phi = 2 * Math.PI / (N * N) * n * n;
            final double x = Math.cos(phi);
            final double y = Math.sin(phi);
            sumX += x;
            sumY += y;
         }
         // if (N % 2 == 0)
         {
            sumX /= N;
            sumY /= N;
         }
         format("%10.7f %10.7f\n", sumX, sumY);
      }
   }

   @Ignore
   @Test
   public void theorem3()
   {

      for (int L = 2; L <= 30; L += 2)
      {
         final int N = 1 << L;
         format("N = 2^%2d = %10d: ", L, N);
         double sumX = 0;
         double sumY = 0;
         for (int n = 0, p = (L + 1) / 2; n < p; n++)
         {
            final double phi = 2 * Math.PI / N / N * n * n;
            final double x = Math.cos(phi);
            final double y = Math.sin(phi);
            sumX += x;
            sumY += y;
         }
         sumX *= 2d / L;
         sumY *= 2d / L;
         format("%15.12f %15.12f\n", sumX, sumY);
      }
   }

   @Ignore
   @Test
   public void theorem1and2()
   {

      for (int L = 4; L <= 15; L++)
      {
         final int N = 1 << 2 * L;
         format("N = 2^%2d = %10d: ", 2 * L, N);
         int period = 0;
         int sumQR = 0;
         for (int n = 0;; n++)
         {
            final int n2 = n * n;
            final int qr = n2 % N;
            final int lastSumQR = sumQR;
            sumQR += qr;
            if (n > 0 && qr == 0)
            {
               // if (n >= 8 * L)
               {
                  period = n;
                  break;
               }
            }
            if (n > 0)
            {
               format(", ");
            }
            final double value = sumQR - lastSumQR;
            format("%d", (int) value);
            Assert.assertTrue(value == Math.floor(value));
            Assert.assertEquals((int) value, n * n);
         }
         format("\n");
         format(" %7d", period);
         format("\n");
      }
   }
}
