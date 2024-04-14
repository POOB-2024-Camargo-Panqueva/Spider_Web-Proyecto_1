package spiderweb.strands;

import shape.Canvas;
import spiderweb.main.SpiderWeb;
import utilities.MessageHandler;

import java.awt.*;
import java.awt.geom.Line2D;

public abstract class Strand {

    protected final Point start;
    protected final Point end;
    protected String color;

    /**
     * Constructs a new instance of Line with the specified start and end points.
     * The default color for the line is gray.
     *
     * @param start The starting point of the line.
     * @param end   The ending point of the line.
     */
    public Strand(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.color = "gray";
    }

    /**
     * Constructs a new instance of Line with the specified start and end points, and color.
     *
     * @param start The starting point of the line.
     * @param end   The ending point of the line.
     * @param color The color of the line.
     */
    public Strand(Point start, Point end, String color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    /**
     * Triggers the action associated with the strand.
     *
     * @param spiderWeb The spider web instance.
     */
    protected abstract void triggerAction(SpiderWeb spiderWeb);

    /**
     * Draws the line on the canvas.
     */
    public void draw() {
        shape.Canvas canvas = shape.Canvas.getCanvas();
        canvas.draw(this, this.color, new Line2D.Double(start.getX(), start.getY(), end.getX(), end.getY()));
    }

    public void erase() {
        shape.Canvas canvas = Canvas.getCanvas();
        canvas.erase(this);
    }

    /**
     * Gets a point on the line at a specified scale between 0 and 1.
     *
     * @param scale The scale value between 0 and 1 indicating the position along the line.
     * @return The point on the line at the specified scale.
     * @throws IllegalArgumentException if the scale is not between 0 and 1.
     */
    public Point getScaledPoint(double scale) {
        if (scale < 0 || scale > 1) {
            MessageHandler.showError("Invalid Scale");
        }

        int x = (int) (start.getX() + (end.getX() - start.getX()) * scale);
        int y = (int) (start.getY() + (end.getY() - start.getY()) * scale);
        return new Point(x, y);
    }

    public void setColor(String color) {
        this.color = color;
        this.draw();
    }

    public Point getEnd() {
        return end;
    }

    public String toString() {
        return String.format("Color: %s", color);
    }

    public boolean equals(Strand strand) {
        return strand.start.equals(this.start) && strand.end.equals(this.end) && strand.color.equals(this.color);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Strand) && this.equals((Strand) obj);
    }

    public enum Types {
        KILLER("normal"),
        NORMAL("fixed");

        private final String type;

        Types(String black) {
            this.type = black;
        }

        public String getType() {
            return type;
        }
    }
}
