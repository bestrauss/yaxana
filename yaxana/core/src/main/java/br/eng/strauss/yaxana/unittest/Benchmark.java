package br.eng.strauss.yaxana.unittest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Benchmark {
   /**
    * @return zero if this benchmark is disabled.
    */
   int value();
}
