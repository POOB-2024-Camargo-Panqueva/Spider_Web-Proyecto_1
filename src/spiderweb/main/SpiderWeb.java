package spiderweb.main;

import shape.Canvas;
import spiderweb.bridges.*;
import spiderweb.spider.Spider;
import spiderweb.strands.Strand;
import utilities.MessageHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

public final class SpiderWeb {

    public static boolean TEST_MODE = false;

    private final ArrayList<Strand> strands;
    private final ArrayList<Bridge> bridges;
    private final ArrayList<Bridge> usedBridges;
    private final Spider spider;

    private boolean lastActionWasOk;
    private boolean isVisible;
    private int currentStrand;
    private int strandCount;
    private int radio;

    /**
     * Constructs a SpiderWeb with the specified number of strands and radio.
     *
     * @param strands The number of strands in the spider web.
     * @param radio   The radius of the spider web.
     */
    public SpiderWeb(int strands, int radio) {
        this.strandCount = strands;
        this.radio = radio;
        this.currentStrand = -1;

        this.isVisible = false;

        this.spider = new Spider(new Point(Canvas.CENTER));
        this.strands = new ArrayList<>(this.strandCount);
        this.bridges = new ArrayList<>();
        this.usedBridges = new ArrayList<>();

        this.generateStrandLines();
    }

    /**
     * Constructs a SpiderWeb with the specified number of strands, favorite strands, and bridges.
     *
     * @param strands        The number of strands in the spider web.
     * @param favoriteStrand The number of favorite strands in the spider web.
     * @param bridges        The bridges in the spider web.
     * @throws Exception If the input is invalid.
     */
    public SpiderWeb(int strands, int favoriteStrand, int[][] bridges) throws Exception {

        // TODO: Add favorite strand logic

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
            temporalBridges.add(new NormalBridge(radioBridge, initialStrand, initialStrand + 1, null, null, bridgeColor));

            //TODO: All Bridge Created by This Constructor Will Be Normals
        }

        final int STRAND_PADDING = 20;

        this.strandCount = strands;
        this.radio = radio + STRAND_PADDING;
        this.currentStrand = -1;

        this.isVisible = false;

        this.spider = new Spider(new Point(Canvas.CENTER));
        this.spider.addFavoriteStrand("default", favoriteStrand);
        this.strands = new ArrayList<>(this.strandCount);
        this.generateStrandLines();

