package org.terifan.math;

import java.io.Serializable;


public class vec2 implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;

	public final double x, y;


	public vec2(double aX, double aY)
	{
		x = aX;
		y = aY;
	}


	public static vec2 as(double aX, double aY)
	{
		return new vec2(aX, aY);
	}


	public double distanceSqr(vec2 v)
	{
		return (x - v.x) * (x - v.x) + (y - v.y) * (y - v.y);
	}


	public double distance(vec2 v)
	{
		return Math.sqrt(distanceSqr(v));
	}


	public vec2 add(vec2 v)
	{
		return new vec2(x + v.x, y + v.y);
	}


	public vec2 add(double x, double y)
	{
		return new vec2(x + this.x, y + this.y);
	}


	public vec2 subtract(vec2 v)
	{
		return new vec2(x - v.x, y - v.y);
	}


	public vec2 subtract(double x, double y)
	{
		return new vec2(x - this.x, y - this.y);
	}


	public vec2 multiply(vec2 v)
	{
		return new vec2(x * v.x, y * v.y);
	}


	public vec2 multiply(double x, double y)
	{
		return new vec2(x * this.x, y * this.y);
	}


	public vec2 multiply(double s)
	{
		return new vec2(x * s, y * s);
	}


	public double dot(vec2 v)
	{
		return x * v.x + y * v.y;
	}


	@Override
	protected Object clone()
	{
		return new vec2(x, y);
	}


	public double distanceLineSegment(vec2 v, vec2 w)
	{
		double l2 = v.distanceSqr(w);

		if (l2 == 0.0)
		{
			return distance(v);
		}

		vec2 vw = w.subtract(v);

		double t = (dot(vw) - v.dot(vw)) / l2;

		vec2 z;

		if (t < 0.0)
		{
			z = v;
		}
		else if (t > 1.0)
		{
			z = w;
		}
		else
		{
			z = v.add(vw.multiply(t));
		}

		return distance(z);
	}
}
