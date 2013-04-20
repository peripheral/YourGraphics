package vehicle.car3D;

import java.io.File;

import javax.media.opengl.GL;

import com.jogamp.opengl.util.texture.*;

/**
 * 
 * @author Artur Vitt
 * 
 */
public class TextureHandler {
	private String texFnm;
	private Texture texture;

	/**
	 * Constructor.
	 * 
	 * @param fnm
	 *            - filename , path to the file
	 * @param mipmap
	 *            - true if it is a mipmap
	 * @param gl
	 */
	public TextureHandler(String fnm, boolean mipmap, GL gl) {
		loadTexture(fnm, mipmap, gl);
	}

	private void loadTexture(String fnm, boolean isMipmap, GL gl) {
		try {
			texFnm = fnm;
			texture = TextureIO.newTexture(new File(texFnm), isMipmap);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		} catch (Exception e) {
			System.out.println("Error loading texture " + texFnm);
			e.printStackTrace();
		}
	} // end of loadTexture()

	public void setTexture(Texture t) {
		texture = t;
	}

	public Texture getTexture() {
		return texture;
	}

}