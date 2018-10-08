package math.function;

import math.Quaternion;


/**
 * Utilities for quaternion functions (function compose, connect, combine,
 * scale, ...)
 *
 */
public class FunctionR1QuaternionUtil
{

	/**
	 * Class only contains static methods.
	 */
	private FunctionR1QuaternionUtil()
	{
	}

	/**
	 * Composes curve f:R->S^3 with function p:R -> R. <br/>
	 * The functions are composed to f(p(t))
	 * 
	 * @param f
	 *            Quaternion Curve
	 * @param p
	 *            Function
	 * @return the reparametrized function.
	 */
	public static FunctionR1Quaternion compose(final FunctionR1Quaternion f,
			final FunctionR1R1 p)
	{

		return new FunctionR1Quaternion(p.getTMin(), p.getTMax())
		{
			public Quaternion eval(float t)
			{
				return f.eval(p.eval(t));
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
	public static FunctionR1Quaternion restrict(final FunctionR1Quaternion f,
			float tMin, float tMax)
	{

		return new FunctionR1Quaternion(tMin, tMax)
		{

			@Override
			public Quaternion eval(float t)
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
	public static FunctionR1Quaternion connect(final FunctionR1Quaternion... f)
	{
		return new FunctionR1Quaternion(f[0].getTMin(),
				f[f.length - 1].getTMax())
		{

			@Override
			public Quaternion eval(float t)
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

		};
	}

	public static FunctionR1Quaternion connect(final FunctionR1Quaternion f1,
			final float lim1, final FunctionR1Quaternion f2, final float lim2,
			final FunctionR1Quaternion f3)
	{
		return new FunctionR1Quaternion(f1.getTMin(), f3.getTMax())
		{

			@Override
			public Quaternion eval(float t)
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

	public static FunctionR1Quaternion connect(final FunctionR1Quaternion f1,
			final float mid, final FunctionR1Quaternion f2)
	{
		return new FunctionR1Quaternion(f1.getTMin(), f2.getTMax())
		{

			@Override
			public Quaternion eval(float t)
			{
				if (t < mid)
				{
					return f1.eval(t);
				} else
					return f2.eval(t);
			}
		};
	}


	public static FunctionR1Quaternion combine(final FunctionR1Quaternion f1,
			final FunctionR1Quaternion f2)
	{
		FunctionR1Quaternion mid = null;
		float leftIntervalBorder = f1.getTMax();
		float rightIntervalBorder = f2.getTMin();
		
		if (leftIntervalBorder == rightIntervalBorder) // same left interval border
			return FunctionR1QuaternionUtil.connect(f1, f2);

		else if (leftIntervalBorder < rightIntervalBorder)
		{
			Quaternion leftValue = f1.eval(leftIntervalBorder);
			Quaternion rightValue = f2.eval(rightIntervalBorder);
			mid = FunctionR1QuaternionUtil.transition(leftValue, rightValue,
					leftIntervalBorder, rightIntervalBorder);
			return FunctionR1QuaternionUtil.connect(f1, mid, f2);
		}
		else
		{
			mid = FunctionR1QuaternionUtil.blend(f1, f2, rightIntervalBorder, leftIntervalBorder);
			return FunctionR1QuaternionUtil.connect(f1, mid, f2);
		}
	}

	public static FunctionR1Quaternion transition(final Quaternion q1,
			final Quaternion q2, float t1, float t2)
	{
		return new FunctionR1Quaternion(t1, t2)
		{

			@Override
			public Quaternion eval(float t)
			{
				float weight = (t - getTMin()) / (getTMax() - getTMin());
				return Quaternion.slerp(weight, q1, q2);
			}
		};
	}

	/**
	 * @param f1
	 * @param f2
	 * @param t1
	 * @param t2
	 * @return blended function
	 */
	public static FunctionR1Quaternion blend(final FunctionR1Quaternion f1,
			final FunctionR1Quaternion f2, float t1, float t2)
	{
		return new FunctionR1Quaternion(t1, t2)
		{

			@Override
			public Quaternion eval(float t)
			{
				float weight = (t - getTMin()) / (getTMax() - getTMin());
				Quaternion q1 = f1.eval(t);
				Quaternion q2 = f2.eval(t);
				return Quaternion.slerp(weight, q1, q2);
			}

		};
	}

	public static FunctionR1Quaternion scaleoffset(final float g,
			final FunctionR1Quaternion m, final FunctionR1Quaternion d,
			final float t1, final float t2, final float min, final float max)
	{
		return new FunctionR1Quaternion(min, max)
		{

			@Override
			public Quaternion eval(float t)
			{
				Quaternion mv = m.eval(t);
				if ((t >= t1) && (t <= t2))
				{
					Quaternion dv = d.eval(t);
					mv.mul(g).add(dv);
				}
				return mv;
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
	public static FunctionR1Quaternion constant(final Quaternion constant,
			final float t1, final float t2)
	{
		return new FunctionR1Quaternion(t1, t2)
		{

			@Override
			public Quaternion eval(float t)
			{
				return constant;
			}
		};
	}
	
	
	public static FunctionR1Quaternion multiply(final FunctionR1Quaternion func, final Quaternion q,
			final float t1, final float t2)
	{
		return new FunctionR1Quaternion(t1, t2)
		{

			@Override
			public Quaternion eval(float t)
			{
				Quaternion p = func.eval(t);
				return Quaternion.mul(q, p);
			}
		};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}
}
