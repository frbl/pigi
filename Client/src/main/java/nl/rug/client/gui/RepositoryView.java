package nl.rug.client.gui;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

/**
 *
 * @author wesschuitema
 */
public class RepositoryView extends GLJPanel {
    
    public RepositoryView() {
        
        GraphicListener listener = new GraphicListener();
        this.addGLEventListener(listener);
        
    }
    
    private class GraphicListener implements GLEventListener {

        public void init(GLAutoDrawable glad) {
            GL gl = glad.getGL();
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        }

        public void display(GLAutoDrawable glad) {
            GL gl = glad.getGL();
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);

            //Red lines with width==1 pt
            gl.glLineWidth(1);
            gl.glDisable(GL.GL_LINE_STIPPLE);
            gl.glDisable(GL.GL_LINE_SMOOTH);
            gl.glColor3ub((byte)255,(byte)0,(byte)0);//color expressed with unsigned byte..
            gl.glBegin(GL.GL_LINES);
                    gl.glVertex2d(0.3,0.3);
                    gl.glVertex2d(0.8,0.8);
                    gl.glVertex2d(0.35,0.6);
                    gl.glVertex2d(0.8,0.3);
            gl.glEnd();

            //Red line-strip with width==1 pt and antialiasing
            gl.glLineWidth(1);
            gl.glDisable(GL.GL_LINE_STIPPLE);
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glColor3ub((byte)255,(byte)0,(byte)0);//color expressed with unsigned byte..
            gl.glBegin(GL.GL_LINE_STRIP);
                    gl.glVertex2d(-0.3,0.3);
                    gl.glVertex2d(-0.8,0.8);
                    gl.glVertex2d(-0.35,0.6);
                    gl.glVertex2d(-0.8,0.3);
            gl.glEnd();

            //Green line-strip with width==12 pt and antialiasing
            gl.glLineWidth(12);
            gl.glDisable(GL.GL_LINE_STIPPLE);
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glColor3ub((byte)0,(byte)255,(byte)0);//color expressed with unsigned byte..
            gl.glBegin(GL.GL_LINE_STRIP);
                    gl.glVertex2d(-0.3,-0.3);
                    gl.glVertex2d(-0.8,-0.8);
                    gl.glVertex2d(-0.35,-0.6);
                    gl.glVertex2d(-0.8,-0.3);
            gl.glEnd();

            //Yellow line-loop with width==12 pt and antialiasing and line-stippling (pattern on lines)
            gl.glLineWidth(4);
            gl.glEnable(GL.GL_LINE_STIPPLE);
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glLineStipple(3,(short)(1+4+16+128));
            gl.glColor3ub((byte)255,(byte)255,(byte)0);//color expressed with unsigned byte..
            gl.glBegin(GL.GL_LINE_LOOP);
                    gl.glVertex2d(0.3,-0.3);
                    gl.glVertex2d(0.8,-0.8);
                    gl.glVertex2d(0.35,-0.6);
                    gl.glVertex2d(0.8,-0.3);
            gl.glEnd();

            //Blue axis with width==4 pt with antialiasing and line-stippling (pattern on lines)
            gl.glLineWidth(4);
            gl.glEnable(GL.GL_LINE_STIPPLE);
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glLineStipple(3,(short)(1+2+4+16));//pattern di stipple
            gl.glColor3ub((byte)0,(byte)0,(byte)255);//color expressed with unsigned byte..
            gl.glBegin(GL.GL_LINES);
                    gl.glVertex2d(-1,0);
                    gl.glVertex2d(1,0);
                    gl.glVertex2d(0,-1);
                    gl.glVertex2d(0,1);
            gl.glEnd();
        }

        public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
            //TODO
        }

        public void displayChanged(GLAutoDrawable glad, boolean bln, boolean bln1) {
            //TODO
        }
        
    }
    
}
