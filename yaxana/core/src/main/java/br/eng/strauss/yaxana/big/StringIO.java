package br.eng.strauss.yaxana.big;

import static br.eng.strauss.yaxana.big.BigFloat.overflow;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link BigFloat}/{@link String} conversion.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
final class StringIO
{

   /** String representation. */
   private static final String DEC_REGEX = "(-?[_0-9]+([.][_0-9]*)?|-?[_0-9]*[.]([_0-9]+)?)(([ep])([+-]?[0-9]+))?";

   /** String representation. */
   private static final Pattern DEC_PATTERN = compile(DEC_REGEX, CASE_INSENSITIVE);

   /** String representation. */
   private static final String HEX_REGEX = "(-?0x[_0-9a-f]+([.][_0-9a-f]*)?|-?0x[_0-9a-f]*[.]([_0-9a-f]+)?)(([ep])([+-]?[0-9]+))?";

   /** String representation. */
   private static final Pattern HEX_PATTERN = compile(HEX_REGEX, CASE_INSENSITIVE);

   /** Pattern for the parser. */
   private static final String TERMINAL_REGEX = "(" + HEX_REGEX + "|" + DEC_REGEX + ")";

   /** Pattern for the parser. */
   private static final Pattern TERMINAL_PATTERN = compile(TERMINAL_REGEX, CASE_INSENSITIVE);

   /**
    * Static methods only.
    */
   private StringIO()
   {

   }

   /**
    * Implementation of {@link BigFloat#BigFloat(String, Rounder)}.
    */
   public static BigFloat bigFloatValueOf(final String value, final Rounder rounder)
         throws NumberFormatException
   {

      BigInteger unscaledValue = null;
      int e10 = 0;
      int e2 = 0;
      Matcher matcher = DEC_PATTERN.matcher(value);
      if (matcher.matches())
      {
         final String sUnscaledValue;
         final String sDecValue = matcher.group(1).replace("_", "");
         final int kDecPoint = sDecValue.indexOf('.');
         if (kDecPoint >= 0)
         {
            final String inte = sDecValue.substring(0, kDecPoint);
            final String frac = sDecValue.substring(kDecPoint + 1);
            sUnscaledValue = inte + frac;
            e10 = -frac.length();
         }
         else
         {
            sUnscaledValue = sDecValue;
         }
         unscaledValue = new BigInteger(sUnscaledValue);
      }
      else
      {
         matcher = HEX_PATTERN.matcher(value);
         if (matcher.matches())
         {
            final String sUnscaledValue;
            String sHexValue = matcher.group(1).replace("_", "");
            final char s = sHexValue.charAt(0);
            if (s == '-' || s == '+')
            {
               sHexValue = sHexValue.substring(1);
            }
            sHexValue = sHexValue.substring(2);
            final String sign = s == '-' ? "-" : "";
            final int kHexPoint = sHexValue.indexOf('.');
            if (kHexPoint >= 0)
            {
               final String inte = sHexValue.substring(0, kHexPoint);
               final String frac = sHexValue.substring(kHexPoint + 1);
               sUnscaledValue = sign + inte + frac;
               e2 = -(4 * frac.length());
            }
            else
            {
               sUnscaledValue = sign + sHexValue;
            }
            unscaledValue = new BigInteger(sUnscaledValue, 16);
         }
      }
      if (unscaledValue != null)
      {
         final boolean baseTwo;
         final int exponent;
         final String group5 = matcher.group(5);
         if (group5 == null)
         {
            baseTwo = true;
            exponent = 0;
            // return new BigFloat(unscaledValue);
         }
         else
         {
            final char digit = group5.charAt(0);
            baseTwo = digit == 'p' || digit == 'P';
            final String sExponent = matcher.group(6);
            exponent = sExponent != null ? Integer.valueOf(sExponent) : 0;
         }
         if (baseTwo)
         {
            final int baseTwoExponent = exponent + e2;
            if (e10 == 0)
            {
               return new BigFloat(unscaledValue, baseTwoExponent);
            }
            return new BigFloat(unscaledValue, baseTwoExponent).div(BigFloat.TEN.pow(-e10),
                                                                    rounder);
         }
         else
         {
            if (e2 != 0)
            {
               throw new UnsupportedOperationException("hex mantissa with base ten exponent");
            }
            final int baseTenExp = exponent + e10;
            if (baseTenExp == 0)
            {
               return new BigFloat(unscaledValue);
            }
            if (baseTenExp > 0)
            {
               return new BigFloat(unscaledValue).mul(BigFloat.TEN.pow(baseTenExp));
            }
            return new BigFloat(unscaledValue).div(BigFloat.TEN.pow(-baseTenExp), rounder);
         }
      }
      throw new NumberFormatException("invalid BigFloat string representation");
   }

