package org.terifan.math;

import java.io.Serializable;


public class vec3 implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;

	public final double x, y, z;

  
	/**
	 * Constructs a new Vector.
	 */
	public vec3(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}


	/**
	 * Sets each of the x, y, z coordinates to their absolute values.
	 */
	public vec3 abs()
	{
		return new vec3(Math.abs(x), Math.abs(y), Math.abs(z));
	}


	/**
	 * Returns true if  (x == 0 && y == 0 && z == 0).
	 */
	public boolean isZero()
	{
		return x==0 && y == 0 && z == 0;
	}


	public vec3 add(vec3 aVector)
	{
		return new vec3(x + aVector.x, y + aVector.y, z + aVector.z);
	}


	public vec3 add(double x, double y, double z)
	{
		return new vec3(this.x + x, this.y + y, this.z + z);
	}


	public vec3 add(double aValue)
	{
		return new vec3(x + aValue, y + aValue, z + aValue);
	}


	public vec3 subtract(vec3 aVector)
	{
		return new vec3(x - aVector.x, y - aVector.y, z - aVector.z);
	}


	public vec3 subtract(double x, double y, double z)
	{
		return new vec3(this.x - x, this.y - y, this.z - z);
	}


	public vec3 subtract(double aValue)
	{
		return new vec3(x - aValue, y - aValue, z - aValue);
	}


	public vec3 scale(double aScale)
	{
		return new vec3(x * aScale, y * aScale, z * aScale);
	}


	public vec3 scale(double aScaleX, double aScaleY, double aScaleZ)
	{
		return new vec3(x * aScaleX, y * aScaleY, z * aScaleZ);
	}


	public vec3 scale(vec3 aVector)
	{
		return new vec3(x * aVector.x, y * aVector.y, z * aVector.z);
	}


	public vec3 divide(double aFactor)
	{
		double scale = 1.0 / aFactor;

		return new vec3(x * scale, y * scale, z * scale);
	}

	
	public vec3 divide(double x, double y, double z)
	{
		return new vec3(this.x / x, this.y / y, this.z / z);
	}

	
	public vec3 divide(vec3 aFactor)
	{
		return new vec3(x / aFactor.x, y * aFactor.y, z * aFactor.z);
	}


	public vec3 normalize()
	{
		double length = Math.sqrt(x * x + y * y + z * z);

		if (length == 0)
		{
			length = 1;
		}

		double s = 1.0 / length;

		return new vec3(x * s, y * s, z * s);
	}


	public double dot(double aValue)
	{
		return x * aValue + y * aValue + z * aValue;
	}


	public double dot(double x, double y, double z)
	{
		return this.x * x + this.y * y + this.z * z;
	}


	public double dot(vec3 aVector)
	{
		return x * aVector.x + y * aVector.y + z * aVector.z;
	}


	public vec3 cross(double x, double y, double z)
	{
		return new vec3(
			this.y * z - this.z * y,
			this.z * x - this.x * z,
			this.x * y - this.y * x);
	}


	public vec3 cross(vec3 aVector)
	{
		return new vec3(
			y * aVector.z - z * aVector.y,
			z * aVector.x - x * aVector.z,
			x * aVector.y - y * aVector.x);
	}

	
	/**
	 * Updates this instance with the interpolated value of the provided vectors.
	 *
	 * @return
	 *   this instance
	 */
	public static vec3 interpolate(vec3 aVectorFrom, vec3 aVectorTo, double aAlpha)
	{
		if (aAlpha == 0)
		{
			return aVectorFrom;
		}
		else if (aAlpha == 1)
		{
			return aVectorTo;
		}
		else
		{
			return new vec3(
				aVectorFrom.x + aAlpha * (aVectorTo.x - aVectorFrom.x),
				aVectorFrom.y + aAlpha * (aVectorTo.y - aVectorFrom.y),
				aVectorFrom.z + aAlpha * (aVectorTo.z - aVectorFrom.z));
		}
	}


	public double distance(vec3 aVector)
	{
		double dx = aVector.x - x;
		double dy = aVector.y - y;
		double dz = aVector.z - z;

		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}


	/**
	 * Returns the length of this Vector.<p>
	 *
	 *   return Math.sqrt(x * x + y * y + z * z);
	 */
	public double length()
	{
		return Math.sqrt(x * x + y * y + z * z);
	}


	/**
	 * Returns the length of this Vector.<p>
	 *
	 *   return x * x + y * y + z * z;
	 */
	public double lengthSqr()
	{
		return x * x + y * y + z * z;
	}


	/**
	 * Clamps the x, y and z component to a value: 0 >= x/y/z <= 1
	 */
	public vec3 clamp()
	{
		return new vec3(
			x < 0 ? 0 : x > 1 ? 1 : x,
			y < 0 ? 0 : y > 1 ? 1 : y,
			z < 0 ? 0 : z > 1 ? 1 : z);
	}


	/**
	 * Returns true if the Vector provided has the exact same coordinate as
	 * this object.
	 */
	@Override
	public boolean equals(Object aVector)
	{
		if (aVector instanceof vec3)
		{
			vec3 v = (vec3)aVector;
			return Double.doubleToLongBits(v.x) == Double.doubleToLongBits(x)
				&& Double.doubleToLongBits(v.y) == Double.doubleToLongBits(y)
				&& Double.doubleToLongBits(v.z) == Double.doubleToLongBits(z);
		}
		return false;
	}


	@Override
	public int hashCode()
	{
		long a = Double.doubleToLongBits(x);
		long b = Double.doubleToLongBits(y);
		long c = Double.doubleToLongBits(z);
		
		int ai = (int)(a ^ (a >>> 32));
		int bi = (int)(b ^ (b >>> 32));
		int ci = (int)(c ^ (c >>> 32));
		
		return ai ^ Integer.rotateRight(bi, 11) ^ Integer.rotateLeft(ci, 11);
	}


	/**
	 * Constructs a clone of this Vector.
	 */
	@Override
	public vec3 clone()
	{
		return new vec3(x, y, z);
	}


	/**
	 * Returns a description of this object.
	 */
	@Override
	public String toString()
	{
		return ("{x=" + x + ", y=" + y + ", z=" + z + "}").replace(".0,", ",").replace(".0}", "}");
	}


	public boolean isValid()
	{
		return !Double.isNaN(x) && !Double.isInfinite(x) && !Double.isNaN(y) && !Double.isInfinite(y) && !Double.isNaN(z) && !Double.isInfinite(z);
	}



	/**
	 * Return XYZ as 8 bit RGB values packed into an int. Components are clamped into range 0-255 with X component shifted 16 bits, Y component shifted 8 bits.
	 */
	public int toRGB()
	{
		int r = Math.max(Math.min((int)x, 255), 0) << 16;
		int g = Math.max(Math.min((int)y, 255), 0) << 8;
		int b = Math.max(Math.min((int)z, 255), 0);

		return r + g + b;
	}

	
	public double getComponent(int aIndex)
	{
		switch (aIndex)
		{
			case 0: return x;
			case 1: return y;
			case 2: return z;
			default: throw new IllegalArgumentException("aIndex out of bounds: " + aIndex);
		}
	}
}
