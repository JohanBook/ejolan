////////////////////////////////////////////////
// Road.java
// A class representing a road.
// Johan Book
// 2015-09-12
////////////////////////////////////////////////

package src;

import util.Util;

public class Road
{
	// the cities the road connect
	private City city1;
	private City city2; 
	
	private Network network; // the network of all roads
	private boolean isOpen = true; // used for quarantine

	// Constructor requires two cities and a Network
	public Road(City city1, City city2, Network network)
	{
		this.city1 = city1;
		this.city2 = city2;
		this.network = network;
	}

	// Return number of city1 and city2
	public int[] getCityNumbers()
	{
		int[] result = { city1.getIndex(), city2.getIndex() };
		return result;
	}

	// Initialization function for the road
	public void connect()
	{
		city1.addRoad(this);
		city2.addRoad(this);
	}

	// Calculates a travel-rate between city a and city b
	// using a magical formula
	public double getTravelRate(City a, City b)
	{
		double travel = 0.005
				* a.getTotalPopulation()
				/ (Math.pow(Util.distance(a, b), 2) * network
						.getCurrentPopulation());
		return travel;
	}

	public double getTravelRate()
	{
		return getTravelRate(city1, city2);
	}

	public void setOpen(boolean isOpen)
	{
		this.isOpen = isOpen;
	}

	public boolean isOpen()
	{
		return isOpen;
	}

	// Returns an array containing position of both cities
	public int[] getPosition()
	{
		int[] position = new int[4];
		position[0] = city1.getPosition()[0];
		position[1] = city1.getPosition()[1];
		position[2] = city2.getPosition()[0];
		position[3] = city2.getPosition()[1];
		return position;
	}

}
