package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Mesh {
//	  final String vertexShader =
//			    "uniform mat4 u_MVPMatrix;      \n"     // A constant representing the combined model/view/projection matrix.
//			  + "uniform mat4 u_MVMatrix;       \n"     // A constant representing the combined model/view matrix.
//			  + "uniform vec3 u_LightPos;       \n"     // The position of the light in eye space.
//			 
//			  + "attribute vec4 a_Position;     \n"     // Per-vertex position information we will pass in.
////			  + "attribute vec4 a_Color;        \n"     // Per-vertex color information we will pass in.
//			  + "attribute vec3 a_Normal;       \n"     // Per-vertex normal information we will pass in.
//			 
//			  + "varying vec4 v_Color2;          \n"     // This will be passed into the fragment shader.
//			 
//			  + "void main()                    \n"     // The entry point for our vertex shader.
//			  + "{                              \n"
//			// Transform the vertex into eye space.
//			  + "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);              \n"
//			// Transform the normal's orientation into eye space.
//			  + "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n"
//			// Will be used for attenuation.
//			  + "   float distance = length(u_LightPos - modelViewVertex);             \n"
//			// Get a lighting direction vector from the light to the vertex.
//			  + "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n"
//			// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
//			// pointing in the same direction then it will get max illumination.
//			  + "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);       \n"
//			// Attenuate the light based on distance.
//			  + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));  \n"
//			// Multiply the color by the illumination level. It will be interpolated across the triangle.
//			  + "   v_Color2 = a_Color * diffuse;                                       \n"
//			// gl_Position is a special variable used to store the final position.
//			// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
//			  + "   gl_Position = u_MVPMatrix * a_Position;                            \n"
//			  + "}                                                                     \n";
    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
        "uniform mat4 u_MVMatrix;"+
        "uniform vec4 u_Color;" +
        
        "attribute vec4 a_Position;" +  // a_Position == a_Position
        "attribute vec3 a_Normal;"+
        "attribute vec2 a_TexCoordinate;" +
        
        "varying vec3 v_Position;" + 
        "varying vec4 v_Color2;" + //v_Color2 == v_Color
        "varying vec3 v_Normal;" +
        "varying vec2 v_TexCoordinate;"+
        "void main() {" +
        
        "v_Position = vec3(u_MVMatrix * a_Position);"+
        "v_Color2 = u_Color;" +
        "v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));" +
//        "v_Color2 = 0.5 * a_Position;" +
        // the matrix must be included as a modifier of gl_Position
        "gl_Position = uMVPMatrix * a_Position ;" +
        "v_TexCoordinate = a_TexCoordinate;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
    
        "uniform vec3 u_LightPos;" +
        "uniform sampler2D u_Texture;" +
        
        "varying vec3 v_Position;"+
        "varying vec4 v_Color2;" +
        "varying vec3 v_Normal;"+
        "varying vec2 v_TexCoordinate;"+
        
        "void main() {" +
        
        "float distance = length(u_LightPos - v_Position);"+
        "vec3 lightVector = normalize(u_LightPos - v_Position);" +
        "float diffuse = max(dot(v_Normal, lightVector), 0.1);"+
        
        "diffuse = diffuse * (1.0/(1.0+(0.1*distance)));"+ // usually is distance ^ 2
        "diffuse = diffuse + 0.3;"+

		"gl_FragColor = diffuse * (vec4((.5, .5, .5, 1)));"+
//        "gl_FragColor = (v_Color2 * diffuse* texture2D(u_Texture, v_TexCoordinate));" + // pull out v_Color2 because we don't want them to mix
        "}";
//	
//    private final String vertexShaderCode =
//            // This matrix member variable provides a hook to manipulate
//            // the coordinates of the objects that use this vertex shader
//            "uniform mat4 uMVPMatrix;" +
//            "uniform vec4 u_Color;" +
//           
//            "attribute vec4 a_Position;" +
//            "varying vec4 v_Color2;" +
//            "void main() {" +
//            "v_Color2 = 0.5 *a_Position;" +
//            // the matrix must be included as a modifier of gl_Position
//            "  gl_Position = a_Position * uMVPMatrix;" +
//            "}";
//
//        private final String fragmentShaderCode =
//            "precision mediump float;" +
//            "varying vec4 v_Color2;" +
//            "void main() {" +
//            "  gl_FragColor = v_Color2;" +
//            "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    
    final int mProgram;
    private int mPositionHandle;
    private int mNormalsHandle;
    
    private int mColorHandle;
    
//    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private final int mTextureCoordinateDataSize = 2;
    private int mTextureDataHandle;
    
    private float[] lightPos = {1,2,3}; // can change these to move the light

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[]; //  = { -0.5f,  0.5f, 0.0f,   // top left
//                                    -0.5f, -0.5f, 0.0f,   // bottom left
//                                     0.5f, -0.5f, 0.0f,   // bottom right
//                                     0.5f,  0.5f, 0.0f }; // top right

//    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    private final short drawOrder[];
    private final int texVertexStride = mTextureCoordinateDataSize * 4;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
//    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    float color[] = { 1.0f, 0.709803922f, 0.898039216f, 1.0f };
//    float color[] = {0f, 0f, 0f, 0f};
//    private final  List<Short> indices;
//    private final List<Vertex> vertices;
    
    
//    private FloatBuffer vertexBuffer;
//    private ShortBuffer drawListBuffer;
    private FloatBuffer normalsBuffer;
    private FloatBuffer texCoordsBuffer;
    
    public Mesh(List<Vertex> finalVerts, List<Short> indices, ResourceLoader rl) {
        // initialize vertex byte buffer for shape coordinates
//    	this.indices = indices;
//    	this.vertices = vertices;
      int i = 0;
      float[] v = new float[finalVerts.size() * 3];
      for(Vertex vertex: finalVerts){
    	  v[i++] = vertex.position.x;
          v[i++] = vertex.position.y;
          v[i++] = vertex.position.z;
      }
      squareCoords = v;
      i = 0;
      float[] n = new float[finalVerts.size() * 3];
      for(Vertex vertex: finalVerts){
    	  n[i++] = vertex.normal.x;
          n[i++] = vertex.normal.y;
          n[i++] = vertex.normal.z;
      }
      
      i = 0;
      //draw list
      short[] f = new short[indices.size()];
      for (Short idx: indices){
          f[i++] = idx;
      }
      
      squareCoords = v;
  	  drawOrder = f;
  	  i = 0;
  	  float[] t = new float[finalVerts.size()*2];
      for(Vertex vertex: finalVerts){
    	  t[i++] = vertex.textureCoord.x;
          t[i++] = vertex.textureCoord.y;
      }
      
//      
//      i = 0;
//      float[] vn = new float[vertexNormals.size()];
//      for(Float n : vertexNormals){
//          vn[i++] = n;
//      }
    	
//    	ResourceLoader.

    	//FOLLOWING CODE MUST MOVE SOMEWHERE ELSE.
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                v.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(v);
        vertexBuffer.position(0);
        
        ByteBuffer bn = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                        n.length * 4);
                bn.order(ByteOrder.nativeOrder());
                normalsBuffer = bn.asFloatBuffer();
                normalsBuffer.put(n);
                normalsBuffer.position(0);
        
                
        ByteBuffer bt = ByteBuffer.allocateDirect(
                        // (# of coordinate values * 4 bytes per float)
                                t.length * 4);
                        bt.order(ByteOrder.nativeOrder());
                        texCoordsBuffer = bt.asFloatBuffer();
                        texCoordsBuffer.put(t);
                        texCoordsBuffer.position(0);        

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                f.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(f);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        MyGLRenderer.checkGlError("Use Program");
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);
        MyGLRenderer.checkGlError("Use Program");
        mProgram = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader, 
				new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});
    	mTextureDataHandle = rl.loadTexture("alduin.etc");
