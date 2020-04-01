package org.terifan.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


public class RelativeLayout implements LayoutManager2
{
	private LinkedHashMap<Element, Constraints> mElements;


	public RelativeLayout()
	{
		mElements = new LinkedHashMap<>();
	}


	public void add(Component aComponent, Constraints aConstraints)
	{
		Element element = new Element(aComponent);

		mElements.put(element, aConstraints == null ? new Constraints(-1, element) : aConstraints);


	}


	@Override
	public void addLayoutComponent(Component aComponent, Object aConstraints)
	{
	}


	@Override
	public void layoutContainer(Container aParent)
	{
		int pw = aParent.getWidth();
		int ph = aParent.getHeight();

		long workId = System.nanoTime();

		ArrayList<Element> pendingElements = new ArrayList<>();

		for (int i = 0; i < aParent.getComponentCount(); i++)
		{
			pendingElements.addAll(mElements.keySet());
		}

		for (int i = 0; !pendingElements.isEmpty(); i++)
		{
			Element element = pendingElements.get(i % pendingElements.size());
			Constraints constraints = mElements.get(element);

			Dimension dim = element.getPreferredSize();

			int x = 0;
			int y = 0;
			boolean isReady = true;

			Element relative = constraints.mRelativeTo;
			Constraints parentConstraints = mElements.get(relative);
//			isReady &= parentConstraints.mWorkId == workId || parentConstraints.mLayoutRule == -1;

//			if (isReady)
			{
				int rx = relative.getX();
				int ry = relative.getY();

				switch (constraints.mLayoutRule)
				{
					case -1:
						x = 0;
						y = 0;
						break;
					case 0:
						x = relative.getX() - dim.width;
						y = ry;
						break;
					case 1:
						x = relative.getX() + relative.getWidth();
						y = ry;
						break;
					case 2:
						y = relative.getY() + relative.getHeight();
						x = rx;
						break;
					case 3:
						y = relative.getY() - relative.getHeight();
						x = rx;
						break;
					default:
						throw new IllegalArgumentException();
				}

				if (constraints.mFill == Fill.HORIZONTAL)
				{
					dim.width = pw - x;
				}
				if (constraints.mFill == Fill.VERTICAL)
				{
					dim.height = ph - y;
				}
				if (constraints.mAnchor == Anchor.EAST)
				{
					x = pw - dim.width;
				}

				constraints.mWorkId = workId;
				element.setBounds(x, y, dim.width, dim.height);

				pendingElements.remove(element);
			}
		}
	}


	@Override
	public float getLayoutAlignmentX(Container aTarget)
	{
		return 0f;
	}


	@Override
	public float getLayoutAlignmentY(Container aTarget)
	{
		return 0f;
	}


	@Override
	public void addLayoutComponent(String aName, Component aComp)
	{
	}


	@Override
	public void removeLayoutComponent(Component aComp)
	{
		mElements.remove(new Element(aComp));
	}


	@Override
	public void invalidateLayout(Container aTarget)
	{
	}


	@Override
	public Dimension preferredLayoutSize(Container aParent)
	{
		return new Dimension(0, 0);
	}


	@Override
	public Dimension maximumLayoutSize(Container aTarget)
	{
		return new Dimension(0, 0);
	}


	@Override
	public Dimension minimumLayoutSize(Container aParent)
	{
		return new Dimension(0, 0);
	}


	public static Constraints leftOf(Component aRelativeTo)
	{
		return new Constraints(0, new Element(aRelativeTo));
	}


	public static Constraints rightOf(Component aRelativeTo)
	{
		return new Constraints(1, new Element(aRelativeTo));
	}


	public static Constraints rightOf(Group aGroup)
	{
		return new Constraints(1, new Element(aGroup));
	}


	public static Constraints below(Component aRelativeTo)
	{
		return new Constraints(2, new Element(aRelativeTo));
	}


	public static Constraints above(Component aRelativeTo)
	{
		return new Constraints(3, new Element(aRelativeTo));
	}


	public void layoutOn(JComponent aComponent)
	{
		aComponent.setLayout(this);
		for (Element e : mElements.keySet())
		{
			if (e.mElement instanceof Component)
			{
				aComponent.add(e.asComponent());
			}
		}
	}


	public static class Constraints
	{
		int mLayoutRule;
		Element mRelativeTo;
		long mWorkId;
		Fill mFill;
		Anchor mAnchor;

		Constraints(int aLayoutRule, Element aRelativeTo)
		{
			mLayoutRule = aLayoutRule;
			mRelativeTo = aRelativeTo;
		}

		Constraints anchorEast()
		{
			mAnchor = Anchor.EAST;
			return this;
		}

		Constraints alignLeft(Component aRelativeTo)
		{
			return this;
		}
	}


	private static class Element
	{
		Object mElement;

		public Element(Component aElement)
		{
			mElement = aElement;
		}

		public Element(Group aElement)
		{
			mElement = aElement;
		}


		private Group asGroup()
		{
			return (Group)mElement;
		}


		private Component asComponent()
		{
			return (Component)mElement;
		}


		private int getX()
		{
			if (mElement instanceof Component)
			{
				return asComponent().getX();
			}
			return asGroup().getX();
		}


		private int getY()
		{
			if (mElement instanceof Component)
			{
				return asComponent().getY();
			}
			return asGroup().getY();
		}


		private int getWidth()
		{
			if (mElement instanceof Component)
			{
				return asComponent().getWidth();
			}
			return asGroup().getWidth();
		}


		private int getHeight()
		{
			if (mElement instanceof Component)
			{
				return asComponent().getHeight();
			}
			return asGroup().getHeight();
		}


