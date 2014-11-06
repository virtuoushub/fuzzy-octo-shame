package com.jed.actor;

import org.lwjgl.opengl.GL11;

import com.jed.util.Vector;

/**
 * 
 * @author jlinde, Peter Colapietro
 *
 */
public class PolygonBoundary extends Boundary {

    /**
     * 
     */
    private double rightBound = 0;
    
    /**
     * 
     */
    private double leftBound = 0;
    
    /**
     * 
     */
    private double upperBound = 0;
    
    /**
     * 
     */
    private double lowerBound = 0;

    /**
     * 
     * @param position position vector
     * @param verticies array of vertices
     */
    public PolygonBoundary(final Vector position, final Vector[] verticies) {
        super(position, verticies);

        //Find Max Bounds for quad tree
        for (final Vector vertex: verticies) {
            if (vertex.x > rightBound) {
                rightBound = vertex.x;
            }
            if (vertex.x < leftBound) {
                leftBound = vertex.x;
            }
            if (vertex.y < upperBound) {
                upperBound = vertex.y;
            }
            if (vertex.y > lowerBound) {
                lowerBound = vertex.y;
            }
        }
    }

    @Override
    public double getRightBound() {
        return owner.position.x + position.x + rightBound;
    }

    @Override
    public double getLeftBound() {
        return owner.position.x + position.x + leftBound;
    }

    @Override
    public double getUpperBound() {
        return owner.position.y + position.y + upperBound;
    }

    @Override
    public double getLowerBound() {
        return owner.position.y + position.y + lowerBound;
    }

    @Override
    public int getWidth() {
        return (int) (rightBound - leftBound);
    }

    @Override
    public int getHeight() {
        return (int) (lowerBound - upperBound);
    }

    @Override
    public void draw() {
        //Bounding Box
        GL11.glColor3f(1f, 0, 0);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (final Vector vertex : verticies) {
            owner.drawChildVertex2f(position.x + vertex.x, position.y + vertex.y);
        }
        GL11.glEnd();
    }

}
