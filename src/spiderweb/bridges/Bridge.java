package spiderweb.bridges;

import interfaces.Copyable;
import shape.Canvas;
import spiderweb.main.SpiderWeb;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.function.Consumer;

public abstract class Bridge implements Copyable<Bridge> {

    protected final int distance;
    protected final int initialStrand;
    protected final int finalStrand;
    protected final String color;
    protected final Point initialPoint;
    protected final Point finalPoint;

    /**
     * Constructs a new instance of spiderweb.Bridge with the specified parameters.
     *
     * @param distance      The distance of the bridge.
     * @param initialStrand The initial strand connected by the bridge.
     * @param finalStrand   The final strand connected by the bridge.
     * @param initialPoint  The initial point of the bridge.
     * @param finalPoint    The final point of the bridge.
     * @param color         The color of the bridge.
     */
    public Bridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        this.distance = distance;
        this.initialStrand = initialStrand;
        this.finalStrand = finalStrand;
        this.color = color;

        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
    }

    public abstract void triggerAction(SpiderWeb spiderWeb);

    /**
     * Draws the bridge on the canvas.
     */
    public void draw() {
        Canvas canvas = Canvas.getCanvas();
        canvas.draw(this, this.color, new Line2D.Double(initialPoint.getX(), initialPoint.getY(), finalPoint.getX(), finalPoint.getY()));
    }

    /**
     * Erases the bridge from the canvas.
     */
    public void erase() {
        Canvas canvas = Canvas.getCanvas();
        canvas.erase(this);
    }

    public int getDistance() {
        return distance;
    }

    public int getInitialStrand() {
        return initialStrand;
    }

    public int getFinalStrand() {
        return finalStrand;
    }

    public String getColor() {
        return color;
    }

    public Point getInitialPoint() {
        return initialPoint;
    }

    public Point getFinalPoint() {
        return finalPoint;
    }

    @Override
    public String toString() {
        return String.format("Initial Strand: %d - Final Strand: %d - Distance: %d - Color: %s", initialStrand, finalStrand, distance, color);
    }

    public boolean equals(Bridge bridge) {
        // TODO : is equals if is instance of respective Bridge, must be abstract
        return bridge.initialStrand == this.initialStrand
                && bridge.finalStrand == this.finalStrand
                && bridge.distance == this.distance
                && bridge.color.equals(this.color)
                && bridge.initialPoint.equals(this.initialPoint)
                && bridge.finalPoint.equals(this.finalPoint);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Bridge) && this.equals((Bridge) obj);
    }

    /**
     * The types of bridges.
     */
    public enum Types {
        NORMAL("normal"),
        FIXED("fixed"),
        TRANSFORMER("transformer"),
        WEAK("weak"),
        MOBILE("mobile");


        private final String type;

        Types(String black) {
            this.type = black;
        }

        public String getType() {
            return type;
        }
    }

}
