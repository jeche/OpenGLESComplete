package com.example.android.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.ETC1Util;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class ResourceLoader {
	
	
	
    Activity stuff;
    ResourceLoader(Activity activity){
        stuff = activity;
    }
public Mesh loadMesh(String meshName, String fileName){
    BufferedReader br = null;
    AssetManager assetMgr = stuff.getAssets();

    try {
        assetMgr.list("shaders");
        for(int i = 0; i < assetMgr.list("shaders").length; i++){
            System.out.println(assetMgr.list("shaders")[i]);
        }
    } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }
    try{
        InputStream iS = assetMgr.open(fileName, AssetManager.ACCESS_STREAMING);
        String line;
        br = new BufferedReader(new InputStreamReader(iS));
        List<Float> vertices = new ArrayList<Float>();
//      List<Short> indices,
        List<Short> tIndices = new ArrayList<Short>();
        List<Short> indices2 = new ArrayList<Short>();
        List<Vertex> vertices2 = new ArrayList<Vertex>();
        List<Vector3f> v3f = new ArrayList<Vector3f>();
        List<Vector3f> vertexNormals = new ArrayList<Vector3f>();
        List<Short> faces = new ArrayList<Short>();
        List<Vector2f> textureVertices = new ArrayList<Vector2f>();
        float[] v = null;
        short[] f = null;
        
        while((line = br.readLine()) != null){
            String[] words;
//          System.out.println(line);
            words = line.split(" ");
            
            if(words[0].equals("v")){
                float x = Float.parseFloat(words[1]);
                float y = Float.parseFloat(words[2]);
                float z = Float.parseFloat(words[3]);
                Vector3f vect = new Vector3f(x, y, z);
                v3f.add(vect); // positions
                vertexNormals.add(new Vector3f(0,0,0));
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
            }else if(words[0].equals("f")){
                parseFace(words, 0 , tIndices, faces);
//                faces.addAll(indices);
//                indices = parseFace(words, 1);
//                tIndices.addAll(indices);
            }else if(words[0].equals("vn")){
//                float x = Float.parseFloat(words[1]);
//                float y = Float.parseFloat(words[2]);
//                float z = Float.parseFloat(words[3]);
////              Vector3f = 
//                vertexNormals.add(x);
//                vertexNormals.add(y);
//                vertexNormals.add(z);
            }else if(words[0].equals("vt")){
                float s = Float.parseFloat(words[1]);
                float t = Float.parseFloat(words[2]);
                t= 1- t;
                Vector2f v2f = new Vector2f(s, t);
				textureVertices.add(v2f);
            }


//          System.out.println("Faces size " + f.length + " " + faces.size());
//          System.out.println("V size " + v.length + " " + vertices.size());
//          int i = 0;
            
        }
        Vector3f[] vNorm = new Vector3f[vertices.size()];
        for(int j = 0; j < vNorm.length; j++){
        	vNorm[j] = new Vector3f(0,0,0);
        }
        for(int i = 0; i < faces.size(); i+=3){
        	Short v1 = faces.get(i);
        	Short v2 = faces.get(i + 1);
        	Short v3 = faces.get(i + 2);
        	Vector3f p = v3f.get(v1).sub(v3f.get(v3));
        	Vector3f p1 = v3f.get(v1).sub(v3f.get(v2));
        	Vector3f norm = Vector3f.cross(p1, p);
        	norm.normalize();
        	vertexNormals.add(norm);
        	vNorm[v1] = vNorm[v1].add(norm);
        	vNorm[v1].normalize();
        	vNorm[v2] = vNorm[v2].add(norm);
        	vNorm[v2].normalize();
        	vNorm[v3] = vNorm[v3].add(norm);
        	vNorm[v3].normalize();
        }
        ArrayList<Vertex> finalVertex = new ArrayList<Vertex>();
        Map<String, Vertex> mapper = new HashMap<String, Vertex>();
        ArrayList<Short> dor = new ArrayList<Short>(); 
        Short l = 0;
        for(int j = 0; j < faces.size(); j++){
        	String m = v3f.get(faces.get(j)).toString() + textureVertices.get(tIndices.get(j)).toString();
        	Vertex vg;
        	if(mapper.containsKey(m)){
        		vg = mapper.get(m);
//        		finalVertex.add(vg); // if problems change this.
        		
        		dor.add(vg.index);
        	}else{
        		vg = new Vertex(v3f.get(faces.get(j)), vNorm[faces.get(j)], textureVertices.get(tIndices.get(j)));
        		vg.index = l;
        		finalVertex.add(vg);
        		mapper.put(m, vg);
        		dor.add(l);
        		l++;
        	}
        	
        }
        // LOOP THROUGH EITHER POSITION OR TEXTURE INDICES
        // concat the vals.  Attempt to find in hashmap, if null set value in uvCoords to val stored at index texIndices and add
        // (we are making a new list of final Vertices) with old position, old normal, and if its a new one the texcoord at wherever it was told, if it is already there
        //  index only increments if you added the new one because of the null; else add index to finalIndices to denote duplicates in final Vertex list.
//        int i = 0;
//        v = new float[vertices.size()];
//        for(Float vertex: vertices){
//            v[i++] = vertex;
//        }
//        i = 0;
//        f = new short[faces.size()];
//        for (Short face: faces){
//            f[i++] = face;
//        }
//        i = 0;
//        float[] vn = new float[vertexNormals.size()];
//        for(Float n : vertexNormals){
//            vn[i++] = n;
//        }
        
//      meshes.put(meshName, mesh);
//        return new Mesh(v, f, indices2, vertices2, this);
        return new Mesh(finalVertex, dor, this);
    }catch(Exception e){
        e.printStackTrace();
    }
    return null;
    
    
}
public static void parseFace(String[] words, int k, List<Short> tIndices, List<Short> faces){
    List<Short> indices = new ArrayList<Short>();
    String[] parts;
    int i = 1;
    for(i = 1; i<4; i++){
        parts = words[i].split("/");
        short s = Short.parseShort(parts[0]);
        s--;
        faces.add(s);
        s = Short.parseShort(parts[1]);
        s--;
        tIndices.add(s);
    }
//    return indices;
}
////
////public String readTextFile(String filename){
////    BufferedReader br = null;
////    String line = "";
////    AssetManager assetMgr = stuff.getAssets();
////    try{
////        InputStream is = assetMgr.open(filename);
////        StringBuilder builder = new StringBuilder();
////        br = new BufferedReader(new InputStreamReader(is));
////        
////    } catch(Exception e){
////        e.printStackTrace();
////    }
////    return line;
////}
////
public int loadTexture(String fileName){
    final int[] textureHandle = new int[1];
    GLES20.glGenTextures(1,  textureHandle, 0);
    AssetManager assetMgr = stuff.getAssets();
    InputStream is = null;
    try{
        is = assetMgr.open(fileName, AssetManager.ACCESS_STREAMING);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
        MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,  GLES20.GL_TEXTURE_MIN_FILTER,  GLES20.GL_NEAREST);
        MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,  GLES20.GL_TEXTURE_MAG_FILTER,  GLES20.GL_NEAREST);
        MyGLRenderer.checkGlError("glGetUniformLocation");
        ETC1Util.loadTexture(GLES10.GL_TEXTURE_2D, 0,0, GLES10.GL_RGB, GLES10.GL_UNSIGNED_SHORT_5_6_5, is);
        MyGLRenderer.checkGlError("glGetUniformLocation");
    	
		
		// Load the bitmap into the bound texture.
		
        
    }catch(IOException ex){
        ex.printStackTrace();
    }finally{
        try{
            if(is != null){
                is.close();
            }
        } catch(IOException e){
        	e.printStackTrace();
        }
    }
    return textureHandle[0];
}
//   
//	public Mesh load(String file) {
//		AssetManager assetMgr = stuff.getAssets();
//		InputStream in = null;
//        try {
//        	in = assetMgr.open(file, AssetManager.ACCESS_STREAMING);
////        	AssetManager assetMgr = stuff.getAssets();
//            List<String> lines = readLines(in);
//
//            float[] vertices = new float[lines.size() * 3];
//            float[] normals = new float[lines.size() * 3];
//            float[] uv = new float[lines.size() * 2];
//
//            int numVertices = 0;
//            int numNormals = 0;
//            int numUV = 0;
//            int numFaces = 0;
//
//            int[] facesVerts = new int[lines.size() * 3];
//            int[] facesNormals = new int[lines.size() * 3];
//            int[] facesUV = new int[lines.size() * 3];
//            int vertexIndex = 0;
//            int normalIndex = 0;
//            int uvIndex = 0;
//            int faceIndex = 0;
//            List<Short> faces = new ArrayList<Short>();
//            short[] f = null;
//
//
//            for (int i = 0; i < lines.size(); i++) {
//                String line = lines.get(i);
//
//                if (line.startsWith("v ")) {
//                    String[] tokens = line.split("[ ]+");
//                    vertices[vertexIndex] = Float.parseFloat(tokens[1]);
//                    vertices[vertexIndex + 1] = Float.parseFloat(tokens[2]);
//                    vertices[vertexIndex + 2] = Float.parseFloat(tokens[3]);
//                    vertexIndex += 3;
//                    numVertices++;
//                    continue;
//                }
//
//                if (line.startsWith("vn ")) {
//                    String[] tokens = line.split("[ ]+");
//                    normals[normalIndex] = Float.parseFloat(tokens[1]);
//                    normals[normalIndex + 1] = Float.parseFloat(tokens[2]);
//                    normals[normalIndex + 2] = Float.parseFloat(tokens[3]);
//                    normalIndex += 3;
//                    numNormals++;
//                    continue;
//                }
//
//                if (line.startsWith("vt")) {
//                    String[] tokens = line.split("[ ]+");
//                    uv[uvIndex] = Float.parseFloat(tokens[1]);
//                    uv[uvIndex + 1] = Float.parseFloat(tokens[2]);
//                    uvIndex += 2;
//                    numUV++;
//                    continue;
//                }
//
//                if (line.startsWith("f ")) {
//                	String sting = line;
//                	String[] words = line.split(" ");
//                	List<Short> indices = parseFace(words);
//                  	faces.addAll(indices);
//                    String[] tokens = line.split("[ ]+");
//
//                    String[] parts = tokens[1].split("/");
//                    facesVerts[faceIndex] = getIndex(parts[0], numVertices);
//                    if (parts.length > 2)
//                        facesNormals[faceIndex] = getIndex(parts[2], numNormals);
//                    if (parts.length > 1)
//                        facesUV[faceIndex] = getIndex(parts[1], numUV);
//                    faceIndex++;
//
//                    parts = tokens[2].split("/");
//                    facesVerts[faceIndex] = getIndex(parts[0], numVertices);
//                    if (parts.length > 2)
//                        facesNormals[faceIndex] = getIndex(parts[2], numNormals);
//                    if (parts.length > 1)
//                        facesUV[faceIndex] = getIndex(parts[1], numUV);
//                    faceIndex++;
//
//                    parts = tokens[3].split("/");
//                    facesVerts[faceIndex] = getIndex(parts[0], numVertices);
//                    if (parts.length > 2)
//                        facesNormals[faceIndex] = getIndex(parts[2], numNormals);
//                    if (parts.length > 1)
//                        facesUV[faceIndex] = getIndex(parts[1], numUV);
//                    faceIndex++;
//                    numFaces++;
//                    continue;
//                }
//            }
//
//            float[] verts = new float[(numFaces * 3)
//                                      * (3 + (numNormals > 0 ? 3 : 0) + (numUV > 0 ? 2 : 0))];
//            for (int i = 0, vi = 0; i < numFaces * 3; i++) {
//                int vertexIdx = facesVerts[i] * 3;
//                verts[vi++] = vertices[vertexIdx];
//                verts[vi++] = vertices[vertexIdx + 1];
//                verts[vi++] = vertices[vertexIdx + 2];
//
//                if (numUV > 0) {
//                    int uvIdx = facesUV[i] * 2;
//                    verts[vi++] = uv[uvIdx];
//                    verts[vi++] = 1 - uv[uvIdx + 1];
//                }
//
//                if (numNormals > 0) {
//                    int normalIdx = facesNormals[i] * 3;
//                    verts[vi++] = normals[normalIdx];
//                    verts[vi++] = normals[normalIdx + 1];
//                    verts[vi++] = normals[normalIdx + 2];
//                }
//            }
//            
//          int i = 0;
//          f = new short[faces.size()];
//          for (Short face: faces){
//              f[i++] = face;
//          }
//
//            Mesh model = new Mesh(vertices, f, normals, uv, this);
////            model.setVertices();
//            return model;
//        } catch (Exception ex) {
//            throw new RuntimeException("couldn't load '" + file + "'", ex);
//        } finally {
//            if (in != null)
//                try {
//                    in.close();
//                } catch (Exception ex) {
//
//                }
//        }
//    }
//
//    static int getIndex(String index, int size) {
//        int idx = Integer.parseInt(index);
//        if (idx < 0)
//            return size + idx;
//        else
//            return idx - 1;
//    }
//
//    static List<String> readLines(InputStream in) throws IOException {
//        List<String> lines = new ArrayList<String>();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        String line = null;
//        while ((line = reader.readLine()) != null)
//            lines.add(line);
//        return lines;
//    }
//	
	
    public String getStringFromFile(String filePath) throws Exception
    {
    	AssetManager assetMgr = stuff.getAssets();
    	InputStream in = assetMgr.open(filePath, AssetManager.ACCESS_STREAMING);
    	
        String returnString = convertStreamToString(in);
        in.close();
        return returnString;
    }

    public static String convertStreamToString(InputStream is) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

