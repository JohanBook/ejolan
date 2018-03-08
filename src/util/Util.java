package util;

import src.City;

// A class containing some useful functions for the city-class
public class Util {

	// Return the minimal distance to another city
	public static double mindist(City[] cities, int x, int y) {
		if (cities[0] == null)
			return 1000;
		double min = distance(cities[0], x, y);
		for (City city : cities)
			if (city != null && distance(city, x, y) < min)
				min = distance(city, x, y);
		return min;
	}

	// Uses a Manhattan metric to calculate the distance between two cities
	public static double distance(City a, int x, int y) {
		return Math.abs(a.getPosition()[0] - x)
				+ Math.abs(a.getPosition()[1] - y);
	}

	// Uses a Manhattan metric to calculate the distance between two cities
	public static double distance(City a, City b) {
		return distance(a, b.getPosition()[0], b.getPosition()[1]);
	}

	// Uses an Euclidean metric to calculate the distance between two cities
	public static double distance2(City a, City b) {
		return Math.sqrt(Math.pow(a.getPosition()[0] - b.getPosition()[0], 2)
				+ Math.pow(a.getPosition()[1] - b.getPosition()[1], 2));
	}

}
