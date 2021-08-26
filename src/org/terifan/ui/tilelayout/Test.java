package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.terifan.ui.ImageResizer;


public class Test
{
	public static void main(String ... args)
	{
		try
		{
			TileLayout layout = new TileLayout(300);
			JPanel contentPanel = new JPanel(layout);
			contentPanel.setBackground(Color.BLACK);

			contentPanel.add(new JLabel("header"), -1);
			contentPanel.add(new JLabel("hello world"));
			contentPanel.add(new JLabel("hello world"), 0.5f);

			File[] files = new File("D:\\tmp\\test_images").listFiles();
			Arrays.sort(files);

			String prefix = "";
			for (File file : files)
			{
				String name = file.getName();

				String p = name.substring(0, 1).toUpperCase();
				String label;
				while (!p.isEmpty() && !(Character.isLetter(p.charAt(0)) || Character.isDigit(p.charAt(0)))) p = p.substring(1);

				if (p.matches("[A-D]")) {p = "A"; label = "A-D";}
				else if (p.matches("[E-H]")) {p = "E"; label = "E-H";}
				else if (p.matches("[I-L]")) {p = "I"; label = "I-L";}
				else if (p.matches("[M-P]")) {p = "M"; label = "M-P";}
				else if (p.matches("[Q-T]")) {p = "Q"; label = "Q-T";}
				else if (p.matches("[U-Z]")) {p = "U"; label = "U-Z";}
				else {p = "0"; label = "0-9";}
				if (!p.equals(prefix))
				{
					prefix = p;
					contentPanel.add(new TileLayoutItem(label, -1f, null));
				}

				BufferedImage image;
				File thumbFile = new File("D:\\tmp\\thumbs" + file.getAbsolutePath().substring(2));
				thumbFile.getParentFile().mkdirs();
				if (thumbFile.exists())
				{
					image = ImageResizer.getScaledImageAspect(ImageIO.read(thumbFile), 2048, layout.getRowHeight(), false);
//					image = ImageIO.read(thumbFile);
				}
				else
				{
					image = ImageResizer.getScaledImageAspect(ImageResizer.convertToRGB(ImageIO.read(file)), 1024, 300, false);
					ImageIO.write(image, "jpeg", thumbFile);
				}

				contentPanel.add(new TileLayoutItem(name, image.getWidth(), image));
//				contentPanel.add(new TileLayoutItem(name, 0.25f, image));
			}

			JScrollPane scrollPane = new JScrollPane(contentPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.getVerticalScrollBar().setUnitIncrement(108);
			scrollPane.getVerticalScrollBar().setBlockIncrement(1000);

			JFrame frame = new JFrame();
			frame.add(scrollPane);
			frame.setSize(1600, 1200);
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
