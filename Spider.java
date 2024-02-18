import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

public class Spider {

    private final int WIDTH = 30;
    private final int HEIGHT = 24;
    private final HashMap<String, Integer> favoriteStrands;

    private final Point position;

    public Spider(Point position) {
        this.position = position;
        favoriteStrands = new HashMap<>();
    }

    public void draw() {
        Canvas canvas = Canvas.getCanvas();
        double xDraw = position.getX() - (double) WIDTH / 2;
        double yDraw = position.getY() - (double) HEIGHT / 2;

        canvas.draw(this, "black", new Ellipse2D.Double(xDraw, yDraw, WIDTH, HEIGHT));
    }

    public Integer removeFavoriteStrand(String color) {
        return favoriteStrands.remove(color);
    }

    public Integer addFavoriteStrand(String color, Integer strand) {
        Integer result = favoriteStrands.remove(color);
        if (result == null) {
            favoriteStrands.put(color, strand);
        }
        return result;
    }

    public Point getPosition() {
        return position;
    }
}
