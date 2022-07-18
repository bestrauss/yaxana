package br.eng.strauss.yaxana.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a test class is to be executed multiple times using all EPUs.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WithAllEPUs {
}
