////////////////////////////////////////////////
// Network.java
// A class containing several cities. This class 
// contains methods to simulate the network.
// Johan Book
// 2015-09-12
////////////////////////////////////////////////

package src;

import java.util.ArrayList;

import settings.Settings;
import solver.Differentiable;
import solver.Euler;
import solver.ExtendedSIR;
import solver.SimpleSIR;
import solver.Solver;
import util.Random;
import util.Util;

// A class holding several cities
@SuppressWarnings("unused")
public class Network
{

	// convention [time][group + number of groups * city]
	public double[][] cities2;
	
	public City[] cities;
	private ArrayList<Road> allroads;
	private Settings settings;

	// convention [city1 + number of cities * city2]
	private double[] travel;

	// Setup grid of cities
	public Network(Settings settings)
	{
		this.settings = settings;

		// Initialize variables
		cities2 = new double[getIndex(settings.tmax)][settings.number_of_groups
				* settings.number_of_cities];
		travel = new double[settings.number_of_cities * settings.number_of_cities];
		cities = new City[settings.number_of_cities];
		allroads = new ArrayList<Road>(settings.number_of_cities
				* settings.number_of_cities);

		final int min_city_distance = (int) (Math.sqrt(settings.panel_size
				.getHeight() * settings.panel_size.getWidth()) / settings.number_of_cities);

		// Generate cities
		for (int i = 0; i < settings.number_of_cities; i++)
		{

			// Generate population
			int population = settings.min_population
					+ (int) (settings.max_population * Random.supergauss());
			int I = getInitiallyInfected(population, i);
			int R = (int) ((population - I) * settings.natural_resistance);
			int S = population - I - R;

			// Generate locations until an allowed one is found
			int x, y;
			do
			{
				x = Random.nextInt(600) + 100;
				y = Random.nextInt(600) + 100;
			} while (Util.mindist(cities, x, y) < min_city_distance);

			cities[i] = new City(x, y, i, settings, this);

			// Store SIR-data in cities2
			double[] store = { S, I, 0, 0, R };
			for (int group = 0; group < settings.number_of_groups; group++)
				cities2[0][group + i * settings.number_of_groups] = store[group];
		}

		// Create links between cities
		for (int i = 0; i < settings.number_of_cities; i++)
		{
			ArrayList<Road> potential_roads = new ArrayList<Road>();
			for (int j = i + 1; j < settings.number_of_cities; j++)
			{
				Road road = new Road(cities[i], cities[j], this);
				double travel = getScore(cities[i], cities[j]);
				if (travel > 0.1)
				{
					road.connect();
					allroads.add(road);
				} else if (!cities[i].isConnected())
					potential_roads.add(road);
			}
			// Find optimal road if none is connected
			if (!cities[i].isConnected() && !potential_roads.isEmpty())
			{
				Road optimal = potential_roads.get(0);
				for (Road road : potential_roads)
					if (road.getTravelRate() > optimal.getTravelRate())
						optimal = road;
				optimal.connect();
				allroads.add(optimal);
			}
		}
	}

	// Simulate the network
	public void simulate()
	{
		settings.t = 0;
		for (; settings.t < settings.tmax; settings.t += settings.dt)
			simulate(settings.t);
	}

	// Simulate a network of cities at the time t
	public void simulate(double t)
	{
		updateTravelRate();

		// Calculate population at next time step
		double[] a = settings.solver.solve(settings.function,
				cities2[getIndex(t)], travel, t, settings.dt, settings);

		// Calculates how many is sick/ incubation/... in every city
		for (int i = 0; i < settings.number_of_cities; i++)
		{

			if (t + settings.dt < settings.tmax)
				for (int j = 0; j < a.length; j++)
				{

					// To avoid negative populations
					if (a[j] < 0)
						a[j] = 0;

					cities2[getIndex(t + settings.dt)][j] = a[j];
				}

			// Determine quarantine for SimpleSIR
			if (settings.function instanceof SimpleSIR)
				if (a[1 + settings.number_of_groups * i] > settings.quarantine_value
						* cities[i].getCurrentPopulation())
					cities[i].setQuarantine();

			// Determine quarantine for ExtendedSIR
			if (settings.function instanceof ExtendedSIR)
				if (a[2 + settings.number_of_groups * i]
						+ a[3 + settings.number_of_groups * i] > cities[i]
						.getTotalPopulation() * settings.quarantine_value)
					cities[i].setQuarantine();

			cities[i].print(get(i, t));
		}
	}

	// Returns cities
	public City[] getCities()
	{
		return cities;
	}

	// Returns sir-data from city i at t
	public double[] get(int i, double t)
	{
		if (t >= settings.tmax)
			t = settings.tmax - settings.dt;
		double[] a = new double[settings.number_of_groups];
		for (int k = 0; k < a.length; k++)
			a[k] = cities2[getIndex(t)][k + i * settings.number_of_groups];
		return a;
	}

	public double[] get(double t)
	{
		if (t >= settings.tmax)
			t = settings.tmax - settings.dt;
		return cities2[getIndex(t)];
	}

	public ArrayList<Road> getRoads()
	{
		return allroads;
	}

	// Sums initial population in all cites
	public double getInitalPopulation()
	{
		double sum = 0;
		for (City city : cities)
			sum += city.getTotalPopulation();
		return sum;
	}

	// Sums current population in all cites
	public double getCurrentPopulation()
	{
		double total = 0;
		for (int i = 0; i < settings.number_of_cities; i++)
			total += cities[i].getCurrentPopulation();

		return total;
	}
	

	// Close all streams associated to all cities
	public void close()
	{
		for (City city : cities)
			city.close();
	}

	// Set travel between city i and j to rate
	private void setTravel(int i, int j, double rate)
	{
		travel[i + settings.number_of_cities * j] = rate;
		travel[j + settings.number_of_cities * i] = rate;
	}

	// Calculate new travel rates
	private void updateTravelRate()
	{
		for (Road road : allroads)
		{
			if (!road.isOpen())
				continue;
			int[] index = road.getCityNumbers();
			setTravel(index[0], index[1], road.getTravelRate());
		}
	}

	// How the disease starts
	private int getInitiallyInfected(int population, int i)
	{
		double percentage = 1. / 13;
		if (i == 30) // start in city 30
			return (int) (percentage * population);
		return 0;
	}

	// Transforms t into an index
	private int getIndex(double t)
	{
		if (t > settings.tmax)
			return getIndex(settings.tmax);
		return (int) Math.ceil(t / settings.dt);
	}

	// Determines a "score" between to cities
	// Roads with high score are drawn
	private static double getScore(City a, City b)
	{
		return (a.getTotalPopulation() + b.getTotalPopulation())
				/ (5 * Util.distance(a, b) * 1000);
	}

}
