package com.example.android.opengl;

import java.util.ArrayList;

public class Face {
	ArrayList<Vertex> vertices;
	public Face(){
		vertices = new ArrayList<Vertex>();
	}
	
	public void addVertex(Vertex vertex) {
		// TODO Auto-generated method stub
		vertices.add(vertex);
		
	}

}
