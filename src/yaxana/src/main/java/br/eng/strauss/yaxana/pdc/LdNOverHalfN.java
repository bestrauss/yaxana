package br.eng.strauss.yaxana.pdc;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
final class LdNOverHalfN
{

   public synchronized static int valueOf(final int n)
   {

      Integer value = CACHE.get(n);
      if (value == null)
      {
         value = calculateValueOf(n);
         CACHE.put(n, value);
      }
      return value;
   }

   private static int calculateValueOf(int n)
   {

      if (2 <= n && n <= Robust.MAX_EXPONENT)
      {
         n = (n & 1) == 1 ? n + 1 : n;
         final BigInteger arg = product(n / 2 + 1, n).divide(product(1, n / 2));
         return arg.bitLength();
      }
      throw new UnreachedException();
   }

   public static BigInteger product(final int n0, final int n1)
   {

      BigInteger product = BigInteger.valueOf(1);
      for (int n = n0; n <= n1; n++)
      {
         product = product.multiply(BigInteger.valueOf(n));
      }
      return product;
   }

   private LdNOverHalfN()
   {
   }

   private final static Map<Integer, Integer> CACHE = new HashMap<>();
}
