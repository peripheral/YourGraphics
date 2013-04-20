package vehicle.car3D;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;


import com.jogamp.opengl.util.FPSAnimator;

/**
 * Main class for application, manages settings for environment and windows
 * settings. Application controlled with mouse, arrow buttons and keys A,S,D,W
 * 
 * @author Artur Vitt
 * 
 */
public class Main extends Frame implements GLEventListener, KeyListener,
		MouseWheelListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default dimensions.
	 */
	private final static Dimension DIMENSIONS = new Dimension(600, 400);

	/**
	 * Canvas to draw on.
	 */
	private GLCanvas canvas;
	/**
	 * Objects to draw.
	 */
	private List<Drawable> objs = new LinkedList<Drawable>();
	/**
	 * Horizontal angular displacement of camera view.
	 */
	private int cameraAngleHorizont = 120;
	/**
	 * Vertical angular displacement of camera view.
	 */
	private int cameraAngleVertical = 0;
	/**
	 * Focus positions of the camera.
	 */
	private float focusX = 0, focusY = 0, focusZ = 0;
	/** Camera position. */
	private float eyePosX = 9, eyePosY = -9, eyePosZ = 0;
	/** Movement in the room. */
	private float step = 1;

	/** Constructor setting up environment and objects */
	public Main(String title) {
		super("3DCar");
		GLCapabilities capabilities = new GLCapabilities(null);
		canvas = new GLCanvas(capabilities);

		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseWheelListener(this);
		canvas.addMouseMotionListener(this);
		add(canvas, BorderLayout.CENTER);
		initObjects();
		FPSAnimator animator = new FPSAnimator(canvas, 15); // Animator reloads
		// display 15
		// times/sec normal
		// 60 fps
		animator.add(canvas);
		animator.start();
	}

	public static void main(String[] args) {
		Frame mainFrame = new Main("3DCar");
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setSize(DIMENSIONS);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = (GL2) drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glDepthMask(true);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL); // enabels polygones fill
		// Only show edges, default is
		gl.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL); // filles both

		// side
		gl.glPolygonOffset(1.0f, 1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		final GLU glu = new GLU();
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		setCamera(gl, glu, 3);
		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		// gl.glTranslatef(12000.0f,0.0f,0.0f); //X
		gl.glVertex3f(-12000.0f, 0.0f, 0.0f);
		gl.glVertex3f(12000.0f, 0.0f, 0.0f);

		gl.glColor3f(0.0f, 1.0f, 0.0f);
		// gl.glTranslatef(0.0f,-12000.0f,0.0f); //Y
		gl.glVertex3f(0.0f, -12000.0f, 0.0f);
		gl.glVertex3f(0.0f, 12000.0f, 0.0f);

		gl.glColor3f(0.0f, 0.0f, 1.0f);
		// gl.glTranslatef(0.0f,0.0f,12000.0f); //Z
		gl.glVertex3f(0.0f, 0.0f, -12000.0f);
		gl.glVertex3f(0.0f, 0.0f, 12000.0f);
		gl.glEnd();
		for (Drawable d : objs) {
			d.draw(gl);
		}
		// setUpLights(gl);
	}

	private void initObjects() {
		objs.add(new Car3D());
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Calculates new viewing vector , eye to focus.
	 * 
	 * @param r
	 *            - radius
	 * @return array of floats x,y,z
	 */
	private float[] newViewingVector(double r) {
		float[] points = new float[3];
		points[0] = (float) (r * Math.cos(Math.toRadians(cameraAngleHorizont)) * Math
				.cos(Math.toRadians(cameraAngleVertical)));
		points[1] = (float) (r * Math.sin(Math.toRadians(cameraAngleHorizont)) * Math
				.cos(Math.toRadians(cameraAngleVertical)));
		points[2] = (float) (r * Math.sin(Math.toRadians(cameraAngleVertical)));
		return points;
	}

	/**
	 * Defines camera view.
	 * 
	 * @param gl
	 * @param glu
	 * @param distance
	 *            - viewing distance
	 */
	private void setCamera(GL2 gl, GLU glu, float distance) {
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();

		// Perspective.
		float widthHeightRatio = (float) getWidth() / (float) getHeight();
		glu.gluPerspective(45, widthHeightRatio, 0.1, 1000);
		float[] eyePoint = newViewingVector(9);
		focusX = eyePosX + eyePoint[0];
		focusY = eyePosY + eyePoint[1];
		focusZ = eyePosZ + eyePoint[2];
		glu.gluLookAt(eyePosX, eyePosY, eyePosZ, focusX, focusY, focusZ, 0.0f,
				0.0f, 1.0f);
		// Change back to model view matrix.
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = (GL2) drawable.getGL();
		gl.glClearColor(1f, 1f, 1f, 0.1f);
		gl.glPointSize(4.0f);

		// Enable z- (depth) buffer for hidden surface removal.
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		// Enable smooth shading.
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

	}

	/**
	 * Not in use, expected for setting up lights
	 * 
	 * @param gl
	 */
	private void setUpLights(GL2 gl) {

		// gl.glEnable(GL2.GL_LIGHT0);
		// est Ambient - vesde suschii svet t. e. netu u nego napravlenija
		float[] light1Ambient = { 0.1f, 0.1f, 0.1f, 1f };
		// Est diffuse - radial oragajet vo vse storoni kakujuto porciju
		float[] light1Diffuse = { 0.2f, 0.2f, 0.2f, 1f };

		// Est spekular
		float[] light1Specular = { 0.8f, 0.8f, 0.8f, 1.0f };
		float[] light1Position = { 100f, 0f, 60f, 1f };
		float[] spotDirection = { -1.0f, -1.0f, 0.0f };
		float[] lmodelAmbient = { 0.0f, 0.0f, 0.f, 1.0f };
		// gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT,light1Ambient,0);
		gl.glLightModelfv(GL2ES1.GL_LIGHT_MODEL_AMBIENT, light1Ambient, 0);
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
		gl.glLightModeli(GL2ES1.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_FALSE);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_SPECULAR,
				light1Specular, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_DIFFUSE,
				light1Diffuse, 0);
		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_POSITION,
				light1Position, 0);

		gl.glLightf(GLLightingFunc.GL_LIGHT1,
				GLLightingFunc.GL_CONSTANT_ATTENUATION, 0.3f);
		gl.glLightf(GLLightingFunc.GL_LIGHT1,
				GLLightingFunc.GL_LINEAR_ATTENUATION, 0.0f);
		gl.glLightf(GLLightingFunc.GL_LIGHT1,
				GLLightingFunc.GL_QUADRATIC_ATTENUATION, 0.0f);

		/*
		 * glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 45.0); glLightfv(GL_LIGHT1,
		 * GL_SPOT_DIRECTION, spot_direction); glLightf(GL_LIGHT1,
		 * GL_SPOT_EXPONENT, 2.0);
		 */
		// gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl.glEnable(GLLightingFunc.GL_LIGHT1);
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

		// Set material properties.
		float[] rgba = { 1f, 1f, 1f, 1f };
		float[] rgbas = { 0.1f, 0.1f, 0.1f, 1f };

		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE,
				rgbas, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.2f);
		// gl.glMaterialf(GL.GL_FRONT, GL2.GL_DIFFUSE, 1);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = (GL2) drawable.getGL();
		/*
		 * gl.glClearColor(204, 255, 255,0); gl.glPointSize(4.0f);
		 */
		gl.glViewport(0, 0, width, height);

		canvas.setPreferredSize(this.getSize());
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		String s = e.getKeyChar() + "";
		if (s.equalsIgnoreCase("A")) {
			if (cameraAngleHorizont + 2 > 360) {
				cameraAngleHorizont = 0;
			}
			cameraAngleHorizont += 2;
		} else if (s.equalsIgnoreCase("D")) {
			if (cameraAngleHorizont - 2 < 0) {
				cameraAngleHorizont = 360;
			}
			cameraAngleHorizont -= 2;
		} else if (s.equalsIgnoreCase("W")) {
			step(1);
		} else if (s.equalsIgnoreCase("S")) {
			step(-1);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			Car3D car = (Car3D) objs.get(0);
			car.turnWheels(35);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Car3D car = (Car3D) objs.get(0);
			car.turnWheels(-35);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			Car3D car = (Car3D) objs.get(0);
			car.move(1);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			Car3D car = (Car3D) objs.get(0);
			car.move(-1);
		}

	}

	private void step(int i) {
		float[] point1 = { focusX, focusY };
		float[] point2 = { eyePosX, eyePosY };
		float dist = VectorOperations.distance(point1, point2);
		if (dist == 0) {
			dist = 1;
		}
		float factor = step / dist;
		float[] directionVector = { (focusX - eyePosX) * factor,
				(focusY - eyePosY) * factor };
		if (i > 0) {
			eyePosX += directionVector[0];
			eyePosY += directionVector[1];
			focusX += directionVector[0];
			focusY += directionVector[1];
		} else {
			eyePosX -= directionVector[0];
			eyePosY -= directionVector[1];
			focusX -= directionVector[0];
			focusY -= directionVector[1];
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		Car3D car = null;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			car = (Car3D) objs.get(0);
			car.turnWheels(0);
			break;
		case KeyEvent.VK_RIGHT:

			car = (Car3D) objs.get(0);
			car.turnWheels(0);
			break;
		case KeyEvent.VK_UP:
			System.out.println("Released up");
			car = (Car3D) objs.get(0);
			car.move(0);

			break;
		case KeyEvent.VK_DOWN:
			System.out.println("Released down");
			car = (Car3D) objs.get(0);
			car.move(0);
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent m) {
		eyePosZ += m.getWheelRotation();

	}

	private int previousX = 0, dirX = 0;
	private int previousY = 0, dirY = 0;

	@Override
	public void mouseDragged(MouseEvent arg) {
		if (Math.signum(previousX - arg.getX()) != 0) {
			dirX = (int) Math.signum(previousX - arg.getX());
		}
		if (Math.signum(previousY - arg.getY()) != 0) {
			dirY = (int) Math.signum(previousY - arg.getY());
		}
		cameraAngleHorizont += 1 * dirX;
		cameraAngleVertical += 1 * dirY;
		if (cameraAngleVertical < -80) {
			cameraAngleVertical = -80;
		}
		if (cameraAngleVertical > 80) {
			cameraAngleVertical = 80;
		}

		previousX = arg.getX();
		previousY = arg.getY();

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
