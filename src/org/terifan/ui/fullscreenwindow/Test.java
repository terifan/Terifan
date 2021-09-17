package org.terifan.ui.fullscreenwindow;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Test
{
	private static FullScreenWindow wnd;


	public static void main(String... args)
	{
		try
		{
			WindowMenuBar menuBar = new WindowMenuBar()
				.add(new WindowMenuItem("File"))
				.add(new WindowMenuItem("Edit"))
				.add(new WindowMenuItem("Render"))
				.add(new WindowMenuItem("Window"))
				.add(new WindowMenuItem("Help"));

			TabSelectedHandler tabHandler = tab ->
			{
				if (tab.getTabBar().indexOf(tab) > 2)
				{
					return false;
				}
				((CardLayout)wnd.getLayout()).show(wnd.getContentPanel(), tab.getLabel());
				return true;
			};

			WindowTabBar tabBar = new WindowTabBar()
				.setOnTabSelected(tabHandler)
				.add(new WindowTabItem("Layout"))
				.add(new WindowTabItem("Modeling"))
				.add(new WindowTabItem("Sculpting"))
				.add(new WindowTabItem("UV Editing"))
				.add(new WindowTabItem("Texture Paint"))
				.add(new WindowTabItem("Shading"))
				.add(new WindowTabItem("Animation"))
				.add(new WindowTabItem("Rendering"))
				.add(new WindowTabItem("Compositing"))
				.add(new WindowTabItem("Scripting"));

			DefaultWindowBorder windowBorder = new BlackWindowBorder()
				.setMenuBar(menuBar)
				.setTabBar(tabBar);

			wnd = new FullScreenWindow(null, "New window", false, true, windowBorder);

			wnd.setOnClosed(() -> System.out.println("closed"));

			CardLayout cardLayout = new CardLayout();
			wnd.setLayout(cardLayout);

			wnd.add(createContent_Modeling(), "Modeling");
			wnd.add(createContent_Layout(), "Layout");
			wnd.add(createContent_Sculpting(), "Sculpting");

			tabBar.selectTab(tabBar.getItem("Sculpting"));

			wnd.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	public static JPanel createContent_Sculpting()
	{
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				image.setRGB(x, y, 0x808080 | x ^ y);
			}
		}
		g.dispose();

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JPanel()
		{
			@Override
			protected void paintComponent(Graphics aGraphics)
			{
				aGraphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
			}
		}, BorderLayout.CENTER);
		return panel;
	}


	public static JPanel createContent_Modeling()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(new JTextArea("xx")), BorderLayout.CENTER);
		return panel;
	}


	public static JPanel createContent_Layout() throws IOException
	{
		JPanel leftPanel = new RoundBorderPanel();
		leftPanel.setBackground(new Color(51, 51, 51));
		leftPanel.add(new JButton(new AbstractAction("undecorated")
		{
			@Override
			public void actionPerformed(ActionEvent aE)
			{
				wnd.setUndecorated(!wnd.isUndecorated());
			}
		}));
		leftPanel.add(new JButton(new AbstractAction("border")
		{
			@Override
			public void actionPerformed(ActionEvent aE)
			{
				wnd.setBorderPainted(!wnd.isBorderPainted());
			}
		}));
		leftPanel.add(new JButton(new AbstractAction("modal")
		{
			@Override
			public void actionPerformed(ActionEvent aE)
			{
//				wnd.setModal(!wnd.isModal());
			}
		}));

		JPanel rightPanel = new RoundBorderPanel();
		rightPanel.setBackground(new Color(51, 51, 51));
		JLabel label = new JLabel("a single line of text");
		label.setForeground(Color.WHITE);
		rightPanel.add(label);

		JPanel topPanel = new JPanel(new BorderLayout(2, 2));
		topPanel.setBackground(new Color(34, 34, 34));
		topPanel.add(leftPanel, BorderLayout.CENTER);
		topPanel.add(rightPanel, BorderLayout.EAST);

		JPanel bottomPanel = new RoundBorderPanel(new FlowLayout(FlowLayout.LEFT));
		label = new JLabel("xxxxxxxxxxxxx");
		label.setBackground(new Color(51, 51, 51));
		label.setForeground(Color.WHITE);
		label.setOpaque(true);
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		bottomPanel.add(label);

		JPanel interiorPanel = new JPanel(new BorderLayout(2, 2));
		interiorPanel.setBackground(new Color(34, 34, 34));
		interiorPanel.add(topPanel, BorderLayout.CENTER);
		interiorPanel.add(bottomPanel, BorderLayout.SOUTH);

		return interiorPanel;
	}
}
