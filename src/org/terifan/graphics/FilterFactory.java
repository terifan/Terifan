package org.terifan.graphics;

import java.awt.image.Kernel;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import org.terifan.util.log.Log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static org.terifan.graphics.FilterFactory.Blackman;
import static org.terifan.graphics.FilterFactory.Bohman;
import static org.terifan.graphics.FilterFactory.Box;
import static org.terifan.graphics.FilterFactory.Catrom;
import static org.terifan.graphics.FilterFactory.Cubic;
import static org.terifan.graphics.FilterFactory.FIXED_POINT_SCALE;
import static org.terifan.graphics.FilterFactory.Gaussian;
import static org.terifan.graphics.FilterFactory.Hamming;
import static org.terifan.graphics.FilterFactory.Hanning;
import static org.terifan.graphics.FilterFactory.Hermite;
import static org.terifan.graphics.FilterFactory.Jinc;
import static org.terifan.graphics.FilterFactory.Kasier;
import static org.terifan.graphics.FilterFactory.Lanczos3;
import static org.terifan.graphics.FilterFactory.Mitchell;
import static org.terifan.graphics.FilterFactory.Quadratic;
import static org.terifan.graphics.FilterFactory.Sinc;
import static org.terifan.graphics.FilterFactory.Triangle;
import static org.terifan.graphics.FilterFactory.Welch;
import static org.terifan.graphics.FilterFactory.values;


public class FilterFactory
{
	public final static int FIXED_POINT_SCALE = 65536;


	public static FilterFactory.Filter [] values()
	{
		return new FilterFactory.Filter[]
		{
			Blackman,
			Bohman,
			Box,
			Catrom,
			Cubic,
			Gaussian,
			Hamming,
			Hanning,
			Hermite,
			Jinc,
			Kasier,
			Lanczos3,
			Mitchell,
			Quadratic,
			Sinc,
			Triangle,
			Welch
		};
	}


	public static abstract class Filter
	{
		private String mName;
		private double mRadius;


		public Filter(String aName, double aRadius)
		{
			mName = aName;
			mRadius = aRadius;
		}


		public String getName()
		{
			return mName;
		}


		public double getRadius()
		{
			return mRadius;
		}


		@Override
		public String toString()
		{
			return mName;
		}


		public Kernel getKernel(int aDiameter)
		{
			double[][] kernel = getKernel2D(aDiameter);
			float[] tmp = new float[aDiameter * aDiameter];

			for (int y = 0, i = 0; y < aDiameter; y++)
			{
				for (int x = 0; x < aDiameter; x++, i++)
				{
					tmp[i] = (float)kernel[y][x];
				}
			}

			return new Kernel(aDiameter, aDiameter, tmp);
		}


		public int[][] getKernel2DInt(int aDiameter)
		{
			double[][] kernel = getKernel2D(aDiameter);
			int[][] tmp = new int[aDiameter][aDiameter];

			for (int y = 0; y < aDiameter; y++)
			{
				for (int x = 0; x < aDiameter; x++)
				{
					tmp[y][x] = (int)(FIXED_POINT_SCALE * kernel[y][x] + 0.5);
				}
			}

			return tmp;
		}


		public double[][] getKernel2D(int aDiameter)
		{
			double step = mRadius / (aDiameter / 2.0);
			double step2 = step * step;
			double c = (aDiameter - 1) / 2.0;

			double[][] kernel = new double[aDiameter][aDiameter];
			double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;

			for (int y = 0; y < aDiameter; y++)
			{
				for (int x = 0; x < aDiameter; x++)
				{
					double d = sqrt(((x - c) * (x - c) + (y - c) * (y - c)) * step2);
					double v = filter(d);

					kernel[y][x] = v;

					if (v > max)
					{
						max = v;
					}
					else if (v < min)
					{
						min = v;
					}
				}
			}

			double scale = 1.0 / Math.max(Math.abs(max), Math.abs(min));

			for (int y = 0; y < aDiameter; y++)
			{
				for (int x = 0; x < aDiameter; x++)
				{
					kernel[y][x] *= scale;
				}
			}

			return kernel;
		}


