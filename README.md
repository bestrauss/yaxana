# yaxana

Yet another exact algebraic numbers API, a Java 17 module (Work in progress).

 * `class Robust extends java.lang.Number`
 * Immutable, thread safe, ideally exact real algebraic number type
   to be used for e.g. robust, exact geometric computations. 
 * Uses `double` interval arithmetic, where possible, and a constructive root bound method, else.
 * Uses a heuristical method to estimate the number of binary places sufficient 
   to read the sign of the value of an expression from an approximation of that value.
 * For usage see javadoc of [class Robust](https://github.com/bestrauss/yaxana/blob/main/yaxana/core/src/main/java/br/eng/strauss/yaxana/Robust.java).
 * For examples see [ExampleTest.java](https://github.com/bestrauss/yaxana/blob/main/yaxana/core/src/test/java/br/eng/strauss/yaxana/examples/ExampleTest.java)


## Free Software

 * Free software: no copyright, no copyleft, no warranty.


## Building

 * (fork and) clone this repository 
 * use `gradlew build` to build jars (including `*-sources.jar` and `*-javadoc.jar`)
 * use `gradlew eclipse` to generate eclipse configuration files
