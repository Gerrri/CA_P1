package math.function;

import math.Quaternion;
import math.Vec3;
import math.function.util.BezierCurve;
import math.function.util.PiecewiseLinear;

public class FunctionR1Vec3Util
{

	/**
	 * Class only contains static methods.
	 */
	private FunctionR1Vec3Util()
	{
	}

	/**
	 * Composes curve f:R->R^3 with function p:R -> R. <br/>
	 * The functions are composed to f(p(t))
	 * 
	 * @param f
	 *            Curve
	 * @param p
	 *            Function
	 * @return the reparameterized function.
	 */
	public static FunctionR1Vec3 compose(final FunctionR1Vec3 f,
			final FunctionR1R1 p)
	{

		return new FunctionR1Vec3(p.getTMin(), p.getTMax())
		{

			@Override
			public Vec3 eval(float t)
			{	
				return f.eval(p.eval(t));
			}

			@Override
			public Vec3 getTangent(float t)
			{
				return f.getTangent(p.eval(t));
			}

			@Override
			public Vec3 getNormal(float t)
			{
				return f.getNormal(p.eval(t));
			}
		};
	}
	
	
	public static FunctionR1Vec3 compose(final FunctionVec3Vec3 g, final FunctionR1Vec3 f)
	{

		return new FunctionR1Vec3(f.getTMin(), f.getTMax())
		{

			@Override
			public Vec3 eval(float t)
			{
				return g.eval(f.eval(t));
			}
		};
	}

	/**
	 * Restricts curve f to the interval [tMin, tMax] <br/>
	 * 
	 * @param f
	 *            Curve
	 * @param tMin
	 *            Left interval border
	 * @param tMax
	 *            Right interval border
	 * @return the restricted function.
	 */
	public static FunctionR1Vec3 restrict(final FunctionR1Vec3 f, float tMin,
			float tMax)
	{

		return new FunctionR1Vec3(tMin, tMax)
		{

			@Override
			public Vec3 eval(float t)
			{
				return f.eval(t);
			}
		};
	}

	/**
	 * Connects f_i [a_i,b_i] -> R^3, with i = 0, ..., n. We assume, that b_i =
	 * a_{i+1} for i = 0, ..., n-1. and b0 < b1 < ... ldots b_{n-1}. a_0 may be
	 * negative infinity, b_n may be positive infinity. For t in [a_0, b_0) the
	 * function f_0 is evaluated, for t in [a_1, b_1) the function f_1 is
	 * evaluated and so on.
	 */
	public static FunctionR1Vec3 connect(final FunctionR1Vec3... f){
	return new FunctionR1Vec3(f[0].getTMin(), f[f.length - 1].getTMax())
	{

			@Override
			public Vec3 eval(float t)
			{
				// System.out.print(t + ",");
				if (f[0].getTMin() == Float.NEGATIVE_INFINITY
						&& t < f[0].getTMax())
				{
					return f[0].eval(t);
				} else if (f[f.length - 1].getTMax() == Float.POSITIVE_INFINITY
						&& t >= f[f.length - 1].getTMin())
				{
					return f[f.length - 1].eval(t);
				} else
				{
					int i = 0;
					while (i < f.length - 1 && t >= f[i + 1].getTMin())
					{
						i++;
					}
					return f[i].eval(t);
				}

			}

			@Override
			public Vec3 getTangent(float t)
			{
				if (f[0].getTMin() == Float.NEGATIVE_INFINITY
						&& t < f[0].getTMax())
				{
					return f[0].getTangent(t);
				} else if (f[f.length - 1].getTMax() == Float.POSITIVE_INFINITY
						&& t >= f[f.length - 1].getTMin())
				{
					return f[f.length - 1].getTangent(t);
				} else
				{
					int i = 0;
					while (i < f.length - 1 && t >= f[i + 1].getTMin())
					{
						i++;
					}
					return f[i].getTangent(t);
				}
			}

			@Override
			public Vec3 getNormal(float t)
			{
				if (f[0].getTMin() == Float.NEGATIVE_INFINITY
						&& t < f[0].getTMax())
				{
					return f[0].getNormal(t);
				} else if (f[f.length - 1].getTMax() == Float.POSITIVE_INFINITY
						&& t >= f[f.length - 1].getTMin())
				{
					return f[f.length - 1].getNormal(t);
				} else
				{
					int i = 0;
					while (i < f.length - 1 && t >= f[i + 1].getTMin())
					{
						i++;
					}
					return f[i].getNormal(t);
				}
			}

		};
	}

