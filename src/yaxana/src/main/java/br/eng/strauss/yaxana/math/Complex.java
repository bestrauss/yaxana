package br.eng.strauss.yaxana.math;

import java.util.Arrays;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.Locale;

/**
 * @author Burkhard Strauss
 * @since 2021-03
 */
public final class Complex implements Formattable, Comparable<Complex>
{

   public Complex()
   {

      this(0d, 0d);
   }

   public Complex(final double real)
   {

      this(real, 0d);
   }

   public Complex(final double real, final double imag)
   {

      this.real = real;
      this.imag = imag;
   }

   public Complex(final Complex complex)
   {

      this(complex.real, complex.imag);
   }

   public static Complex[] newArray(final int L)
   {

      final Complex[] samples = new Complex[L];
      Arrays.fill(samples, Complex.ZERO);
      return samples;
   }

   @Override
   public int hashCode()
   {

      return Double.hashCode(this.real) ^ Double.hashCode(this.imag);
   }

   @Override
   public boolean equals(final Object object)
   {

      if (object != this)
      {
         if (object instanceof Complex)
         {
            final Complex that = (Complex) object;
            return this.real == that.real && this.imag == that.imag;
         }
         return false;
      }
      return true;
   }

   public boolean isNaN()
   {

      return Double.isNaN(this.real) || Double.isNaN(this.imag);
   }

   public boolean isFinite()
   {

      return Double.isFinite(this.real) || Double.isFinite(this.imag);
   }

   public boolean isInfinite()
   {

      return Double.isInfinite(this.real) || Double.isInfinite(this.imag);
   }

   public final boolean isZero()
   {

      return this.real == 0d && this.imag == 0d;
   }

   public final boolean isCloseTo(final Complex that, final double epsilon)
   {

      return sub(that).abs() < epsilon;
   }

   @Override
   public final int compareTo(final Complex that)
   {

      final int compare = Double.valueOf(abs()).compareTo(that.abs());
      if (compare == 0)
      {
         return Double.valueOf(arg()).compareTo(that.arg());
      }
      return compare;
   }

   public double real()
   {

      return this.real;
   }

   public double imag()
   {

      return this.imag;
   }

   public Complex conj()
   {

      return new Complex(this.real, -this.imag);
   }

   public Complex toCartesian()
   {

      return new Complex(Math.cos(this.imag), Math.sin(this.imag)).multiply(this.real);
   }

   public Complex toPolar()
   {

      return new Complex(abs(), arg());
   }

   public double abs()
   {

      return Math.hypot(this.real, this.imag);
   }

   public double arg()
   {

      return Math.atan2(this.imag, this.real);
   }

   public double absSqr()
   {

      return this.real * this.real + this.imag * this.imag;
   }

   public Complex sqr()
   {

      return mul(this);
   }

