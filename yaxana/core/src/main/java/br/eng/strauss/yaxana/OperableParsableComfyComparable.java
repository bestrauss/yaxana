package br.eng.strauss.yaxana;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * Interface aggregating capabilities.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
public abstract sealed interface OperableParsableComfyComparable<E extends OperableParsableComfyComparable<E>>
      extends Parsable<E>, ComfyComparable<E> permits Algebraic, Robust
{

}
