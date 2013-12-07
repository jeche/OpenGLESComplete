uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform vec3 u_LightPos;
uniform vec4 u_Color;
attribute vec4 vPosition;

attribute vec4 a_Position;

varying vec4 v_Color;

void main(){
	v_Color = a_Position;
	gl_Position =  a_Position * uMVPMatrix;
	
}