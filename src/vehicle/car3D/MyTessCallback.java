package vehicle.car3D;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

import com.jogamp.graph.geom.Vertex;

/**
 * Callback adapter.
 * 
 * @author Macro_El
 * 
 */
public class MyTessCallback extends GLUtessellatorCallbackAdapter {
	/**
	 * Context for GL.
	 */
	private GL2 gl;
	private int type;
	private List<Float> vertexes = new LinkedList<Float>();
	private List<Vertex> vertexV = new LinkedList<Vertex>();
	private boolean hasTexture = false;

	public MyTessCallback(GL2 gl) {
		this.gl = gl;
	}

	@Override
	public void begin(int type) {
		this.type = type;
		if (type == GL.GL_TRIANGLES) {
			// System.out.println("Triangles");
		}
		if (type == GL.GL_TRIANGLE_FAN)
			System.out.println("Triangle fan");
		if (type == GL.GL_TRIANGLE_STRIP)
			System.out.println("Triangle strip");
		gl.glBegin(type);
	}

	@Override
	public void end() {
		// System.out.println("Drawing element"+vertexV.size());
		// drawElements();
		gl.glEnd();
	}

	public void setHasTexture(boolean b) {
		hasTexture = b;
	}

	private void drawElements() {
		gl.glBegin(type);
		Iterator<Vertex> it = vertexV.iterator();

		Vertex v;

		while (it.hasNext()) {
			v = it.next();
			float[] texCoords = v.getTexCoord();
			// gl.glNormal3f(normal[0], normal[1],normal[2]);
			if (texCoords != null) {
				gl.glTexCoord2f(texCoords[0], texCoords[1]);
			}
			gl.glVertex3f(v.getX(), v.getY(), v.getZ());

		}

		gl.glEnd();
		vertexes = new LinkedList<Float>();
		vertexV = new LinkedList<Vertex>();
	}

	@Override
	public void vertex(Object o) {
		Vertex v = (MyVertex) o;
		vertexes.add(v.getX());
		vertexes.add(v.getY());
		vertexes.add(v.getZ());
		gl.glVertex3f(v.getX(), v.getY(), v.getZ());
		vertexV.add(v);
	}

	@Override
	public void beginData(int type, Object polygonData) {

	}

	@Override
	public void combine(double[] coords, Object[] data, float[] weight,
			Object[] outData) {
		Vertex newVertex = new MyVertex();
		float[] coordsf = new float[] { new Float(coords[0]),
				new Float(coords[1]), new Float(coords[2]) };
		newVertex.setCoord(coordsf, 0, 0);
		Object[] ret = new Object[] { newVertex.getCoord()[0],
				newVertex.getCoord()[1], newVertex.getCoord()[2] };
		gl.glVertex3d(coords[0], coords[1], coords[2]);
		System.out.println("Combine called" + outData.toString());
		outData = ret;
	}

	private float[] getNormal(float[] vector1, float[] vector2) {

		return new float[] { vector1[1] * vector2[2] - vector1[2] * vector2[1],
				vector1[2] * vector2[0] - vector1[0] * vector2[2],
				vector1[0] * vector2[1] - vector1[1] * vector2[0] };
	}

	@Override
	public void combineData(double[] arg0, Object[] arg1, float[] arg2,
			Object[] arg3, Object arg4) {
		System.out.println("Combine data, called");

	}

	@Override
	public void edgeFlag(boolean arg0) {
		gl.glEdgeFlag(arg0);
	}

	@Override
	public void edgeFlagData(boolean arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endData(Object arg0) {
		System.out.println("End data");

	}

	@Override
	public void error(int arg0) {

		switch (arg0) {
		case GLU.GLU_TESS_MISSING_BEGIN_CONTOUR:
			System.out.println("Missing begin contour");
			break;
		case GLU.GLU_TESS_MISSING_BEGIN_POLYGON:
			System.out.println("Missing begin polygon");
			break;
		case GLU.GLU_TESS_MISSING_END_CONTOUR:
			System.out.println("Missing  end countrour");
			break;
		case GLU.GLU_TESS_MISSING_END_POLYGON:
			System.out.println("Missing  end polygon");
			break;
		case GLU.GLU_TESS_COORD_TOO_LARGE:
			System.out.println("Tess coords too large");
			break;
		case GLU.GLU_TESS_ERROR_DATA:
			System.out.println("Error Data");
			break;
		case GLU.GLU_INVALID_VALUE:
			System.out.println("Error Data");
			break;
		case GLU.GLU_OUT_OF_MEMORY:
			System.out.println("GLu out of memory");
			break;
		default:
			System.out.println("Unrecognized error: " + arg0);
			break;
		}

	}

	@Override
	public void errorData(int arg0, Object arg1) {
		System.out.println("Error" + arg0);
	}

	@Override
	public void vertexData(Object arg0, Object arg1) {

	}

}
