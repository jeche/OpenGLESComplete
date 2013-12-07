package com.example.android.opengl;

public class Vector3f {
	public float x;
	public float y;
	public float z;
	
	public float magnitude(){
		double sum = Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2);
		return (float) Math.sqrt(sum);
	}
	

    public Vector3f normal()
    {
        float l = magnitude();

        return new Vector3f(x / l, y / l, z / l);
    }
	
	@Override
	public String toString() {
		return "Vector3f [x=" + x + ", y=" + y + ", z=" + z + "]";
	}


	public void normalize(){
		float mag = this.magnitude();
		this.x = this.x/mag;
		this.y = this.y/mag;
		this.z = this.z/mag;
	}
	
	public static Vector3f cross(Vector3f a, Vector3f b){
		 float s1 = a.y*b.z - a.z * b.y;
		 float s2 = a.z*b.x - a.x*b.z;
		 float s3 = a.x * b.y - a.y * b.x;
		 return new Vector3f(s1,s2,s3);
	}
	
	public Vector3f add (Vector3f v){
		float s1 = this.x + v.x;
		float s2 = this.y + v.y;
		float s3 = this.z + v.z;
		return new Vector3f(s1, s2 ,s3);
	}

	public Vector3f sub (Vector3f v){
		float s1 = this.x - v.x;
		float s2 = this.y - v.y;
		float s3 = this.z - v.z;
		return new Vector3f(s1, s2 ,s3);
	}
	
	public Vector3f(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
