package br.eng.strauss.yaxana;

import java.util.function.Supplier;
import java.util.regex.Pattern;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.Cache;
import br.eng.strauss.yaxana.epu.robo.RootBoundEPU;
import br.eng.strauss.yaxana.epu.yaxa.YaxanaEPU;
import br.eng.strauss.yaxana.rnd.RandomRobust;

/**
 * Static thread safe methods to control the environment of {@link Robust} calculations.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class Robusts
{

   /**
    * Clears the cache.
    * <p>
    * For more relevant info about the cache see {@link #setMaximumCacheSize(int)}.
    */
   public static void clearCache()
   {

      Cache.getInstance().clear();
   }

   /**
    * Sets the maximum size of the cache.
    * <p>
    * {@link Robust} instances, whose sign cannot be determined by interval arithmetic, are cached.
    * The size of the Cache is limited, and the least recently used entries are discarded first to
    * avoid cache overflow.
    * <p>
    * Lowering the maximum size of the cache may eject cached entries.
    * 
    * @param maximumCacheSize
    *           The maximum size of the cache. The size of the cache is the total number of nodes of
    *           the syntax trees of the stored {@link Robust} values. Use
    *           {@code maximumCacheSize = 0} to switch off caching. The default
    *           {@code maximumCacheSize} is {@code 1,000,000}.
    */
   public static void setMaximumCacheSize(final int maximumCacheSize)
   {

      Cache.getInstance().setMaximumSize(maximumCacheSize);
   }

   /**
    * Returns the maximum size of the cache.
    * <p>
    * For more relevant info about the cache see {@link #setMaximumCacheSize(int)}.
    * 
    * @return the maximum size of the cache.
    */
   public static int getMaximumCacheSize()
   {

      return Cache.getInstance().getMaximumSize();
   }

   /**
    * Switches between vintage mode, using the {@code BFMSS[2]} method for sign detection where
    * interval arithmetic fails, and cutting-edge mode {@code BFMSS[2]UP}.
    * <p>
    * The default mode is cutting-edge mode. This method is intended to allow comparisons of modes.
    * 
    * @param vintageMode
    *           whether vintage mode is to be set.
    */
   public static void setVintageMode(final boolean vintageMode)
   {

      Algebraic.setEPU(vintageMode ? () -> new RootBoundEPU() : () -> new YaxanaEPU());
   }

   /**
    * Returns certain sequences of {@link Robust} instances.
    * <p>
    * For each {@code maxDepth} the returned sequence is always the same for each call.
    * 
    * @param maxDepth
    *           the maximum depth of the abstract syntax tree of the {@link Robust} instances.
    * @return a certain sequence of {@link Robust} instances.
    */
   public static Supplier<Robust> randomSequence(final int maxDepth)
   {

      final RandomRobust randomRobust = new RandomRobust(maxDepth);
      return () -> randomRobust.next();
   }

   /**
    * Returns a pattern which matches allowed terminal string representations.
    * <p>
    * The pattern matches the well known formats produced by {@link Double#toString(double)} and
    * {@link Double#toHexString(double)}. The mantissa may be decimal or hexadecimal. A hexadecimal
    * mantissa is preceded by {@code 0x}. The mantissa may contain a decimal point (or a hexadecimal
    * point). The optional exponent is decimal. It may be base ten {@code e}, {@code E} or base two
    * {@code p}, {@code P}. Mantissa and exponent optionally have a sign.
    * 
    * @return a pattern which matches allowed terminal string representations.
    */
   public static Pattern terminalPattern()
   {

      return BigFloat.terminalPattern();
   }

   private Robusts()
   {
   }
}
