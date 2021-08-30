package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import org.terifan.ui.ImageResizer;


public class Test
{
	public static void main(String ... args)
	{
		try
		{
			TileLayout layout = new TileLayout(300).setPadding(new Point(5, 5));
			JPanel contentPanel = new JPanel(layout);
			contentPanel.setBackground(new Color(29, 29, 29));
			contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

			JLabel header = new JLabel("header");
			header.setFont(new Font("arial", Font.PLAIN, 48));
			header.setForeground(Color.WHITE);
			contentPanel.add(header, -1, -1);
			contentPanel.add(new JLabel("hello world"));
			contentPanel.add(new JLabel("hello world"), 0.5f, -1);

			List<File> files = Arrays.asList(new File("D:\\tmp\\test_images").listFiles());
			Collections.shuffle(files);
			files = files.subList(0, 100);
			Collections.sort(files);

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
					JLabel groupHeader = new JLabel(label);
					groupHeader.setFont(new Font("arial", Font.PLAIN, 48));
					groupHeader.setForeground(Color.WHITE);
					contentPanel.add(groupHeader, -1, -1);
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

				contentPanel.add(new TileLayoutItem(name, image), image.getWidth(), -1);
//				contentPanel.add(new TileLayoutItem(name, image), 0.1f, -1);
			}

			JScrollPane scrollPane = new JScrollPane(contentPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.getVerticalScrollBar().setUnitIncrement(108);
			scrollPane.getVerticalScrollBar().setBlockIncrement(1000);
			scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
//			scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
//			scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
			scrollPane.setBorder(null);

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