        this.bridges = new ArrayList<>();
        this.usedBridges = new ArrayList<>();

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
    public void moveSpiderTo(int targetStrand) {

        this.spider.resetTraceLines();

        if (targetStrand < 0 || targetStrand >= this.strandCount) {

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

        ArrayList<Bridge> bridges = new ArrayList<>(this.bridges);
        bridges.sort(Comparator.comparingInt(Bridge::getDistance));

        boolean flag = true;
        int currentStrand = targetStrand;
        int currentDistance = 0;
        boolean isFinalMovement = false;

        while (flag) {
            int candidates = 0;

            for (Bridge bridge : bridges) {
                if ((currentStrand != bridge.getInitialStrand() && currentStrand != bridge.getFinalStrand()) || currentDistance >= bridge.getDistance()) {
                    continue;
                }
                candidates++;

                if (currentStrand == bridge.getFinalStrand()) {
                    currentStrand = bridge.getInitialStrand();
                    this.spider.moveTo(bridge.getFinalPoint());
                    this.spider.moveTo(bridge.getInitialPoint());
                } else {
                    currentStrand = bridge.getFinalStrand();
                    this.spider.moveTo(bridge.getInitialPoint());
                    this.spider.moveTo(bridge.getFinalPoint());
                }

                bridge.triggerAction(this);

                currentDistance = bridge.getDistance();
                usedBridges.add(bridge);

                break;
            }

            flag = candidates != 0;
        }

        this.spider.moveTo(strands.get(currentStrand).getEnd());

        this.currentStrand = targetStrand;

    }

    /**
     * Adds a strand to the spider web.
     */
    public void addStrand() {
        this.strandCount++;

        this.generateStrandLines();

        if (this.currentStrand != -1)
            this.spider.setPosition(new Point(this.strands.get(currentStrand).getEnd()));

        ArrayList<Bridge> temporalBridges = new ArrayList<>();
        ArrayList<Bridge> bridgesClone = new ArrayList<>(this.bridges);

        for (Bridge bridge : bridgesClone) {
            temporalBridges.add(bridge.copy());

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
            this.spider.setPosition(new Point(this.strands.get(currentStrand).getEnd()));

        lastActionWasOk = true;
    }

    /**
     * Sits the spider in the center of the spider web.
     */
    public void sitSpiderOnCenter() {

        // TODO: Animate sit action
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
        for (Strand strand : this.strands) {
            strand.erase();
        }

        this.strands.clear();

        for (int index = this.strandCount; index >= 0; index--) {
            double angle = Math.toRadians((double) 360 / this.strandCount * index);
            int x = (int) (this.radio * Math.cos(angle));
            int y = (int) (this.radio * Math.sin(angle));
            this.strands.add(new Strand(new Point(Canvas.CENTER), new Point(Canvas.CENTER.x + x, Canvas.CENTER.y + y)));
        }
    }

    /**
     * Makes the spider web visible.
     */
    public void makeVisible() {
        this.isVisible = true;
        this.spider.makeVisible();
        this.draw();

        lastActionWasOk = true;
    }

    /**
     * Makes the spider web invisible.
     */
    public void makeInvisible() {
        this.isVisible = false;
        this.spider.makeInvisible();
        Canvas canvas = Canvas.getCanvas();
        canvas.setVisible(false);
        // TODO: Make invisible each element on the canvas, but not the canvas itself

        lastActionWasOk = true;
    }

    /**
     * Draws the spider web, including strands, bridges, and the spider.
     */
    private void draw() {
        if (this.isVisible) {
            this.strands.forEach(Strand::draw);
            this.bridges.forEach(Bridge::draw);
            this.spider.draw();
        }
    }

    /**
     * Displays information about the spider web using a pop-up message.
     */
    public void printWebInfo() {
        // TODO: Create individual methods for each type of information
        StringBuilder info = new StringBuilder();

        info.append(String.format("The spider is at the point [%s, %s]\n", this.spider.getPosition().x, this.spider.getPosition().y));
        info.append(String.format("The spider is at the strand %d\n", this.currentStrand));
        info.append(String.format("The spider web is %s\n", this.isVisible ? "visible" : "invisible"));
        info.append(String.format("The spider web has %d strands\n", this.strandCount));
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
     * Checks if the bridge is invalid based on the specified color, distance, initial strand, and final strand.
     *
     * @param color         The color of the bridge
     * @param distance      The distance of the bridge
     * @param initialStrand The initial strand of the bridge
     * @param finalStrand   The final strand of the bridge
     * @return True if the bridge is invalid, otherwise false
     */
    private boolean isInvalidBridge(String color, int distance, int initialStrand, int finalStrand) {
        if (initialStrand < 0 || initialStrand >= this.strandCount) {

            if (isVisible)
                MessageHandler.showError("Invalid strand", "The strand " + initialStrand + " is not valid");

            return true;
        }
        if (distance < 0 || distance > radio) {

            if (isVisible)
                MessageHandler.showError("Invalid distance", "The distance " + distance + " is not valid");

            return true;
        }
        if (this.bridges.stream().anyMatch(bridge -> bridge.getColor().equals(color))) {

            if (isVisible)
                MessageHandler.showError("The bridge already exists", "The bridge with color " + color + " already exists");

            return true;
        }

        boolean inConflict = this.bridges.stream().anyMatch(bridge -> bridge.getDistance() == distance && (
                (bridge.getInitialStrand() == initialStrand ||
                        bridge.getFinalStrand() == finalStrand) ||
                        bridge.getInitialStrand() == finalStrand ||
                        bridge.getFinalStrand() == initialStrand));

        if (inConflict) {

            if (isVisible)
                MessageHandler.showError("Bridge in conflict", "Can't create two bridges with the same distance on adjacent strands");

            return true;
        }

        return false;
    }

    /**
     * Adds a bridge to the spider web with the specified color, distance, initial strand and type.
     *
     * @param color         The color of the bridge.
     * @param distance      The distance of the bridge.
     * @param initialStrand The initial strand of the bridge.
     * @param type          The type of the bridge.
     */
    public void addBridge(String color, int distance, int initialStrand, Bridge.Types type) {

        int finalStrand = initialStrand == this.strandCount - 1 ? 0 : initialStrand + 1;

        if (this.isInvalidBridge(color, distance, initialStrand, finalStrand)) {
            lastActionWasOk = false;
            return;
        }

        Point initialPoint = this.strands.get(initialStrand).getScaledPoint((double) distance / this.radio);
        Point finalPoint = this.strands.get(finalStrand).getScaledPoint((double) distance / this.radio);

        switch (type) {
            case NORMAL:
                this.bridges.add(new NormalBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color));
                break;
            case FIXED:
                this.bridges.add(new FixedBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color));
                break;
            case TRANSFORMER:
                this.bridges.add(new TransformerBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color));
                break;
            case WEAK:
                this.bridges.add(new WeakBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color));
                break;
            case MOBILE:
                this.bridges.add(new MobileBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color));
                break;
            default:
                // Manejo de un tipo desconocido
                break;
        }

        this.draw();

        this.lastActionWasOk = true;

    }

    /**
     * Adds a bridge to the spider web with the specified color, distance, and initial strand.
     *
     * @param color         The color of the bridge.
     * @param distance      The distance of the bridge.
     * @param initialStrand The initial strand of the bridge.
     */
    public void addBridge(String color, int distance, int initialStrand) {
        this.addBridge(color, distance, initialStrand, Bridge.Types.NORMAL);
    }

    /**
     * Relocates a bridge with the specified color and updates its distance.
     *
     * @param color    The color of the bridge to relocate.
     * @param distance The new distance of the bridge.
     */
    public void relocateBridge(String color, int distance) {
        Bridge targetBridge = this.removeBridge(color);

        if (targetBridge == null) {
            lastActionWasOk = false;
            return;
        }

        String bridgeColorId = String.format("%s-%s", targetBridge.getInitialStrand(), distance);

        this.addBridge(bridgeColorId, distance, targetBridge.getInitialStrand());

        lastActionWasOk = true;
    }

    /**
     * Removes a bridge with the specified color from the spider web.
     *
     * @param color The color of the bridge to remove.
     * @return The removed bridge, or null if no bridge was removed.
     */
    public Bridge removeBridge(String color) {
        Bridge targetBridge = null;

        for (int i = 0; i < this.bridges.size(); i++) {
            if (this.bridges.get(i).getColor().equals(color)) {
                if (this.bridges.get(i) instanceof FixedBridge) {
                    MessageHandler.showError("You cannot delete a 'Fixed' Bridge");
                    break;
                }

                targetBridge = this.bridges.remove(i);
                targetBridge.erase();
                break;
            }
        }

        if (targetBridge == null) {

            if (isVisible)
                MessageHandler.showError("Bridge not found", "The bridge with color: " + color + " was not found");

            lastActionWasOk = false;
            return null;
        }

        this.draw();
        lastActionWasOk = true;

        return targetBridge;
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

        this.strands.get(strand).setColor(color);

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
                MessageHandler.showError("Favorite strand not found", "The favorite strand with color: " + color + " was not found");

            lastActionWasOk = false;
            return;
        }

        this.strands.get(result).setColor("gray");
        MessageHandler.showInfo("The Strand " + color + " was deleted");

        lastActionWasOk = true;
    }

    /**
     * Prints the favorite strands of the spider.
     */
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

    /**
     * Clears the bridges used by the spider.
     */
    public void resetUsedBridges() {
        usedBridges.clear();
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

    public ArrayList<Strand> getStrands() {
        return strands;
    }

    public ArrayList<Bridge> getBridges() {
        return bridges;
    }

    public int[][] getBridgesAsConsoleInput() {
        int[][] bridges = new int[this.bridges.size()][2];
        for (int i = 0; i < this.bridges.size(); i++) {
            bridges[i][0] = this.bridges.get(i).getDistance();
            bridges[i][1] = this.bridges.get(i).getInitialStrand();
        }
        return bridges;
    }

    public ArrayList<Bridge> getUsedBridges() {
        return usedBridges;
    }

    public Spider getSpider() {
        return spider;
    }

    public int getStrandCount() {
        return strandCount;
    }

    public int getFavoriteStrand() {
        return spider.getFavoriteStrands().get("default");
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
}
