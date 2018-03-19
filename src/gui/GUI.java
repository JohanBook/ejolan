////////////////////////////////////////////////////////////////
// DrawCities.java
// A class for GUI. Do not edit anything in here.
// Johan Book
// 2015-09-12
//
// Requires a settings object to be  instantiated.
////////////////////////////////////////////////////////////////

package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import settings.Settings;
import src.City;
import src.Main;
import src.Road;
import util.Util;

@SuppressWarnings("serial")
public class GUI extends JFrame
{
	private Settings settings;
	private BufferedImage image;

	// TextArea for displaying city information
	private final JTextArea labelCity = new JTextArea(); // display info on
																			// chosen city
	private final JTextArea labelNetwork = new JTextArea(); // display info on
																				// whole network

	// The city for which to display information
	private City selectedCity;

	// Constructor
	public GUI(final Settings settings)
	{
		this.settings = settings;
		
		// Menu
		final JPanel menu = new JPanel();

		// Button for start/pause simulation
		final JButton btnStart = new JButton("Start Simulation");
		btnStart.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (btnStart.getText().equals("Start Simulation"))
				{
					btnStart.setText("Pause Simulation");
					Main.start();
				} else
				{
					btnStart.setText("Start Simulation");
					Main.pause();
				}
			}
		});
		menu.add(btnStart);

		// Button for saving an image of the GUI
		final JButton btnSave = new JButton("Save Image");
		btnSave.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveImage("image" + settings.t);
			}
		});
		menu.add(btnSave);

		// Network info panel
		final JPanel panelNetwork = new JPanel();
		panelNetwork.add(labelNetwork);

		// City info panel
		final JPanel panelLabel = new JPanel();
		panelLabel.add(labelCity);

		// Panel where all graphics will go
		Panel panel = new Panel(settings);
		panel.setForeground(Color.white);
		panel.setPreferredSize(new Dimension(1000, 750));

		add(panel, BorderLayout.CENTER);
		add(menu, BorderLayout.SOUTH);
		add(panelNetwork, BorderLayout.WEST);
		add(panelLabel, BorderLayout.EAST);

		addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				int x = e.getX();
				int y = e.getY();
				selectedCity = mindist(settings.network.getCities(), x, y);
				updateLabels();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				// intentionally left empty
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				// intentionally left empty
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				// intentionally left empty
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// intentionally left empty
			}
		});

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("The ejolan project");
		pack();
		setVisible(true);
		requestFocus();
	}

	// Update the labels with city and network information
	private void updateLabels()
	{
		String text;
		
		// ==== Network information ====
		text = "hey";
		labelNetwork.setText(text);

		// ==== City information ====
		if (selectedCity == null)
			return;

		double[] sir = selectedCity.get();
		text = selectedCity.toString();

		double current_population = selectedCity.getCurrentPopulation();

		text += "\nInitial population:\t"
				+ (int) selectedCity.getTotalPopulation();
		text += "\nCurrent population:\t" + (int) current_population;

		// Avoid dividing by zero
		if (current_population <= 0)
			current_population = 1;

		text += "\n\nHealthy:\t\t" + (int) (100 * (sir[0]) / current_population)
				+ " %";
		text += "\nIncubation:\t\t" + (int) (100 * (sir[1]) / current_population)
				+ " %";
		text += "\nSick:\t\t" + (int) (100 * (sir[2]) / current_population) + " %";
		text += "\nImmune:\t\t" + (int) (100 * sir[4] / current_population) + " %";
		text += "\nDead:\t\t" + (int) (100 * sir[3] / current_population) + " %";
		text += "\nQuarantine:\t\t" + selectedCity.isInQuarantine();
		labelCity.setText(text);
	}

	// ================ Panel ================
	private class Panel extends JPanel
	{
		private final Settings settings;

		Panel(final Settings settings)
		{
			this.settings = settings;
		}

		@Override
		protected void paintComponent(Graphics gr)
		{
			super.paintComponent(gr);
			image = drawToImage(settings, this.getWidth(), this.getHeight());
			gr.drawImage(image, 0, 0, null);
			updateLabels();
		}

	}

	// Method for drawing graphics and storing it in an image
	private BufferedImage drawToImage(Settings settings, int X, int Y)
	{
		Color closedRoad = new Color(255, 220, 220);
		Color openRoad = new Color(100, 255, 100);
		Color cityName = new Color(150, 150, 150);

		BufferedImage image = new BufferedImage(X, Y, BufferedImage.TYPE_INT_ARGB);
		Graphics gr = image.createGraphics();

		// Roads
		for (Road road : settings.network.getRoads())
		{
			int[] x = road.getPosition();
			if (road.isOpen())
				gr.setColor(openRoad);
			else
				gr.setColor(closedRoad);
			gr.drawLine(x[0], x[1], x[2], x[3]);
		}

		// Cities
		for (City city : settings.network.getCities())
		{
			int x[] = city.getPosition();
			double[] s = city.get();

			// Calculate rgb colour values
			int r = (int) (255 * (s[1] + s[2]) / city.getCurrentPopulation());
			int g = (int) (255 * s[0] / city.getTotalPopulation());
			int b = (int) (255 * s[4] / city.getTotalPopulation());

			r = force(r);
			g = force(g);
			b = force(b);

			gr.setColor(new Color(r, g, b));
			int size = 5 + (int) city.getSize();
			if (size > 25)
				size = 40;
			gr.fillOval(x[0] - size / 2, x[1] - size / 2, size, size);
			gr.setColor(Color.black);
			gr.drawOval(x[0] - size / 2, x[1] - size / 2, size, size);
			gr.setColor(cityName);
			gr.drawString(city.toString(), x[0] - 2 * size / 3, x[1] - 2 * size
					/ 3);

			// Draw quarantine
			if (city.isInQuarantine())
			{
				gr.setColor(Color.black);
				gr.drawOval(x[0] - size, x[1] - size, 2 * size, 2 * size);
			}

		}

		return image;
	}

	// Save image of GUI to file
	private void saveImage(String string)
	{
		try
		{
			// retrieve image
			File outputfile = new File("data/" + string + ".png");
			ImageIO.write(image, "png", outputfile);
			System.out.println("Network image saved");
		} catch (Exception e)
		{
			System.out.println("ERROR: Unable to save image to file");
		}

	}

	// A special method used only here
	private static City mindist(City[] cities, int x, int y)
	{
		if (cities[0] == null)
			return null;
		double min = Util.distance(cities[0], x, y);
		City ret = cities[0];
		for (City city : cities)
			if (city != null && Util.distance(city, x, y) < min)
			{
				min = Util.distance(city, x, y);
				ret = city;
			}
		return ret;
	}

	// Forces x into the range [0,255]
	private static int force(int x)
	{
		if (x < 0)
			return 0;
		if (x > 255)
			return 255;
		return x;
	}
}
