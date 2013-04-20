package vehicle.car3D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.swing.Timer;

import com.jogamp.opengl.util.texture.Texture;



/**
 * Represents a 3D car with supplied mechanics for movement in 3d space.
 * 
 * @author Artur Vitt
 */
public class Car3D implements Drawable, ActionListener {
	/** Rotation center used during rotation to the right of vehicle. */
	private double[] rightRotCenter = { 0, 4 };
	/** Rotation center used during rotation to the left of vehicle. */
	private double[] leftRotCenter = { 4, 4 };
	/** Right front corner of vehicle. */
	private double[] renderingPos = { 0, 0 };
	/** Wheel spin shared among all wings. */
	private double wheelSpin = 0;
	/** Spin per car displacement through move */
	private double spinPerMove = 1;
	/** Speed factor. multiplies carVectorDirection. */
	private double speedFactor = 0.2;
	/** Turn factor, multiplies to car angular turn, calculated from wheelTurn */
	private double turnFactor = 20;
	/** actual Vehicle position x, right front corner. */
	private double startX = 0;
	/** actual Vehicle position y, right front corner. */
	private double startY = 0;
	/** NA. */
	private double frontWheelDir = 0;
	/** Direction of a car. */
	private double carDir = 0;
	// private double velocity = 1;
	/** Vector direction works as a step. */
	private double[] carVectorDirection = { Math.sin(Math.toRadians(carDir)),
			Math.cos(Math.toRadians(carDir)) };
	/** Used during movement of vehicle. */
	private int vehicleMovementDir = 0;
	/** Timer used steering, calls wheel turn method. */
	private Timer steeringTimer = null;
	/** Timer used for moving, calls move method. */
	private Timer movementTimer = null;

