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
import javax.swing.SwingConstants;
import org.terifan.ui.ImageResizer;


public class Test
{
	public static void main(String ... args)
	{
		try
		{
			TileLayout layout = new TileLayout(!true, 5, 5);

			List<File> files = Arrays.asList(new File("D:\\dev\\test_images").listFiles(e->e.getName().matches(".*png|.*jpg")));
//			Collections.shuffle(files, new Random(1));
//			files = files.subList(0, 250);
//			Collections.sort(files);

			JPanel contentPanel = new JPanel(layout);
			contentPanel.setBackground(new Color(29, 29, 29));
			contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
//			contentPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 50));

			JLabel header = new JLabel("header");
			header.setFont(new Font("arial", Font.PLAIN, 48));
			header.setForeground(Color.WHITE);

			String lastPrefix = "";
			for (int i = 0; i < files.size(); i++)
			{
				String name = files.get(i).getName();

				String prefix = name.substring(0, 1).toUpperCase();
				while (!prefix.isEmpty() && !(Character.isLetter(prefix.charAt(0)) || Character.isDigit(prefix.charAt(0)))) prefix = prefix.substring(1);

				String label;
				if (prefix.matches("[A-D]")) {prefix = "A"; label = "A-D";}
				else if (prefix.matches("[E-H]")) {prefix = "E"; label = "E-H";}
				else if (prefix.matches("[I-L]")) {prefix = "I"; label = "I-L";}
				else if (prefix.matches("[M-P]")) {prefix = "M"; label = "M-P";}
				else if (prefix.matches("[Q-T]")) {prefix = "Q"; label = "Q-T";}
				else if (prefix.matches("[U-Z]")) {prefix = "U"; label = "U-Z";}
				else {prefix = "0"; label = "0-9";}
				if (!prefix.equals(lastPrefix))
				{
					lastPrefix = prefix;
					JLabel lbl = new JLabel(label);
					lbl.setVerticalAlignment(SwingConstants.TOP);
					lbl.setFont(new Font("arial", Font.PLAIN, 48));
					lbl.setForeground(Color.WHITE);
					contentPanel.add(lbl, -1, -1);
				}

				int size = prefix.equals("M") ? 64 : i < 10 ? 600 : i < 100 ? 300 : 150;
				BufferedImage image = loadImage(files, i);
				image = layout.isVertical() ? ImageResizer.getScaledImageAspect(image, 2048, size, false) : ImageResizer.getScaledImageAspect(image, size, 2048, false);

//				if (prefix.equals("M"))
//					contentPanel.add(new TileLayoutItem(name, image), 64, -1);
//				else if (prefix.equals("Q"))
//					contentPanel.add(new TileLayoutItem(name, image), 0.1, -1);
//				else
					contentPanel.add(new TileLayoutItem(name, image));
			}

			JScrollPane scrollPane = new JScrollPane(contentPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.getHorizontalScrollBar().setUnitIncrement(108);
			scrollPane.getHorizontalScrollBar().setBlockIncrement(1000);
			scrollPane.getVerticalScrollBar().setUnitIncrement(108);
			scrollPane.getVerticalScrollBar().setBlockIncrement(1000);
			scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
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


	private static BufferedImage loadImage(List<File> aFiles, int aIndex) throws IOException
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

		return image;
	}
}
