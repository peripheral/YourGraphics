package vehicle.car3D;

import com.jogamp.graph.geom.Vertex;

public class MyVertex implements Vertex {
	private float[] coords = null;
	private float[] color = null;
	private float[] texCoord = null;
	private int id;
	private boolean onCurve = false;

	public MyVertex() {
	}

	public MyVertex(int offset, double[] arr) {
		coords = new float[] { (float) arr[offset], (float) arr[offset + 1],
				(float) arr[offset + 2] };
	}

	public MyVertex(int offset, double[] arr, int texOffset, double[] tex) {
		coords = new float[] { (float) arr[offset], (float) arr[offset + 1],
				(float) arr[offset + 2] };
		texCoord = new float[] { (float) tex[texOffset],
				(float) tex[texOffset + 1] };
	}

	@Override
	public float[] getCoord() {
		return coords;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public float[] getTexCoord() {
		return texCoord;
	}

	@Override
	public float getX() {
		if (coords == null) {
			throw new RuntimeException("No coords defined");
		}
		return coords[0];
	}

	@Override
	public float getY() {
		if (coords == null) {
			throw new RuntimeException("No coords defined");
		}
		return coords[1];
	}

	@Override
	public float getZ() {
		if (coords == null) {
			throw new RuntimeException("No coords defined");
		}
		return coords[2];
	}

	@Override
	public boolean isOnCurve() {
		return onCurve;
	}

	@Override
	public void setCoord(float x, float y, float z) {
		coords = new float[] { x, y, z };
	}

	@Override
	public void setCoord(float[] coords, int arg1, int arg2) {
		this.coords = coords;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setOnCurve(boolean arg0) {
		onCurve = arg0;
	}

	@Override
	public void setTexCoord(float s, float t) {
		texCoord = new float[] { s, t };
	}

	@Override
	public void setTexCoord(float[] arg0, int arg1, int arg2) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setX(float x) {
		coords[0] = x;
	}

	@Override
	public void setY(float y) {
		coords[1] = y;
	}

	@Override
	public void setZ(float z) {
		coords[2] = z;
	}

	@Override
	public Vertex clone() {
		throw new RuntimeException("Operation is not supported");

	}

	public Vertex substract(Vertex v) {
		double[] coords = new double[] { this.coords[0] - v.getX(),
				this.coords[1] - v.getY(), this.coords[2] - v.getZ() };
		return new MyVertex(0, coords);
	}
}