	/**
	 * Constructor initiates timers used during car turn and movement.
	 */
	public Car3D() {
		steeringTimer = new Timer(200, this);
		movementTimer = new Timer(100, null);

		spinPerMove = speedFactor / (2 * Math.PI / 360);
		movementTimer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				move(vehicleMovementDir);
				if (vehicleMovementDir == 0) {
					movementTimer.stop();
				}
			}
		});
	}

	@Override
	public void draw(GL2 gl) {
		drawGround(gl);
		gl.glPushMatrix();
		gl.glRotated(carDir, 0, 0, 1);
		correctDisplacement(gl);
		double[] temp = VectorOperations.rotateVector(new double[] { startX,
				startY }, -carDir);
		gl.glTranslated(temp[0], temp[1], 0);
		drawWheels(gl);
		drawBody(gl);
		gl.glPopMatrix();
	}

	/**
	 * Used in car turning , places car in to pre end position *
	 * 
	 * @param gl
	 */
	private void correctDisplacement(GL2 gl) {
		gl.glTranslated(renderingPos[0], renderingPos[1], 0);
	}

	/**
	 * Draws the body of the car purpose to distribute draw calls to other parts
	 * of car body
	 * 
	 * @param gl
	 */
	private void drawBody(GL2 gl) {
		drawTop(gl);
	}

	/**
	 * Draws top layer of the car
	 * 
	 * @param gl
	 */
	private void drawTop(GL2 gl) {
		List<double[]> list = new LinkedList<double[]>();
		setupSufaceVertices(list);

		GLUtessellator tess = GLU.gluNewTess();
		GLUtessellatorCallback tCallback = new MyTessCallback(gl);
		GLU.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, tCallback);
		GLU.gluTessCallback(tess, GLU.GLU_TESS_END, tCallback);
		GLU.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, tCallback);
		GLU.gluTessCallback(tess, GLU.GLU_TESS_COMBINE, tCallback);
		GLU.gluTessCallback(tess, GLU.GLU_TESS_EDGE_FLAG, tCallback);
		GLU.gluTessCallback(tess, GLU.GLU_TESS_WINDING_POSITIVE, tCallback);
		// GLU.gluTessCallback(tess,GLU.GLU_TESS_ERROR,tCallback);
		gl.glFrontFace(GL.GL_CCW);
		gl.glColor3f(1, 0, 0);
		GLU.gluTessNormal(tess, 0, 0, 0);

		for (double[] vertexes : list) {
			GLU.gluBeginPolygon(tess);
			GLU.gluTessBeginContour(tess);
			for (int offset = 0; offset < vertexes.length; offset += 3) {
				GLU.gluTessVertex(tess, vertexes, offset, new MyVertex(offset,
						vertexes));
			}
			GLU.gluTessEndContour(tess);
			GLU.gluEndPolygon(tess);
		}
	}

	/**
	 * Builds up arrays of vertices for top layer
	 * 
	 * @param list
	 */
	private void setupSufaceVertices(List<double[]> list) {

		double[] vertexes = { 0, -1.5, 1, 0, -1.4, 2.2, 4, -1.4, 2.2, 4, -1.5,
				1 };
		list.add(vertexes);
		double[] hood = { 0, -1.4, 2.2, 0, 1.5, 2.3, 4, 1.5, 2.3, 4, -1.4, 2.2, };
		list.add(hood);
		double[] roof = { 0, 2.4, 5, 0, 5.3, 5, 4, 5.3, 5, 4, 2.4, 5,

		};
		list.add(roof);
		double[] front = { 0, 1.5, 2.3, 0, 2.4, 5, 4, 2.4, 5, 4, 1.5, 2.3, };
		list.add(front);
		double[] back = { 0, 5.3, 5, 0, 5.3, 1, 4, 5.3, 1, 4, 5.3, 5, };
		list.add(back);
		double[] rightSide = { -0.1, -1.5, 1, -0.1, -1.3, 2.2, -0.1, 1.5, 2.3,
				-0.1, 3, 5, -0.1, 5.3, 5, -0.1, 5.3, 1, -0.1, 4, 2, -0.1, 2, 1,
				-0.1, 0, 2, };
		list.add(rightSide);
		double[] leftSide = { 4.1, -1.5, 1, 4.1, -1.3, 2.2, 4.1, 1.5, 2.3, 4.1,
				3, 5, 4.1, 5.3, 5, 4.1, 5.3, 1, 4.1, 4, 2, 4.1, 2, 1, 4.1, 0,
				2, };
		list.add(leftSide);
	}

	/**
	 * Draws ground, a square.
	 */
	private void drawGround(GL2 gl) {
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1);
		gl.glNormal3f(0, 0, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(-10, -10, 0);
		gl.glVertex3f(-10, 10, 0);
		gl.glVertex3f(10, 10, 0);
		gl.glVertex3f(10, -10, 0);
		gl.glEnd();
	}

	private double wheelTurn = 0; // wheel turn corresponding to the car in
									// Degrees
	private int turningDir; /*
							 * Negative value corresponds activate right turning
							 * and positive respectively to left
							 */
	private Texture wheelProt = null; /* Texture of the wheels */

	/**
	 * Draws wheels, given wheelTurn value affects only front wheels
	 * 
	 * @param gl
	 */
	private void drawWheels(GL2 gl) {
		if (wheelProt == null) {
			TextureHandler handler = new TextureHandler("wheelProt.JPG", false,
					gl);
			wheelProt = handler.getTexture();
			wheelProt.enable(gl);
			wheelProt.bind(gl);
			gl.glMatrixMode(GL2.GL_TEXTURE);
			gl.glLoadIdentity();
			gl.glScaled(4.5, 1, 0);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
		}
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

		GLU glu = new GLU();
		GLUquadric obj = glu.gluNewQuadric();
		glu.gluQuadricOrientation(obj, GLU.GLU_OUTSIDE);
		glu.gluQuadricTexture(obj, true);

		double[] v = { 0, 1 };

		// Front right wheel
		gl.glPushMatrix();
		gl.glTranslated(1, 0, 1);
		gl.glRotated(-wheelSpin, 0, 0, 1);
		v = VectorOperations.rotateVector(v, wheelSpin);
		gl.glRotated(-90, v[0], v[1], 0);
		v = VectorOperations.rotateVector(v, 90);
		gl.glRotated(frontWheelDir - wheelTurn, v[0], v[1], 0);
		v = new double[] { -1, 0 };
		v = VectorOperations.rotateVector(v, -wheelSpin);

		gl.glColor3f(1, 1, 1);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPushMatrix();
		gl.glTranslated(0, 0, 1);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPopMatrix();
		// gl.glColor3f(0,1,0);
		gl.glMatrixMode(GL2.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glRotated(180, 0, 0, 1);
		glu.gluCylinder(obj, 1f, 1f, 1, 60, 60);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glTranslated(1, 0, 0);
		gl.glPopMatrix();

		// Left front wheel
		gl.glPushMatrix();
		gl.glTranslated(3, 0, 1);
		gl.glRotated(wheelSpin, 0, 0, 1);
		v = new double[] { 0, 1 };
		v = VectorOperations.rotateVector(v, -wheelSpin);
		gl.glRotated(90, v[0], v[1], 0);
		v = VectorOperations.rotateVector(v, -90);
		gl.glRotated(frontWheelDir - wheelTurn, v[0], v[1], 0);
		gl.glColor3f(0, 0, 0);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPushMatrix();
		gl.glTranslated(0, 0, 1);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPopMatrix();
		gl.glColor3f(0, 1, 0);
		glu.gluCylinder(obj, 1f, 1f, 1, 16, 16);
		gl.glTranslated(1, 0, 0);
		// glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPopMatrix();

		// Back left wheel
		gl.glPushMatrix();
		gl.glTranslated(3, 4, 1);
		gl.glRotated(wheelSpin, 0, 0, 1);
		v = new double[] { 0, 1 };
		v = VectorOperations.rotateVector(v, -wheelSpin);
		gl.glRotated(90, v[0], v[1], 0);
		gl.glColor3f(0, 0, 0);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPushMatrix();
		gl.glTranslated(0, 0, 1);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPopMatrix();
		gl.glColor3f(0, 1, 0);
		glu.gluCylinder(obj, 1f, 1f, 1, 16, 16);
		gl.glTranslated(1, 0, 0);
		// glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPopMatrix();

		// Back right wheel
		gl.glPushMatrix();
		gl.glTranslated(0, 4, 1);
		gl.glRotated(wheelSpin, 0, 0, 1);
		v = new double[] { 0, 1 };
		v = VectorOperations.rotateVector(v, -wheelSpin);
		gl.glRotated(90, v[0], v[1], 0);
		gl.glColor3f(0, 0, 0);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPushMatrix();
		gl.glTranslated(0, 0, 1);
		glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPopMatrix();
		gl.glColor3f(0, 1, 0);
		glu.gluCylinder(obj, 1f, 1f, 1, 16, 16);
		gl.glTranslated(1, 0, 0);
		// glu.gluDisk(obj, 0, 1, 16, 16);
		gl.glPopMatrix();
		drawAxels(gl);

	}

	/**
	 * Draws axels between wheels
	 */
	private void drawAxels(GL2 gl) {
		GLU glu = new GLU();
		GLUquadric obj = glu.gluNewQuadric();
		// Front axel
		gl.glPushMatrix();
		gl.glRotated(90, 0, 1, 0);
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glTranslated(-1, 0, 0);
		glu.gluCylinder(obj, 0.2, 0.2, 3, 16, 16);
		gl.glPopMatrix();
		// Back axel

		gl.glPushMatrix();
		gl.glRotated(90, 0, 1, 0);
		gl.glColor3f(0.5f, 0.1f, 0.5f);
		gl.glTranslated(-1, 4, 0);
		glu.gluCylinder(obj, 0.2, 0.2, 3, 16, 16);

		gl.glPopMatrix();
	}

	/**
	 * all values narrowed down according to the function below -35 <= dir <= 35
	 * 
	 * @param dir
	 */
	public void turnWheels(int dir) {
		turningDir = dir;
		steeringTimer.start();
	}

	/**
	 * dir == -1 grants movement back dir == 1 grants movement forward dir == 0
	 * stops acceleration and acts as retardation of motion
	 * 
	 * @param dir
	 */
	public void move(int dir) {
		double temp = 0;
		temp = calculateTurn();
		vehicleMovementDir = dir;
		movementTimer.start();
		if (dir > 0) {
			changeCarDir(temp);
			setSpeed();
			startX = startX - carVectorDirection[0];
			startY = startY - carVectorDirection[1];
			wheelSpin += spinPerMove;

		} else if (dir < 0) {
			changeCarDir(-temp);
			setSpeed();
			startX = startX + carVectorDirection[0];
			startY = startY + carVectorDirection[1];
			wheelSpin -= spinPerMove;
		} else {

		}

	}

	/**
	 * First insight in to modifying speeds
	 */
	private void setSpeed() {
		carVectorDirection[0] *= speedFactor;
		carVectorDirection[1] *= speedFactor;
	}

	private void changeCarDir(double increament) {
		carDir += increament;
		changeRotCenter(increament);
		System.out.println("Turn: " + increament + " Wheel turn: " + wheelTurn
				+ " Car direction " + carDir + " Increament: " + increament);
		carVectorDirection = new double[] {
				Math.cos(Math.toRadians(carDir + 90))
						* Math.cos(Math.toRadians(wheelTurn)),
				Math.sin(Math.toRadians(carDir + 90))
						* Math.cos(Math.toRadians(wheelTurn)) };
		double[] v;
		// Based on wheelTurn car is rotated, left or right
		if (wheelTurn < 0) {
			v = rightRotCenter.clone();
			v[0] = v[0] + (4 * Math.cos(Math.toRadians(carDir - 90)));
			v[1] = v[1] + (4 * Math.sin(Math.toRadians(carDir - 90)));
			v = VectorOperations.rotateVector(v, -carDir);
			System.out.println(v[0] + " " + v[1] + " Angle: " + (carDir - 90));
			renderingPos = v;
		} else if (wheelTurn > 0) {
			v = leftRotCenter.clone();
			v[0] = v[0]
					+ (Math.sqrt(32) * Math.cos(Math.toRadians(carDir - 135)));
			v[1] = v[1]
					+ (Math.sqrt(32) * Math.sin(Math.toRadians(carDir - 135)));
			v = VectorOperations.rotateVector(v, -carDir);
			System.out.println(v[0] + " " + v[1] + " Angle: " + (carDir - 90));
			renderingPos = v;
		}
		if (carDir < -360) {
			carDir += 360;
		}
		if (carDir > 360) {
			carDir -= 360;
		}
	}

	/**
	 * Required to maintain expected car position during rotation
	 */
	private void changeRotCenter(double d) {
		double[] vector;
		if (d < 0) {
			vector = new double[] { leftRotCenter[0] - rightRotCenter[0],
					leftRotCenter[1] - rightRotCenter[1] };
			vector = VectorOperations.rotateVector(vector, d);
			leftRotCenter[0] = rightRotCenter[0] + vector[0];
			leftRotCenter[1] = rightRotCenter[1] + vector[1];
		} else if (d > 0) {
			vector = new double[] { rightRotCenter[0] - leftRotCenter[0],
					rightRotCenter[1] - leftRotCenter[1] };
			vector = VectorOperations.rotateVector(vector, d);
			rightRotCenter[0] = leftRotCenter[0] + vector[0];
			rightRotCenter[1] = leftRotCenter[1] + vector[1];
		}
	}

	/**
	 * Calculates car turn based on wheel turn.
	 * 
	 * @return - car turn in degrees
	 */
	private double calculateTurn() {
		double amplitude = Math.atan(Math.sin(Math.toRadians(wheelTurn)) / 4);
		return amplitude *= turnFactor;
	}

	/**
	 * turningDir < 0 - turn to the right turningDir > 0 - turn to the left
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (turningDir < 0) {
			if (wheelTurn >= -34) {
				wheelTurn -= 5;
			}
		}
		if (turningDir > 0) {
			if (wheelTurn <= 34) {
				wheelTurn += 5;
			}
		}
		if (turningDir == 0) {
			if (wheelTurn < 0)
				wheelTurn += 1;
			if (wheelTurn > 0)
				wheelTurn -= 1;
		}
		if (turningDir == 0 && wheelTurn == 0) {
			steeringTimer.stop();
		}
	}
}
