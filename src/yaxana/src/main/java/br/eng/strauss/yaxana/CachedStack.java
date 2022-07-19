package br.eng.strauss.yaxana;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
final class CachedStack<T>
{

   public CachedStack()
   {

   }

   public T pop()
   {

      return stack.pop();
   }

   public void push(final T object)
   {

      T cached = cache.get(object);
      if (cached == null)
      {
         cache.put(object, cached = object);
      }
      stack.push(cached);
   }

   private final Map<T, T> cache = new HashMap<>();

   private final Stack<T> stack = new Stack<>();
}