		public int[] getKernel1DInt(int aDiameter)
		{
			double[] kernel = getKernel1D(aDiameter);
			int[] tmp = new int[aDiameter];

			for (int x = 0; x < aDiameter; x++)
			{
				tmp[x] = (int)(FIXED_POINT_SCALE * kernel[x] + 0.5);
			}

			return tmp;
		}


		public double[] getKernel1D(int aDiameter)
		{
			double step = mRadius / ((aDiameter-1) / 2.0);
			double c = (aDiameter - 1) / 2.0;

			double[] kernel = new double[aDiameter];
			double sum = 0;

			for (int x = 0; x < aDiameter; x++)
			{
				double d = (x - c) * step;
				double v = filter(d);

				kernel[x] = v;
				sum += v;
			}

			if (sum != 0)
			{
				for (int x = 0; x < aDiameter; x++)
				{
					kernel[x] /= sum;
				}
			}

			return kernel;
		}


		public abstract double filter(double x);
	}


	public final static FilterFactory.Filter Box = new FilterFactory.Filter("Box", 0.5)
	{
		@Override
		public double filter(double x)
		{
			if (x < -0.5)
			{
				return 0;
			}
			if (x < 0.5)
			{
				return 1;
			}
			return 0;
		}
	};


	public final static FilterFactory.Filter Triangle = new FilterFactory.Filter("Triangle", 1.0)
	{
		@Override
		public double filter(double x)
		{
			if (x < -1)
			{
				return 0;
			}
			if (x < 0)
			{
				return 1 + x;
			}
			if (x < 1)
			{
				return 1 - x;
			}
			return 0;
		}
	};


	public final static FilterFactory.Filter Quadratic = new FilterFactory.Filter("Quadratic", 1.5)
	{
		@Override
		public double filter(double x)
		{
			if (x < -1.5)
			{
				return 0;
			}
			if (x < -0.5)
			{
				double t = x + 1.5;
				return 0.5 * t * t;
			}
			if (x < 0.5)
			{
				return 0.75 - x * x;
			}
			if (x < 1.5)
			{
				double t = x - 1.5;
				return 0.5 * t * t;
			}
			return 0;
		}
	};


	public final static FilterFactory.Filter Cubic = new FilterFactory.Filter("Cubic", 2.0)
	{
		@Override
		public double filter(double x)
		{
			if (x < -2.0)
			{
				return 0;
			}
			if (x < -1.0)
			{
				double t = 2 + x;
				return t * t * t / 6;
			}
			if (x < 0.0)
			{
				return (4 + x * x * (-6 + x * -3)) / 6;
			}
			if (x < 1.0)
			{
				return (4 + x * x * (-6 + x * 3)) / 6;
			}
			if (x < 2.0)
			{
				double t = 2 - x;
				return t * t * t / 6;
			}
			return 0;
		}
	};


	public final static FilterFactory.Filter Catrom = new FilterFactory.Filter("Catrom", 2.0)
	{
		@Override
		public double filter(double x)
		{
			if (x < -2)
			{
				return 0.0;
			}
			if (x < -1)
			{
				return 0.5 * (4.0 + x * (8.0 + x * (5.0 + x)));
			}
			if (x < 0)
			{
				return 0.5 * (2.0 + x * x * (-5.0 + x * -3.0));
			}
			if (x < 1)
			{
				return 0.5 * (2.0 + x * x * (-5.0 + x * 3.0));
			}
			if (x < 2)
			{
				return 0.5 * (4.0 + x * (-8.0 + x * (5.0 - x)));
			}
			return 0.0;
		}
	};


	public final static FilterFactory.Filter Gaussian = new FilterFactory.Filter("Gaussian", 1.25)
	{
		@Override
		public double filter(double x)
		{
			return Math.exp(-2.0 * x * x) * sqrt(2.0 / PI);
		}
	};


	public final static FilterFactory.Filter Sinc = new FilterFactory.Filter("Sinc", 5.0)
	{
		@Override
		public double filter(double x)
		{
			if (x == 0)
			{
				return 1;
			}
			double alpha = PI * x;
			return sin(alpha) / alpha;
		}
	};


