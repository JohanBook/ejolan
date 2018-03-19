////////////////////////////////////////////////////////////////
// Settings.java
// A class where all settings for one simulation is stored.
// Johan Book
// 2015-09-13
////////////////////////////////////////////////////////////////

package settings;

import java.awt.Dimension;

import solver.Differentiable;
import solver.ExtendedSIR;
import solver.RungeKutta4;
import solver.Solver;
import src.Network;

// A class for holding lots of vital information
public class Settings
{
	public Network network;
	
	// Simulation settings
	public int number_of_cities = 34;
	public int max_population = 100000;
	public int min_population = 100;
	public double quarantine_value = 10;
	public Dimension panel_size = new Dimension(1000, 700);

	// Important stuff
	public double tmax = 500; // max time. Ola: 30
	public double dt = 1.0; // time interval. Ola: 0.44
	public double t = 0; // current time
	public int number_of_groups = 5; // the number of groups (t.ex. infect)
	public int time_interval = 100; // the time interval used in the timer
	public static final int seed = 24; // seed for the random generator

	// The solver to use i.e. Euler or RungeKutta4
	public final Solver solver = new RungeKutta4();

	// The model to use i.e. SimpleSIR or ExtendedSIR
	public final Differentiable function = new ExtendedSIR();

	// Printing settings
	public boolean writeFractionOfPopulation = false;

	// if to write data for each city at each time step
	public boolean writeEachCity = true;

	// Disease settings
	public double alpha = 0.01; // rate of people recovering/immune
	public double beta1 = 0.25; // rate of people becoming sick (from those who
											// are in incubation period)
	public double beta2 = 0; // rate of people becoming sick (from those who are
										// showing symptoms)
	public double incubation = 10; // days of incubation
	public double deathrate = 0.01; // 0.25;
	public final double natural_resistance = 0.001; // how many is natural immune
	
	// Default is Euclidean distance but using this option the program uses
	// Manhattan distances instead - which is less computationally heavy
	public static final boolean Manhattan_distance = false;

}