   /**
    * Implementation of {@link BigFloat#toString()}.
    */
   public static String stringValueOf(final BigFloat thiz)
   {

      if (thiz.scale == 0)
      {
         return thiz.unscaledValue.toString();
      }
      if (thiz.scale > 0)
      {
         if (!(thiz.unscaledValue.abs().equals(BigInteger.ONE) && thiz.scale > 10))
         {
            if (thiz.abs().compareTo(new BigFloat(1E36)) <= 0)
            {
               return thiz.unscaledValue.shiftLeft(thiz.scale).toString();
            }
            final BigDecimal bd = new BigDecimal(thiz.unscaledValue)
                  .multiply(new BigDecimal(2).pow(thiz.scale));
            return bd.toString();
         }
      }
      // length should be divisible by 16
      final int N = thiz.unscaledValue.abs().bitLength() - 1;
      final int shift = 4 - (N & 0x3) & 0x3;
      try
      {
         final BigInteger unscaledValue = thiz.unscaledValue.shiftLeft(shift);
         final int scale = overflow((long) thiz.scale - shift + (N + shift & 0xFFFFFFFC));
         final String mantissa;
         final String S;
         if (unscaledValue.abs().equals(BigInteger.ONE))
         {
            mantissa = "1";
            S = thiz.unscaledValue.signum() < 0 ? "-1" : "1";
         }
         else
         {
            mantissa = unscaledValue.abs().toString(16).toUpperCase();
            S = thiz.unscaledValue.signum() < 0 ? "-0x1." : "0x1.";
         }
         final String P = (scale >= 0 ? "P+" : "P-") + Math.abs(scale);
         return S + mantissa.substring(1) + P;
      }
      catch (final ArithmeticException e)
      {
         return hexStringValueOf(thiz);
      }
   }

   /**
    * Undocumented.
    * 
    * @param thiz
    *           a number.
    * @return the hexadecimal string representation of this number.
    */
   public static String hexStringValueOf(final BigFloat thiz)
   {

      final String p = (thiz.scale >= 0 ? "P+" : "P-") + Math.abs(thiz.scale);
      return thiz.unscaledValue.signum() < 0
            ? "-0x" + thiz.unscaledValue.negate().toString(16).toUpperCase() + p
            : "0x" + thiz.unscaledValue.toString(16).toUpperCase() + p;
   }

   /**
    * Implementation of {@link BigFloat#toString(MathContext)}.
    */
   public static String stringValueOf(final BigFloat thiz, final MathContext mc)
   {

      BigDecimal bd = new BigDecimal(thiz.unscaledValue);
      if (thiz.scale != 0)
      {
         bd = bd.multiply(new BigDecimal(2).pow(thiz.scale, mc), mc);
      }
      bd = bd.round(mc);
      final int precision = mc.getPrecision();
      if (precision > 0)
      {
         final String format = "%." + (precision - 1) + "E";
         final String string = String.format(Locale.US, format, bd);
         return string.endsWith("E+00") ? string.substring(0, string.length() - 4) : string;
      }
      return bd.toString();
   }

   /**
    * Returns the pattern to be used by the parser to recognize terminals.
    * 
    * @return the pattern to be used by the parser to recognize terminals.
    */
   public static Pattern terminalPattern()
   {

      return TERMINAL_PATTERN;
   }
}
