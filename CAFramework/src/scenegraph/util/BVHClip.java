package scenegraph.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import math.Quaternion;
import math.Vec3;
import math.function.FunctionR1Quaternion;
import math.function.FunctionR1QuaternionUtil;
import math.function.FunctionR1R1;
import math.function.FunctionR1Vec3;
import math.function.FunctionR1Vec3Util;
import math.function.util.Offset;
import math.function.util.PiecewiseLinear;
import math.function.util.PolygonVec3;
import math.function.util.ScaleInterval;
import math.function.util.SphericalLinear;
import scenegraph.AbstNode;
import scenegraph.AbstSpatial;
import scenegraph.Group;
import scenegraph.MaterialState;
import animation.AbstController;
import animation.JointController;

public class BVHClip implements Cloneable
{

	private Group skeletonRoot;
	private ArrayList<JointController> controllers;
	private float timePerFrame;
	private int numFrames;
	private Group skeleton;
	private int numJoints;
	private float start = 0;
	private float end = 0;
	private float duration = 0;
	private String name;

	public class Pose
	{
		public Vec3[] transValues;
		public Quaternion[] rotValues;
	}

	public BVHClip()
	{
		this.skeletonRoot = null;
		this.controllers = new ArrayList<JointController>();
		this.timePerFrame = 0;
		this.numFrames = 0;
		this.skeleton = null;
		this.name = "";
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		BVHClip obj = (BVHClip) super.clone();
		obj.skeleton = (Group) skeleton.clone();
		obj.skeletonRoot = (Group) obj.skeleton.getChild(1);
		obj.controllers = new ArrayList<JointController>();
		obj.retargetControllers(obj.skeletonRoot, controllers.listIterator());

		return obj;
	}

	private void retargetControllers(Group joint,
			ListIterator<JointController> iterController)
	{
		if (joint == null)
			return;

		if (!joint.getName().equals("Site"))
		{
			if (iterController.hasNext())
			{
				JointController jc = (JointController) iterController.next();
				JointController jcNew = null;
				try
				{
					jcNew = (JointController) jc.clone();
				} catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (jcNew != null)
				{
					jcNew.setRotChannel(joint.getChannel(AbstSpatial.ROTATION));
					jcNew.setTransChannel(
							joint.getChannel(AbstSpatial.TRANSLATION));
					controllers.add(jcNew);
				}
			} else
				System.err.println("No matching controller found for joint");
		}

		List<AbstNode> children = joint.getChildren();
		if (children != null)
		{
			ListIterator<AbstNode> childrenIterator = joint.getChildren()
					.listIterator();
			while (childrenIterator.hasNext())
			{
				retargetControllers((Group) childrenIterator.next(),
						iterController);
			}
		}
	}

	public ArrayList<JointController> getControllers()
	{
		return controllers;
	}

	public Group getSkeleton()
	{
		return skeleton;
	}

	public void setSkeleton(Group joint)
	{
		this.skeletonRoot = joint;
		this.skeleton = new Group("Skeleton");
		MaterialState mat = new MaterialState();
		mat.setAmbient(1, 1, 1);
		mat.setDiffuse(1, 1, 1);
		skeleton.attachChild(mat);
		skeleton.attachChild(joint);
	}

	public float getTimePerFrame()
	{
		return timePerFrame;
	}

	public void setTimePerFrame(float timePerFrame)
	{
		this.timePerFrame = timePerFrame;
		if (numFrames != 0)
			this.end = numFrames * timePerFrame;
	}

	public int getNumFrames()
	{
		return numFrames;
	}

	public void setNumFrames(int numFrames)
	{
		this.numFrames = numFrames;
		if (timePerFrame != 0)
			this.end = numFrames * timePerFrame;
	}

	public int getNumJoints()
	{
		return numJoints;
	}

	public void setNumJoints(int numJoints)
	{
		this.numJoints = numJoints;
	}

	public void extract(float start, float end)
	{
		this.duration = end - start;
		for (AbstController controller : controllers)
		{
			controller.setMinTime(start);
			controller.setMaxTime(end);
		}

		this.compose((new ScaleInterval(0, duration, start, end)));
		this.start = 0;
		this.end = duration;
	}

	public void setRepeatType(AbstController.RepeatType type)
	{
		for (AbstController controller : controllers)
			controller.setRepeatType(type);
	}

