package org.terifan.util.concurrent;


public class _TestAsync
{
	public static void main(String ... args)
	{
		try
		{
			AsyncTask<String, Integer, String> task1 = new AsyncTask<String, Integer, String>()
			{
				@Override
				protected String doInBackground(String aParam) throws Throwable
				{
					System.out.println("started 1");
					Thread.sleep(2000);
					System.out.println("finished 1");
					return "";
				}


				@Override
				protected void onCancelled(String aResult) throws Throwable
				{
					System.out.println("cancelled 1");
				}


				@Override
				protected void onError(Throwable aThrowable) throws Throwable
				{
					System.out.println("error 1");
				}


				@Override
				protected void onPreExecute() throws Throwable
				{
					System.out.println("pre 1");
				}


				@Override
				protected void onPostExecute(String aResult) throws Throwable
				{
					System.out.println("post 1");
				}


				@Override
				protected void onResultUpdate(String aResult)
				{
					System.out.println("result update 1");
				}


				@Override
				protected void onProgressUpdate(Integer aProgress)
				{
					System.out.println("update 1");
				}
			}.execute();

			System.out.println("waiting 1");

			AsyncTask<String, Integer, String> task2 = new AsyncTask<String, Integer, String>()
			{
				@Override
				protected String doInBackground(String aParam) throws Throwable
				{
					System.out.println("started 2");
					Thread.sleep(2000);
					System.out.println("finished 2");
					return "";
				}


				@Override
				protected void onCancelled(String aResult) throws Throwable
				{
					System.out.println("cancelled 2");
				}


				@Override
				protected void onError(Throwable aThrowable) throws Throwable
				{
					System.out.println("error 2");
				}


				@Override
				protected void onPreExecute() throws Throwable
				{
					System.out.println("pre 2");
				}


				@Override
				protected void onPostExecute(String aResult) throws Throwable
				{
					System.out.println("post 2");
				}


				@Override
				protected void onResultUpdate(String aResult)
				{
					System.out.println("result update 2");
				}


				@Override
				protected void onProgressUpdate(Integer aProgress)
				{
					System.out.println("update 2");
				}
			}.execute();

			System.out.println("waiting 2");
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


//	public static void xmain(String... args)
//	{
//		try
//		{
//			for (int i = 0; i < 1000; i++)
//			{
//				AsyncTask<Float, Integer, String> task = new AsyncTask<Float, Integer, String>()
//				{
//					@Override
//					protected String doInBackground(Float aParam) throws Throwable
//					{
//						for (int i = 0; i <= 10; i++)
//						{
//							onProgressUpdate(10 * i);
//						}
//						return "value=" + aParam;
//					}
//
//
//					@Override
//					protected void onPostExecute(String aResult) throws Throwable
//					{
//						System.out.println(aResult);
//					}
//
//
//					@Override
//					protected void onProgressUpdate(Integer aProgress)
//					{
//						System.out.println(aProgress + "%");
//					}
//				};
//
//				task.execute((float)i);
//			}
//
//			AsyncTask.execute(() -> System.out.println("+".repeat(100)));
//
//			System.out.println("#".repeat(100));
//
//			new Thread()
//			{
//				@Override
//				public void run()
//				{
//					for (int _i = 0; _i < 100; _i++)
//					{
//						int i = _i;
//						AsyncTask.execute(() -> System.out.println("*".repeat(10)+" "+i));
//					}
//				}
//			}.start();
//
//			AsyncTask.waitFor();
//
//			System.out.println("-".repeat(100));
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}
