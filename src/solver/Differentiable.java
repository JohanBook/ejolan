////////////////////////////////////////////////////////////////
// Differentiable.java
// An interface that guarantees a function is differentiable.
// Johan Book
// 2015-09-13
////////////////////////////////////////////////////////////////

package solver;

import settings.Settings;

public abstract interface Differentiable
{
	public double[] differentiate(double[] y, double[] x, double t,
			final Settings settings);
}
