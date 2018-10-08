package math.function.util;

import math.Vec3;
import math.function.FunctionVec3Vec3;


public class AddOffsetVec3 extends FunctionVec3Vec3 {
	private Vec3 offset;
	
	public AddOffsetVec3(Vec3 v1, Vec3 v2) {
		this.offset = Vec3.sub(v1, v2);
	}
	
	@Override
	public Vec3 eval(Vec3 v) {
		return Vec3.add(v,  offset);
	}
}
	
	
