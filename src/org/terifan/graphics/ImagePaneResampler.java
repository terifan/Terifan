package org.terifan.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import org.terifan.util.log.Log;


class ImagePaneResampler extends Thread
{
	private AtomicBoolean mAbort;
	private BufferedImage mInput;
	private ResampledImageCallback mCallback;
	private int mSrcX;
	private int mSrcY;
	private int mSrcWidth;
	private int mSrcHeight;
	private int mDstWidth;
	private int mDstHeight;


	public ImagePaneResampler(BufferedImage aImage, int aSrcX, int aSrcY, int aSrcWidth, int aSrcHeight, int aDstWidth, int aDstHeight, ResampledImageCallback aCallback)
	{
		mInput = aImage;
		mSrcX = aSrcX;
		mSrcY = aSrcY;
		mSrcWidth = aSrcWidth;
		mSrcHeight = aSrcHeight;
		mDstWidth = aDstWidth;
		mDstHeight = aDstHeight;
		mCallback = aCallback;
		mAbort = new AtomicBoolean();
	}


	public void abort()
	{
		mAbort.set(true);
	}


	@Override
	public void run()
	{
		BufferedImage temp = new BufferedImage(mSrcWidth, mSrcHeight, BufferedImage.TYPE_INT_RGB);

		Graphics g = temp.createGraphics();
		g.drawImage(mInput, 0, 0, mSrcWidth, mSrcHeight, mSrcX, mSrcY, mSrcX + mSrcWidth, mSrcY + mSrcHeight, null);
		g.dispose();

//		BufferedImage output = ImageResizer.resize(temp, mDstWidth, mDstHeight, FilterFactory.Lanczos3, mAbort);
		BufferedImage output = ImageResizer.resize(temp, mDstWidth, mDstHeight, FilterFactory.Box, mAbort);
		Log.out.println(mDstWidth+" "+mDstHeight);
		if (!mAbort.get())
		{
			mCallback.update(output);
		}
	}
	
	
	public interface ResampledImageCallback
	{
		public void update(BufferedImage aOutput);
	}
}
