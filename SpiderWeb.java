import java.awt.*;
import java.util.ArrayList;

public final class SpiderWeb {

    private final ArrayList<Line> strandLines;
    private final ArrayList<Bridge> bridges;
    private final Spider spider;

    private final int strands;
    private final int radio;

    private boolean isVisible;

    public SpiderWeb(int strands, int radio) {
        this.strands = strands;
        this.radio = radio;

        this.isVisible = false;

        this.spider = new Spider(new Point(Canvas.CENTER));
        this.strandLines = new ArrayList<>(this.strands);
        this.bridges = new ArrayList<>();

        this.generateStrandLines();
    }

    public void generateStrandLines() {
        for (int index = this.strands; index >= 0; index--) {
            double angle = Math.toRadians((double) 360 / this.strands * index);
            int x = (int) (this.radio * Math.cos(angle));
            int y = (int) (this.radio * Math.sin(angle));
            this.strandLines.add(new Line(new Point(Canvas.CENTER), new Point(Canvas.CENTER.x + x, Canvas.CENTER.y + y)));
        }
    }

    public void makeVisible() {
        this.isVisible = true;
        this.draw();
    }

    public void makeInvisible() {
        this.isVisible = false;
        Canvas canvas = Canvas.getCanvas();
        canvas.setVisible(false);
    }

    private void draw() {
        if (this.isVisible) {
            this.strandLines.forEach(Line::draw);
            this.bridges.forEach(Bridge::draw);
            this.spider.draw();
        }
    }

    public void addBridge(String color, int distance, int firstStrand) {
        if (firstStrand < 0 || firstStrand >= this.strands) {
            MessageHandler.showError("Invalid strand", "The strand " + firstStrand + " is not valid");
            return;
        }
        if (distance < 0 | distance > radio) {
            MessageHandler.showError("Invalid distance", "The distance " + distance + " is not valid");
            return;
        }
        if (!this.isVisible) {
            MessageHandler.showError("Spider web is not visible yet", "The spider web is not visible yet");
            return;
        }

        int finalStrand = firstStrand + 1;
        Point initialPoint = this.strandLines.get(firstStrand).getScaledPoint((double) distance / this.radio);
        Point finalPoint = this.strandLines.get(finalStrand).getScaledPoint((double) distance / this.radio);

        this.bridges.add(new Bridge(distance, firstStrand, finalStrand, initialPoint, finalPoint, color));

        this.draw();
    }

    public void relocateBridge(String color, int distance) {

        Bridge targetBridge = null;

        for (int i = 0; i < this.bridges.size(); i++) {
            if (this.bridges.get(i).getColor().equals(color)) {
                targetBridge = this.bridges.remove(i);
                targetBridge.erase();
                break;
            }
        }

        if (targetBridge == null) {
            MessageHandler.showError("Bridge not found", "The bridge with color: " + color + " was not found");
            return;
        }

        this.addBridge(color, distance, targetBridge.getInitialStrand());
    }

    public void removeBridge(String color) {
        Bridge targetBridge = null;

        for (int i = 0; i < this.bridges.size(); i++) {
            if (this.bridges.get(i).getColor().equals(color)) {
                targetBridge = this.bridges.remove(i);
                targetBridge.erase();
                break;
            }
        }

        if (targetBridge == null) {
            MessageHandler.showError("Bridge not found", "The bridge with color: " + color + " was not found");
            return;
        }

        this.draw();
    }

    public void addFavoriteStrand(String color, Integer strand) {
        Integer result = spider.addFavoriteStrand(color, strand);

        if (result != null) {
            MessageHandler.showError("The new strand cannot be added", "already exist a strand with color: " + color);
        }
    }

    public void removeFavoriteStrand(String color) {
        Integer result = spider.removeFavoriteStrand(color);

        if (result == null) {
            MessageHandler.showError("Nothing was found to delete", "");
            return;
        }

        MessageHandler.showInfo("The Strand " + color + "was deleted");
    }

    public void finish() {
        Canvas canvas = Canvas.getCanvas();
        canvas.setVisible(false);

        MessageHandler.showInfo("The spider web is finished");

        System.exit(0);
    }
}