//    	Log.e("MESH", mDataHandle + " stuff");
    	MyGLRenderer.checkGlError("Use Program");
//        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
//        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
//        MyGLRenderer.checkGlError("Use Program");
//        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
//        MyGLRenderer.checkGlError("Use Program");
//        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
//        MyGLRenderer.checkGlError("Use Program");
    }
    
    public void setLighting(float[] mvMatrix){
    	int mvMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
    	MyGLRenderer.checkGlError("get model view matrix");
    	int lightLocHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
    	MyGLRenderer.checkGlError("get light location");
    	
    	GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);
    	MyGLRenderer.checkGlError("set model view matrix");
    	
    	GLES20.glUniform3fv(lightLocHandle, 1, lightPos, 0);
    	MyGLRenderer.checkGlError("light location set");
    }
    
//    public void initialize(){
//    	int i = 0; 
//    	int j = 0; 
//    	int k = 0;
//    	
////    	for(j = 0; j < vertices.size(); j = j+3){
////    		vertices;
////    		int i1 = j;
////    		int i2 = j + 1;
////    		int i3 = j + 2;
////    		vertices.get(i1).position - vertices.get(location)
////    	}
//    	
//    	// Load texture function
//    	
//    	
////      mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
////      GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
////      GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
////      GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
//    }

    public void draw(float[] mvpMatrix, float[] mvMatrix) {
////        // Add program to OpenGL environment
    	Log.v("FRAK", "frak" + mProgram);
    	GLES20.glUseProgram(mProgram);
//        GLES20.glUseProgram(mProgram);
////        
//        MyGLRenderer.checkGlError("Use Program");
////
////        // get handle to vertex shader's a_Position member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        MyGLRenderer.checkGlError("get Position attribute");
        mNormalsHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");

//        MyGLRenderer.checkGlError("glUniformMatrix4fv");
////        
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
        MyGLRenderer.checkGlError("get Color uniform");
////        
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");MyGLRenderer.checkGlError("glGetUniformLocation");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");MyGLRenderer.checkGlError("glGetUniformLocation");
////        
////        
////
////        
        GLES20.glEnableVertexAttribArray(mPositionHandle);MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glEnableVertexAttribArray(mNormalsHandle);MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);MyGLRenderer.checkGlError("glGetUniformLocation");
         
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);MyGLRenderer.checkGlError("glGetUniformLocation");
        
     
//////        
        GLES20.glVertexAttribPointer(mNormalsHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, normalsBuffer);MyGLRenderer.checkGlError("glGetUniformLocation");
        
