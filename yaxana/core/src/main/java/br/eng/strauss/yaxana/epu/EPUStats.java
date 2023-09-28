package br.eng.strauss.yaxana.epu;

import static java.lang.Math.max;
import static java.lang.String.format;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Accumulation of EPU statistical data.
 * 
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class EPUStats
{

   public static final EPUStats getInstance()
   {

      return INSTANCE;
   }

   public synchronized void clear()
   {

      this.noOfSignComputations = 0;
      this.sumOfTimeNs = 0;
      this.maxNoOfNodes = 0;
      this.sumNoOfNodes = 0;
      this.noOfExceptions = 0;
   }

   public int signum(final int noOfNodes, final Supplier<Integer> signum)
   {

      final long startNanoTime = System.nanoTime();
      try
      {
         return signum.get();
      }
      catch (final RuntimeException | Error e)
      {
         this.noOfExceptions++;
         throw e;
      }
      finally
      {
         this.maxNoOfNodes = max(this.maxNoOfNodes, noOfNodes);
         this.sumNoOfNodes += noOfNodes;
         this.sumOfTimeNs += System.nanoTime() - startNanoTime;
         this.noOfSignComputations++;
      }
   }

   @Override
   public synchronized String toString()
   {

      final int avgNoOfNodes = noOfSignComputations > 0
            ? (int) ((double) sumNoOfNodes / noOfSignComputations + 0.5)
            : 0;
      final String totalTime;
      if (sumOfTimeNs < 10_000_000L)
      {
         totalTime = String.valueOf((int) (sumOfTimeNs / 1_000 + 0.5)) + "us";
      }
      else if (sumOfTimeNs < 10_000_000_000L)
      {
         totalTime = String.valueOf((int) (sumOfTimeNs / 1_000_000 + 0.5)) + "ms";
      }
      else if (sumOfTimeNs < 10_000_000_000_000L)
      {
         totalTime = String.valueOf((int) (sumOfTimeNs / 1_000_000_000 + 0.5)) + "s ";
      }
      else
      {
         final Duration duration = Duration.ofNanos(sumOfTimeNs);
         totalTime = duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ")
               .toLowerCase();
      }
      final String format = "total time: %6s, total signs computed: %5d, avg/max nodes: %3d/%3d, exceptions: %3d";
      return format(format, totalTime, noOfSignComputations, avgNoOfNodes, maxNoOfNodes,
                    noOfExceptions);
   }

   private EPUStats()
   {

      clear();
   }

   private static final EPUStats INSTANCE = new EPUStats();

   private long noOfSignComputations;

   private long sumOfTimeNs;

   private long maxNoOfNodes;

   private long sumNoOfNodes;

   private long noOfExceptions;
}
