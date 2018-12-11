package Aufgabenblatt_8.Asbach_Dustin;
/*CA Winter 2018/19
*Name,  Vorname :  Asbach, Dustin
*Matrikelnummer :  11117108
*Aufgabenblatt : 8
*Aufgabe : 8.2
*/
import math.Mat4;
import math.Quaternion;
import math.Vec3;
import math.function.FunctionR1Quaternion;
import math.function.InterpolationQuaternion;

public class SphericalCubic extends FunctionR1Quaternion implements InterpolationQuaternion {

	private Quaternion[] quats, contQuats;
	private float[] grid;

	/**
	 * Given the N float values, the function interpolates these values linearly
	 * over the interval [tmin, tmax]
	 * 
	 * @param angles The angles to be interpolated
	 * @param grid
	 */
	public SphericalCubic(Vec3[] angles, Vec3[] contP, float[] grid) {
		super(grid[0], grid[grid.length - 1]);

		Mat4 rotateMatrix;
		quats = new Quaternion[angles.length];
		contQuats = new Quaternion[contP.length];

		for (int i = 0; i < angles.length; i++) {
			Vec3 angle = angles[i];
			rotateMatrix = Mat4.rotationZ(angle.z);
			rotateMatrix.mul(Mat4.rotationY(angle.y));
			rotateMatrix.mul(Mat4.rotationX(angle.x));
			this.quats[i] = new Quaternion(rotateMatrix);
		}
		//calculating quaternions for controlangles
		for (int i = 0; i < contP.length; i++) {
			Vec3 controllP = contP[i];
			rotateMatrix = Mat4.rotationZ(controllP.z);
			rotateMatrix.mul(Mat4.rotationY(controllP.y));
			rotateMatrix.mul(Mat4.rotationX(controllP.x));
			this.contQuats[i] = new Quaternion(rotateMatrix);
		}
		this.grid = grid;
	}

	public SphericalCubic(Quaternion[] rots, float[] grid) {
		super(grid[0], grid[grid.length - 1]);
		this.quats = rots;
		this.grid = grid;
	}

	/**
	 * 
	 */
	@Override
	public Quaternion eval(float t) {
		int index = binarysearch(t);
		if (index == -1) {
			// return start value: does it make sense?
			return quats[0];
		}
		if (index == grid.length - 1) {
			return quats[grid.length - 1];
		} else {
			float deltaT = grid[index + 1] - grid[index];
			float weight = (t - grid[index]) / deltaT;
			//use squad instead of slerp and add the relevant controlquaternions
			return Quaternion.squad(weight, quats[index], quats[index + 1], contQuats[index * 2],
					contQuats[index * 2 + 1]);
		}
	}

	/**
	 * Find the index of the interval with t \in [t_i,t_i+1)
	 * 
	 * @param t
	 * @return index of interval
	 */
	private int binarysearch(float t) {
		int low = 0;
		int high = grid.length - 1;

		if (t < grid[0] || t > grid[grid.length - 1])
			return -1;

		int mid = 0;
		while (high - low > 1) {
			mid = low + (high - low) / 2;
			if (t <= grid[mid]) {
				high = mid;
			} else if (t > grid[mid]) {
				low = mid;
			}
		}

		return low;
	}

	public Quaternion[] getPoints() {
		return quats;
	}

	public float[] getGrid() {
		return grid;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
