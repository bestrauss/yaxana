package br.eng.strauss.yaxana.proof;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;
import br.eng.strauss.yaxana.exc.UnreachedException;
import br.eng.strauss.yaxana.tools.YaxanaTest;

/**
 * Wieviele Nachkommastellen können in den nicht verschwindenden Bruchteilen zweier Wurzeln aus
 * ganzen Zahlen übereinstimmen?
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class NoOfEqualDigits extends YaxanaTest
{

   @Disabled
   @Test
   public void testA()
   {

      format("maxN  minMsb n0    n1    \\n0        \\n1        diff    \n");
      format("---------------------------------------------------------\n");
      final int SAFE_PRECISION = 128;
      final Rounder rounder = new Rounder(SAFE_PRECISION);
      for (int maxN = 2; maxN <= 4096; maxN <<= 1)
      {
         int minMsb = Integer.MAX_VALUE;
         int minN0 = -1;
         int minN1 = -1;
         for (int n0 = 1; n0 <= maxN; n0++)
         {
            for (int n1 = n0 + 1; n1 <= maxN; n1++)
            {
               BigFloat a = new BigFloat(n0).sqrt(rounder);
               a = a.sub(new BigFloat(a.bigIntegerValue()));
               BigFloat b = new BigFloat(n1).sqrt(rounder);
               b = b.sub(new BigFloat(b.bigIntegerValue()));
               final boolean doAbsDiff = true;
               if (doAbsDiff)
               {
                  final BigFloat absDiff = a.sub(b).abs();
                  if (absDiff.isZero())
                  {
                     if (!a.isZero())
                     {
                        throw new UnreachedException();
                     }
                  }
                  else
                  {
                     final int msb = absDiff.msb();
                     if (minMsb > msb)
                     {
                        minMsb = msb;
                        minN0 = n0;
                        minN1 = n1;
                     }
                  }
               }
               final boolean doRelDiff = false;
               if (doRelDiff && !a.isZero() && !b.isZero())
               {
                  final BigFloat relDiff = BigFloat.ONE.sub(b.div(a, rounder)).abs();
                  if (relDiff.isZero())
                  {
                     throw new UnreachedException();
                  }
                  else
                  {
                     final int msb = relDiff.msb();
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
            final double a = new BigFloat(minN0).sqrt(rounder).doubleValue();
            final double b = new BigFloat(minN1).sqrt(rounder).doubleValue();
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
