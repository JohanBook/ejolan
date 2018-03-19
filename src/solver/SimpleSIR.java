////////////////////////////////////////////////
// SimpleSIR.java
// A class representing a SIR system.
// Johan Book
// 2015-09-12
////////////////////////////////////////////////

package solver;

import settings.Settings;

public class SimpleSIR implements Differentiable
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

			// only calculate travel if city i is not in quarantine
			if (!settings.network.cities[i].isInQuarantine())
				for (int j = 0; j < settings.number_of_cities; j++)
				{
					if (i == j)
						continue;

					// skip target city j if j is in quarantine
					if (settings.network.cities[j].isInQuarantine())
						continue;

					for (int group = 0; group < settings.number_of_groups; group++)
						travelsum[group] -= x[i + settings.number_of_cities * j]
								* y[i * settings.number_of_groups + group]
								- x[j + settings.number_of_cities * i]
								* y[j * settings.number_of_groups + group];
				}

			int k = i * settings.number_of_groups;

			// Get total population
			double N = y[k + 0] + y[k + 1] + y[k + 2] + y[k + 4];

			// Susceptible
			dy[k + 0] = -settings.beta1 * y[k + 0] * y[k + 1] / N + travelsum[0];

			// Infected
			dy[k + 1] = settings.beta1 * y[k + 0] * y[k + 1] / N - settings.alpha
					* y[k + 1] + travelsum[1];

			// Recovered
			dy[k + 4] = settings.alpha * y[k + 1] + travelsum[4];

		}

		return dy;
	}

}
