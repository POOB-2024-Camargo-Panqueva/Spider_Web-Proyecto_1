package spiderweb;

import shape.Canvas;

import java.awt.*;
import java.awt.geom.Line2D;

public final class Strand {

    private final Point start;
    private final Point end;
    private String color;

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
            //TODO: Check if this throws an exception or show a message
            throw new IllegalArgumentException("Invalid scale");
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
        return String.format("Line [%s, %s]", start, end);
    }

    public boolean equals(Strand strand) {
        return strand.start.equals(this.start) && strand.end.equals(this.end) && strand.color.equals(this.color);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Strand) && this.equals((Strand) obj);
    }
}
