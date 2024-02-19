import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public final class SpiderWeb {

    private final ArrayList<Line> strandLines;
    private final ArrayList<Bridge> bridges;
    private final Spider spider;

    private final int strands;
    private final int radio;

    private boolean lastActionWasOk;
    private boolean isVisible;
    private int currentStrand;

    public SpiderWeb(int strands, int radio) {
        this.strands = strands;
        this.radio = radio;
        this.currentStrand = -1;

        this.isVisible = false;

        this.spider = new Spider(new Point(Canvas.CENTER));
        this.strandLines = new ArrayList<>(this.strands);
        this.bridges = new ArrayList<>();

        this.generateStrandLines();
    }

    private ArrayList<Point> getShortestWay(int targetStrand) {
        bridges.sort((Bridge b1, Bridge b2) -> b2.getDistance() - b1.getDistance());

        ArrayList<Point> movementPoints = new ArrayList<>();
        boolean flag = true;
        int currentStrand = targetStrand;
        int currentDistance = this.radio;

        movementPoints.add(strandLines.get(targetStrand).getEnd());

        while (flag) {
            int candidates = 0;

            for (Bridge bridge : bridges) {
                if ((currentStrand != bridge.getInitialStrand() && currentStrand != bridge.getFinalStrand()) || currentDistance <= bridge.getDistance()) {
                    continue;
                }
                candidates++;

                if (currentStrand == bridge.getFinalStrand()) {
                    movementPoints.add(bridge.getFinalPoint());
                    movementPoints.add(bridge.getInitialPoint());
                    currentStrand = bridge.getInitialStrand();
                    currentDistance = bridge.getDistance();
                    break;
                } else {
                    movementPoints.add(bridge.getInitialPoint());
                    movementPoints.add(bridge.getFinalPoint());
                    currentStrand = bridge.getFinalStrand();
                    currentDistance = bridge.getDistance();
                }
            }

            flag = candidates != 0;
        }

        movementPoints.add(Canvas.CENTER);

        return movementPoints;
    }

    public void moveSpiderTo(int targetStrand) {

        if (targetStrand < 0 || targetStrand >= this.strands) {
            MessageHandler.showError("Invalid strand", "The strand " + targetStrand + " is not valid");
            lastActionWasOk = false;
            return;
        }

        if (this.currentStrand != -1) {
            MessageHandler.showInfo("The spider isn't on the center, please relocate the spider on the center");
            lastActionWasOk = false;
            return;
        }

        ArrayList<Point> movementPoints = this.getShortestWay(targetStrand);
        Collections.reverse(movementPoints);

        this.spider.moveTo(movementPoints);
        this.currentStrand = targetStrand;

        lastActionWasOk = true;
    }

    public void moveSpiderToCenter() {
        if (this.currentStrand == -1) {
            MessageHandler.showInfo("The spider is already on the center");
            lastActionWasOk = false;
            return;
        }

        ArrayList<Point> movementPoints = this.getShortestWay(this.currentStrand);

        this.spider.moveTo(movementPoints);
        this.currentStrand = -1;

        lastActionWasOk = true;
    }

    public void sitSpiderOnCenter() {
        if (this.currentStrand == -1) {
            MessageHandler.showInfo("The spider is already on the center");
            lastActionWasOk = false;
            return;
        }

        this.spider.setPosition(new Point(Canvas.CENTER));
        this.currentStrand = -1;

        lastActionWasOk = true;
    }

    private void generateStrandLines() {
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

        lastActionWasOk = true;
    }

    public void makeInvisible() {
        this.isVisible = false;
        Canvas canvas = Canvas.getCanvas();
        canvas.setVisible(false);

        lastActionWasOk = true;
    }

    private void draw() {
        if (this.isVisible) {
            this.strandLines.forEach(Line::draw);
            this.bridges.forEach(Bridge::draw);
            this.spider.draw();
        }
    }

    public void showWebInfo() {
        StringBuilder info = new StringBuilder();

        info.append(String.format("The spider is at the point [%s, %s]\n", this.spider.getPosition().x, this.spider.getPosition().y));
        info.append(String.format("The spider is at the strand %d\n", this.currentStrand));
        info.append(String.format("The spider web is %s\n", this.isVisible ? "visible" : "invisible"));
        info.append(String.format("The spider web has %d strands\n", this.strands));
        info.append(String.format("The spider web has a radio of %d\n", this.radio));
        info.append(String.format("The spider web has %d bridges\n", this.bridges.size()));

        for (int i = 0; i < this.bridges.size(); i++) {
            info.append(String.format("    + Bridge %d: %s\n", i + 1, this.bridges.get(i)));
        }

        info.append(String.format("The spider web has %d favorite strands\n", this.spider.getFavoriteStrands().size()));
        Set<String> favoriteStrands = this.spider.getFavoriteStrands().keySet();

        for (String color : favoriteStrands) {
            info.append(String.format("    + Favorite Strand (%s): %s\n", color, this.spider.getFavoriteStrands().get(color)));
        }

        MessageHandler.showInfo(info.toString());

        lastActionWasOk = true;
    }

    public void addBridge(String color, int distance, int firstStrand) {
        if (firstStrand < 0 || firstStrand >= this.strands) {
            MessageHandler.showError("Invalid strand", "The strand " + firstStrand + " is not valid");
            lastActionWasOk = false;
            return;
        }
        if (distance < 0 | distance > radio) {
            MessageHandler.showError("Invalid distance", "The distance " + distance + " is not valid");
            lastActionWasOk = false;
            return;
        }
        if (!this.isVisible) {
            MessageHandler.showError("Spider web is not visible yet", "The spider web is not visible yet");
            lastActionWasOk = false;
            return;
        }

        int finalStrand = firstStrand + 1;
        Point initialPoint = this.strandLines.get(firstStrand).getScaledPoint((double) distance / this.radio);
        Point finalPoint = this.strandLines.get(finalStrand).getScaledPoint((double) distance / this.radio);

        this.bridges.add(new Bridge(distance, firstStrand, finalStrand, initialPoint, finalPoint, color));

        this.draw();

        lastActionWasOk = true;
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
            lastActionWasOk = false;
            return;
        }

        this.addBridge(color, distance, targetBridge.getInitialStrand());

        lastActionWasOk = true;
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
            lastActionWasOk = false;
            return;
        }

        this.draw();

        lastActionWasOk = true;
    }

    public void addFavoriteStrand(String color, Integer strand) {
        Integer result = spider.addFavoriteStrand(color, strand);

        if (result != null) {
            MessageHandler.showError("The new strand cannot be added", "already exist a strand with color: " + color);

            lastActionWasOk = false;
            return;
        }

        this.strandLines.get(strand).setColor(color);

        lastActionWasOk = false;
    }

    public void removeFavoriteStrand(String color) {
        Integer result = spider.removeFavoriteStrand(color);

        if (result == null) {
            MessageHandler.showError("Nothing was found to delete", "");
            lastActionWasOk = false;
            return;
        }

        this.strandLines.get(result).setColor("gray");
        MessageHandler.showInfo("The Strand " + color + " was deleted");

        lastActionWasOk = true;
    }

    public boolean lastActionWasOk() {
        return lastActionWasOk;
    }

    public void finish() {
        Canvas canvas = Canvas.getCanvas();
        canvas.setVisible(false);

        MessageHandler.showInfo("The spider web is finished");
        lastActionWasOk = true;

        System.exit(0);
    }
}
