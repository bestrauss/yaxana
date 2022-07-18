package br.eng.strauss.yaxana;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks types and methods that are experimental and not guaranteed to work as expected and
 * documented.
 * <p>
 * Nothing is guaranteed to work anyways.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Experimental {
}
