package org.terifan.factory;


public class Demo
{
	public static void main(String ... args)
	{
		try
		{
			Factory factory = new Factory();
			factory.addNamedSupplier(Color.class, "border", () -> new Color(255, 0, 0));
			factory.addNamedSupplier(Color.class, "background", () -> new Color(0, 255, 0));
			factory.addNamedSupplier(Color.class, e -> e.equals("text") ? new Color(0, 0, 255) : e.equals("strange") ? new Color(123,57,204) : new Color(1,1,1));
			factory.addDefaultSupplier(Color.class, () -> new Color(50, 150, 250));
			factory.addTypeMapping(ColorSpace.class, RGBColorSpace.class);
			factory.addDefaultSupplier(ColorSpace.class, () -> new ColorSpace());

			ComponentByFields cls1 = factory.newInstance(ComponentByFields.class);
			ComponentByConstructor cls2 = factory.newInstance(ComponentByConstructor.class);
			ComponentBySetters cls3 = factory.newInstance(ComponentBySetters.class);
			ComponentByInitializer cls4 = factory.newInstance(ComponentByInitializer.class);
			ComponentByFactory cls5 = new ComponentByFactory(factory);

			System.out.println("fields: " + cls1);
			System.out.println("constr: " + cls2);
			System.out.println("setter: " + cls3);
			System.out.println("initia: " + cls4);
			System.out.println("factor: " + cls5);

			System.out.println(factory.newInstance(ComponentByAllTypes.class));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

	static class ComponentByAllTypes
	{
		private Color mBorderColor;
		private Color mBackgroundColor;
		private Color mTextColor;
		@Inject private Color mOtherColor;
		@Inject("strange") private Color mStrangeColor;

		@Inject
		public ComponentByAllTypes(@Named("border") Color aBorderColor)
		{
			mBorderColor = aBorderColor;
		}

		@Inject
		public void initialize(@Named("text") Color aTextColor)
		{
			mTextColor = aTextColor;
		}

		@Inject("background")
		public void setBackgroundColor(Color aBackgroundColor)
		{
			mBackgroundColor = aBackgroundColor;
		}

		@Override
		public String toString()
		{
			return "Component{" + "mBorderColor=" + mBorderColor + ", mBackgroundColor=" + mBackgroundColor + ", mTextColor=" + mTextColor + ", mOtherColor=" + mOtherColor + ", mStrangeColor=" + mStrangeColor + '}';
		}
	}

	static class ComponentByFactory
	{
		@Inject("border") private Color mBorderColor;
		@Inject("background") private Color mBackgroundColor;
		@Inject("text") private Color mTextColor;
		@Inject private Color mOtherColor;

		private Color mStrangeColor;

		public ComponentByFactory(Factory aFactory)
		{
			aFactory.prepareInstance(this);

			mStrangeColor = aFactory.newInstance(Color.class);
		}

		@Override
		public String toString()
		{
			return "Component{" + "mBorderColor=" + mBorderColor + ", mBackgroundColor=" + mBackgroundColor + ", mTextColor=" + mTextColor + ", mOtherColor=" + mOtherColor + ", mStrangeColor=" + mStrangeColor + '}';
		}
	}

	static class ComponentByFields
	{
		@Inject("border") private Color mBorderColor;
		@Inject("background") private Color mBackgroundColor;
		@Inject("text") private Color mTextColor;
		@Inject private Color mOtherColor;

		public ComponentByFields()
		{
		}

		@Override
		public String toString()
		{
			return "Component{" + "mBorderColor=" + mBorderColor + ", mBackgroundColor=" + mBackgroundColor + ", mTextColor=" + mTextColor + ", mOtherColor=" + mOtherColor + '}';
		}
	}

	static class ComponentByConstructor
	{
		private Color mBorderColor;
		private Color mBackgroundColor;
		private Color mTextColor;
		private Color mOtherColor;

		public ComponentByConstructor()
		{
		}

		@Inject
		public ComponentByConstructor(@Named("border") Color aBorderColor, @Named("background") Color aBackgroundColor, @Named("text") Color aTextColor, Color aOtherColor)
		{
			mBorderColor = aBorderColor;
			mBackgroundColor = aBackgroundColor;
			mTextColor = aTextColor;
			mOtherColor = aOtherColor;
		}

		@Override
		public String toString()
		{
			return "Component{" + "mBorderColor=" + mBorderColor + ", mBackgroundColor=" + mBackgroundColor + ", mTextColor=" + mTextColor + ", mOtherColor=" + mOtherColor + '}';
		}
	}

	static class ComponentBySetters
	{
		private Color mBorderColor;
		private Color mBackgroundColor;
		private Color mTextColor;
		private Color mOtherColor;

		public ComponentBySetters()
		{
		}

		@Override
		public String toString()
		{
			return "Component{" + "mBorderColor=" + mBorderColor + ", mBackgroundColor=" + mBackgroundColor + ", mTextColor=" + mTextColor + ", mOtherColor=" + mOtherColor + '}';
		}

		@Inject("border")
		public void setBorderColor(Color aBorderColor)
		{
			mBorderColor = aBorderColor;
		}

		@Inject("background")
		public void setBackgroundColor(Color aBackgroundColor)
		{
			mBackgroundColor = aBackgroundColor;
		}

		@Inject("text")
		public void setTextColor(Color aTextColor)
		{
			mTextColor = aTextColor;
		}

		@Inject
		public void setOtherColor(Color aOtherColor)
		{
			mOtherColor = aOtherColor;
		}
	}

	static class ComponentByInitializer
	{
		private Color mBorderColor;
		private Color mBackgroundColor;
		private Color mTextColor;
		private Color mOtherColor;

		public ComponentByInitializer()
		{
		}

		@Override
		public String toString()
		{
			return "Component{" + "mBorderColor=" + mBorderColor + ", mBackgroundColor=" + mBackgroundColor + ", mTextColor=" + mTextColor + ", mOtherColor=" + mOtherColor + '}';
		}

		@Inject
		public void initialize(@Named("border") Color aBorderColor, @Named("background") Color aBackgroundColor, @Named("text") Color aTextColor, Color aOtherColor)
		{
			mBorderColor = aBorderColor;
			mBackgroundColor = aBackgroundColor;
			mTextColor = aTextColor;
			mOtherColor = aOtherColor;
		}
	}

	static class Color
	{
		@Inject int r;
		@Inject int g;
		@Inject int b;
		@Inject ColorSpace mColorSpace;

		public Color()
		{
		}

		public Color(int aR, int aG, int aB)
		{
			r = aR;
			g = aG;
			b = aB;
		}

		@Override
		public String toString()
		{
			return "Color{" + "r=" + r + ", g=" + g + ", b=" + b + ", coloSpace=" + mColorSpace + '}';
		}
	}

	static class ColorSpace
	{
		protected String x = "cs";

		@Override
		public String toString()
		{
			return "ColorSpace{" + "x=" + x + '}';
		}
	}

	static class RGBColorSpace extends ColorSpace
	{
		protected String y = "rgb-cs";

		@Override
		public String toString()
		{
			return "RGBColorSpace{" + "x=" + x + ", y=" + y + '}';
		}
	}
}
