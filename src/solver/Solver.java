package solver;

import settings.Settings;

public abstract class Solver {
	public abstract double[] solve(Differentiable function, double[] y, double[] x, double t, double h, final Settings settings);

	// Methods
	public static double[] add(double[] a, double[] b) {
		if (a.length != b.length)
			return null;
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] + b[i];
		return c;
	}

	public static double[] multiply(double[] a, double t) {
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] * t;
		return c;
	}

	public static double[] sum(double[]... a) {
		double[] c = new double[a[0].length];
		for (double[] x : a)
			c = add(c, x);
		return c;
	}

	public static double total(double[] a) {
		double sum = 0;
		for (double x : a)
			sum += x;
		return sum;
	}

}
