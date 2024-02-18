import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Spider {

    private final int WIDTH = 30;
    private final int HEIGHT = 24;

    private final Point position;

    public Spider(Point position) {
        this.position = position;
    }

    public void draw() {
        Canvas canvas = Canvas.getCanvas();
        double xDraw = position.getX() - (double) WIDTH / 2;
        double yDraw = position.getY() - (double) HEIGHT / 2;

        canvas.draw(this, "black", new Ellipse2D.Double(xDraw, yDraw, WIDTH, HEIGHT));
    }
}
