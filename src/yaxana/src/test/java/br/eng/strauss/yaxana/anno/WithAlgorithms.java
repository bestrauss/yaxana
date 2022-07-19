package br.eng.strauss.yaxana.anno;

import static br.eng.strauss.yaxana.Algorithm.BFMSS2;
import static br.eng.strauss.yaxana.Algorithm.YAXANA;
import static br.eng.strauss.yaxana.Algorithm.ZVAA;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.eng.strauss.yaxana.Algorithm;

/**
 * Indicates that a test class is to be executed multiple times using all EPUs.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WithAlgorithms {
   Algorithm[] value() default { BFMSS2, ZVAA, YAXANA };
}
