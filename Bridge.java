import java.awt.*;
import java.awt.geom.Line2D;

public final class Bridge {

    private final int distance;
    private final int initialStrand;
    private final int finalStrand;
    private final String color;
    private final Point initialPoint;
    private final Point finalPoint;


    public Bridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        this.distance = distance;
        this.initialStrand = initialStrand;
        this.finalStrand = finalStrand;
        this.color = color;

        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
    }

    public void draw() {
        Canvas canvas = Canvas.getCanvas();
        canvas.draw(this, this.color, new Line2D.Double(initialPoint.getX(), initialPoint.getY(), finalPoint.getX(), finalPoint.getY()));
    }
}
