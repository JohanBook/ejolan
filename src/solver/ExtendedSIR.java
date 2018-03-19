////////////////////////////////////////////////
// ExtendedSIR.java
// A class representing a SIR system.
// Johan Book
// 2015-09-12
////////////////////////////////////////////////

package solver;

import settings.Settings;

public class ExtendedSIR implements Differentiable
{

	@Override
	public double[] differentiate(double[] y, double[] x, double t,
			final Settings settings)
	{
		double[] dy = new double[y.length];

		for (int i = 0; i < settings.number_of_cities; i++)
		{

			// Calculate travel
			double[] travelsum = new double[settings.number_of_groups];
			if (!settings.network.cities[i].isInQuarantine())
				for (int j = 0; j < settings.number_of_cities; j++)
				{
					if (i == j || settings.network.cities[j].isInQuarantine())
						continue;
					for (int group = 0; group < settings.number_of_groups; group++)
						travelsum[group] -= x[i + settings.number_of_cities * j]
								* (y[i * settings.number_of_groups + group] - y[j
										* settings.number_of_groups + group]);
				}

			int k = i * settings.number_of_groups;

			// Get total population
			double N = y[k + 0] + y[k + 1] + y[k + 2] + y[k + 4];

			// Susceptible
			dy[k + 0] = -settings.beta1 * y[k + 0] * (y[k + 1]) / N
					- -settings.beta2 * y[k + 0] * (y[k + 2]) / N + travelsum[0];

			// Incubation
			dy[k + 1] = settings.beta1 * y[k + 0] * (y[k + 1]) / N
					+ settings.beta2 * y[k + 0] * (y[k + 2]) / N - settings.alpha
					* y[k + 1] - y[k + 1] / settings.incubation + travelsum[1];

			// Sick
			dy[k + 2] = y[k + 1] / settings.incubation - y[k + 2]
					* settings.deathrate - settings.alpha * y[k + 2]; // remove
			// travelsum?

			// Dead
			dy[k + 3] = y[k + 2] * settings.deathrate;

			// Recovered
			dy[k + 4] = settings.alpha * (y[k + 1] + y[k + 2]) + travelsum[4];

		}

		return dy;
	}

}
