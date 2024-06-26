package shape;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Canvas is a class to allow for simple graphical drawing on a canvas.
 * This is a modification of the general purpose Canvas, specially made for
 * the BlueJ "shapes" example.
 *
 * @author: Bruce Quig
 * @author: Michael Kolling (mik)
 * @version: 1.6 (shapes)
 */
public class Canvas {
    // Note: The implementation of this class (specifically the handling of
    // shape identity and colors) is slightly more complex than necessary. This
    // is done on purpose to keep the interface and instance fields of the
    // shape objects in this project clean and simple for educational purposes.

    private static Canvas canvasSingleton;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    public static final Point CENTER = new Point(WIDTH / 2, HEIGHT / 2);

    private static final String CANVAS_TITLE = "SpiderWeb Camargo Panqueva";

    /**
     * Factory method to get the canvas singleton object.
     */
    public static Canvas getCanvas() {
        if (canvasSingleton == null) {
            canvasSingleton = new Canvas(CANVAS_TITLE, WIDTH, HEIGHT, Color.white);
        }
        canvasSingleton.setVisible(true);
        return canvasSingleton;
    }

    //  ----- instance part -----

    private final JFrame frame;
    private final CanvasPane canvas;
    private final Color backgroundColour;
    private final List<Object> objects;
    private final HashMap<Object, ShapeDescription> shapes;
    private Image canvasImage;
    private Graphics2D graphic;

    /**
     * Create a Canvas.
     *
     * @param title    title to appear in Canvas Frame
     * @param width    the desired width for the canvas
     * @param height   the desired height for the canvas
     * @param bgColour the desired background colour of the canvas
     */
    private Canvas(String title, int width, int height, Color bgColour) {
        frame = new JFrame();
        canvas = new CanvasPane();
        objects = new ArrayList<>();
        shapes = new HashMap<>();

        frame.setContentPane(canvas);
        frame.setTitle(title);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas.setPreferredSize(new Dimension(width, height));

        backgroundColour = bgColour;
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    /**
     * Set the canvas visibility and brings canvas to the front of screen
     * when made visible. This method can also be used to bring an already
     * visible canvas to the front of other windows.
     *
     * @param visible boolean value representing the desired visibility of
     *                the canvas (true or false)
     */
    public void setVisible(boolean visible) {

        if (graphic == null) {
            // first time: instantiate the off-screen image and fill it with
            // the background colour
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D) canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }

        frame.setVisible(visible);
    }

    /**
     * Draw a given shape onto the canvas.
     *
     * @param referenceObject an object to define identity for this shape
     * @param color           the color of the shape
     * @param shape           the shape object to be drawn on the canvas
     */
    // Note: this is a slightly backwards way of maintaining the shape
    // objects. It is carefully designed to keep the visible shape interfaces
    // in this project clean and simple for educational purposes.
    public void draw(Object referenceObject, String color, Shape shape) {
        objects.remove(referenceObject);   // just in case it was already there
        objects.add(referenceObject);      // add at the end
        shapes.put(referenceObject, new ShapeDescription(shape, color));
        redraw();
    }

    /**
     * Erase a given shape's from the screen.
     *
     * @param referenceObject the shape object to be erased
     */
    public void erase(Object referenceObject) {
        objects.remove(referenceObject);   // just in case it was already there
        shapes.remove(referenceObject);
        redraw();
    }

    /**
     * Set the foreground colour of the Canvas.
     *
     * @param colorString the new colour for the foreground of the Canvas
     */
    public void setForegroundColor(String colorString) {
        // if colorString starts with a HEX color with 6 chars extract it and ignore the rest
        if (colorString.matches("^#[A-Fa-f0-9]{6}.*")) {
            graphic.setColor(Color.decode(colorString.substring(0, 7)));
            return;
        }

        switch (colorString) {
            case "red" -> graphic.setColor(new Color(227, 64, 64));
            case "blue" -> graphic.setColor(new Color(64, 64, 227));
            case "yellow" -> graphic.setColor(new Color(204, 204, 59));
            case "green" -> graphic.setColor(new Color(86, 203, 86));
            case "magenta" -> graphic.setColor(new Color(213, 89, 213));
            case "white" -> graphic.setColor(new Color(255, 255, 255));
            case "gray" -> graphic.setColor(new Color(150, 150, 150));
            default -> graphic.setColor(new Color(0, 0, 0));
        }
    }

    /**
     * Wait for a specified number of milliseconds before finishing.
     * This provides an easy way to specify a small delay which can be
     * used when producing animations.
     *
     * @param milliseconds the number
     */
    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            // ignoring exception at the moment
        }
    }

    /**
     * Redraw all shapes currently on the Canvas.
     */
    private void redraw() {
        erase();
        for (Object object : objects) {
            shapes.get(object).draw(graphic);
        }
        canvas.repaint();
    }

    /**
     * Erase the whole canvas. (Does not repaint.)
     */
    private void erase() {
        Color original = graphic.getColor();
        graphic.setColor(backgroundColour);
        Dimension size = canvas.getSize();
        graphic.fill(new java.awt.Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }

    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class CanvasPane extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(canvasImage, 0, 0, null);
        }
    }

    /************************************************************************
     * Inner class CanvasPane - the actual canvas component contained in the
     * Canvas frame. This is essentially a JPanel with added capability to
     * refresh the image drawn on it.
     */
    private class ShapeDescription {
        private final Shape shape;
        private final String colorString;

        public ShapeDescription(Shape shape, String color) {
            this.shape = shape;
            this.colorString = color;
        }

        public void draw(Graphics2D graphic) {
            setForegroundColor(colorString);
            graphic.draw(shape);
            graphic.fill(shape);
        }
    }
}