		private Dimension getPreferredSize()
		{
			if (mElement instanceof Component)
			{
				return asComponent().getPreferredSize();
			}
			return asGroup().getPreferredSize();
		}


		@Override
		public int hashCode()
		{
			int hash = 3;
			hash = 17 * hash + Objects.hashCode(this.mElement);
			return hash;
		}


		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final Element other = (Element)obj;
			if (!Objects.equals(this.mElement, other.mElement))
			{
				return false;
			}
			return true;
		}


		private void setBounds(int aX, int aY, int aWidth, int aHeight)
		{
			if (mElement instanceof Component)
			{
				asComponent().setBounds(aX, aY, aWidth, aHeight);
			}
		}
	}


	public static class Group
	{
		private final Component[] mComponents;

		public Group(Component... aComponents)
		{
			mComponents = aComponents;
		}

		public int getX()
		{
			int x = Integer.MAX_VALUE;
			for (Component c : mComponents)
			{
				x = Math.min(x, c.getX());
			}
			return x;
		}

		public int getY()
		{
			int y = Integer.MAX_VALUE;
			for (Component c : mComponents)
			{
				y = Math.min(y, c.getY());
			}
			return y;
		}

		public int getWidth()
		{
			int w = Integer.MIN_VALUE;
			for (Component c : mComponents)
			{
				w = Math.max(w, c.getWidth());
			}
			return w;
		}

		public int getHeight()
		{
			int h = Integer.MIN_VALUE;
			for (Component c : mComponents)
			{
				h = Math.max(h, c.getHeight());
			}
			return h;
		}

		public Dimension getPreferredSize()
		{
			return new Dimension(getWidth(), getHeight());
		}
	}


	private static void creatTest1(JTabbedPane aTabbedPane)
	{
		JTextField jTextField0 = new JTextField(20);
		JLabel jLabel0 = new JLabel("Server type:");
		JLabel jLabel1 = new JLabel("Server name:");
		JTextField jTextField1 = new JTextField();
		JLabel jLabel2 = new JLabel("Authentication:");
		JComboBox jComboBox = new JComboBox();
		JLabel jLabel3 = new JLabel("Login:");
		JTextField jTextField2 = new JTextField();
		JLabel jLabel4 = new JLabel("Password:");
		JPasswordField jPasswordField = new JPasswordField();
		JCheckBox jCheckBox = new JCheckBox("Remember password");
		JButton jButton0 = new JButton("Connect");
		JButton jButton1 = new JButton("Cancel");
		JButton jButton2 = new JButton("Help");
		JButton jButton3 = new JButton("Options >>");
		JSeparator jSeparator = new JSeparator();

		Group g = new Group(jLabel0, jLabel1, jLabel2, jLabel3, jLabel4);

		RelativeLayout layout = new RelativeLayout();

		layout.add(jLabel0, null);
//		layout.add(jTextField0, rightOf(g).alignBaseLine(jLabel0));

		layout.add(jLabel1, below(jTextField0).alignLeft(jLabel0));
		layout.add(jTextField1, below(jTextField0));
//
//		layout.add(jLabel2, leftOf(jComboBox));
//		layout.add(jComboBox, below(jTextField1).alignLeft(jTextField0));
//
//		layout.add(jLabel3, leftOf(jTextField2));
//		layout.add(jTextField2, below(jComboBox).alignLeft(jTextField0));
//
//		layout.add(jLabel4, leftOf(jPasswordField));
//		layout.add(jPasswordField, below(jTextField2).alignLeft(jTextField0));
//
//		layout.add(jCheckBox, below(jPasswordField).alignLeft(jTextField0));
//
//		layout.add(jSeparator, below(jCheckBox).alignLeft(null));
//
//		layout.add(jButton3, below(jSeparator).anchorEast());
//		layout.add(jButton2, leftOf(jButton3));
//		layout.add(jButton1, leftOf(jButton2));
//		layout.add(jButton0, leftOf(jButton1));

		JPanel panel = new JPanel();
		layout.layoutOn(panel);

		panel.setBackground(Color.PINK);
		aTabbedPane.add("Login", panel);
	}


//	private static void creatTest2(JTabbedPane aTabbedPane)
//	{
//		RelativeLayout areaLayout = new RelativeLayout();
//		areaLayout.setInnerSpacing(new Insets(5, 5, 5, 5));
//		areaLayout.setOuterSpacing(new Insets(5, 5, 5, 5));
//
//		JPanel panel = new JPanel(areaLayout);
//		panel.setBackground(Color.PINK);
//
//		panel.add(new JButton("A"), new Constraints( 0,  0, 15, 10, Anchor.EAST, Fill.VERTICAL));
//		panel.add(new JButton("B"), new Constraints(15,  0,  5, 15));
//		panel.add(new JButton("C"), new Constraints( 0, 10,  5,  5));
//		panel.add(new JButton("D"), new Constraints(10, 15, 10, 10));
//		panel.add(new JButton("E"), new Constraints( 5, 10,  5, 15, Anchor.SOUTH_EAST, Fill.NONE));
//		panel.add(new JButton("F"), new Constraints( 0, 15,  5, 10));
//		panel.add(new JButton("G"), new Constraints(10, 10,  5,  5, Anchor.CENTER, Fill.HORIZONTAL));
//
//		aTabbedPane.add("One", panel);
//	}


	public static void main(String... args)
	{
		try
		{
			JTabbedPane tabbedPane = new JTabbedPane();

			creatTest1(tabbedPane);
//			creatTest2(tabbedPane);

			JFrame frame = new JFrame();
			frame.add(tabbedPane);
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
