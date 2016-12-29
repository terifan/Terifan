package org.terifan.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class CardinalSpline
{
	private Point[] mVertices;
	private int mQuality;
	private int mSegments;
	private double mTension;
	private double mSegmentLength;


	public CardinalSpline(Point... aVertices)
	{
		mVertices = aVertices;
		mSegmentLength = 10;
		mQuality = 1;
		mTension = 0.5;
	}


	/**
	 * Sets the Spline tension.
	 *
	 * @param aTension
	 *   Range 0 to 1 with 0 being straight lines between vertices and 1 being very round shapes.
	 */
	public CardinalSpline setTension(double aTension)
	{
		if (aTension < 0 || aTension > 1)
		{
			throw new IllegalArgumentException();
		}

		mTension = aTension;
		return this;
	}


	/**
	 * Sets the number of segment between each vertex.
	 */
	public CardinalSpline setSegments(int aSegments)
	{
		if (aSegments < 1 || aSegments > 1000)
		{
			throw new IllegalArgumentException();
		}

		mSegments = aSegments;
		mSegmentLength = 0;
		return this;
	}


	/**
	 * Sets the preferred length of each segment between vertices.
	 */
	public CardinalSpline setSegmentLength(double aSegmentLength)
	{
		if (aSegmentLength <= 0)
		{
			throw new IllegalArgumentException();
		}

		mSegments = 0;
		mSegmentLength = aSegmentLength;
		return this;
	}


	/**
	 * Sets the measurement quality.
	 *
	 * @param aQuality
	 *   range 1 to 100 with 1 being lowest quality.
	 */
	public CardinalSpline setQuality(int aQuality)
	{
		if (aQuality < 1 || aQuality > 100)
		{
			throw new IllegalArgumentException();
		}

		mQuality = aQuality;
		return this;
	}


	public ArrayList<Point> generate()
	{
		ArrayList<Point> output = new ArrayList<>();

		for (int i = 0; i < mVertices.length - 1; i++)
		{
			Point prevVal = mVertices[Math.max(i-1, 0)];
			Point startVal = mVertices[i];
			Point endVal = mVertices[i+1];
			Point nextVal = mVertices[Math.min(i+2, mVertices.length-1)];

			double[] lengths = new double[mQuality];
			double qualityStep = 1.0 / mQuality;
			double totalLength = 0;
			Point prev = null;

			for (int j = 0; j <= mQuality; j++)
			{
				double alpha = j * qualityStep;

				double t = mTension;//0.5 + 0.4 * qualityStep;

				Point next = computeCardinalSplinePoint(prevVal, startVal, endVal, nextVal, alpha, new Point(), t);

				if (prev != null)
				{
					lengths[j - 1] = prev.distance(next);
					totalLength += lengths[j - 1];
				}

				prev = next;
			}

			int segments;
			if (mSegments > 0)
			{
				segments = mSegments;
			}
			else
			{
				segments = (int)Math.ceil(totalLength / mSegmentLength);
			}

			double segmentStep = 1.0 / segments;

			for (int j = 0; j <= segments; j++)
			{
				double alpha = j * segmentStep;

				if (mQuality > 1 && alpha > 0 && alpha < 1)
				{
					alpha = adjustAlpha(alpha * totalLength, lengths, qualityStep);
				}

				Point next = new Point();

				double t = mTension;//0.5 + 0.4 * alpha;

				output.add(computeCardinalSplinePoint(prevVal, startVal, endVal, nextVal, alpha, next, t));
			}
		}

		return output;
	}


	private double adjustAlpha(double aTargetLength, double[] aLengths, double aQualityStep)
	{
		double accum = 0;

		for (int i = 0; i < mQuality; i++)
		{
			double current = accum;

			accum += aLengths[i];

			if (accum > aTargetLength)
			{
				return (i + (aTargetLength - current) / (accum - current)) * aQualityStep;
			}
		}

		return (mQuality - 1 + (aTargetLength - accum) / (1 - accum)) * aQualityStep;
	}


	private Point computeCardinalSplinePoint(Point aPrevVal, Point aStartVal, Point aEndVal, Point aNextVal, double aAlpha, Point aOut, double mTension)
	{
		aOut.x = (int)computeCardinalSplinePoint(aPrevVal.x, aStartVal.x, aEndVal.x, aNextVal.x, aAlpha, mTension);
		aOut.y = (int)computeCardinalSplinePoint(aPrevVal.y, aStartVal.y, aEndVal.y, aNextVal.y, aAlpha, mTension);

		return aOut;
	}


	private double computeCardinalSplinePoint(double aPrevVal, double aStartVal, double aEndVal, double aNextVal, double aAlpha, double mTension)
	{
		double alpha2 = aAlpha * aAlpha;
		double alpha3 = alpha2 * aAlpha;

		double h1 = (2 * alpha3) - (3 * alpha2) + 1;
		double h2 = -(2 * alpha3) + (3 * alpha2);
		double h3 = alpha3 - (2 * alpha2) + aAlpha;
		double h4 = alpha3 - alpha2;

		return (h1 * aStartVal) + (h2 * aEndVal) + (h3 * (aEndVal - aPrevVal) * mTension) + (h4 * (aNextVal - aStartVal) * mTension);
	}


	public static void main(String ... args)
	{
		try
		{
			JFrame frame = new JFrame();
			frame.add(new JPanel()
			{
				@Override
				protected void paintComponent(Graphics aGraphics)
				{
					aGraphics.setColor(Color.WHITE);
					aGraphics.fillRect(0, 0, getWidth(), getHeight());

					Point[] verts = {
						new Point(300,50),
						new Point(150,120),
						new Point(300,200),
						new Point(900,400),
						new Point(600,600),
						new Point(150,200)
					};

					aGraphics.setColor(Color.RED);
					draw(aGraphics, new CardinalSpline(verts).setTension(0.2).setSegments(10).setQuality(10));
					aGraphics.setColor(Color.BLUE);
					draw(aGraphics, new CardinalSpline(verts).setTension(0.5).setSegments(10).setQuality(10));
					aGraphics.setColor(Color.GREEN);
					draw(aGraphics, new CardinalSpline(verts).setTension(0.8).setSegments(10).setQuality(10));

					aGraphics.setColor(Color.BLACK);
					for (Point v : verts)
					{
						aGraphics.drawLine((int)v.x-5, (int)v.y-5, (int)v.x+5, (int)v.y+5);
						aGraphics.drawLine((int)v.x+5, (int)v.y-5, (int)v.x-5, (int)v.y+5);
					}
				}

				private void draw(Graphics aGraphics, CardinalSpline aSpline)
				{
					Point prev = null;
					for (Point next : aSpline.generate())
					{
						if (prev != null)
						{
							aGraphics.drawLine((int)prev.x, (int)prev.y, (int)next.x, (int)next.y);
						}
						aGraphics.fillOval((int)next.x-2, (int)next.y-2, 5, 5);
						prev = next;
					}
				}
			});
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
