package br.eng.strauss.yaxana.pdc;

/**
 * @author Burkhard Strauss
 * @since August 2017
 */
public enum ApproximationType {
   /**
    * Indicates that a given {@code precision} specifies the number of exact digits on the right
    * side of the binary point. (Absolute precision in the sense of Yap.)
    */
   FRACTIONAL_DIGITS,
   /**
    * Indicates that a given {@code precision} specifies the number of exact digits starting at the
    * most significant bit. (Relative precision in the sense of Yap.)
    */
   SIGNIFICANT_DIGITS
}
