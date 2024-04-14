import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shape.Canvas;
import spiderweb.bridges.Bridge;
import spiderweb.main.SpiderWeb;

import java.awt.*;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SpiderWebTest {

    private SpiderWeb spiderWeb;


    @BeforeEach
    void setUp() {
        SpiderWeb.TEST_MODE = true;
        spiderWeb = new SpiderWeb(7, 200);

        spiderWeb.makeInvisible();
        spiderWeb.addBridge("1", 100, 1);
        spiderWeb.addBridge("2", 120, 2);
        spiderWeb.addBridge("3", 140, 3);
        spiderWeb.addBridge("4", 160, 4);
        spiderWeb.addBridge("5", 180, 5);
        spiderWeb.addBridge("6", 200, 6);
        spiderWeb.addBridge("7", 95, 6);
        spiderWeb.addBridge("8", 80, 3);
        spiderWeb.addBridge("9", 60, 5);
    }

    /**
     * Tests the movement of the spider to a new strand.
     *
     * <p>It sets the spider to move to a specific strand, and then checks whether the spider has successfully
     * moved to the specified strand. Additionally, it verifies that the last action was successful.</p>
     */
    @Test
    public void testSpiderMovedCorrectly() {
        int moveTo = 5;

        spiderWeb.moveSpiderTo(moveTo);
        assertEquals(spiderWeb.getCurrentStrand(), moveTo);
        assertTrue(spiderWeb.isLastActionWasOk());
    }

    /**
     * Tests the spider's attempt to move to an invalid strand.
     *
     * <p>It sets an invalid target strand for the spider to move to and checks whether the spider
     * fails to move to the specified strand.</p>
     */
    @Test
    public void testMoveSpiderToValidStrand() {
        int targetStrand = 10000000;

        spiderWeb.moveSpiderTo(targetStrand);

        assertFalse(spiderWeb.lastActionWasOk());
        assertEquals(spiderWeb.getCurrentStrand(), -1);
        assertEquals(spiderWeb.getSpider().getPosition(), new Point(shape.Canvas.CENTER));
    }

    @Test
    public void testSitSpiderOnCenter() {
        spiderWeb.sitSpiderOnCenter();
        assertEquals(spiderWeb.getCurrentStrand(), -1);
        assertEquals(spiderWeb.getSpider().getPosition(), new Point(Canvas.CENTER));
        assertTrue(spiderWeb.isLastActionWasOk());
    }

    /**
     * Tests the spider's ability to sit at the center of the canvas.
     *
     * <p>It instructs the spider to sit on the center of the canvas and checks whether the spider's
     * current strand is set to Center, its position is at the center, and the last action was successful.</p>
     */
    @Test
    public void testAddBridgeValidGlobalParameters() {
        int bridgesInitialSize = spiderWeb.getBridges().size();

        spiderWeb.addBridge("ColorTest1", 16, 0);

        assertEquals(spiderWeb.getBridges().size(), bridgesInitialSize + 1);

        assertTrue(spiderWeb.isLastActionWasOk());
    }

    /**
     * Tests the addition of bridges with invalid parameters.
     *
     * <p>It attempts to add bridges with various invalid parameters,
     * including an invalid strand, negative distance, and repeated color. The test verifies that the last
     * action was unsuccessful, and the number of bridges remains unchanged.</p>
     */
    @Test
    public void testAddBridgeWithInvalidParams() {
        spiderWeb.addBridge("ColorTest1", 16, 1); //Valid spiderweb.Bridge

        HashSet<Bridge> previousBridges = new HashSet<>(spiderWeb.getBridges());

        int invalidStrand = 1000;
        String invalidRepeatedColor = "ColorTest1";
        int invalidDistance = -1;
        int initialBridges = spiderWeb.getBridges().size();

        spiderWeb.addBridge("ColorTest0", 26, invalidStrand);
        spiderWeb.addBridge("ColorTest3", invalidDistance, 1);
        spiderWeb.addBridge(invalidRepeatedColor, 2, 1);

        assertFalse(spiderWeb.lastActionWasOk());
        assertEquals(initialBridges, spiderWeb.getBridges().size());

        HashSet<Bridge> currentBridges = new HashSet<>(spiderWeb.getBridges());

        assertEquals(previousBridges, currentBridges); //It Compares Like a Set
    }

    /**
     * Test to add or relocate bridges with conflicts in their radius.
     * <p>
     * This test adds valid bridges to different strands and attempts to add bridges with conflicting
     * radio to the same or different strands. It also tests the relocation of a bridge with a conflicting
     * radius to another valid position.
     */
    @Test
    public void testAddOrRelocateBridgesWithConflictInTheirRadius() {
        spiderWeb.addBridge("ColorTestValid1", 116, 1); //Valid spiderweb.Strand
        spiderWeb.addBridge("ColorTestValid2", 16, 0); //Valid spiderweb.Strand

        spiderWeb.addBridge("ColorTestSameStrand1", 16, 0);
        assertFalse(spiderWeb.lastActionWasOk());

        spiderWeb.addBridge("ColorTestSameStrand2", 116, 0);
        spiderWeb.relocateBridge("ColorTestSameStrand1", 116);
        assertFalse(spiderWeb.lastActionWasOk());
    }

    /**
     * Test to remove a bridge.
     * <p>
     * This test removes a bridge with a specified color from the spider web. It checks if the number of
     * bridges decreases by one after the removal, and the last action was successful.
     */
    @Test
    public void testRemoveBridge() {
        int initialBridges = spiderWeb.getBridges().size();

        String bridgeColorToRemove = "1";

        spiderWeb.removeBridge(bridgeColorToRemove);

        assertEquals(initialBridges - 1, spiderWeb.getBridges().size());
        assertTrue(spiderWeb.isLastActionWasOk());
    }

    /**
     * Test to add strands to the spider web.
     * <p>
     * This test adds a specified number of strands to the spider web and checks if the total number of strands
     * increases accordingly. It also verifies that the last action was successful.
     */
    @Test
    public void testAddStrand() {
        int initialStrands = spiderWeb.getStrandCount();

        int numOfNewStrands = 5;

        for (int i = 0; i < numOfNewStrands; i++) {
            spiderWeb.addStrand();
        }

        assertEquals(spiderWeb.getStrandCount(), initialStrands + numOfNewStrands);

        assertTrue(spiderWeb.isLastActionWasOk());
    }

    @Test
    public void testCanSpiderMoveToNewStrands() {
        spiderWeb.addStrand();
        spiderWeb.addStrand();
        spiderWeb.addStrand();

        int moveTo = spiderWeb.getStrandCount() - 1;

        spiderWeb.moveSpiderTo(moveTo);
        assertEquals(spiderWeb.getCurrentStrand(), moveTo);
        assertTrue(spiderWeb.isLastActionWasOk());

        spiderWeb.sitSpiderOnCenter();

        moveTo = spiderWeb.getStrandCount() - 3;

        spiderWeb.moveSpiderTo(moveTo);
        assertEquals(spiderWeb.getCurrentStrand(), moveTo);
        assertTrue(spiderWeb.isLastActionWasOk());
    }

    /**
     * Test to check if the spider can move to new strands.
     * <p>
     * This test adds three new strands to the spider web, moves the spider to the last strand, checks if the spider's
     * current strand matches the expected value, and verifies that the last action was successful. Then, it moves the spider
     * to a strand that is three positions before the last strand, checks if the spider's current strand matches the expected
     * value, and verifies that the last action was successful.
     */
    @Test
    public void testAddBridgeToStrandCreatedWithAddStrand() {
        int initialBridges = spiderWeb.getBridges().size();

        spiderWeb.addStrand();
        spiderWeb.addBridge("ColorTestNewStrand", 50, spiderWeb.getStrandCount() - 1);

        assertEquals(initialBridges + 1, spiderWeb.getBridges().size());
        assertTrue(spiderWeb.isLastActionWasOk());
    }

    /**
     * Test to check if the spider web can be expanded.
     * <p>
     * This test expands the spider web's radio by a specified amount, and then checks if the radio has increased by the
     * expected value. It also verifies that the number of bridges and strands remains unchanged and that the last action
     * was successful.
     */
    @Test
    public void testExpandSpiderWeb() {
        int initialBridge = spiderWeb.getBridges().size();
        int initialStrands = spiderWeb.getStrandCount();


        int previousRadio = spiderWeb.getRadio();
        int expandBy = 10;

        spiderWeb.expandRadio(expandBy);

        assertEquals(previousRadio + expandBy, spiderWeb.getRadio());
        assertEquals(initialBridge, spiderWeb.getBridges().size());
        assertEquals(initialStrands, spiderWeb.getStrandCount());
        assertTrue(spiderWeb.isLastActionWasOk());
    }

    /**
     * Test to add and remove a favorite strand for the spider.
     * <p>
     * This test adds a favorite strand with a specified color and strand index, checks if the addition is successful, and
     * verifies that the number of favorite strands has increased. Then, it removes the added favorite strand, checks if the
     * removal is successful, and verifies that the number of favorite strands returns to its initial value.
     */
    @Test
    public void testAddAndRemoveFavoriteStrand() {

        spiderWeb.addFavoriteStrand(2);
        assertTrue(spiderWeb.isLastActionWasOk());

        assertEquals(2, spiderWeb.getFavoriteStrand());

        spiderWeb.removeFavoriteStrand();
        assertTrue(spiderWeb.isLastActionWasOk());

        assertEquals(-1, spiderWeb.getFavoriteStrand());
    }
}


