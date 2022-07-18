package br.eng.strauss.yaxana.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.eng.strauss.yaxana.YaxanaTest;

/**
 * Marker for {@code @Test}-methods of {@link YaxanaTest}s.
 * <p>
 * Indicates that the method demonstrates an open Bug or shows that a resolved bug is indeed
 * resolved.
 * 
 * @author Burkhard Strauss
 * @since 05-2022
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenBug {
}