	public static FunctionR1Vec3 connect(final FunctionR1Vec3 f1,
			final float lim1, final FunctionR1Vec3 f2, final float lim2,
			final FunctionR1Vec3 f3)
	{
		return new FunctionR1Vec3(f1.getTMin(), f3.getTMax())
		{

			@Override
			public Vec3 eval(float t)
			{
				if (t < lim1)
				{
					return f1.eval(t);
				} else if (t < lim2)
				{
					return f2.eval(t);
				} else
					return f3.eval(t);
			}
		};
	}

	public static FunctionR1Vec3 connect(final FunctionR1Vec3 f1,
			final float mid, final FunctionR1Vec3 f2)
	{
		return new FunctionR1Vec3(f1.getTMin(), mid + f2.getTMax())
		{

			@Override
			public Vec3 eval(float t)
			{
				if (t < mid)
				{
					return f1.eval(t);
				} else
					return f2.eval(t);
			}
		};
	}


	/**
	 * Constructs a function out of two functions, and shifts the second
	 * function so that the values of both functions coincide at the interval
	 * borders. This is useful for combining translation functions for skeleton
	 * movements, as the skeleton does not move to another position when
	 * combining clips.
	 * 
	 * @param f1
	 *            function f1
	 * @param f2
	 *            function f2
	 * @return combined function, which is either blended on the overlapping
	 *         interval or interpolated on an interval gap. The second part of
	 *         the curve is shifted, so that the values coincides on the
	 *         interval borders
	 */
	public static FunctionR1Vec3 combineShift(final FunctionR1Vec3 f1,
			final FunctionR1Vec3 f2)
	{

		float leftIntervalBorder = f1.getTMax();
		float rightIntervalBorder = f2.getTMin();
		
		FunctionR1Vec3 mid;
		FunctionR1Vec3 special;

		if (leftIntervalBorder < rightIntervalBorder)
		{
			Vec3 leftValue = f1.eval(leftIntervalBorder);
			Vec3 rightValue = f2.eval(rightIntervalBorder);
			Vec3 diff = Vec3.sub(leftValue, rightValue);
			special = FunctionR1Vec3Util.shift(f2, diff, rightIntervalBorder,
					f2.getTMax());
			mid = FunctionR1Vec3Util.constant(leftValue, rightIntervalBorder, leftIntervalBorder);
			return FunctionR1Vec3Util.connect(f1, mid, special);
		} else
		{
			Vec3 diff = Vec3.sub(f1.eval(rightIntervalBorder), f2.eval(leftIntervalBorder));
			special = FunctionR1Vec3Util.shift(f2, diff, rightIntervalBorder,f2.getTMax() );
			if (leftIntervalBorder == rightIntervalBorder)
				return FunctionR1Vec3Util.connect(f1, special);
			else
			{
				mid = FunctionR1Vec3Util.blend(f1, special, rightIntervalBorder,
						leftIntervalBorder);
				return FunctionR1Vec3Util.connect(f1, rightIntervalBorder, mid, leftIntervalBorder, special);
			}
		}

	}

	/**
	 * Builds transition between two interval borders as linear function.
	 * 
	 * @param v1
	 *            value at left interval border
	 * @param v2
	 *            value at right interval border
	 * @param t1
	 *            left interval border
	 * @param t2
	 *            right interval border
	 * @return transition function
	 */
	public static FunctionR1Vec3 transition(final Vec3 v1, final Vec3 v2,
			final float t1, final float t2)
	{
		return new FunctionR1Vec3(t1, t2)
		{

			@Override
			public Vec3 eval(float t)
			{
				float weight = (t - t1) / (t2 - t1);
				return Vec3.mix(v1, v2, weight);
			}

		};
	}

	/**
	 * Builds constant function
	 * 
	 * @param constant
	 *            the constant that should be returned by the function
	 * @param t1
	 *            left interval border
	 * @param t2
	 *            right interval border
	 * @return constant function
	 */
	public static FunctionR1Vec3 constant(final Vec3 constant, final float t1,
			final float t2)
	{
		return new FunctionR1Vec3(t1, t2)
		{

			@Override
			public Vec3 eval(float t)
			{
				return constant;
			}
		};
	}

