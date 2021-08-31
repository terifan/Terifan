package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
			int height = 150;

			TileLayout layout = new TileLayout(20, 20);

			List<File> files = Arrays.asList(new File("D:\\dev\\test_images").listFiles());
			Collections.shuffle(files, new Random(1));
			files = files.subList(0, 250);
			Collections.sort(files);

			JPanel contentPanel = new JPanel(layout);
			contentPanel.setBackground(new Color(29, 29, 29));
//			contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
			contentPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 50));

			JLabel header = new JLabel("header");
			header.setFont(new Font("arial", Font.PLAIN, 48));
			header.setForeground(Color.WHITE);

//			for (int i = 0; i < 10; i++)
//			{
//				String name = files.get(i).getName();
//
//				BufferedImage image = loadImage(files, i, 600);
//
//				contentPanel.add(new TileLayoutItem(name, image));
//			}

			String prefix = "";
			for (int i = 10; i < files.size(); i++)
			{
				String name = files.get(i).getName();

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

				BufferedImage image = loadImage(files, i, height);

//				if (p.equals("0"))
//					contentPanel.add(new TileLayoutItem(name, image));
////				else
//				else if (p.equals("A"))
					contentPanel.add(new TileLayoutItem(name, image), 0.1, -1);
//				else
//					contentPanel.add(new TileLayoutItem(name, image), 128, -1);
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


	private static BufferedImage loadImage(List<File> aFiles, int aIndex, int aHeight) throws IOException
	{
		File thumbFile = new File("D:\\dev\\thumbs" + aFiles.get(aIndex).getAbsolutePath().substring(2));
		thumbFile.getParentFile().mkdirs();

		BufferedImage image;
		if (thumbFile.exists())
		{
			image = ImageIO.read(thumbFile);
		}
		else
		{
			image = ImageResizer.getScaledImageAspect(ImageResizer.convertToRGB(ImageIO.read(aFiles.get(aIndex))), 2048, 600, false);
			ImageIO.write(image, "jpeg", thumbFile);
		}

		image = ImageResizer.getScaledImageAspect(image, 2048, aHeight, false);

		return image;
	}
}