	public final static FilterFactory.Filter Welch = new FilterFactory.Filter("Welch", 1.0)
	{
		@Override
		public double filter(double x)
		{
			if (x < -1.0)
			{
				return 0;
			}
			if (x < 1.0)
			{
				return 1.0 - x * x;
			}
			return 0.0;
		}
	};


	public final static FilterFactory.Filter Mitchell = new FilterFactory.Filter("Mitchell", 2.0)
	{
        private static final double b = 1.0 / 3.0;
        private static final double c = 1.0 / 3.0;

		@Override
		public double filter(double x)
		{
			double p0 = (6.0 - 2.0 * b) / 6.0;
			double p2 = (-18.0 + 12.0 * b + 6.0 * c) / 6.0;
			double p3 = (12.0 - 9.0 * b - 6.0 * c) / 6.0;
			double q0 = (8.0 * b + 24.0 * c) / 6.0;
			double q1 = (-12.0 * b - 48.0 * c) / 6.0;
			double q2 = (6.0 * b + 30.0 * c) / 6.0;
			double q3 = (-b - 6.0 * c) / 6.0;

			if (x < -2.0)
			{
				return 0.0;
			}
			if (x < -1.0)
			{
				return q0 - x * (q1 - x * (q2 - x * q3));
			}
			if (x < 0.0)
			{
				return p0 + x * x * (p2 - x * p3);
			}
			if (x < 1.0)
			{
				return p0 + x * x * (p2 + x * p3);
			}
			if (x < 2.0)
			{
				return q0 + x * (q1 + x * (q2 + x * q3));
			}
			return 0.0;
		}
	};


	/**
	 * Source: http://code.google.com/p/java-image-scaling
	 */
	public final static FilterFactory.Filter Lanczos3 = new FilterFactory.Filter("Lanczos3", 3.0)
	{
		@Override
		public double filter(double x)
		{
			if (x == 0)
			{
				return 1.0;
			}
			if (x < 0.0)
			{
				x = -x;
			}
			if (x < 3.0)
			{
				x *= PI;
				return sin(x) / x * sin(x / 3.0) / (x / 3.0);
			}
			return 0.0;
		}
	};


	/**
	 * Source: http://code.google.com/p/java-image-scaling
	 */
	public final static FilterFactory.Filter Hermite = new FilterFactory.Filter("Hermite", 1.0)
	{
		@Override
		public double filter(double x)
		{
			if (x < 0.0)
			{
				x = - x;
			}
			if (x < 1.0)
			{
				return (2.0 * x - 3.0) * x * x + 1.0;
			}
			return 0.0;
		}
	};