	/**
	 * Constructs shift function that shifts the result by a certain value
	 * 
	 * @param f
	 *            function to be shifted
	 * @param diff
	 *            shift value
	 * @param t1
	 *            left interval border
	 * @param t2
	 *            right interval border
	 * @return shifted function
	 */
	public static FunctionR1Vec3 shift(final FunctionR1Vec3 f, final Vec3 diff,
			final float t1, final float t2)
	{
		return new FunctionR1Vec3(t1, t2)
		{

			@Override
			public Vec3 eval(float t)
			{
				Vec3 result = Vec3.add(f.eval(t), diff);
				return result;
			}
		};
	}

	/**
	 * Blends two functions in a linear way
	 * 
	 * @param f1
	 *            function f1
	 * @param f2
	 *            function f2
	 * @param t1
	 *            left interval border
	 * @param t2
	 *            right interval border
	 * @return blended function
	 */
	public static FunctionR1Vec3 blend(final FunctionR1Vec3 f1,
			final FunctionR1Vec3 f2, final float t1, final float t2)
	{
		return new FunctionR1Vec3(t1, t2)
		{

			@Override
			public Vec3 eval(float t)
			{
				float weight = (t - t1) / (t2 - t1);
				Vec3 v1 = f1.eval(t);
				Vec3 v2 = f2.eval(t);
				return Vec3.mix(v1, v2, weight);
			}

		};
	}

	public static FunctionR1Vec3 scaleoffset(final float g,
			final FunctionR1Vec3 m, final FunctionR1Vec3 d, final float t1,
			final float t2)
	{
		return new FunctionR1Vec3(t1, t2)
		{

			@Override
			public Vec3 eval(float t)
			{
				Vec3 mv = m.eval(t);
				Vec3 dv = d.eval(t);
				return (Vec3.mul(mv, g)).add(dv);
			}
		};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Vec3[] points1 = { new Vec3(0.0f, 0.0f, 0.0f),
				new Vec3(0.0f, 1.0f, 0.0f), new Vec3(1.0f, 1.0f, 0.0f),
				new Vec3(1.0f, 0.0f, 0.0f) };
		Vec3[] points2 = { new Vec3(1.0f, 0.0f, 0.0f),
				new Vec3(1.0f, -1.0f, 0.0f), new Vec3(2.0f, -1.0f, 0.0f),
				new Vec3(2.0f, 0.0f, 0.0f) };
		Vec3[] points3 = { new Vec3(2.0f, 0.0f, 0.0f),
				new Vec3(2.0f, 1.0f, 0.0f), new Vec3(3.0f, 1.0f, 0.0f),
				new Vec3(3.0f, 0.0f, 0.0f) };

		BezierCurve b1 = new BezierCurve(points1);
		BezierCurve b2 = new BezierCurve(points2);
		BezierCurve b3 = new BezierCurve(points3);

		// shift b2 in time to interval [1,2]
		float[] t1 = { 0.0f, 1.0f };
		float[] grid1 = { 1.0f, 2.0f };
		PiecewiseLinear trafo1 = new PiecewiseLinear(t1, grid1);
		FunctionR1Vec3 b2prime = FunctionR1Vec3Util.compose(b2, trafo1);

		// shift b3 in time to interval [2,3]
		float[] t2 = { 0.0f, 1.0f };
		float[] grid2 = { 2.0f, 3.0f };
		PiecewiseLinear trafo2 = new PiecewiseLinear(t2, grid2);
		FunctionR1Vec3 b3prime = FunctionR1Vec3Util.compose(b3, trafo2);
		System.out.println(b3prime.getTMax() + " " + b3prime.getTMin());

		FunctionR1Vec3 curve = FunctionR1Vec3Util.connect(b1, b2prime, b3prime);

		int samples = 100;
		System.out.println(curve.getTMax() + " " + curve.getTMin());
		float delta = (curve.getTMax() - curve.getTMin()) / (samples - 1);

		for (int i = 0; i < samples; i++)
		{
			float t = curve.getTMin() + i * delta;
			Vec3 v = curve.eval(t);
			System.out.println(v.x + ", " + v.y);
		}

	}

}
