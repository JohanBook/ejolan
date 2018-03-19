package solver;

import settings.Settings;

public class RungeKutta4 extends Solver
{

	@Override
	public double[] solve(Differentiable function, double[] y, double[] x,
			double t, double h, final Settings settings)
	{
		double[] k1 = function.differentiate(y, x, t, settings);
		double[] k2 = function.differentiate(add(y, multiply(k1, 0.5 * h)), x, t
				+ 0.5 * h, settings);
		double[] k3 = function.differentiate(add(y, multiply(k2, 0.5 * h)), x, t
				+ 0.5 * h, settings);
		double[] k4 = function.differentiate(add(y, multiply(k3, h)), x, t + h,
				settings);
		double[] sum = multiply(sum(k1, multiply(k2, 2), multiply(k3, 2), k4),
				h / 6);
		return add(y, sum);
	}

}