//    public Mesh loadOBJ(String modelLocation) throws Exception
//    {
//        Log.d("OBJToolkit", "Location searched for model: " + modelLocation);
//
//        ArrayList<Vector3f> allVertices = new ArrayList<Vector3f>();
//        ArrayList<Vector2f> allTextureCoords = new ArrayList<Vector2f>();
//        ArrayList<Vector3f> allNormals = new ArrayList<Vector3f>();
//
//        ArrayList<Face> faces = new ArrayList<Face>();
//
////        Mesh mesh = new Mesh();
//
//        String lines = this.getStringFromFile(modelLocation);
//
//        Log.d("OBJToolkit", "About to read the contents of the model");
//        String[] individualLines = lines.split("\\n");
//
//        int numberOfLines = individualLines.length;
//        for (int i = 0; i < numberOfLines; i++)
//        {
//            String line = individualLines[i];
//            if (line == null)
//                break;
//
//            if (line.startsWith("v "))
//            {
//            	Vector3f vectf = new Vector3f(Float.valueOf(line.split(" ")[1]), Float.valueOf(line.split(" ")[2]), Float.valueOf(line.split(" ")[3]));
//            	
//                allVertices.add(new Vector3f(Float.valueOf(line.split(" ")[1]), Float.valueOf(line.split(" ")[2]), Float.valueOf(line.split(" ")[3])));
//            	allNormals.add(vectf.normal());
//                
//            }
//
//            if (line.startsWith("vt "))
//            {
//                allTextureCoords.add(new Vector2f(Float.valueOf(line.split(" ")[1]),Float.valueOf(line.split(" ")[2])));
//            }
//
////            if (line.startsWith("vn "))
////            {
////                allNormals.add(new Vector3f(Float.valueOf(line.split(" ")[1]), Float.valueOf(line.split(" ")[2]), Float.valueOf(line.split(" ")[3])));
////            }
//
//            if (line.startsWith("f "))
//            {
//                //Log.d("OBJToolkit", line);
//                Face f = new Face();
//                String[] faceVertexArray = line.split(" ");
//                ArrayList<String> s = new ArrayList<String>();
//                for (int index = 1; index < faceVertexArray.length; index++)
//                {
//                    String[] valueArray = faceVertexArray[index].split("/");
//                    s.add(valueArray[0]);
//
//                }
//                if(s.size()==3&&Integer.valueOf(s.get(0)) - 1 < allVertices.size() && Integer.valueOf(s.get(2)) - 1 < allNormals.size() && Integer.valueOf(s.get(1)) - 1 < allTextureCoords.size()){
////                	Log.e(null, "YAY");
////                	for(int i = 0; i < s.size(); i++)
//                if (allTextureCoords.size() > 0)
//                    f.addVertex(new Vertex(allVertices.get(Integer.valueOf(s.get(0)) - 1), allTextureCoords.get(Integer.valueOf(s.get(1)) - 1)));
//                else
//                    f.addVertex(new Vertex(allVertices.get(Integer.valueOf(s.get(0)) - 1), allNormals.get(Integer.valueOf(s.get(2)) - 1), new Vector2f(0, 0)));
//                }
//                faces.add(f);
//            }
//        }
//
//        Log.d("OBJToolkit", "Number of vertices: " + allVertices.size());
//        Log.d("OBJToolkit", "Number of normals: " + allNormals.size());
//        Log.d("OBJToolkit", "Number of texture coords: " + allTextureCoords.size());
//
//        lines = null;
//        allVertices = null;
//        allNormals = null;
//        allTextureCoords = null;
//
//        ArrayList<Vector3f> VBOVertices = new ArrayList<Vector3f>();
//        ArrayList<Vector2f> VBOTextureCoords = new ArrayList<Vector2f>();
//        ArrayList<Vector3f> VBONormals = new ArrayList<Vector3f>();
//        ArrayList<Integer> VBOIndices = new ArrayList<Integer>();
//
//        Log.d("OBJToolkit", "About to reorganize each point of data");
//        int counter = 0;
//        for (Face f : faces)
//        {
//            for (Vertex v : f.vertices)
//            {
//                VBOVertices.add(v.position);
//                VBONormals.add(v.normal);
//                VBOTextureCoords.add(v.textureCoord);
//                VBOIndices.add(counter);
//                counter++;
//            }
//        }
//
//        faces = null;
//        Mesh mesh = new Mesh(vector3fListToFloatArray(VBOVertices),vector2fListToFloatArray(VBOTextureCoords), vector3fListToFloatArray(VBONormals), integerListToShortArray(VBOIndices), this );
////        mesh.createBuffers(vector3fListToFloatArray(VBOVertices), integerListToShortArray(VBOIndices), null, vector2fListToFloatArray(VBOTextureCoords), vector3fListToFloatArray(VBONormals));
//
//        VBOVertices = null;
//        VBONormals = null;
//        VBOTextureCoords = null;
//        VBOIndices = null;
//        return mesh;
//    }

    public static void printFloatArrayList(ArrayList<Float> list)
    {
        String strToPrint = "";
        for (float value : list)
        {
            strToPrint += (value + ", ");
        }
        Log.d("OBJToolkit", strToPrint);
    }

    public static String floatArrayToString(ArrayList<Float> list)
    {
        String strToPrint = "";
        for (float value : list)
        {
            strToPrint += (value + ", ");
        }
        return strToPrint;
    }

    public static String vector3fArrayToString(ArrayList<Vector3f> list)
    {
        String strToPrint = "";
        for (Vector3f v : list)
        {
            strToPrint += v.x + ", ";
            strToPrint += v.y + ", ";
            strToPrint += v.z + ", ";
        }
        return strToPrint;
    }

    public static void printStringArray(String[] list)
    {
        String strToPrint = "";
        for (String s : list)
        {
            strToPrint += s + ",";
        }
        Log.d("OBJToolkit", strToPrint);
    }

    public static void printIntegerArrayList(ArrayList<Integer> list)
    {
        String strToPrint = "";
        for (float value : list)
        {
            strToPrint += (value + ", ");
        }
        Log.d("OBJToolkit", strToPrint);
    }

    public static float[] floatListToFloatArray(ArrayList<Float> list)
    {
        Log.d("OBJToolkit", "Converting ArrayList Float");
        float[] returnArray = new float[list.size()];
        int counter = 0;
        for (Float i : list)
        {
            returnArray[counter] = i.floatValue();
            counter++;
        }
        return returnArray;
    }

    public static short[] integerListToShortArray(ArrayList<Integer> list)
    {
        Log.d("OBJToolkit", "Converting ArrayList Integer");
        short[] returnArray = new short[list.size()];
        int counter = 0;
        for (int i : list)
        {
            returnArray[counter] = (short)i;
            counter++;
        }
        return returnArray;
    }

    public static float[] vector3fListToFloatArray(ArrayList<Vector3f> list)
    {
        Log.d("OBJToolkit", "Converting ArrayList Vector3f");
        float[] returnArray = new float[list.size() * 3];
        int counter = 0;
        for (Vector3f v : list)
        {
            returnArray[counter] = v.x;
            counter++;
            returnArray[counter] = v.y;
            counter++;
            returnArray[counter] = v.z;
            counter++;
        }

        return returnArray;
    }

    public static float[] vector2fListToFloatArray(ArrayList<Vector2f> list)
    {
        Log.d("OBJToolkit", "Converting ArrayList Vector2f");
        float[] returnArray = new float[list.size() * 2];
        int counter = 0;
        for (Vector2f v : list)
        {
            returnArray[counter] = v.x;
            counter++;
            returnArray[counter] = v.y;
            counter++;
        }

        return returnArray;
    }
	
	
}