	public final static FilterFactory.Filter Jinc = new FilterFactory.Filter("Jinc", 3.0)
	{
		@Override
		public double filter(double x)
		{
			if (x == 0.0)
			{
				return 0.5 * PI;
			}
			return BesselOrderOne(PI*x)/x;
		}

		private double BesselOrderOne(double x)
		{
			if (x == 0.0)
			{
				return 0.0;
			}
			double p = x;
			if (x < 0.0)
			{
				x = -x;
			}
			if (x < 8.0)
			{
				return p * J1(x);
			}
			double q = sqrt((2.0 / (PI * x))) * (P1(x) * (1.0 / sqrt(2.0) * (sin(x) - cos(x))) - 8.0 / x * Q1(x) * (-1.0 / sqrt(2.0) * (sin(x) + cos(x))));
			if (p < 0.0)
			{
				q = -q;
			}
			return q;
		}

		private double J1(double x)
		{
			double Pone[] =
			{
				0.581199354001606143928050809e+21,
				-0.6672106568924916298020941484e+20,
				0.2316433580634002297931815435e+19,
				-0.3588817569910106050743641413e+17,
				0.2908795263834775409737601689e+15,
				-0.1322983480332126453125473247e+13,
				0.3413234182301700539091292655e+10,
				-0.4695753530642995859767162166e+7,
				0.270112271089232341485679099e+4
			};
			double Qone[] =
			{
				0.11623987080032122878585294e+22,
				0.1185770712190320999837113348e+20,
				0.6092061398917521746105196863e+17,
				0.2081661221307607351240184229e+15,
				0.5243710262167649715406728642e+12,
				0.1013863514358673989967045588e+10,
				0.1501793594998585505921097578e+7,
				0.1606931573481487801970916749e+4,
				0.1e+1
			};

			double p = Pone[8];
			double q = Qone[8];
			for (int i = 7; i >= 0; i--)
			{
				p = p * x * x + Pone[i];
				q = q * x * x + Qone[i];
			}
			return p/q;
		}

		private double P1(double x)
		{
			double Pone[] =
			{
				0.352246649133679798341724373e+5,
				0.62758845247161281269005675e+5,
				0.313539631109159574238669888e+5,
				0.49854832060594338434500455e+4,
				0.2111529182853962382105718e+3,
				0.12571716929145341558495e+1
			};
			double Qone[] =
			{
				0.352246649133679798068390431e+5,
				0.626943469593560511888833731e+5,
				0.312404063819041039923015703e+5,
				0.4930396490181088979386097e+4,
				0.2030775189134759322293574e+3,
				0.1e+1
			};

			double p = Pone[5];
			double q = Qone[5];
			for (int i = 4; i >= 0; i--)
			{
				p = p * (8.0 / x) * (8.0 / x) + Pone[i];
				q = q * (8.0 / x) * (8.0 / x) + Qone[i];
			}
			return (p / q);
		}

		private double Q1(double x)
		{
			double Pone[] =
			{
				0.3511751914303552822533318e+3,
				0.7210391804904475039280863e+3,
				0.4259873011654442389886993e+3,
				0.831898957673850827325226e+2,
				0.45681716295512267064405e+1,
				0.3532840052740123642735e-1
			};
			double Qone[] =
			{
				0.74917374171809127714519505e+4,
				0.154141773392650970499848051e+5,
				0.91522317015169922705904727e+4,
				0.18111867005523513506724158e+4,
				0.1038187585462133728776636e+3,
				0.1e+1
			};

			double p = Pone[5];
			double q = Qone[5];
			for (int i = 4; i >= 0; i--)
			{
				p = p * (8.0 / x) * (8.0 / x) + Pone[i];
				q = q * (8.0 / x) * (8.0 / x) + Qone[i];
			}
			return (p / q);
		}
	};


	public final static FilterFactory.Filter Bohman = new FilterFactory.Filter("Bohman", 1.0)
	{
		@Override
		public double filter(double x)
		{
			if (x < 0)
			{
				x = -x;
			}
			if (x <= 1.0)
			{
				return (1 - x) * cos(PI * x) + sin(PI * x) / PI;
			}
			return 0;
		}
	};


	public final static FilterFactory.Filter Hanning = new FilterFactory.Filter("Hanning", 1.0)
	{
		@Override
		public double filter(double x)
		{
			return 0.5 + 0.5 * cos(PI * x);
		}
	};


	public final static FilterFactory.Filter Hamming = new FilterFactory.Filter("Hamming", 1.0)
	{
		@Override
		public double filter(double x)
		{
			return 0.54 + 0.46 * cos(PI * x);
		}
	};


	public final static FilterFactory.Filter Blackman = new FilterFactory.Filter("Blackman", 1.0)
	{
		@Override
		public double filter(double x)
		{
			return 0.42 + 0.50 * cos(PI * x) + 0.08 * cos(2.0 * PI * x);
		}
	};


	public final static FilterFactory.Filter Kasier = new FilterFactory.Filter("Kasier", 1.0)
	{
		@Override
		public double filter(double x)
		{
			return kaiser(x, 6.5, 0.0);
		}


		private double kaiser(double x, double a, double b)
		{
			double i0a = 1.0 / bessel_i0(a);

			return bessel_i0(a * sqrt(1.0 - x * x)) * i0a;
		}


		private double bessel_i0(double x)
		{
			double sum = 1.0;
			double y = x * x / 4.0;
			double t = y;
			for (int i = 2; t > 1e-7; i++)
			{
				sum += t;
				t *= y / (i * i);
			}
			return sum;
		}
	};
}
