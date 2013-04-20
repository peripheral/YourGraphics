package vehicle.car3D;

import javax.media.opengl.GL2;

/**
 * Interface that would allow draw object
 * 
 * @author Artur Vitt
 * 
 */
public interface Drawable {

	/**
	 * Executes drawing of the object
	 * 
	 */
	void draw(GL2 gl);
}
