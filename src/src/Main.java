////////////////////////////////////////////////
// Main.java
// The main class.
// 2015-09-12
////////////////////////////////////////////////

package src;

import gui.DrawCities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import settings.Settings;
import util.Printer;

public class Main
{
	private static Timer update;

	// Start graphical simulation
	public static void start()
	{
		update.start();
	}

	// Pause graphical simulation
	public static void pause()
	{
		update.stop();
	}

	// Main method to run the program
	// Arguments: [mode] [number of cities] [tmax] [dt] [updatetime]
	public static void main(String[] args)
	{
		Settings settings = new Settings();

		args = new String[1];
		args[0] = "gui";

		// updatetime
		if (args.length >= 5)
			settings.time_interval = Integer.parseInt(args[4]);

		// dt
		if (args.length >= 4)
			settings.tmax = Double.parseDouble(args[3]);

		// tmax
		if (args.length >= 3)
			settings.dt = Double.parseDouble(args[2]);

		// Number of cities
		if (args.length >= 2)
			settings.number_of_cities = Integer.parseInt(args[1]);

		// Mode
		if (args.length < 1 || args[0].equals("gui"))
			simulation(settings, "master");
		else if (args[0].equals("ab"))
			simulation1(settings);
		else if (args[0].equals("id"))
			simulation2(settings);
	}

	// Starts a new simulation
	// Input :
	// - Settings about simulation
	// - String containing name on output file
	private static void simulation(final Settings settings, final String string)
	{
		System.out.println("Data points: "
				+ (int) (settings.number_of_cities * settings.number_of_groups
						* settings.tmax / settings.dt));

		// Initialize variables
		settings.network = new Network(settings);
		settings.writeEachCity = false;

		// Run simulation to estimate error
		settings.dt *= 2;
		for (; settings.t < settings.tmax - settings.dt; settings.t += settings.dt)
			settings.network.simulate(settings.t);
		final double[] result = settings.network.get(settings.tmax);

		// Create a GUI for the simulation and run it
		settings.dt /= 2;
		settings.t = 0;
		settings.writeEachCity = true;
		settings.network = new Network(settings);
		final DrawCities draw = new DrawCities(settings);

		// Create update timer
		update = new Timer(settings.time_interval, new ActionListener()
		{

			public void actionPerformed(ActionEvent actionEvent)
			{

				// Check if simulation is complete
				if (settings.t > settings.tmax - settings.dt)
				{
					settings.network.close();

					// Write data to file
					Printer printer = new Printer(string, null);
					Printer printer2 = new Printer(string + "_total", null);
					printer
							.write("#City\tSusc\tIncu\tSick\tDead\tImmn\tErr_Susc\tErr_Incu\tErr_Sick\tErr_Dead\tErr_Immn");
					printer2
							.write("#City\tSusc\tIncu\tSick\tDead\tImmn\tErr_Susc\tErr_Incu\tErr_Sick\tErr_Dead\tErr_Immn");

					double[] sum1 = new double[settings.number_of_groups];
					double[] sum2 = new double[settings.number_of_groups];

					for (int i = 0; i < settings.number_of_cities; i++)
					{

						// Get data for city i and add into string
						double[] a = settings.network.get(i, settings.tmax);
						double denom = settings.network.getCities()[i]
								.getCurrentPopulation();
						String string = "" + i;
						for (double x : a)
							if (settings.writeFractionOfPopulation)
								string += "\t" + (x / denom);
							else
								string += "\t" + x;

						// Calculate error and add into string
						for (int j = 0; j < a.length; j++)
						{
							double d = Math.abs(a[j]
									- result[j + i * settings.number_of_groups]) / 15;
							if (settings.writeFractionOfPopulation)
								string += "\t" + (d / denom);
							else
								string += "\t" + d;

							// Sum all cities
							sum1[j] += a[j];
							sum2[j] += result[j + i * settings.number_of_groups];
						}

						printer.write(string);
					}
					printer.close();

					// Summarize all data in one file
					String out = "";
					double denom = settings.network.getCurrentPopulation();
					for (double x : sum1)
						out += (x / denom) + "\t";
					for (int k = 0; k < sum2.length; k++)
						if (settings.writeFractionOfPopulation)
							out += Math.abs(sum1[k] - sum2[k]) / (15 * denom) + "\t";
						else
							out += Math.abs(sum1[k] - sum2[k]) / (15) + "\t";
					printer2.write(out);
					printer2.close();

					update.stop();

					System.out.println("Simulation complete");

					return;
				}

				// Take simulation step
				settings.network.simulate(settings.t);
				if (settings.writeEachCity)
					System.out.print("Day " + settings.t + "\tTotal population: "
							+ (int) settings.network.getCurrentPopulation() + "\n");

				settings.t += settings.dt;

				// Draw on the GUI
				draw.repaint();
			}
		});
	}

	// Calculate the total percentage of deaths given some settings
	private static double calculate_deaths(final Settings settings)
	{
		settings.network = new Network(settings);
		settings.network.simulate();

		double sum = 0;
		for (int i = 0; i < settings.number_of_cities; i++)
			sum += settings.network.get(i, settings.tmax - 2 * settings.dt)[3];
		sum /= settings.network.getInitalPopulation();

		return sum;
	}

	// Loop generating data for different alpha and beta
	private static void simulation1(Settings settings)
	{
		System.out.println("Calculating landscape data for alpha and beta");
		Printer printer = new Printer("simulation_alpha_beta", null);
		settings.writeEachCity = false;
		for (double a = 0.01; a < 1; a += 0.01)
			for (double b = 0.01; b < 1; b += 0.01)
			{
				settings.alpha = a;
				settings.beta1 = b;
				printer.write(settings.alpha + "\t" + settings.beta1 + "\t"
						+ calculate_deaths(settings));
			}

		printer.close();
		System.out.println("Complete!");

	}

	// Loop generating data for different incubation and death-rate
	private static void simulation2(Settings settings)
	{
		System.out
				.println("Calculating landscape data for incubation and deathrate");
		Printer printer = new Printer("simulation_inc_dr", null);
		settings.writeEachCity = false;
		for (double inc = 1; inc < 10; inc += 0.1)
			for (double dr = 0.1; dr < 1; dr += 0.01)
			{
				settings.incubation = inc;
				settings.deathrate = dr;
				printer.write(settings.deathrate + "\t" + settings.incubation
						+ "\t" + calculate_deaths(settings));
			}

		printer.close();
		System.out.println("Complete!");
	}
}
