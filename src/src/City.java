package src;

import java.util.ArrayList;

import settings.Settings;
import util.NameGenerator;
import util.Printer;

// A class representing a city
public class City {
	private final ArrayList<Road> roads = new ArrayList<Road>();
	private final String name = NameGenerator.getName();
	private final Settings settings;
	private final Network network;
	private final int index; // city number
	private Printer printer;
	private boolean quarantine;

	private int[] position = new int[2];

	public City(int x, int y, int index, final Settings settings,
			Network network) {
		position[0] = x;
		position[1] = y;

		this.settings = settings;
		this.network = network;
		this.index = index;

		if (settings.writeEachCity)
			printer = new Printer("city" + index, settings);
	}

	// For initialization
	public void addRoad(Road road) {
		roads.add(road);
	}

	// Get the initial total population
	public int getTotalPopulation() {
		double[] a = network.get(index, 0);
		return (int) (a[0] + a[1] + a[2] + a[4]);
	}

	// Get the current population (not including dead)
	public int getCurrentPopulation() {
		double[] a = network.get(index, settings.t);
		return (int) (a[0] + a[1] + a[2] + a[4]);
	}

	public int[] getPosition() {
		return position;
	}

	// Returns the current group data
	public double[] get() {
		return network.get(index, settings.t);
	}

	// Returns a "size" of the city. Used in GUI.
	public double getSize() {
		return 10 * getTotalPopulation() / settings.max_population;
	}

	public boolean isConnected() {
		if (roads == null || roads.isEmpty())
			return false;
		return true;
	}

	// Sets the city in quarantine
	public void setQuarantine() {
		if (!quarantine)
			settings.quarantine_value *= 0.9;
		for (Road road : roads)
			road.setOpen(false);
		quarantine = true;
	}

	public boolean isUnderQuarantine() {
		return quarantine;
	}

	public String toString() {
		return name;
	}

	public void print(double[] a) {
		if (settings.writeEachCity)
			printer.write(a);
	}

	public void close() {
		if (settings.writeEachCity)
			printer.close();
	}

	public int getIndex() {
		return index;
	}
}
