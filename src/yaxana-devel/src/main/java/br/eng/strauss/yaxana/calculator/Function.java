package br.eng.strauss.yaxana.calculator;

import java.util.Map;

import br.eng.strauss.yaxana.Parsable;
import br.eng.strauss.yaxana.Robust;

/**
 * A function which manipulates a given table.
 * 
 * @author Burkhard Strauss
 * @since September 2017
 */
public abstract class Function<P extends Parsable<P>>
{

   /** Default function of the display. */
   @SuppressWarnings("exports")
   public static Function<Robust> ZERO = new Function<Robust>()
   {
      @Override
      public void value(final Map<String, Robust> map)
      {

         map.put("f", Robust.ZERO);
      }
   };

   /**
    * Manipulates the given table possibly adding new entries.
    * 
    * @param map
    *           the table. Contains a variable named {@code x}.
    */
   public abstract void value(Map<String, P> map);

   /**
    * Looks up a variable in a given table.
    * 
    * @param map
    *           the table.
    * @param key
    *           the name of the variable.
    * @return the value of the variable.
    * @throws NumberFormatException
    *            if there is no variable with the given name in the table.
    */
   protected static Object get(final Map<String, Object> map, final String key)
         throws NumberFormatException
   {

      final Object object = map.get(key);
      if (object == null)
      {
         String msg = "error evaluating expressions:\n";
         msg += "unknown variable: " + key;
         throw new NumberFormatException(msg);
      }
      return object;
   }
}
