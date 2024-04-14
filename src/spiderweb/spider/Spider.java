package spiderweb.spider;

import shape.Canvas;
import spiderweb.main.SpiderWeb;
import spiderweb.strands.NormalStrand;
import spiderweb.strands.Strand;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Function;

public class Spider {

    private final int WIDTH = 30;
    private final int HEIGHT = 24;
    private final ArrayList<Strand> traceStrands;
    private final ArrayList<Leg> legs;
    private Boolean isVisible = true;
    private Point position;

    private boolean isAlive;

    /**
     * Constructs a new instance of Spider with the given initial position.
     *
     * @param position The initial position of the spider.
     */
    public Spider(Point position) {
        this.position = position;
        this.traceStrands = new ArrayList<>();
        this.legs = new ArrayList<>();

        this.isAlive = true;

        for (int i = 0; i < 8; i++) {
            legs.add(new Leg(i));
        }
    }

    /**
     * Draws the spider on the canvas.
     *
     * @param tick The current tick of the animation.
     */
    public void draw(int tick) {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();

            for (Leg leg : legs) {
                leg.draw(this.position, tick);
            }

            double xDraw = position.getX() - (double) WIDTH / 2;
            double yDraw = position.getY() - (double) HEIGHT / 2;

            canvas.draw(this, this.isAlive ? "black" : "#909090", new Ellipse2D.Double(xDraw, yDraw, WIDTH, HEIGHT));
            canvas.draw(this + "leftEye", "red", new Ellipse2D.Double(xDraw + 5, yDraw + 5, 5, 5));
            canvas.draw(this + "rightEye", "red", new Ellipse2D.Double(xDraw + 20, yDraw + 5, 5, 5));

            for (Strand strand : traceStrands) {
                strand.draw();
            }
        }
    }

    /**
     * Draws the spider on the canvas.
     * The spider is represented by a black ellipse centered at its current position.
     */
    public void draw() {
        this.draw(0);
    }

    /**
     * Makes the spider invisible on the canvas.
     */
    public void makeInvisible() {
        isVisible = false;
    }

    /**
     * Makes the spider visible on the canvas.
     */
    public void makeVisible() {
        isVisible = true;

        for (Leg leg : legs) {
            leg.makeVisible(this.position, 0);
        }
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
        int tick = 0;

        while (parameter >= 0 && parameter < 1) {
            this.position = lineFunction.apply(parameter);
            canvas.wait(16);

            this.draw(tick++);

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
            this.traceStrands.add(new NormalStrand(initialPosition, newPosition, "red"));
        }
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
        this.draw();
    }

    /**
     * Resets the trace lines drawn by the spider.
     */
    public void resetTraceLines() {
        for (Strand strand : traceStrands) {
            strand.erase();
        }
        traceStrands.clear();
    }

    public void kill() {
        this.isAlive = false;
    }

    public void respawn() {
        this.isAlive = true;
    }

    public boolean isAlive() {
        return this.isAlive;
    }
}
