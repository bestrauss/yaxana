package br.eng.strauss.yaxana.test;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Burkhard Strauss
 * @since 04-2022
 */
final class TestTools
{

   public static String getCallingMethodName()
   {

      final StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
      return walker.walk((stream) -> {
         final Iterator<StackFrame> iterator = stream.iterator();
         iterator.next();
         iterator.next();
         return iterator.next().getMethodName();
      });
   }

   public static Class<?> getCallingClass()
   {

      final StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
      return walker.walk((stream) -> {
         final Iterator<StackFrame> iterator = stream.iterator();
         iterator.next();
         iterator.next();
         return iterator.next().getDeclaringClass();
      });
   }

   public static <T extends Annotation> T getAnnotationOnTestClassOrMethod(
         final Class<T> annotationClass)
   {

      final StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
      final AtomicReference<T> annotation = new AtomicReference<>();
      return walker.walk((stream) -> {
         final Iterator<StackFrame> iterator = stream.iterator();
         iterator.next();
         iterator.next();
         final StackFrame stackFrame = iterator.next();
         try
         {
            final Class<?> clasz = Class.forName(stackFrame.getClassName());
            T t = clasz.getAnnotation(annotationClass);
            if (t == null)
            {
               t = clasz.getMethod(stackFrame.getMethodName()).getAnnotation(annotationClass);
            }
            annotation.set(t);
            return t != null;
         }
         catch (final Exception e)
         {
            return false;
         }
      }) ? annotation.get() : null;
   }

   private TestTools()
   {
   }
}
