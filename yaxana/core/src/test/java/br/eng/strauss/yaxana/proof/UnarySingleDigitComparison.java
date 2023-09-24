package br.eng.strauss.yaxana.proof;

import static br.eng.strauss.yaxana.Type.NEG;
import static br.eng.strauss.yaxana.Type.ROOT;
import static br.eng.strauss.yaxana.big.BigFloat.twoTo;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class UnarySingleDigitComparison extends YaxanaTest
{

   @Disabled
   @Test
   public void test()
   {

      final int SAFE_PRECISION = 1024;
      final MathContext mc = new MathContext(5);
      final Map<Integer, BigFloat> minApprox = new TreeMap<>();
      final Map<Integer, String> minExpression = new TreeMap<>();
      minApprox.put(0, new BigFloat(1234));
      minExpression.put(0, "");
      for (final int n : rootExponents(ROOT))
      {
         minApprox.put(n, new BigFloat(1234));
         minExpression.put(n, "");
      }
      for (int c0 = 1; c0 <= 19; c0++)
      {
         for (final Type leftOp : new Type[] { null, NEG, ROOT })
         {
            for (final int n : rootExponents(leftOp))
            {
               Algebraic left = new Algebraic(c0);
               if (leftOp != null)
               {
                  left = leftOp == NEG ? left.neg() : left.root(n);
               }
               final Algebraic a = Algebraic.ONE.sub(left);
               final BigFloat approx = a.approximation(SAFE_PRECISION).abs();
               // format("%s\n", a.approximation().toString(mc));
               if (approx.compareTo(twoTo(-SAFE_PRECISION)) >= 0)
               {
                  if (minApprox.get(n).compareTo(approx) > 0)
                  {
                     minApprox.put(n, approx);
                     minExpression.put(n, a.toString());
                  }
               }
            }
         }
      }
      for (final int n : minApprox.keySet())
      {
         format("%3d: %-10s %s\n", n, minApprox.get(n).toString(mc), minExpression.get(n));
      }
   }

   private static Integer[] rootExponents(final Type type)
   {

      if (type == Type.ROOT)
      {
         final List<Integer> list = new ArrayList<>(64);
         for (int k = 2; k < 16; k++)
         {
            list.add(k);
         }
         for (int k = 16; k <= 256; k <<= 1)
         {
            list.add(k);
         }
         return list.toArray(new Integer[list.size()]);
      }
      return new Integer[] { Integer.valueOf(0) };
   }
}
