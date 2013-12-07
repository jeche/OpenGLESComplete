package com.example.android.opengl;

public class Vector2f {
	@Override
	public String toString() {
		return "Vector2f [x=" + x + ", y=" + y + "]";
	}

	public float x;
	public float y;
	
	public float magnitude(){
		double sum = Math.pow(this.x, 2) + Math.pow(this.y, 2);
		return (float) Math.sqrt(sum);
	}
	
	public void normalize(){
		float mag = this.magnitude();
		this.x = this.x/mag;
		this.y = this.y/mag;
	}
	
	public Vector2f add (Vector2f v){
		float s1 = this.x + v.x;
		float s2 = this.y + v.y;
		return new Vector2f(s1, s2);
	}

	public Vector2f sub (Vector2f v){
		float s1 = this.x - v.x;
		float s2 = this.y - v.y;
		return new Vector2f(s1, s2);
	}
	
	public Vector2f(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}
}
