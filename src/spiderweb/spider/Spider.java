package spiderweb.spider;

import shape.Canvas;
import spiderweb.strands.Strand;
import spiderweb.main.SpiderWeb;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class Spider {

    private final int WIDTH = 30;
    private final int HEIGHT = 24;
    private final HashMap<String, Integer> favoriteStrands;
    private final ArrayList<Strand> traceStrands;
    private Boolean isVisible = true;
    private Point position;

    /**
     * Constructs a new instance of Spider with the given initial position.
     *
     * @param position The initial position of the spider.
     */
    public Spider(Point position) {
        this.position = position;
        this.favoriteStrands = new HashMap<>();
        this.traceStrands = new ArrayList<>();
    }

    /**
     * Draws the spider on the canvas.
     * The spider is represented by a black ellipse centered at its current position.
     */
    public void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            double xDraw = position.getX() - (double) WIDTH / 2;
            double yDraw = position.getY() - (double) HEIGHT / 2;

            canvas.draw(this, "black", new Ellipse2D.Double(xDraw, yDraw, WIDTH, HEIGHT));
            canvas.draw(this + "leftEye", "red", new Ellipse2D.Double(xDraw + 5, yDraw + 5, 5, 5));
            canvas.draw(this + "rightEye", "red", new Ellipse2D.Double(xDraw + 20, yDraw + 5, 5, 5));

            for (Strand strand : traceStrands) {
                strand.draw();
            }
        }
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

    public void makeInvisible() {
        isVisible = false;
    }

    public void makeVisible() {
        isVisible = true;
    }

    /**
     * Moves the spider smoothly to the specified new position.
     * The movement is performed in a straight line at a constant speed.
     *
     * @param newPosition The new position to which the spider will move.
     */
    public void moveTo(Point newPosition) {
        final double STEP = (double) 1 / getPosition().distance(newPosition);

        Point2D.Double director = new Point2D.Double(newPosition.getX() - position.getX(), newPosition.getY() - position.getY());
        Point initialPosition = new Point((int) position.getX(), (int) position.getY());
        Canvas canvas = Canvas.getCanvas();

        Function<Double, Point> lineFunction = (Double t) -> {
            int x = (int) (initialPosition.getX() + t * director.x);
            int y = (int) (initialPosition.getY() + t * director.y);

            return new Point(x, y);
        };

        double parameter = SpiderWeb.TEST_MODE ? -1 : 0;

        while (parameter >= 0 && parameter < 1) {
            this.position = lineFunction.apply(parameter);
            canvas.wait(16);
            this.draw();

            if (!SpiderWeb.TEST_MODE) {
                canvas.draw(this + "currentLine", "red", new Line2D.Double(initialPosition.getX(), initialPosition.getY(), position.getX(), position.getY()));
            }

            parameter += STEP;
        }

        if (parameter != 1) {
            this.position = newPosition;
            this.draw();
        }

        if (!SpiderWeb.TEST_MODE) {
            this.traceStrands.add(new Strand(initialPosition, newPosition, "red"));
        }
    }

    /**
     * Moves the spider smoothly through a series of positions.
     * The spider transitions smoothly from its current position to each of the specified positions in sequence.
     *
     * @param positions An array of points representing the positions to which the spider will move.
     */
    public void moveTo(ArrayList<Point> positions) {
        for (Point position : positions) {
            this.moveTo(position);
        }
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
        this.draw();
    }

    public HashMap<String, Integer> getFavoriteStrands() {
        return favoriteStrands;
    }

    public void resetTraceLines() {
        for (Strand strand : traceStrands) {
            strand.erase();
        }
        traceStrands.clear();
    }
}
