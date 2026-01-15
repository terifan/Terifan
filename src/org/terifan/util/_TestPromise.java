package org.terifan.util;

import java.util.concurrent.CompletableFuture;


public class _TestPromise
{
//	static class Panel extends JPanel
//	{
//		BufferedImage mImage;
//
//		void setImage(BufferedImage aImage)
//		{
//			mImage = aImage;
//			repaint();
//		}
//
//
//		public BufferedImage getImage()
//		{
//			return mImage;
//		}
//
//
//		@Override
//		protected void paintComponent(Graphics aG)
//		{
//			aG.drawImage(mImage, 0, 0, this);
//		}
//	}
//
//	static class FilterTask implements Future.Task<int[]>
//	{
//		private BufferedImage mImage;
//		private int mX;
//		private int mY;
//
//		public FilterTask(BufferedImage aImage, int aX, int aY)
//		{
//			mImage = aImage;
//			mX = aX;
//			mY = aY;
//		}
//
//
//		@Override
//		public int[] run()
//		{
//			int[] im = new int[16 * 16];
//
//			double[][] kernel = {
//	{0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0028,0.0582,0.0914,0.1025,0.0914,0.0582,0.0028,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000},
//	{0.0000,0.0000,0.0000,0.0000,0.0139,0.1136,0.1911,0.2465,0.2798,0.2909,0.2798,0.2465,0.1911,0.1136,0.0139,0.0000,0.0000,0.0000,0.0000},
//	{0.0000,0.0000,0.0000,0.0582,0.1801,0.2798,0.3573,0.4127,0.4460,0.4571,0.4460,0.4127,0.3573,0.2798,0.1801,0.0582,0.0000,0.0000,0.0000},
//	{0.0000,0.0000,0.0582,0.2022,0.3241,0.4238,0.5014,0.5568,0.5900,0.6011,0.5900,0.5568,0.5014,0.4238,0.3241,0.2022,0.0582,0.0000,0.0000},
//	{0.0000,0.0139,0.1801,0.3241,0.4460,0.5457,0.6233,0.6787,0.7119,0.7230,0.7119,0.6787,0.6233,0.5457,0.4460,0.3241,0.1801,0.0139,0.0000},
//	{0.0000,0.1136,0.2798,0.4238,0.5457,0.6454,0.7230,0.7784,0.8116,0.8227,0.8116,0.7784,0.7230,0.6454,0.5457,0.4238,0.2798,0.1136,0.0000},
//	{0.0028,0.1911,0.3573,0.5014,0.6233,0.7230,0.8006,0.8560,0.8892,0.9003,0.8892,0.8560,0.8006,0.7230,0.6233,0.5014,0.3573,0.1911,0.0028},
//	{0.0582,0.2465,0.4127,0.5568,0.6787,0.7784,0.8560,0.9114,0.9446,0.9557,0.9446,0.9114,0.8560,0.7784,0.6787,0.5568,0.4127,0.2465,0.0582},
//	{0.0914,0.2798,0.4460,0.5900,0.7119,0.8116,0.8892,0.9446,0.9778,0.9889,0.9778,0.9446,0.8892,0.8116,0.7119,0.5900,0.4460,0.2798,0.0914},
//	{0.1025,0.2909,0.4571,0.6011,0.7230,0.8227,0.9003,0.9557,0.9889,1.0000,0.9889,0.9557,0.9003,0.8227,0.7230,0.6011,0.4571,0.2909,0.1025},
//	{0.0914,0.2798,0.4460,0.5900,0.7119,0.8116,0.8892,0.9446,0.9778,0.9889,0.9778,0.9446,0.8892,0.8116,0.7119,0.5900,0.4460,0.2798,0.0914},
//	{0.0582,0.2465,0.4127,0.5568,0.6787,0.7784,0.8560,0.9114,0.9446,0.9557,0.9446,0.9114,0.8560,0.7784,0.6787,0.5568,0.4127,0.2465,0.0582},
//	{0.0028,0.1911,0.3573,0.5014,0.6233,0.7230,0.8006,0.8560,0.8892,0.9003,0.8892,0.8560,0.8006,0.7230,0.6233,0.5014,0.3573,0.1911,0.0028},
//	{0.0000,0.1136,0.2798,0.4238,0.5457,0.6454,0.7230,0.7784,0.8116,0.8227,0.8116,0.7784,0.7230,0.6454,0.5457,0.4238,0.2798,0.1136,0.0000},
//	{0.0000,0.0139,0.1801,0.3241,0.4460,0.5457,0.6233,0.6787,0.7119,0.7230,0.7119,0.6787,0.6233,0.5457,0.4460,0.3241,0.1801,0.0139,0.0000},
//	{0.0000,0.0000,0.0582,0.2022,0.3241,0.4238,0.5014,0.5568,0.5900,0.6011,0.5900,0.5568,0.5014,0.4238,0.3241,0.2022,0.0582,0.0000,0.0000},
//	{0.0000,0.0000,0.0000,0.0582,0.1801,0.2798,0.3573,0.4127,0.4460,0.4571,0.4460,0.4127,0.3573,0.2798,0.1801,0.0582,0.0000,0.0000,0.0000},
//	{0.0000,0.0000,0.0000,0.0000,0.0139,0.1136,0.1911,0.2465,0.2798,0.2909,0.2798,0.2465,0.1911,0.1136,0.0139,0.0000,0.0000,0.0000,0.0000},
//	{0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0028,0.0582,0.0914,0.1025,0.0914,0.0582,0.0028,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000}
//};
//			int fs = kernel.length / 2;
//
//			for (int y = 0, o = 0; y < 16; y++)
//			{
//				for (int x = 0; x < 16; x++)
//				{
//					double r = 0, g = 0, b = 0, v = 0;
//					for (int fy = -fs; fy <= fs; fy++)
//					{
//						for (int fx = -fs; fx <= fs; fx++)
//						{
//							int ix = mX + x + fx;
//							int iy = mY + y + fy;
//							if (ix >= 0 && iy >= 0 && ix < mImage.getWidth() && iy < mImage.getHeight())
//							{
//								int c = mImage.getRGB(ix, iy);
//								int ir = 0xff & (c >>> 16);
//								int ig = 0xff & (c >>> 8);
//								int ib = 0xff & (c);
//								double f = kernel[fs + fy][fs + fx];
//								r += f * ir;
//								g += f * ig;
//								b += f * ib;
//								v += f;
//							}
//						}
//					}
//
//					int ir = Math.max(0, Math.min(255, (int)(r / v)));
//					int ig = Math.max(0, Math.min(255, (int)(g / v)));
//					int ib = Math.max(0, Math.min(255, (int)(b / v)));
//					im[o++] = (ir << 16) + (ig << 8) + ib;
//				}
//			}
//
//			return im;
//		}
//	}
//
//	static class Painter implements Consumer<int[]>
//	{
//		private final Panel mPanel;
//		private final int mX;
//		private final int mY;
//
//		public Painter(Panel aPanel, int aX, int aY)
//		{
//			mPanel = aPanel;
//			mX = aX;
//			mY = aY;
//		}
//
//
//		@Override
//		public void accept(int[] aRaster)
//		{
//			mPanel.getImage().setRGB(mX, mY, 16, 16, aRaster, 0, 16);
//			mPanel.repaint();
//		}
//	}
//
//
//	public static void xxmain(String ... args)
//	{
//		try
//		{
//			Panel panel = new Panel();
//
//			JFrame frame = new JFrame();
//			frame.add(panel);
//			frame.setSize(1024, 768);
//			frame.setLocationRelativeTo(null);
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.setVisible(true);
//
////			Promise<BufferedImage> p = new Promise<>(() -> ImageIO.read(new File("d:\\desktop\\_sent_juncker1 (12).jpg")))
////				.then(im->panel.setImage(im));
//
//			CompletableFuture<BufferedImage> p = CompletableFuture.supplyAsync(() ->
//			{
//				try
//				{
//					return ImageIO.read(new File("d:\\home\\pictures\\floridaman.jpg"));
//				}
//				catch (Exception e)
//				{
//					throw new IllegalStateException(e);
//				}
//			});
//
//			BufferedImage src = p.get();
//			panel.setImage(src);
//
//			for (int i = 0; i < 10; i++)
//			{
//				BufferedImage workImage = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
//				Graphics2D g = workImage.createGraphics();
//				g.drawImage(src, 0, 0, null);
//				g.dispose();
//
////				ArrayList<CompletableFuture> futureList = new ArrayList<>();
//				try (FixedThreadExecutor executor = new FixedThreadExecutor(8))
//				{
//
//				for (int y = 0; y < workImage.getHeight()-15; y+=16)
//				{
//					for (int x = 0; x < workImage.getWidth()-15; x+=16)
//					{
//						//					Promise<int[]> q = new Promise<>(new GaussianTask(image, x, y));
//						//
//						//					q.handle(new Painter(panel, x, y));
//
//
//						FilterTask task = new FilterTask(workImage, x, y);
//						Painter painter = new Painter(panel, x, y);
//						executor.submit(()->painter.accept(task.run()));
//
//
////						FilterTask task = new FilterTask(workImage, x, y);
////
////						futureList.add(CompletableFuture
////							.supplyAsync(()->task.run())
////							.thenAcceptAsync(new Painter(panel, x, y))
////						);
//					}
//				};
//
////				CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
//				}
//			}
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
//
//
//	public static void main(String... args)
//	{
//		try
//		{
//			Future<Integer> p = new Future<>(() -> 1);
//			Future<Double> q = new Future<>(() -> 3.0);
//
//			p.complete(10);
//
//			p.then(r -> System.out.println("ok " + r));
//			p.then(r -> System.out.println("ok " + r), r -> System.out.println("error " + r));
//			p.onError(r -> System.out.println("error " + r));
//			p.onFinally(() -> System.out.println("done"));
//
//			System.out.println("----------");
//
//			CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> 1);
//
//			System.out.println(f.get());
//
//
////			Future<Integer> a = find("xxx");
////			Future<Integer> b = find("xxx");
////
////			a.then(v -> {}).get();
////			b.then(v -> {}).get();
//
//
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}

	static Promise<Integer> convert(String aValue)
	{
		return new Promise<>(() -> Integer.parseInt(aValue));
	}

	public static void main(String... args)
	{
		try
		{
			Promise<Integer> p = convert("1");
			Promise<Integer> q = convert("2");

			p.then(r -> System.out.println("ok " + r));
			p.then(r -> System.out.println("ok " + r), r -> System.out.println("error " + r));

			System.out.println("----------");

			CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> 1);

			System.out.println(f.get());




//			Future<Integer> a = find("xxx");
//			Future<Integer> b = find("xxx");
//
//			a.then(v -> {}).get();
//			b.then(v -> {}).get();


		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
