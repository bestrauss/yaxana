package br.eng.strauss.yaxana;

/**
 * Abstract syntax tree node types.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public enum Type {
   /** Terminal node. */
   TERMINAL,
   /** Binary addition node. */
   ADD,
   /** Binary subtraction node. */
   SUB,
   /** Binary multiplication node. */
   MUL,
   /** Binary division node. */
   DIV,
   /** Binary n-th power node. */
   POW,
   /** Binary n-th root node. */
   ROOT,
   /** Unary absolute value node. */
   ABS,
   /** Unary negation node. */
   NEG
}