   public Complex sqrt()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      if (this.real == 0d && this.imag == 0d)
      {
         return ZERO;
      }
      final double t = Math.sqrt(0.5 * (Math.abs(this.real) + abs()));
      if (this.real >= 0d)
      {
         return new Complex(t, 0.5 * this.imag / t);
      }
      else if (this.imag >= 0d)
      {
         return new Complex(0.5 * Math.abs(this.imag) / t, t);
      }
      else
      {
         return new Complex(0.5 * Math.abs(this.imag) / t, -t);
      }
   }

   public Complex pow(final double y)
   {

      if (this.isNaN())
      {
         return NaN;
      }
      final double abs = Math.pow(this.abs(), y);
      final double arg = this.arg() * y;
      return new Complex(abs).multiply(Math.exp(arg));
   }

   public Complex pow(final Complex y)
   {

      throw new UnsupportedOperationException("not supported");
   }

   public Complex exp()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      final double expReal = Math.exp(this.real);
      return new Complex(expReal * Math.cos(this.imag), expReal * Math.sin(this.imag));
   }

   public Complex log()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      return new Complex(Math.log(this.abs()), Math.atan2(this.imag, this.real));
   }

   public Complex cosh()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      return new Complex(Math.cosh(this.real) * Math.cos(this.imag),
            Math.sinh(this.real) * Math.sin(this.imag));
   }

   public Complex sinh()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      return new Complex(Math.sinh(this.real) * Math.cos(this.imag),
            Math.cosh(this.real) * Math.sin(this.imag));
   }

   public Complex cos()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      return new Complex(Math.cos(this.real) * Math.cosh(this.imag),
            -Math.sin(this.real) * Math.sinh(this.imag));
   }

   public Complex sin()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      return new Complex(Math.sin(this.real) * Math.cosh(this.imag),
            Math.cos(this.real) * Math.sinh(this.imag));
   }

   public Complex acos()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      throw new UnsupportedOperationException();
   }

   public Complex asin()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      throw new UnsupportedOperationException();
   }

   public Complex tan()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      final double re2 = 2d * this.real;
      final double im2 = 2d * this.imag;
      final double den = Math.cosh(re2) + Math.cosh(im2);
      return new Complex(Math.sin(re2) / den, Math.sinh(re2) / den);
   }

   public Complex atan()
   {

      return I.add(this).div(I.sub(this)).log().mul(I).mul(new Complex(0.5, 0d));
   }

   public Complex add(final double that)
   {

      return new Complex(this.real + that, this.imag);
   }

   public Complex add(final Complex that)
   {

      return new Complex(this.real + that.real, this.imag + that.imag);
   }

   public Complex subtract(final double that)
   {

      return new Complex(this.real - that, this.imag);
   }

   public Complex sub(final Complex that)
   {

      return new Complex(this.real - that.real, this.imag - that.imag);
   }

   public Complex neg()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      return new Complex(-this.real, -this.imag);
   }

   public Complex multiply(final int that)
   {

      return multiply((double) that);
   }

   public Complex multiply(final double that)
   {

      return new Complex(this.real * that, this.imag * that);
   }

   public Complex mul(final Complex that)
   {

      if (isNaN() || that.isNaN())
      {
         return NaN;
      }
      if (isInfinite() || that.isInfinite())
      {
         return INFINITY;
      }
      return new Complex(this.real * that.real - this.imag * that.imag,
            this.imag * that.real + this.real * that.imag);
   }

   public Complex div(final Complex that)
   {

      if (this.isNaN() || that.isNaN())
      {
         return NaN;
      }
      final double c = that.real;
      final double d = that.imag;
      if (c == 0d && d == 0d)
      {
         return NaN;
      }
      if (that.isInfinite() && !this.isInfinite())
      {
         return ZERO;
      }
      if (Math.abs(c) < Math.abs(d))
      {
         final double q = c / d;
         final double denominator = c * q + d;
         return new Complex((this.real * q + this.imag) / denominator,
               (this.imag * q - this.real) / denominator);
      }
      else
      {
         final double q = d / c;
         final double denominator = d * q + c;
         return new Complex((this.imag * q + this.real) / denominator,
               (this.imag - this.real * q) / denominator);
      }
   }

   public Complex reciprocal()
   {

      if (this.isNaN())
      {
         return NaN;
      }
      if (this.isInfinite())
      {
         return ZERO;
      }
      if (this.real == 0d && this.imag == 0d)
      {
         return INFINITY;
      }
      if (Math.abs(this.real) < Math.abs(this.imag))
      {
         final double q = this.real / this.imag;
         final double scale = 1d / (this.real * q + this.imag);
         return new Complex(scale * q, -scale);
      }
      else
      {
         final double q = this.imag / this.real;
         final double scale = 1d / (this.imag * q + this.real);
         return new Complex(scale, -scale * q);
      }
   }

   @Override
   public final void formatTo(final Formatter formatter, final int flags, final int width,
         final int precision)
   {

      formatTo(this.real, formatter, flags, width, precision);
      if (this.imag < 0)
      {
         formatter.format(" - ");
         formatTo(-this.imag, formatter, flags, width, precision);
      }
      else
      {
         formatter.format(" + ");
         formatTo(this.imag, formatter, flags, width, precision);
      }
      formatter.format("i");
   }

   private static void formatTo(final double value, final Formatter formatter, final int flags,
         final int width, final int precision)
   {

      if (Double.isNaN(value))
      {
         formatter.format("%f", Double.NaN);
      }
      else if (Double.isInfinite(value))
      {
         formatter.format("%f", value < 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
      }
      else
      {
         final boolean left = (flags & FormattableFlags.LEFT_JUSTIFY) != 0;
         final boolean upper = (flags & FormattableFlags.UPPERCASE) != 0;
         final boolean alt = (flags & FormattableFlags.ALTERNATE) != 0;
         final String ws = width < 0 ? "" : String.valueOf(width);
         final String ps = precision < 0 ? "" : "." + String.valueOf(precision);
         final String lj = (left ? "-" : "") + (alt ? "#" : "");
         final String di = upper ? "e" : "f";
         final String format = String.format("%%%s%s%s%s", lj, ws, ps, di);
         formatter.format(format, value);
      }
   }

   @Override
   public final String toString()
   {

      if (this.imag < 0)
      {
         return String.format(Locale.US, "%s - %si", real, -imag);
      }
      else
      {
         return String.format(Locale.US, "%s + %si", real, imag);
      }
   }

   public static Complex ZERO = new Complex(0d);

   public static Complex ONE = new Complex(1d);

   public static Complex I = new Complex(0d, 1d);

   public static Complex NaN = new Complex(Double.NaN, Double.NaN);

   public static Complex INFINITY = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

   private final double real;

   private final double imag;

}
