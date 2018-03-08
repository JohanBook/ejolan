package solver;

import settings.Settings;

public interface Differentiable 
{
	public double[] differentiate(double[] y, double[] x, double t, final Settings settings);
}
