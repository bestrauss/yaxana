package br.eng.strauss.yaxana.pdc;

import static java.lang.Math.max;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Accumulation of PDC statistical data.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class PDCStats
{

   /**
    * Returns a new instance.
    */
   public PDCStats()
   {

      this.noOfApproximations = 0;
      this.sumOfTimeNs = 0L;
      this.enterTimeNs = 0L;
      this.maxPrecision = 0;
      this.sumPrecision = 0L;
   }

   /**
    * Enters profiling.
    * 
    * @param precision
    *           the precision of the approximation being profiled.
    * @return {@code true}.
    */
   public static boolean enter(final int precision)
   {

      final PDCStats instace = INSTANCE.get();
      if (instace != null)
      {
         instace.maxPrecision = max(instace.maxPrecision, precision);
         instace.sumPrecision += precision;
         instace.enterTimeNs = System.nanoTime();
      }
      return true;
   }

   /**
    * Exits profiling.
    * 
    * @return {@code true}.
    */
   public static boolean exit()
   {

      final PDCStats instace = INSTANCE.get();
      if (instace != null)
      {
         instace.sumOfTimeNs += System.nanoTime() - instace.enterTimeNs;
         instace.noOfApproximations++;
      }
      return true;
   }

   /**
    * Returns statistics.
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {

      if (noOfApproximations > 0)
      {
         final int avgPrecision = (int) ((double) sumPrecision / noOfApproximations + 0.5);
         return String.format(Locale.US, "%8.2fms, count: [%d], avg/max prec: [%d/%d]",
                              1E-6d * sumOfTimeNs, noOfApproximations, avgPrecision, maxPrecision);
      }
      else
      {
         return String.format(Locale.US, "%8.2fms [0] prec: [0/0]", 0d);
      }
   }

   /** The instance. No profiling while no instance is set. */
   public static final AtomicReference<PDCStats> INSTANCE = new AtomicReference<>();

   private int noOfApproximations;

   private long sumOfTimeNs;

   private long enterTimeNs;

   private int maxPrecision;

   private long sumPrecision;
}
