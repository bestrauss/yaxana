package br.eng.strauss.yaxana.epu;

import java.util.Formatter;
import java.util.LinkedHashMap;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;

/**
 * Thread safe cache for {@link Robust} instances.
 * <p>
 * Yaxana caches {@link Robust} instances, whose sign cannot be determined by {@code double}
 * interval arithmetic.
 * 
 * @author Burkhard Strauss
 * @since 05-2022
 * @see Robusts#setMaximumCacheSize(int)
 * @see Robusts#getMaximumCacheSize()
 */
public final class Cache
{

   /**
    * Returns the singleton instance.
    * 
    * @return the singleton instance.
    */
   public static Cache getInstance()
   {

      return INSTANCE;
   }

   /**
    * Clears this cache.
    */
   public void clear()
   {

      synchronized (this.map)
      {
         this.map.clear();
         this.size = 0;
      }
   }

   /**
    * Returns the maximum size of the cache (the size is the total number of nodes of the abstract
    * syntax trees of stored {@link Robust} instances).
    * 
    * @return the maximum size of the cache.
    */
   public int getMaximumSize()
   {

      synchronized (this.map)
      {
         return maximumSize;
      }
   }

   /**
    * Sets the maximum size of the cache (the size is the total number of nodes of the abstract
    * syntax trees of stored {@link Robust} instances).
    * 
    * @param maximumSize
    *           the maximum size.
    */
   public void setMaximumSize(final int maximumSize)
   {

      synchronized (this.map)
      {
         this.maximumSize = maximumSize;
         ensureSpaceFor(0);
      }
   }

   /**
    * Returns a stored entry, which equals a given entry, if present, or else {@code null}.
    * 
    * @param entry
    *           an entry.
    * @return the stored entry, which equals the given entry, if present, or else {@code null}.
    */
   public Robust get(final Robust entry)
   {

      synchronized (this.map)
      {
         final Robust value = this.map.get(entry);
         if (value != null)
         {
            this.map.remove(value);
            this.map.put(value, value);
         }
         return value;
      }
   }

   /**
    * Stores a given value, beforehand possibly removing least recently stored entries.
    * 
    * @param value
    *           the value.
    */
   public void put(final Robust value)
   {

      synchronized (this.map)
      {
         if (this.maximumSize > 0)
         {
            this.size -= this.map.remove(value) == value ? value.noOfNodes() : 0;
            ensureSpaceFor(value.noOfNodes());
            this.map.put(value, value);
            this.size += value.noOfNodes();
         }
      }
   }

   /**
    * Returns the number of entries stored in the cache.
    * 
    * @return the number of entries stored in the cache.
    */
   public int noOfEntries()
   {

      synchronized (this.map)
      {
         return this.map.size();
      }
   }

   public void print(final String indent, final Formatter formatter)
   {

      synchronized (this.map)
      {
         formatter.format("%s%d Cache entries: (least recent entry first)\n", indent, map.size());
         map.forEach((key, value) -> {
            formatter.format("%s  %s\n", indent, key);
         });
      }
   }

   private void ensureSpaceFor(final int size)
   {

      while (!this.map.isEmpty() && this.size + size > this.maximumSize)
      {
         final Robust leastRecentEntry = this.map.keySet().iterator().next();
         this.map.remove(leastRecentEntry);
         this.size -= leastRecentEntry.noOfNodes();
      }
   }

   private Cache()
   {

      this.map = new LinkedHashMap<>();
      this.maximumSize = DEFAULT_MAXIMUM_SIZE;
      this.size = 0;
   }

   private static final int DEFAULT_MAXIMUM_SIZE = 1_000_000;

   private static final Cache INSTANCE = new Cache();

   private final LinkedHashMap<Robust, Robust> map;

   private int maximumSize;

   private int size;
}
