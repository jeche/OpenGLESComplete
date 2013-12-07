package com.example.android.opengl;

public class Vertex{
	public Vertex(Vector3f vector3f, Vector3f vector3f2, Vector2f vector2f) {
		// TODO Auto-generated constructor stub
		position = vector3f;
		normal = vector3f2;
		textureCoord = vector2f;
	}
	short index;
	Vector3f position;
	Vector3f normal;
//	Vector2f tex;
	Vector2f textureCoord;
}
