import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.function.Function;

public class Spider {

    private final double STEP = (double) 1 / 100;

    private final int WIDTH = 30;
    private final int HEIGHT = 24;
    private final HashMap<String, Integer> favoriteStrands;

    private Point position;

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

    public void moveTo(Point newPosition) {
        Point2D.Double director = new Point2D.Double(newPosition.getX() - position.getX(), newPosition.getY() - position.getY());
        Point initialPosition = new Point((int) position.getX(), (int) position.getY());

        Function<Double, Point> lineFunction = (Double t) -> {
            int x = (int) (initialPosition.getX() + t * director.x);
            int y = (int) (initialPosition.getY() + t * director.y);

            return new Point(x, y);
        };

        double parameter = 0;
        while (parameter < 1) {
            this.position = lineFunction.apply(parameter);

            Canvas canvas = Canvas.getCanvas();
            canvas.wait(16);

            this.draw();

            //TODO: Implements constant speed
            parameter += STEP;
        }
    }

    public void moveTo(Point[] positions) {
        for (Point position : positions) {
            this.moveTo(position);
        }
    }

    public Point getPosition() {
        return position;
    }
}
