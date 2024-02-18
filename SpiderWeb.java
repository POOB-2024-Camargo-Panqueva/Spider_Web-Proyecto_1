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

    private void draw() {
        if (this.isVisible) {
            this.strandLines.forEach(Line::draw);
            this.bridges.forEach(Bridge::draw);
            this.spider.draw();
        }

        System.out.println(this.bridges.size() + " bridges");
    }

    public void addBridge(String color, int distance, int firstStrand) throws IllegalArgumentException, IllegalStateException {
        if (firstStrand < 0 || firstStrand >= this.strands) {
            throw new IllegalArgumentException("Invalid first strand");
        }
        if (distance < 0 | distance > radio) {
            throw new IllegalArgumentException("Invalid distance");
        }
        if (!this.isVisible) {
            throw new IllegalStateException("Spider web is not visible yet");
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
            //TODO: throw exception and show message with JOptionPane
            System.out.println("Bridge not found");
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
            //TODO: throw exception and show message with JOptionPane
            System.out.println("Bridge not found");
            return;
        }

        this.draw();
    }
}
