package spiderweb.spider;

import shape.Canvas;

import java.awt.*;
import java.awt.geom.Line2D;

public class Leg {

    private final int index;
    private boolean isVisible;

    public Leg(int index) {

        if (index < 0) {
            throw new IllegalArgumentException("Leg index must be non-negative.");
        }

        if (index > 7) {
            throw new IllegalArgumentException("Leg index must be less than 8.");
        }

        isVisible = false;
        this.index = index;
    }

    /**
     * Calculates the offset for the leg based on the index and the current tick.
     *
     * @param index The index of the leg.
     * @param tick  The current tick.
     * @return The offset for the leg.
     */
    private double calculateOffset(int index, int tick) {
        int indexWeight = (index % 4) * 16;
        int remainder = (tick - indexWeight) % 4;
        double scaled = remainder * 0.15;

        return 1.5 + scaled;
    }

    /**
     * Draws the leg on the canvas.
     *
     * @param spiderPosition The position of the spider.
     * @param tick           The current tick of the animation.
     */
    public void draw(Point spiderPosition, int tick) {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            int xCenter = (int) spiderPosition.getX();
            int yCenter = (int) spiderPosition.getY();

            int xEnd;
            int yEnd;
            int angle = 22;
            double offset = calculateOffset(index, tick);

            double radians = Math.toRadians(angle * ((index % 4) - offset));

            int relativeX = (int) (Math.cos(radians) * 24);
            int relativeY = (int) (Math.sin(radians) * 24);
            yEnd = yCenter + relativeY;
            xEnd = xCenter + (index < 4 ? 1 : -1) * relativeX;

            canvas.draw(this, "black", new Line2D.Double(xCenter, yCenter, xEnd, yEnd));
        }
    }

    /**
     * Makes the leg visible and draws it on the canvas.
     *
     * @param spiderPosition The position of the spider.
     * @param tick           The current tick of the animation.
     */
    public void makeVisible(Point spiderPosition, int tick) {
        isVisible = true;
        draw(spiderPosition, tick);
    }
}