//////                                    
                                     
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize,
                GLES20.GL_FLOAT, false,
                texVertexStride, texCoordsBuffer);MyGLRenderer.checkGlError("glGetUniformLocation");
        
//////        
////        
//        Log.v("MESH", "TEX COORD HANDLE " + mTextureCoordinateHandle);
        
//        
        
////
//        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);MyGLRenderer.checkGlError("glGetUniformLocation");
////        // Enable a handle to the triangle vertices
//        GLES20.glEnableVertexAttribArray(mPositionHandle);MyGLRenderer.checkGlError("glGetUniformLocation");
////
////        // Prepare the triangle coordinate data
////
////
////        
//        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
//                                     GLES20.GL_FLOAT, false,
//                                     vertexStride, vertexBuffer);MyGLRenderer.checkGlError("glGetUniformLocation");
//        
//        GLES20.glVertexAttribPointer(mNormalsHandle, COORDS_PER_VERTEX,
//                GLES20.GL_FLOAT, false,
//                vertexStride, normalsBuffer);MyGLRenderer.checkGlError("glGetUniformLocation");
//        
//        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize,
//                GLES20.GL_FLOAT, false,
//                texVertexStride, texCoordsBuffer);MyGLRenderer.checkGlError("glGetUniformLocation");
//        
//        
//        // get handle to fragment shader's vColor member
//        mColorHandle = GLES20.glGetUniformLocation(mProgram, "v_Color2");
        MyGLRenderer.checkGlError("glGetUniformLocation");
//        // Set color for drawing the triangle
        
        
        
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        
        
//        MyGLRenderer.checkGlError("glGetUniformLocation");
//
//        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");
//
//        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
//
        setLighting(mvMatrix);
//        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
//        
//        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
//
//        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
        GLES20.glDisableVertexAttribArray(mNormalsHandle);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
//         USE ETC1 to convert files to things
    }
    

}
