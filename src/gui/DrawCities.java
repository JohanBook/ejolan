// A class for GUI. Do not edit anything in here.
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
public class DrawCities extends JFrame {
	private BufferedImage image;

	// TextArea for displaying city information
	private final JTextArea label = new JTextArea();

	// Constructor
	public DrawCities(final Settings settings) {

		// Menu
		final JPanel menu = new JPanel();
		final JButton btnStart = new JButton("Start");
		final JButton btnSave = new JButton("Save");

		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnStart.getText().equals("Start")) {
					btnStart.setText("Pause");
					Main.start();
				} else {
					btnStart.setText("Start");
					Main.pause();
				}
			}
		});

		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveImage("image" + settings.t);
			}
		});

		menu.add(btnStart);
		menu.add(btnSave);

		// Info menu
		final JPanel info = new JPanel();
		info.add(label);

		Panel panel = new Panel(settings);
		panel.setForeground(Color.white);
		panel.setPreferredSize(new Dimension(1000, 750));

		add(panel, BorderLayout.CENTER);
		add(menu, BorderLayout.SOUTH);
		add(info, BorderLayout.EAST);

		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				City city = mindist(settings.network.getCities(), x, y);
				double[] sir = city.get();
				String txt = city.toString();

				double current_population = city.getCurrentPopulation();

				txt += "\nInitial population:\t"
						+ (int) city.getTotalPopulation();
				txt += "\nCurrent population:\t" + (int) current_population;

				// Avoid dividing by zero
				if (current_population <= 0)
					current_population = 1;

				txt += "\nIncubation:\t\t"
						+ (int) (100 * (sir[1]) / current_population) + " %";
				txt += "\nSick:\t\t"
						+ (int) (100 * (sir[2]) / current_population) + " %";
				txt += "\nImmune:\t\t"
						+ (int) (100 * sir[4] / current_population) + " %";
				txt += "\nDead:\t\t" + (int) (sir[3]);
				txt += "\nQuarantine:\t\t" + city.isUnderQuarantine();
				label.setText(txt);
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("The EJOLAN project");
		pack();
		setVisible(true);
		requestFocus();
	}

	// ================ Panel ================
	private class Panel extends JPanel {
		private final Settings settings;

		Panel(final Settings settings) {
			this.settings = settings;
		}

		@Override
		protected void paintComponent(Graphics gr) {
			super.paintComponent(gr);
			image = drawToImage(settings, this.getWidth(), this.getHeight());
			gr.drawImage(image, 0, 0, null);
		}

	}

	// Method for drawing graphics and storing it in an image
	private BufferedImage drawToImage(Settings settings, int X, int Y) {
		Color closedRoad = new Color(255, 220, 220);
		Color openRoad = new Color(100, 255, 100);
		Color cityName = new Color(150, 150, 150);

		BufferedImage image = new BufferedImage(X, Y,
				BufferedImage.TYPE_INT_ARGB);
		Graphics gr = image.createGraphics();

		// Roads
		for (Road road : settings.network.getRoads()) {
			int[] x = road.getPosition();
			if (road.isOpen())
				gr.setColor(openRoad);
			else
				gr.setColor(closedRoad);
			gr.drawLine(x[0], x[1], x[2], x[3]);
		}

		// Cities
		for (City city : settings.network.getCities()) {
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
			if (city.isUnderQuarantine()) {
				gr.setColor(Color.black);
				gr.drawOval(x[0] - size, x[1] - size, 2 * size, 2 * size);
			}

		}

		return image;
	}

	private void saveImage(String string) {
		try {
			// retrieve image
			File outputfile = new File("data/" + string + ".png");
			ImageIO.write(image, "png", outputfile);
			System.out.println("Network image saved");
		} catch (Exception e) {
			System.out.println("ERROR: Unable to save image to file");
		}

	}

	// A special method used only here
	private static City mindist(City[] cities, int x, int y) {
		if (cities[0] == null)
			return null;
		double min = Util.distance(cities[0], x, y);
		City ret = cities[0];
		for (City city : cities)
			if (city != null && Util.distance(city, x, y) < min) {
				min = Util.distance(city, x, y);
				ret = city;
			}
		return ret;
	}

	// Forces x into the range [0,255]
	private static int force(int x) {
		if (x < 0)
			return 0;
		if (x > 255)
			return 255;
		return x;
	}
}