	public void schedule(float time)
	{
		float min = getMin();
		float max = getMax();
		// this.compose((new ScaleInterval(time, time + duration, min, max)));
		this.compose(new Offset(time, min, max));
		for (AbstController controller : controllers)
			controller.setGlobalStartTime(time);
		this.start += time;
		this.end += time;
	}

	public void setRate(float rate)
	{
		for (AbstController controller : controllers)
			controller.setRate(rate);
	}

	public void combine(BVHClip second)
	{
		ArrayList<JointController> secondControllers = second.getControllers();
		int secondNumJoints = second.getNumJoints();
		assert(numJoints == secondNumJoints) : "Can only combine clips with same number of joints";
		for (int i = 0; i < numJoints; i++)
		{
			JointController jc1 = controllers.get(i);
			JointController jc2 = secondControllers.get(i);
			FunctionR1Vec3 tf1 = jc1.getTranslationsFunc();
			FunctionR1Vec3 tf2 = jc2.getTranslationsFunc();
			this.start = jc1.getMinTime();
			this.end = jc2.getMaxTime();
			if ((tf1 != null) && (tf2 != null))
				jc1.setTranslationsFunc(
						FunctionR1Vec3Util.combineShift(tf1, tf2));
			FunctionR1Quaternion rf1 = jc1.getRotationsFunc();
			FunctionR1Quaternion rf2 = jc2.getRotationsFunc();
			if ((rf1 != null) && (rf2 != null))
			{
				jc1.setRotationsFunc(
						FunctionR1QuaternionUtil.combine(rf1, rf2));
				jc1.setMaxTime(rf2.getTMax());
			}
		}
	}

	/**
	 * @param s
	 */
	public void compose(FunctionR1R1 s)
	{
		for (AbstController controller : controllers)
		{
			JointController jc = (JointController) controller;
			FunctionR1Vec3 transFunc = jc.getTranslationsFunc();
			if (transFunc != null)
				jc.setTranslationsFunc(
						FunctionR1Vec3Util.compose(transFunc, s));
			FunctionR1Quaternion rotFunc = jc.getRotationsFunc();
			if (rotFunc != null)
				jc.setRotationsFunc(
						FunctionR1QuaternionUtil.compose(rotFunc, s));
			jc.setMinTime(s.getTMin());
			jc.setMaxTime(s.getTMax());
		}
	}

	/**
	 * @param time
	 * @return pose at specified time
	 */
	public ArrayList<Pose> getPoses(float[] timeGrid)
	{
		ArrayList<Pose> warpPoses = new ArrayList<Pose>();
		for (float time : timeGrid)
		{
			assert(time >= start
					&& time <= end) : "Cannot set warp pose for specified time "
							+ time
							+ ", because time is outside allowed interval ["
							+ start + ',' + end + ']';

			int indexTrans = 0;
			int indexRot = 0;
			Pose pose = new Pose();
			pose.transValues = new Vec3[numJoints];
			pose.rotValues = new Quaternion[numJoints];

			for (AbstController controller : controllers)
			{
				JointController jc = (JointController) controller;
				FunctionR1Vec3 transFunc = jc.getTranslationsFunc();
				if (transFunc != null)
				{
					Vec3 pos = transFunc.eval(time);
					pose.transValues[indexTrans++] = pos;
				}
				FunctionR1Quaternion rotFunc = jc.getRotationsFunc();
				if (rotFunc != null)
				{
					Quaternion orientation = rotFunc.eval(time);
					pose.rotValues[indexRot++] = orientation;
				}
			}
			warpPoses.add(pose);
		}
		return warpPoses;
	}

	/**
	 * @param time
	 * @return pose at specified time
	 */
	public Pose getPose(float time)
	{
		assert(time >= start
				&& time <= end) : "Cannot set warp pose for specified time "
						+ time + ", because time is outside allowed interval ["
						+ start + ',' + end + ']';

		int indexTrans = 0;
		int indexRot = 0;
		Pose pose = new Pose();
		pose.transValues = new Vec3[numJoints];
		pose.rotValues = new Quaternion[numJoints];

		for (AbstController controller : controllers)
		{
			JointController jc = (JointController) controller;
			time = ((AbstController) jc).getLocalTime(time);
			FunctionR1Vec3 transFunc = jc.getTranslationsFunc();
			if (transFunc != null)
			{
				Vec3 pos = transFunc.eval(time);
				pose.transValues[indexTrans++] = pos;
			}
			FunctionR1Quaternion rotFunc = jc.getRotationsFunc();
			if (rotFunc != null)
			{
				Quaternion orientation = rotFunc.eval(time);
				pose.rotValues[indexRot++] = orientation;
			}
		}

		return pose;
	}

