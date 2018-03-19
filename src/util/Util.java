package util;

import settings.Settings;
import src.City;

// A class containing some useful functions for the city-class
public class Util
{

	// Returns the minimal distance to a city in an array to a point (x,y)
	public static double mindist(City[] cities, int x, int y)
	{
		if (cities[0] == null)
			return 1000;
		double min = distance(cities[0], x, y);
		for (City city : cities)
			if (city != null && distance(city, x, y) < min)
				min = distance(city, x, y);
		return min;
	}

	// Uses a Manhattan metric to calculate the distance between two cities
	public static double distance(City a, int x, int y)
	{
		return distance(a.getPosition()[0], a.getPosition()[1], x, y);
	}

	// Uses a Manhattan metric to calculate the distance between two cities
	public static double distance(City a, City b)
	{
		return distance(a, b.getPosition()[0], b.getPosition()[1]);
	}

	// Uses a Manhattan or Euclidean metric (depending on settings) to calculate
	// the distance
	// between two points (x1, y1) and (x2, y2)
	@SuppressWarnings("unused")
	public static double distance(double x1, double y1, double x2, double y2)
	{
		if (Settings.Manhattan_distance == true)
			return Math.abs(x1 - x2) + Math.abs(y1 - y2);
		else
			return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

}
