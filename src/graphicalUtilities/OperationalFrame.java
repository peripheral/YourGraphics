package graphicalUtilities;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * Make a house of quads or polys (like the picture below) with you own measurements 
 * and a camera that views it from an angle and use the mouse to rotate it (we do that next week)
 * @author Artur Vitt
 *
 */

public class OperationalFrame extends Frame implements GLEventListener,KeyListener,MouseWheelListener {
	
	 	private static final Dimension PREFFERED_FRAME_SIZE=new Dimension(600,400);
	 	private List<Drawable> list= new LinkedList<Drawable>();
	 	private GLCanvas canvas;
	 	private int Rx=0,Ry=0,Dist=10;
	 	public static void main(String[] args){
	 		Frame mainFrame = new OperationalFrame();
	 		mainFrame.pack();
	 		mainFrame.setVisible(true);
	 		mainFrame.setSize(PREFFERED_FRAME_SIZE);
	 		mainFrame.addWindowListener(new WindowAdapter(){
	 			public void windowClosing(WindowEvent e){
	 				System.exit(0);
	 			}
	 		});		
	 	}
	 	
	 	public OperationalFrame(){
	 		super("Window");
	 		GLCapabilities capabilities = new GLCapabilities(null);
	 		canvas = new GLCanvas(capabilities);
	 		canvas.addGLEventListener(this);
	 		canvas.addKeyListener(this);
	 		canvas.addMouseWheelListener(this);
	 		add(canvas,BorderLayout.CENTER);
	 		initObjectsToDraw();
	 		FPSAnimator animator = new FPSAnimator(canvas, 25);			//Animator reloads display 10 times/sec normal 60  fps
	 		animator.add(canvas);
	 		animator.start();
	 	}
	 	
	 	public Dimension getPrefferedSize(){
	 		return PREFFERED_FRAME_SIZE;
	 	} 

	 	@Override
	 	public void display(GLAutoDrawable drawable) {
	 		GL2 gl = (GL2)drawable.getGL();
	 		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	 		int width =300,height=300;
	 		final GLU glu = new GLU();

	 		gl.glViewport(0, 0, this.getWidth(),this.getHeight());					// size of the window
	 		gl.glMatrixMode(GL2.GL_PROJECTION);
	 		gl.glLoadIdentity();

	 		glu.gluPerspective(90,(width*1.0f)/height,1,20);
	 		//45 = field of view ,width/height = aspect ratio , 1 = near clipping plane 20 = far clipping plane 
	 		//http://pyopengl.sourceforge.net/documentation/ref/glu/perspective.html

	 		float[] eyePoint = newEyePoint(Dist);
	 		glu.gluLookAt(eyePoint[0],eyePoint[1],eyePoint[2],0.0f,0.0f,0.0f,0.0f,0.0f,1.0f);
	 		gl.glEnable(GL2.GL_CULL_VERTEX_EYE_POSITION_EXT);
	 		for(Drawable i: list){
	 			i.draw(gl);
	 		}
	 	}

	 	@Override
	 	public void dispose(GLAutoDrawable drawable) {
	 		// TODO Auto-generated method stub
	 		
	 	}

	 	@Override
	 	public void init(GLAutoDrawable drawable) {
	 		GL2 gl = (GL2)drawable.getGL();
	 		gl.glClearColor(1.0f,1.0f,1.0f,1.0f);
	 		gl.glColor3f(0.5f, 0.5f, 0.5f);
	 		gl.glPointSize(4.0f);
	 		
	 	}
	 	
	 	public void initObjectsToDraw(){
	 		//list.add(new House());
	 	}
	 	
	 	private float[] newEyePoint(int r) {
	 		float[] points= new float[3];
	 		points[0] = (float) (r*Math.cos(Math.toRadians(Rx))*Math.cos(Math.toRadians(Ry)));
	 		points[1] = (float) (r*Math.sin(Math.toRadians(Rx))*Math.cos(Math.toRadians(Ry)));
	 		points[2] = (float) (r*Math.sin(Math.toRadians(Ry)));
	 		return points;
	 	}

	 	@Override
	 	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	 			int height) { 
	 		canvas.setPreferredSize(this.getSize());
	 		GL2 gl = drawable.getGL().getGL2();
	 		
	 		gl.glViewport(0, 0, width, height);
	 		gl.glMatrixMode(GL2.GL_PROJECTION);
	 		gl.glLoadIdentity();
	 		final GLU glu = new GLU();
	 		//glu.gluOrtho2D(0.0, 450.0, 0.0, 375.0);	
	 		glu.gluPerspective(90,(width*1.0f)/height,1,20);
	 		float[] v= newEyePoint(Dist);
	 		glu.gluLookAt(v[0],v[1],v[2],0.0f,0.0f,0.0f,0.0f,0.0f,1.0f);
	 		glu.gluOrtho2D(0.0, 20.0, 0.0, 40.0);
	 	}
	 	
	 	
	 	
	 	@Override
	 	public void keyPressed(KeyEvent e)
	 	{
	 		//	  System.out.println("key press");
	 		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){

	 			System.exit(0);}

	 		if (e.getKeyCode() == KeyEvent.VK_W){
	 			Ry=Ry+2;   }
	 		if (e.getKeyCode() == KeyEvent.VK_S){
	 			Ry=Ry-2;}

	 		if (e.getKeyCode() == KeyEvent.VK_A){
	 			Rx=Rx-1;   }
	 		if (e.getKeyCode() == KeyEvent.VK_D){
	 			Rx=Rx+1;}

	 		if (e.getKeyCode() == KeyEvent.VK_L){
	 			//showLines = !showLines;
	 		}
	 		System.out.println(newEyePoint(5)[0]+" "+newEyePoint(5)[1]+" "+newEyePoint(5)[2]);
	 	}

	 	@Override
	 	public void keyReleased(KeyEvent arg0) {
	 		// TODO Auto-generated method stub
	 		
	 	}

	 	@Override
	 	public void keyTyped(KeyEvent arg0) {
	 		// TODO Auto-generated method stub
	 		
	 	}

	 	@Override
	 	public void mouseWheelMoved(MouseWheelEvent m) {
	 		Dist+=m.getWheelRotation();
	 		
	 	}

}
