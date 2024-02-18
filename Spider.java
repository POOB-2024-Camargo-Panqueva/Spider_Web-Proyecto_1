import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Spider {

    private final int WIDTH = 30;
    private final int HEIGHT = 24;
    private int favoriteStrand;

    private final Point position;

    public Spider(Point position, int favoriteStrand) {
        this.position = position;
        this.favoriteStrand = favoriteStrand;
    }

    public void draw() {
        Canvas canvas = Canvas.getCanvas();
        double xDraw = position.getX() - (double) WIDTH / 2;
        double yDraw = position.getY() - (double) HEIGHT / 2;

        canvas.draw(this, "black", new Ellipse2D.Double(xDraw, yDraw, WIDTH, HEIGHT));
    }

    public void setFavoriteStrand(int newStrand){
        this.favoriteStrand = newStrand;
    }

    public int getFavoriteStrand() {
        return favoriteStrand;
    }
}
