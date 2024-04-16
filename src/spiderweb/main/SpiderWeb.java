package spiderweb.main;

import shape.Canvas;
import spiderweb.bridges.*;
import spiderweb.spider.Spider;
import spiderweb.strands.BouncyStrand;
import spiderweb.strands.KillerStrand;
import spiderweb.strands.NormalStrand;
import spiderweb.strands.Strand;
import utilities.MessageHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class SpiderWeb {

    public static boolean TEST_MODE = false;

    private final ArrayList<Strand> strands;
    private final ArrayList<Bridge> bridges;
    private final ArrayList<Bridge> usedBridges;
    private final Spider spider;
    private int favoriteStrand;

    private boolean lastActionWasOk;
    private boolean isVisible;
    private int currentStrand;
    private int strandCount;
    private int radio;
    private int currentDistance;

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
        this.favoriteStrand = -1;
        this.currentDistance = 0;

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
        this.currentDistance = 0;

        this.isVisible = false;

        this.spider = new Spider(new Point(Canvas.CENTER));
        this.strands = new ArrayList<>(this.strandCount);
        this.generateStrandLines();
        this.addFavoriteStrand(favoriteStrand);

        this.bridges = new ArrayList<>();
        this.usedBridges = new ArrayList<>();

        for (Bridge bridge : temporalBridges) {
            this.addBridge(bridge.getColor(), bridge.getDistance(), bridge.getInitialStrand());
        }
    }

    /**
     * Validates if the spider can move to the target strand.
     * Checks if the spider is alive, if the target strand is valid,
     *
     * @param targetStrand The target strand to which the spider intends to move.
     * @return True if the movement is valid, otherwise false.
     */
    private Boolean validateMovement(int targetStrand, boolean moveToCenter) {
        if (!this.spider.isAlive()) {
            MessageHandler.showError("The spider is dead", "The spider can't move, respawn the spider first.");
            return false;
        }

        this.spider.resetTraceLines();

        if (targetStrand < 0 || targetStrand >= this.strandCount) {

            if (isVisible)
                MessageHandler.showError("Invalid strand", "The strand " + targetStrand + " is not valid");
            return false;
        }

        if (this.currentStrand != -1 && !moveToCenter) {

            if (isVisible)
                MessageHandler.showInfo("The spider isn't on the center, please relocate the spider on the center");
            return false;
        }

        return true;
    }

    /**
     * Gets the shortest way to the target strand, considering bridges.
     *
     * @param targetStrand The target strand to which the spider will move.
     */
    private void moveSpider(int targetStrand, boolean moveToCenter) {

        if (!this.validateMovement(targetStrand, moveToCenter)) {
            this.lastActionWasOk = false;
            return;
        }

        bridges.sort(Comparator.comparingInt(Bridge::getDistance));
        this.currentDistance = 0;

        if(moveToCenter){
            this.bridges.sort((Bridge b1, Bridge b2) -> b2.getDistance() - b1.getDistance());
            this.currentDistance = this.radio;
        }

        this.currentStrand = targetStrand;

        boolean flag = true;
        this.strands.get(currentStrand).triggerAction(this);

        while (flag) {
            if (!spider.isAlive()) {
                break;
            }
            int candidates = 0;

            for (int i = 0; i < bridges.size(); i++) {

                Bridge bridge = bridges.get(i);

                if(moveToCenter){
                    if ((currentStrand != bridge.getInitialStrand() && currentStrand != bridge.getFinalStrand()) || this.currentDistance <= bridge.getDistance()) {
                        continue;
                    }
                }
                else{
                    if ((currentStrand != bridge.getInitialStrand() && currentStrand != bridge.getFinalStrand()) || this.currentDistance >= bridge.getDistance()) {
                        continue;
                    }
                }

                candidates++;

                if (currentStrand == bridge.getFinalStrand()) {
                    this.currentStrand = bridge.getInitialStrand();
                    this.spider.moveTo(bridge.getFinalPoint());
                    this.spider.moveTo(bridge.getInitialPoint());
                    this.currentStrand = bridge.getInitialStrand();
                } else {
                    this.currentStrand = bridge.getFinalStrand();
                    this.spider.moveTo(bridge.getInitialPoint());
                    this.spider.moveTo(bridge.getFinalPoint());
                    this.currentStrand = bridge.getFinalStrand();
                }
                this.strands.get(currentStrand).triggerAction(this);

                bridge.triggerAction(this);

                if (moveToCenter){
                    this.bridges.sort((Bridge b1, Bridge b2) -> b2.getDistance() - b1.getDistance());
                }
                else {
                    bridges.sort(Comparator.comparingInt(Bridge::getDistance));
                }

                this.currentDistance = bridge.getDistance();
                usedBridges.add(bridge);

                break;
            }

            if (!spider.isAlive()) {
                break;
            }

            flag = candidates != 0;
        }

        if (spider.isAlive()) {
            if(moveToCenter){
                this.spider.moveTo(Canvas.CENTER);
                this.currentStrand = -1;
                this.currentDistance = 0;
                return;
            }
            this.spider.moveTo(strands.get(currentStrand).getEnd());
            this.currentDistance = this.radio;
        }

    }

    /**
     * Moves the spider to the specified target strand by finding the initial way and then moving the spider.
     *
     * @param targetStrand The target strand to which the spider will move.
     */
    public void moveSpiderTo(int targetStrand) {
        this.moveSpider(this.findInitialWay(targetStrand), false);
    }

    /**
     * Moves the spider from the specified target strand by directly moving the spider.
     * This method is used when the spider is already positioned on the target strand.
     *
     * @param targetStrand The target strand from which the spider will move.
     */
    public void moveSpiderFrom(int targetStrand) {
        moveSpider(targetStrand, false);
    }

    public void moveSpiderToCenter(){
        moveSpider(this.currentStrand, true);
    }

    private int findInitialWay(int targetStrand) {

        ArrayList<Bridge> bridges = new ArrayList<>(this.bridges);

        bridges.sort((Bridge b1, Bridge b2) -> b2.getDistance() - b1.getDistance());

        boolean flag = true;
        int currentStrand = targetStrand;
        int currentDistance = this.radio;

        while (flag) {
            int candidates = 0;
            for (Bridge bridge : bridges) {
                if ((currentStrand != bridge.getInitialStrand() && currentStrand != bridge.getFinalStrand()) || currentDistance <= bridge.getDistance()) {
                    continue;
                }
                candidates++;

                if (currentStrand == bridge.getFinalStrand()) {
                    currentStrand = bridge.getInitialStrand();

                } else {
                    currentStrand = bridge.getFinalStrand();
                }
                currentDistance = bridge.getDistance();
                break;
            }
            flag = candidates != 0;
        }

        return currentStrand;
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

        this.spider.resetTraceLines();

        if (this.currentStrand == -1) {

            if (isVisible)
                MessageHandler.showInfo("The spider is already on the center");

            lastActionWasOk = true;
            return;
        }

        this.spider.setPosition(new Point(Canvas.CENTER));
        this.currentStrand = -1;
        this.currentDistance = 0;

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
            this.strands.add(new NormalStrand(new Point(Canvas.CENTER), new Point(Canvas.CENTER.x + x, Canvas.CENTER.y + y)));
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

        info.append(String.format("    + Favorite Strand: (%d)\n", this.getFavoriteStrand()));

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
        if (distance < 0 || distance >= radio) {

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

        if(bridges.stream().anyMatch(candidate -> candidate.getColor().equals(color))){
            removeBridge(color);
        }

        int finalStrand = initialStrand == this.strandCount - 1 ? 0 : initialStrand + 1;

        if (this.isInvalidBridge(color, distance, initialStrand, finalStrand)) {
            lastActionWasOk = false;
            return;
        }

        Point initialPoint = this.strands.get(initialStrand).getScaledPoint((double) distance / this.radio);
        Point finalPoint = this.strands.get(finalStrand).getScaledPoint((double) distance / this.radio);

        Bridge bridge = BridgeFactory.buildBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color, type);
        this.bridges.add(bridge);

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
            Bridge bridgeToRemove = this.bridges.get(i);
            if (bridgeToRemove.getColor().equals(color)) {
                if (this.bridges.get(i) instanceof FixedBridge) {
                    MessageHandler.showError("You cannot delete a 'Fixed' Bridge");
                    break;
                }
                if (this.bridges.get(i) instanceof TransformerBridge) {
                    this.removeFavoriteStrand();
                    this.addFavoriteStrand(bridgeToRemove.getInitialStrand());
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

    private boolean isInvalidStrand(int strand) {
        if (strand < 0 || strand > strands.size() - 1) {
            MessageHandler.showError("Favorite Strand out of Range");
            return true;
        }

        if (strand == favoriteStrand) {
            if (isVisible)
                MessageHandler.showInfo("The new favorite strand cannot be added", "already exist as a favorite");
            return true;
        }

        if (favoriteStrand != -1) {
            //TODO: error or just substitution
            if (isVisible)
                MessageHandler.showError("The new favorite strand cannot be added", "First remove the current favorite 'removeFavoriteStrand()'");
            return true;
        }

        return false;
    }

    /**
     * Adds a favorite strand to the spider associated with the specified color and strand.
     *
     * @param strand The value of the favorite strand.
     */
    public void addFavoriteStrand(int strand, String color, Strand.Types type) {

        if (this.isInvalidStrand(strand)) {
            this.lastActionWasOk = false;
            return;
        }

        Strand favoriteStrand = this.strands.get(strand);

        switch (type) {
            case NORMAL:
                this.strands.set(strand, new NormalStrand(Canvas.CENTER, favoriteStrand.getEnd(), color));
                break;
            case KILLER:
                this.strands.set(strand, new KillerStrand(Canvas.CENTER, favoriteStrand.getEnd(), color));
                break;
            case BOUNCY:
                this.strands.set(strand, new BouncyStrand(Canvas.CENTER, favoriteStrand.getEnd(), color));
                break;
            default:
                // TODO: Handle invalid Strand type
                break;
        }
        this.favoriteStrand = strand;

        this.draw();

        lastActionWasOk = true;
    }

    public void addFavoriteStrand(Integer strand) {
        this.addFavoriteStrand(strand, "green", Strand.Types.NORMAL);

    }

    public void addFavoriteStrand(Integer strand, String color) {
        this.addFavoriteStrand(strand, color, Strand.Types.NORMAL);

    }

    public void addFavoriteStrand(Integer strand, Strand.Types type) {
        this.addFavoriteStrand(strand, "green", type);
    }


    /**
     * Removes a favorite strand.
     * Removes a favorite strand.
     */
    public void removeFavoriteStrand() {

        if (favoriteStrand == -1) {

            if (isVisible)
                MessageHandler.showError("Favorite strand not found", "There is not a favorite Strand yet");

            lastActionWasOk = false;
            return;
        }

        Strand favoriteStrandToRemove = this.strands.get(favoriteStrand);

        this.strands.set(favoriteStrand, new NormalStrand(Canvas.CENTER, favoriteStrandToRemove.getEnd()));

        favoriteStrand = -1;
        MessageHandler.showInfo("The favorite Strand was deleted");

        this.draw();

        lastActionWasOk = true;
    }

    /**
     * Prints the favorite strands of the spider.
     */
    public void printFavoriteStrand() {

        String info;

        if (favoriteStrand == -1) {
            info = "There is not a favorite Strand yet";
        } else {
            info = "The spider has the following favorite strands:\n" +
                    String.format("Favorite Strand: (%d):", this.favoriteStrand);
        }
        MessageHandler.showInfo(info);

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

    /**
     * Kills the spider, making it unable to move or trigger any actions.
     */
    public void killSpider() {
        this.spider.kill();

        lastActionWasOk = true;
    }

    /**
     * Respawns the spider in the center of the spider web, resets its trace lines, and displays an information message.
     */
    public void respawnSpider() {
        this.spider.respawn();

        this.spider.setPosition(new Point(Canvas.CENTER));
        this.currentStrand = -1;
        this.spider.resetTraceLines();

        MessageHandler.showInfo("The spider has been respawned");

        lastActionWasOk = true;
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
        return this.favoriteStrand;
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

    public void setCurrentStrand(int currentStrand) {
        this.currentStrand = currentStrand;
    }

    public void setStrandType(int strand, String color, Strand.Types type) {
        if (isInvalidStrand(strand)) {
            this.lastActionWasOk = false;
            return;
        }

        Strand favoriteStrand = this.strands.get(strand);

        switch (type) {
            case NORMAL:
                this.strands.set(strand, new NormalStrand(Canvas.CENTER, favoriteStrand.getEnd(), color));
                break;
            case KILLER:
                this.strands.set(strand, new KillerStrand(Canvas.CENTER, favoriteStrand.getEnd(), color));
                break;
            case BOUNCY:
                this.strands.set(strand, new BouncyStrand(Canvas.CENTER, favoriteStrand.getEnd(), color));
                break;
            default:
                // TODO: Handle invalid Strand type
                break;
        }
        this.favoriteStrand = strand;

        this.draw();

        lastActionWasOk = true;
    }

    public int getCurrentDistance() {
        return currentDistance;
    }
}
