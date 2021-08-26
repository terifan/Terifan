package org.terifan.ui.tilelayout;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.terifan.ui.ImageResizer;


public class Test
{
	public static void main(String ... args)
	{
		try
		{
			JPanel contentPanel = new JPanel(new TileLayout(100));
			contentPanel.setBackground(Color.BLACK);

			File[] files = new File("D:\\Pictures\\Wallpapers\\High Quality").listFiles();
			Arrays.sort(files);

			String prefix = "";
			for (Object file : files)
			{
				String name = ((File)file).getName();

				String p = name.substring(0, 1).toUpperCase();
				String label = "";
				if (p.matches("[0-9]")) {p = "0"; label = "0-9";}
				if (p.matches("[A-D]")) {p = "A"; label = "A-D";}
				if (p.matches("[E-H]")) {p = "E"; label = "E-H";}
				if (p.matches("[I-R]")) {p = "I"; label = "I-R";}
				if (p.matches("[Q-Z]")) {p = "Q"; label = "Q-Z";}
				if (!p.equals(prefix))
				{
					prefix = p;
					contentPanel.add(new TileLayoutItem(label, -1f, null));
				}

				BufferedImage image;
				File thumbFile = new File("D:\\temp\\thumbs", name);
				if (thumbFile.exists())
				{
					image = ImageResizer.getScaledImageAspect(ImageIO.read(thumbFile), 2048, 100, false);
				}
				else
				{
					image = ImageResizer.getScaledImageAspect(ImageIO.read((File)file), 1024, 300, false);
					ImageIO.write(image, "jpeg", thumbFile);
				}

				contentPanel.add(new TileLayoutItem(name, image.getWidth(), image));
//				contentPanel.add(new TileLayoutItem(name, 0.25f, image));
			}

			JFrame frame = new JFrame();
			frame.add(contentPanel);
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
