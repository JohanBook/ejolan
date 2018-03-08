package solver;

import settings.Settings;

public class Euler extends Solver {

	@Override
	public double[] solve(Differentiable function, double[] y, double[] x,
			double t, double h, final Settings settings) {
		return add(y, multiply(function.differentiate(y, x, t, settings), h));
	}

}
