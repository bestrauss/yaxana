package br.eng.strauss.yaxana.compiler;

/**
 * Defines a class from byte code.
 * <p>
 * The loader is to be used once for one instance of one class. No references to this class loader
 * should be kept but the reference of the class, which should be kept only by the instance to
 * ensure that the class and the loader perish together with the instance, when the garbage
 * collector comes around to finish all rests of the instance.
 * </p>
 * 
 * @author Burkhard Strauss
 * @since September 2017
 */
final class Loader extends ClassLoader
{

   /**
    * Returns a new Instance.
    */
   public Loader()
   {

      super();
   }

   /**
    * Loads and returns a class with given byte code.
    * 
    * @param byteCode
    *           The byte code of the class.
    * @return the class with given byte code.
    */
   public Class<? extends Function<?>> load(final String className, final byte[] byteCode)
         throws Exception
   {

      final Class<?> newClass = defineClass(className, byteCode, 0, byteCode.length);
      resolveClass(newClass);
      @SuppressWarnings("unchecked")
      final Class<? extends Function<?>> newFunctionClass = (Class<? extends Function<?>>) newClass;
      return newFunctionClass;
   }
}
