import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public final class SpiderWeb {

    public static boolean TEST_MODE = false;

    private final ArrayList<Line> strandLines;
    private final ArrayList<Bridge> bridges;
    private final ArrayList<Bridge> usedBridges = new ArrayList<Bridge>();
//    HashMap<Integer, ArrayList<Bridge>> bridgesByStrands = new HashMap<>();
    private final Spider spider;

    private boolean lastActionWasOk;
    private boolean isVisible;
    private int currentStrand;
    private int strands;
    private int radio;

    /**
     * Constructs a SpiderWeb with the specified number of strands and radio.
     *
     * @param strands The number of strands in the spider web.
     * @param radio   The radius of the spider web.
     */
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

    /**
     * Constructs a SpiderWeb with the specified number of strands, favorite strands, and bridges.
     *
     * @param strands         The number of strands in the spider web.
     * @param favoriteStrands The number of favorite strands in the spider web.
     * @param bridges         The bridges in the spider web.
     * @throws Exception If the input is invalid.
     */
    public SpiderWeb(int strands, int favoriteStrands, int[][] bridges) throws Exception {

        ArrayList<Bridge> temporalBridges = new ArrayList<>();
        int radio = 0;

        for (int[] bridge : bridges) {
            if (bridge.length != 2) {
                MessageHandler.showFatalError("Enter a valid input! in the form: 'd t'");
            }

            int radioBridge = bridge[0];
            int initialStrand = bridge[1];

            if (initialStrand < 0 || initialStrand >= strands) {
                MessageHandler.showFatalError("The bridge must be built on a valid strand");
            }

            if (radioBridge < 1 || radioBridge > 1_000_000_000) {
                MessageHandler.showFatalError("Invalid Range for 'd' radio bridge");
            }

            if (radioBridge > radio) {
                radio = radioBridge;
            }

            String bridgeColor = String.format("%s-%s", initialStrand, radioBridge);
            temporalBridges.add(new Bridge(radioBridge, initialStrand, initialStrand + 1, null, null, bridgeColor));

            //TODO: here verify that all radio bridges are different
        }

        final int STRAND_PADDING = 20;

        this.strands = strands;
        this.radio = radio + STRAND_PADDING;
        this.currentStrand = -1;

        this.isVisible = false;

        this.spider = new Spider(new Point(Canvas.CENTER));
        this.strandLines = new ArrayList<>(this.strands);
        this.generateStrandLines();

        this.bridges = new ArrayList<>();

        for (Bridge bridge : temporalBridges) {
            this.addBridge(bridge.getColor(), bridge.getDistance(), bridge.getInitialStrand());
        }
    }

    /**
     * Gets the shortest way to the target strand, considering bridges.
     *
     * @param targetStrand The target strand to which the spider will move.
     * @return ArrayList of Points representing the movement points.
     */
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
                    usedBridges.add(bridge);
                    currentStrand = bridge.getInitialStrand();
                    currentDistance = bridge.getDistance();
                    break;
                } else {
                    movementPoints.add(bridge.getInitialPoint());
                    movementPoints.add(bridge.getFinalPoint());
                    usedBridges.add(bridge);
                    currentStrand = bridge.getFinalStrand();
                    currentDistance = bridge.getDistance();
                }
            }

            flag = candidates != 0;
        }

        movementPoints.add(Canvas.CENTER);

        return movementPoints;
    }

    /**
     * Moves the spider to the specified strand.
     *
     * @param targetStrand The target strand to which the spider will move.
     */
    public void moveSpiderTo(int targetStrand) {

        this.spider.resetTraceLines();

        if (targetStrand < 0 || targetStrand >= this.strands) {

            if (isVisible)
                MessageHandler.showError("Invalid strand", "The strand " + targetStrand + " is not valid");
            lastActionWasOk = false;
            return;
        }

        if (this.currentStrand != -1) {

            if (isVisible)
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

    /**
     * Adds a strand to the spider web.
     */
    public void addStrand() {
        this.strands++;

        this.generateStrandLines();

        if (this.currentStrand != -1)
            this.spider.setPosition(new Point(this.strandLines.get(currentStrand).getEnd()));

        ArrayList<Bridge> temporalBridges = new ArrayList<>();
        ArrayList<Bridge> bridgesClone = new ArrayList<>(this.bridges);

        for (Bridge bridge : bridgesClone) {
            temporalBridges.add(new Bridge(bridge.getDistance(), bridge.getInitialStrand(), bridge.getFinalStrand(), null, null, bridge.getColor()));

            bridge.erase();
            this.bridges.remove(bridge);
        }

        for (Bridge bridge : temporalBridges) {
            this.addBridge(bridge.getColor(), bridge.getDistance(), bridge.getInitialStrand());
        }
        this.draw();

        lastActionWasOk = true;
    }

    /**
     * Expands the radio of the spider web by the specified amount.
     *
     * @param radio The amount by which to expand the radio of the spider web.
     */
    public void expandRadio(int radio) {

        if (radio < 0 || radio > 1_000_000_000) {
            MessageHandler.showError("Invalid Range for 'd' radio bridge");

            lastActionWasOk = false;
            return;
        }

        this.radio += radio;
        this.generateStrandLines();

        this.draw();

        if (this.currentStrand != -1)
            this.spider.setPosition(new Point(this.strandLines.get(currentStrand).getEnd()));

        lastActionWasOk = true;
    }

    /**
     * Moves the spider back to the center of the spider web.
     */
    public void moveSpiderToCenter() {

        this.spider.resetTraceLines();

        if (this.currentStrand == -1) {

            if (isVisible)
                MessageHandler.showInfo("The spider is already on the center");

            lastActionWasOk = false;
            return;
        }

        ArrayList<Point> movementPoints = this.getShortestWay(this.currentStrand);

        this.spider.moveTo(movementPoints);
        this.currentStrand = -1;

        lastActionWasOk = true;
    }

    /**
     * Sits the spider in the center of the spider web.
     */
    public void sitSpiderOnCenter() {

        this.spider.resetTraceLines();

        if (this.currentStrand == -1) {

            if (isVisible)
                MessageHandler.showInfo("The spider is already on the center");

            lastActionWasOk = true;
            return;
        }

        this.spider.setPosition(new Point(Canvas.CENTER));
        this.currentStrand = -1;

        lastActionWasOk = true;
    }

    /**
     * Generates strand lines based on the number of strands.
     */
    private void generateStrandLines() {
        for (Line line : this.strandLines) {
            line.erase();
        }

        this.strandLines.clear();

        for (int index = this.strands; index >= 0; index--) {
            double angle = Math.toRadians((double) 360 / this.strands * index);
            int x = (int) (this.radio * Math.cos(angle));
            int y = (int) (this.radio * Math.sin(angle));
            this.strandLines.add(new Line(new Point(Canvas.CENTER), new Point(Canvas.CENTER.x + x, Canvas.CENTER.y + y)));
        }
    }

    /**
     * Makes the spider web visible.
     */
    public void makeVisible() {
        this.isVisible = true;
        this.draw();

        lastActionWasOk = true;
    }

    /**
     * Makes the spider web invisible.
     */
    public void makeInvisible() {
        this.isVisible = false;
        Canvas canvas = Canvas.getCanvas();
        canvas.setVisible(false);

        lastActionWasOk = true;
    }

    /**
     * Draws the spider web, including strands, bridges, and the spider.
     */
    private void draw() {
        if (this.isVisible) {
            this.strandLines.forEach(Line::draw);
            this.bridges.forEach(Bridge::draw);
            this.spider.draw();
        }
    }

    /**
     * Displays information about the spider web using a pop-up message.
     */
    public void printWebInfo() {
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

    /**
     * Adds a bridge to the spider web with the specified color, distance, and initial strand.
     *
     * @param color         The color of the bridge.
     * @param distance      The distance of the bridge.
     * @param initialStrand The initial strand of the bridge.
     */
    public void addBridge(String color, int distance, int initialStrand) {

        int finalStrand = initialStrand + 1;

        if (initialStrand < 0 || initialStrand >= this.strands) {

            if (isVisible)
                MessageHandler.showError("Invalid strand", "The strand " + initialStrand + " is not valid");

            lastActionWasOk = false;
            return;
        }
        if (distance < 0 || distance > radio) {

            if (isVisible)
                MessageHandler.showError("Invalid distance", "The distance " + distance + " is not valid");

            lastActionWasOk = false;
            return;
        }
        if (this.bridges.stream().anyMatch(bridge -> bridge.getColor().equals(color))) {

            if (isVisible)
                MessageHandler.showError("The bridge already exists", "The bridge with color " + color + " already exists");

            lastActionWasOk = false;
            return;
        }

        boolean inConflict = this.bridges.stream().anyMatch(bridge -> bridge.getDistance() == distance && (
                (bridge.getInitialStrand() == initialStrand ||
                        bridge.getFinalStrand() == finalStrand) ||
                        bridge.getInitialStrand() == finalStrand ||
                        bridge.getFinalStrand() == initialStrand));

        if (inConflict) {

            if (isVisible)
                MessageHandler.showError("Bridge in conflict", "Can't create two bridges with the same distance on adjacent strands");

            lastActionWasOk = false;
            return;
        }


        Point initialPoint = this.strandLines.get(initialStrand).getScaledPoint((double) distance / this.radio);
        Point finalPoint = this.strandLines.get(finalStrand).getScaledPoint((double) distance / this.radio);

        this.bridges.add(new Bridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color));

        this.draw();

        lastActionWasOk = true;
    }

    /**
     * Relocates a bridge with the specified color and updates its distance.
     *
     * @param color    The color of the bridge to relocate.
     * @param distance The new distance of the bridge.
     */
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

            if (isVisible)
                MessageHandler.showError("Bridge not found", "The bridge with color: " + color + " was not found");

            lastActionWasOk = false;
            return;
        }

        this.addBridge(String.format("%s-%s", targetBridge.getInitialStrand(), distance), distance, targetBridge.getInitialStrand());

        lastActionWasOk = true;
    }

    /**
     * Removes a bridge with the specified color from the spider web.
     *
     * @param color The color of the bridge to remove.
     */
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

            if (isVisible)
                MessageHandler.showError("Bridge not found", "The bridge with color: " + color + " was not found");

            lastActionWasOk = false;
            return;
        }

        this.draw();

        lastActionWasOk = true;
    }

    /**
     * Adds a favorite strand to the spider associated with the specified color and strand.
     *
     * @param color  The color of the favorite strand.
     * @param strand The value of the favorite strand.
     */
    public void addFavoriteStrand(String color, Integer strand) {
        Integer result = spider.addFavoriteStrand(color, strand);

        if (result != null) {

            if (isVisible)
                MessageHandler.showError("The new strand cannot be added", "already exist a strand with color: " + color);

            lastActionWasOk = false;
            return;
        }

        this.strandLines.get(strand).setColor(color);

        lastActionWasOk = true;
    }

    /**
     * Removes a favorite strand from the spider based on the specified color.
     *
     * @param color The color of the favorite strand to remove.
     */
    public void removeFavoriteStrand(String color) {
        Integer result = spider.removeFavoriteStrand(color);

        if (result == null) {

            if (isVisible)
                MessageHandler.showError("Nothing was found to delete", "");

            lastActionWasOk = false;
            return;
        }

        this.strandLines.get(result).setColor("gray");
        MessageHandler.showInfo("The Strand " + color + " was deleted");

        lastActionWasOk = true;
    }

    public void printFavoriteStrands() {
        StringBuilder info = new StringBuilder();

        info.append("The spider has the following favorite strands:\n");

        Set<String> favoriteStrands = this.spider.getFavoriteStrands().keySet();

        for (String color : favoriteStrands) {
            info.append(String.format("    + Favorite Strand (%s): %s\n", color, this.spider.getFavoriteStrands().get(color)));
        }

        MessageHandler.showInfo(info.toString());

        lastActionWasOk = true;
    }

    public void resetUsedBridges(){
        usedBridges .clear();
    }

    /**
     * Finishes the spider web application, hides the canvas, and shows a closing message.
     */
    public void finish() {
        Canvas canvas = Canvas.getCanvas();
        canvas.setVisible(false);

        MessageHandler.showInfo("The spider web is finished");
        lastActionWasOk = true;

        System.exit(0);
    }

    public boolean lastActionWasOk() {
        return lastActionWasOk;
    }

    public ArrayList<Line> getStrandLines() {
        return strandLines;
    }

    public ArrayList<Bridge> getBridges() {
        return bridges;
    }

    public Spider getSpider() {
        return spider;
    }

    public int getStrands() {
        return strands;
    }

    public int getRadio() {
        return radio;
    }

    public boolean isLastActionWasOk() {
        return lastActionWasOk;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int getCurrentStrand() {
        return currentStrand;
    }

    public ArrayList<Bridge> getUsedBridges() {
        Collections.reverse(usedBridges);
        return usedBridges;
    }

    //    public void simulate(int initialStrand, int finalStrand) {
//        bridges.sort((Bridge b1, Bridge b2) -> b2.getDistance() - b1.getDistance());
//        HashMap<Integer, ArrayList<Bridge>> bridgesByStrands = new HashMap<>();
//
//        for (int i = 0; i < this.strands; i++) {
//            bridgesByStrands.put(i, new ArrayList<>());
//        }
//
//        for (Bridge bridge : bridges) {
//            bridgesByStrands.get(bridge.getInitialStrand() - 1).add(bridge);
//            bridgesByStrands.get(bridge.getFinalStrand() - 1).add(bridge);
//        }
//
//        int currentStrand = finalStrand;
//        int clockwiseNeighbor = -1;
//        int counterclockwiseNeighbor = -1;
//        int currentRadio = this.radio;
//        ArrayList<Point> movementPoints = new ArrayList<>();
//
//        movementPoints.add(strandLines.get(finalStrand).getEnd());
//
//        while (true) {
//            clockwiseNeighbor = currentStrand - 1;
//            counterclockwiseNeighbor = currentStrand + 1;
//            if (currentStrand == 1) {
//                clockwiseNeighbor = strands;
//            } else if (currentStrand == strands) {
//                counterclockwiseNeighbor = 1;
//            }
//        }
//    }
//
//    public void sortBridges() {
//        bridges.sort((Bridge b1, Bridge b2) -> b2.getDistance() - b1.getDistance());
//    }
//
//    public void createBridgesHashmap() {
//        for (int i = 0; i < this.strands; i++) {
//            bridgesByStrands.put(i, new ArrayList<>());
//        }
//
//        for (Bridge bridge : bridges) {
//            bridgesByStrands.get(bridge.getInitialStrand() - 1).add(bridge);
//            bridgesByStrands.get(bridge.getFinalStrand() - 1).add(bridge);
//        }
//    }
//
//    public ArrayList<Integer> solution = new ArrayList<Integer>();
//
//    public void findSolution() {
//        //TODO: we need to save the solution in spiderweb
//
//    }
//
//    private ArrayList<Bridge> pathMaker(int currStrand, int iRadio, ArrayList<Bridge> record) {
//
//        if (record.size() > solution.get(currStrand)) {
//            return new ArrayList<Bridge>();
//        }
//
//        int clockwiseNeighbor = currStrand - 1;
//        int counterclockwiseNeighbor = currStrand + 1;
//        if (currStrand == 1) {
//            clockwiseNeighbor = strands;
//        } else if (currStrand == strands) {
//            counterclockwiseNeighbor = 1;
//        }
//
//        int limitZone = findNextZone(iRadio, bridgesByStrands.get(clockwiseNeighbor), bridgesByStrands.get(counterclockwiseNeighbor));
//
//        ArrayList<Bridge> straight = pathMaker(currStrand, limitZone, record);
//        ArrayList<Bridge> CounterClock = pathMaker(counterclockwiseNeighbor, limitZone, record);
//
//        return null;
//
//    }
//
//    public int findNextZone(int iRadio, ArrayList<Bridge> neighborC, ArrayList<Bridge> neighborCc) {
//        Optional<Bridge> endZone = neighborC.stream().filter(candidate -> candidate.getDistance() < iRadio).findFirst();
//        int zoneValue = -1;
//        if (endZone.isPresent()) {
//            zoneValue = endZone.get().getDistance();
//        }
//        endZone = neighborCc.stream().filter(candidate -> candidate.getDistance() < iRadio).findFirst();
//        if (endZone.isPresent() && zoneValue < endZone.get().getDistance()) {
//            zoneValue = endZone.get().getDistance();
//        }
//
//        return zoneValue;
//    }
//
//    public int findAMovement(int start, int limit, int currStrand, ArrayList<Bridge> searchHere) {
//        Optional<Bridge> nextWay = searchHere.stream()
//                    .filter(candidate -> (candidate.getDistance() > limit && candidate.getDistance() < start) && candidate.getInitialStrand() == currStrand).findFirst();
//        int nextStrand = -1;
//        if (nextWay.isPresent()) {
//            nextStrand = nextWay.get().getFinalStrand();
//        }
//
//        if (nextStrand == -1){
//            return currStrand;
//        }
//
//        return nextStrand;
//    }

}
