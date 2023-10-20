package br.eng.strauss.yaxana;

import br.eng.strauss.yaxana.io.Parser;

/**
 * Interface which makes an expression type parsable by a {@link Parser}.
 * 
 * @param <P>
 *           The type of object to be produced by the parser.
 * @author Burkhard Strauss
 * @since July 2017
 */
public abstract sealed interface Parsable<P extends Parsable<P>> extends Operable<P>
      permits OperableParsableComfyComparable
{

   /**
    * Returns a new instance of {@code this} representing a given terminal.
    * 
    * @param terminal
    *           The terminal.
    * @return a new instance of {@code this} representing the given terminal.
    */
   public abstract P newTerminal(String terminal);

   /**
    * Returns the value of the right subexpression of this {@link Type#POW}- or
    * {@link Type#ROOT}-expression, or else the value of this expression, as an integer.
    * 
    * @return the value of the right subexpression of this {@link Type#POW}- or
    *         {@link Type#ROOT}-expression, or else the value of this expression, as an integer.
    */
   public abstract int index();
}
