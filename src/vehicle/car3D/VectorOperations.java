package vehicle.car3D;

/**
 * Class provides utilities for vector operations.
 * 
 * @author Artur Vitt
 * 
 */
public class VectorOperations {

	/**
	 * Performs rotation of a vector.
	 * 
	 * @param v
	 *            - vector to be rotated (x,y,z)
	 * @param angle
	 *            - angle in degrees
	 * @return - new vector (x,y)
	 */
	public static double[] rotateVector(double[] v, double angle) {
		double[] temp = new double[2];
		temp[0] = v[0] * Math.cos(Math.toRadians(angle)) - v[1]
				* Math.sin(Math.toRadians(angle));
		temp[1] = v[0] * Math.sin(Math.toRadians(angle)) + v[1]
				* Math.cos(Math.toRadians(angle));
		return temp;
	}

	/**
	 * Calculates distance between points.
	 * 
	 * @param point1
	 *            - frist point
	 * @param point2
	 *            - second point (x,y,z)
	 * @return - distance between points
	 */
	public static float distance(float[] point1, float[] point2) {
		float[] vector = new float[3];
		for (int i = 0; i < point1.length; i++) {
			vector[i] = point1[i] - point2[i];
		}
		return amplitude(vector);
	}

	/**
	 * Calculates distance between vectors.
	 * 
	 * @param vector
	 *            - vector to be used in calculation
	 * @return - amplitude of the vector
	 */
	public static float amplitude(float[] vector) {
		float sum = 0;
		for (float f : vector) {
			sum = (float) (sum + Math.pow(f, 2));
		}

		float amp = (float) Math.sqrt(sum);
		return amp;
	}

	/**
	 * Calculates distance between vectors.
	 * 
	 * @param vector
	 *            - vector to be used in calculation
	 * @return - amplitude of the vector
	 */
	public static double amplitude(double[] vector) {
		double sum = 0;
		for (double f : vector) {
			sum = (sum + Math.pow(f, 2));
		}

		double amp = Math.sqrt(sum);
		return amp;
	}
}
