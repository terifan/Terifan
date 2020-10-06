package org.terifan.util;


public class Float16
{
	public static float toFloat(int hbits)
	{
		int mant = hbits & 0x03ff;            // 10 bits mantissa
		int exp = hbits & 0x7c00;            // 5 bits exponent
		if (exp == 0x7c00)                   // NaN/Inf
		{
			exp = 0x3fc00;                    // -> NaN/Inf
		}
		else if (exp != 0)                   // normalized value
		{
			exp += 0x1c000;                   // exp - 15 + 127
			if (mant == 0 && exp > 0x1c400)  // smooth transition
			{
				return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13 | 0x3ff);
			}
		}
		else if (mant != 0)                  // && exp==0 -> subnormal
		{
			exp = 0x1c400;                    // make it normal
			do
			{
				mant <<= 1;                   // mantissa * 2
				exp -= 0x400;                 // decrease exp by 1
			}
			while ((mant & 0x400) == 0); // while not normal
			mant &= 0x3ff;                    // discard subnormal bit
		}                                     // else +/-0 -> +/-0
		return Float.intBitsToFloat((hbits & 0x8000) << 16 | (exp | mant) << 13); // combine all parts sign  << ( 31 - 15 ) value << ( 23 - 10 )
	}


	public static int fromFloat(float fval)
	{
		int fbits = Float.floatToIntBits(fval);
		int sign = fbits >>> 16 & 0x8000;          // sign only
		int val = (fbits & 0x7fffffff) + 0x1000; // rounded value

		if (val >= 0x47800000)               // might be or become NaN/Inf
		{                                     // avoid Inf due to rounding
			if ((fbits & 0x7fffffff) >= 0x47800000)
			{                                 // is or must become NaN/Inf
				if (val < 0x7f800000)        // was value but too large
				{
					return sign | 0x7c00;     // make it +/-Inf
				}
				return sign | 0x7c00 | (fbits & 0x007fffff) >>> 13; // remains +/-Inf or NaN keep NaN (and Inf) bits
			}
			return sign | 0x7bff;             // unrounded not quite Inf
		}
		if (val >= 0x38800000)               // remains normalized value
		{
			return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
		}
		if (val < 0x33000000)                // too small for subnormal
		{
			return sign;                      // becomes +/-0
		}
		val = (fbits & 0x7fffffff) >>> 23;  // tmp exp for subnormal calc
		return sign | ((fbits & 0x7fffff | 0x800000) + (0x800000 >>> val - 102) >>> 126 - val);  // add subnormal bit round depending on cut off div by 2^(1-(exp-127+15)) and >> 13 | exp=0
	}


	public static void main(String ... args)
	{
		try
		{
			System.out.println(toFloat(0x7700));
			System.out.println(toFloat(0x23d8));
			System.out.println(toFloat(0xf9fc));

			System.out.println();

			System.out.println(toFloat(0x0077));
			System.out.println(toFloat(0xd823));
			System.out.println(toFloat(0xfcf9));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
