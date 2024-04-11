import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpiderWebContestTest {
    @Test
    public void testDefaultCasesForSpiderWalkProblem() {
        int[][] input1 = {{20, 0}, {40, 2}, {60, 2}, {80, 6}, {100, 4}};

        ArrayList<Integer> output1 = new ArrayList<>();
        output1.add(2);
        output1.add(1);
        output1.add(1);
        output1.add(1);
        output1.add(0);
        output1.add(1);
        output1.add(2);
        SpiderWebContest spiderWeb1 = new SpiderWebContest();

        assertEquals(output1, spiderWeb1.solve(7, 5, input1));

        int[][] input2 = {{1, 0}, {2, 1}, {3, 2}, {4, 3}};

        ArrayList<Integer> output2 = new ArrayList<>();
        output2.add(1);
        output2.add(1);
        output2.add(0);
        output2.add(1);
        SpiderWebContest spiderWeb2 = new SpiderWebContest();

        assertEquals(output2, spiderWeb2.solve(4, 1, input2));
    }

    @Test
    public void testGetFinalStrandBySimulatingMovement() {

        SpiderWeb spiderWeb = null;
        SpiderWebContest contest = null;
        int[][] bridges = null;

        try {
            contest = new SpiderWebContest();

            bridges = new int[][]{{20, 0}, {40, 2}, {60, 2}, {80, 6}, {100, 4}};
            spiderWeb = new SpiderWeb(7, 5, bridges);

            ArrayList<Bridge> bridgesList = spiderWeb.getBridges();

            assertEquals(1, contest.getFinalStrandBySimulatingMovement(0, bridgesList));
            assertEquals(6, contest.getFinalStrandBySimulatingMovement(1, bridgesList));
            assertEquals(2, contest.getFinalStrandBySimulatingMovement(2, bridgesList));
            assertEquals(3, contest.getFinalStrandBySimulatingMovement(3, bridgesList));
            assertEquals(5, contest.getFinalStrandBySimulatingMovement(4, bridgesList));
            assertEquals(4, contest.getFinalStrandBySimulatingMovement(5, bridgesList));
            assertEquals(0, contest.getFinalStrandBySimulatingMovement(6, bridgesList));

            bridges = new int[][]{{10, 0}, {30, 0}, {50, 0}, {20, 1}, {40, 1}, {60, 1}, {100, 2}};
            spiderWeb = new SpiderWeb(3, 2, bridges);

            bridgesList = spiderWeb.getBridges();

            assertEquals(2, contest.getFinalStrandBySimulatingMovement(0, bridgesList));
            assertEquals(1, contest.getFinalStrandBySimulatingMovement(1, bridgesList));
            assertEquals(0, contest.getFinalStrandBySimulatingMovement(2, bridgesList));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
