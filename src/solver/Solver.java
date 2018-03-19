////////////////////////////////////////////////////////////////
// Solver.java
// An abstract class for a general solver.
// Johan Book
// 2015-09-13
////////////////////////////////////////////////////////////////

package solver;

import settings.Settings;

public abstract class Solver
{
	public abstract double[] solve(Differentiable function, double[] y,
			double[] x, double t, double h, final Settings settings);

	// Methods
	static double[] add(double[] a, double[] b)
	{
		if (a.length != b.length)
			return null;
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] + b[i];
		return c;
	}

	static double[] multiply(double[] a, double t)
	{
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] * t;
		return c;
	}

	static double[] sum(double[]... a)
	{
		double[] c = new double[a[0].length];
		for (double[] x : a)
			c = add(c, x);
		return c;
	}

	static double total(double[] a)
	{
		double sum = 0;
		for (double x : a)
			sum += x;
		return sum;
	}

}
