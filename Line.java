import java.awt.*;
import java.awt.geom.Line2D;

public final class Line {

    private final Point start;
    private final Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public void draw() {
        Canvas canvas = Canvas.getCanvas();
        canvas.draw(this, "red", new Line2D.Double(start.getX(), start.getY(), end.getX(), end.getY()));
    }

    public Point getScaledPoint(double scale) {
        if (scale < 0 || scale > 1) {
            //TODO: Check if this throws an exception or show a message
            throw new IllegalArgumentException("Invalid scale");
        }

        int x = (int) (start.getX() + (end.getX() - start.getX()) * scale);
        int y = (int) (start.getY() + (end.getY() - start.getY()) * scale);
        return new Point(x, y);
    }

    public String toString() {
        return String.format("Line [%s, %s]", start, end);
    }
}
