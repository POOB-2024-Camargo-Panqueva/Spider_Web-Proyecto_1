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

    /**
     * Constructs a new instance of Spider with the given initial position.
     *
     * @param position The initial position of the spider.
     */
    public Spider(Point position) {
        this.position = position;
        favoriteStrands = new HashMap<>();
    }

    /**
     * Draws the spider on the canvas.
     * The spider is represented by a black ellipse centered at its current position.
     */
    public void draw() {
        Canvas canvas = Canvas.getCanvas();
        double xDraw = position.getX() - (double) WIDTH / 2;
        double yDraw = position.getY() - (double) HEIGHT / 2;

        canvas.draw(this, "black", new Ellipse2D.Double(xDraw, yDraw, WIDTH, HEIGHT));
    }

    /**
     * Removes the favorite strand associated with the specified color from the spider.
     *
     * @param color The color of the favorite strand to remove.
     * @return The value of the removed favorite strand, or null if no favorite strand was associated with the specified color.
     */
    public Integer removeFavoriteStrand(String color) {
        return favoriteStrands.remove(color);
    }

    /**
     * Adds a favorite strand to the spider with the specified color and value.
     * If a favorite strand with the same color already exists, it will be replaced with the new value.
     *
     * @param color  The color of the favorite strand to add.
     * @param strand The value of the favorite strand to add.
     * @return The previous value associated with the specified color, or null if no favorite strand was associated with the color.
     */
    public Integer addFavoriteStrand(String color, Integer strand) {
        Integer result = favoriteStrands.remove(color);
        if (result == null) {
            favoriteStrands.put(color, strand);
        }

        return result;
    }

    /**
     * Moves the spider smoothly to the specified new position.
     * The movement is performed in a straight line at a constant speed.
     *
     * @param newPosition The new position to which the spider will move.
     */
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

    /**
     * Moves the spider smoothly through a series of positions.
     * The spider transitions smoothly from its current position to each of the specified positions in sequence.
     *
     * @param positions An array of points representing the positions to which the spider will move.
     */
    public void moveTo(Point[] positions) {
        for (Point position : positions) {
            this.moveTo(position);
        }
    }

    public Point getPosition() {
        return position;
    }
}
