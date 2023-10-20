package br.eng.strauss.yaxana;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * This data structure is used to ensure reuse of sub-expressions when converting a {@link Robust}
 * into an {@link Algebraic}.
 * 
 * @author Burkhard Strauﬂ
 * @since 2023-09
 * @see Robust#toAlgebraic()
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
