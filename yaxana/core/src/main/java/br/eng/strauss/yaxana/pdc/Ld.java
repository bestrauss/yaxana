package br.eng.strauss.yaxana.pdc;

/**
 * Logarithmus dualis.
 * 
 * @author Burkhard Strauﬂ
 * @since July 2017
 */
final class Ld
{

   /**
    * Returns the number of nested square root operations which is equally bad or slightly worse
    * with respect to sign calculation than an n-th root operation.
    * 
    * @param n
    *           The n in n-th root.
    * @return the smallest {@code k} such that {@code 2}<sup>{@code k}</sup> {@code >= |n|}, which
    *         for {@code n != 0} equals {@code ceil(log}<sub>2</sub>{@code |n|}).
    */
   public static int ceilOfLdOfAbsOf(final int n)
   {

      return n != 0 ? 32 - Integer.numberOfLeadingZeros(Math.abs(n) - 1) : 0;
   }

   private Ld()
   {
   }
}