	public void setPose(float time)
	{
		for (AbstController controller : controllers)
		{
			JointController jc = (JointController) controller;
			jc.update(time);
		}
	}

	public void warp(ArrayList<Pose> warpPoses, float[] timeGrid,
			float[] scaledTimeGrid, final float g)
	{
		assert(warpPoses.size() >= timeGrid.length) : "Not enough warp poses specified for given time grid";

		int positions = timeGrid.length;
		float[] nTimeGrid = new float[positions + 2];
		float[] nScaledTimeGrid = new float[positions + 2];
		nTimeGrid[0] = start;
		nTimeGrid[positions + 1] = end;
		nScaledTimeGrid[0] = start;
		float diff = end - timeGrid[positions - 1];
		float newEnd = scaledTimeGrid[positions - 1] + diff;
		nScaledTimeGrid[positions + 1] = newEnd;

		for (int i = 0; i < positions; i++)
		{
			nTimeGrid[i + 1] = timeGrid[i];
			nScaledTimeGrid[i + 1] = scaledTimeGrid[i];
		}

		PiecewiseLinear timeScale = new PiecewiseLinear(nTimeGrid,
				nScaledTimeGrid);

		for (int index = 0; index < controllers.size(); index++)
		{
			JointController jc = (JointController) controllers.get(index);
			// FunctionR1Vec3 transFunc = jc.getTranslationsFunc();
			// if (transFunc != null)
			// {
			// Vec3 d[] = new Vec3[positions];
			// for (int i = 0; i < positions; i++)
			// {
			// Vec3 x = warpPoses.get(i).transValues[index];
			// Vec3 m = transFunc.eval(timeGrid[i]).mul(g);
			// d[i] = Vec3.sub(x, m);
			// }
			//
			// PolygonVec3 dfunc = new PolygonVec3(d, timeGrid);
			// FunctionR1Vec3 warp = FunctionR1Vec3Util.scaleoffset(g,
			// transFunc, dfunc, timeGrid[0], timeGrid[positions - 1]);
			// jc.setTranslationsFunc(warp);
			// }

			FunctionR1Vec3 transFunc = jc.getTranslationsFunc();
			if (transFunc != null)
			{
				FunctionR1Vec3 stransFunc = FunctionR1Vec3Util
						.compose(transFunc, timeScale);
				jc.setTranslationsFunc(stransFunc);
			}

			FunctionR1Quaternion rotFunc = jc.getRotationsFunc();
			if (rotFunc != null)
			{
				Quaternion qd[] = new Quaternion[positions];
				for (int i = 0; i < positions; i++)
				{
					Quaternion x = warpPoses.get(i).rotValues[index];
					Quaternion m = rotFunc.eval(timeGrid[i]).mul(-g);
					qd[i] = Quaternion.add(x, m);
				}

				SphericalLinear dfunc = new SphericalLinear(qd, timeGrid);

				FunctionR1Quaternion warp = FunctionR1QuaternionUtil
						.scaleoffset(g, rotFunc, dfunc, timeGrid[0],
								timeGrid[positions - 1], start, end);

				FunctionR1Quaternion swarp = FunctionR1QuaternionUtil
						.compose(warp, timeScale);

				jc.setRotationsFunc(swarp);
				jc.setMaxTime(newEnd);
			}
		}
		end = newEnd;
	}

	public float getDuration()
	{
		float duration = 0;
		if (controllers != null)
		{
			AbstController c = controllers.get(0);
			duration = c.getInitialDuration();
		}
		return duration;
	}

	public float getMin()
	{
		float min = 0;
		if (controllers != null)
		{
			AbstController c = controllers.get(0);
			min = c.getMinTime();
		}
		return min;
	}

	public float getMax()
	{
		float max = 0;
		if (controllers != null)
		{
			AbstController c = controllers.get(0);
			max = c.getMaxTime();
		}
		return max;
	}

	public float getGlobalStart()
	{
		float globalStart = 0;
		if (controllers != null)
		{
			AbstController c = controllers.get(0);
			globalStart = c.getGlobalStartTime();
		}
		return globalStart;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
