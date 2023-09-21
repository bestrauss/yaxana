package br.eng.strauss.yaxana.proof;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * Wieviele Nachkommastellen können in den nicht verschwindenden Bruchteilen zweier Wurzeln aus
 * ganzen Zahlen übereinstimmen?
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class NoOfEqualDigitsDouble extends YaxanaTest
{

   @Disabled
   @Test
   public void test()
   {

      format("maxN  minMsb n0    n1    \\n0        \\n1        diff    \n");
      format("---------------------------------------------------------\n");
      for (int maxN = 2; maxN <= 65536; maxN <<= 1)
      {
         int minMsb = Integer.MAX_VALUE;
         int minN0 = -1;
         int minN1 = -1;
         for (int n0 = 1; n0 <= maxN; n0++)
         {
            for (int n1 = n0 + 1; n1 <= maxN; n1++)
            {
               double a = Math.sqrt(n0);
               a = a - (int) a;
               double b = Math.sqrt(n1);
               b = b - (int) b;
               final boolean doAbsDiff = true;
               if (doAbsDiff)
               {
                  final double absDiff = Math.abs(a - b);
                  if (absDiff == 0d)
                  {
                     if (a != 0d)
                     {
                        throw new UnreachedException();
                     }
                  }
                  else
                  {
                     final int msb = (int) Math.ceil(Math.log(absDiff) / Math.log(2d));
                     if (minMsb > msb)
                     {
                        minMsb = msb;
                        minN0 = n0;
                        minN1 = n1;
                     }
                  }
               }
               if (!doAbsDiff && a != 0d && b != 0d)
               {
                  final double relDiff = Math.abs(1d - b / a);
                  if (relDiff == 0d)
                  {
                     throw new UnreachedException();
                  }
                  else
                  {
                     final int msb = (int) Math.ceil(Math.log(relDiff) / Math.log(2d));
                     if (minMsb > msb)
                     {
                        minMsb = msb;
                        minN0 = n0;
                        minN1 = n1;
                     }
                  }
               }
            }
         }
         if (minN0 >= 0 && minN0 >= 0)
         {
            final double a = Math.sqrt(minN0);
            final double b = Math.sqrt(minN1);
            format("%5d", maxN);
            format(" %6d", minMsb);
            format(" %5d", minN0);
            format(" %5d", minN1);
            format(" %10.8f", a - (int) a);
            format(" %10.8f", b - (int) b);
            format(" %10.8f", Math.abs(a - (int) a - (b - (int) b)));
            format("\n");
            if (false)
            {
               // LaTeX
               final double d = a;
               format("  $%d$ &\n", maxN);
               format("  $%d$ &\n", minMsb);
               format("  $%.8f$ &\n", d - (int) d);
               format("  $%.8f-%d$ &\n", d, (int) d);
               format("  $%d$ &\n", minN0);
               format("  $1$ \\\\\n");
            }
         }
      }
   }
}
