import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpiderWebContestTest {
    @Test
    public void testDefaultCasesForSpiderWalkProblem() throws Exception {
        int[][] input1 = {{20, 1}, {40, 3}, {60, 3}, {80, 7}, {100, 5}};

        ArrayList<Integer> output1 = new ArrayList<>();
        output1.add(2);
        output1.add(1);
        output1.add(1);
        output1.add(1);
        output1.add(0);
        output1.add(1);
        output1.add(2);
        SpiderWebContest spiderWeb1 = new SpiderWebContest();

        assertEquals(output1, spiderWeb1.solve(7, 6, input1));

        int[][] input2 = {{1,1},{2,2},{3,3},{4,4}};

        ArrayList<Integer> output2 = new ArrayList<>();
        output2.add(1);
        output2.add(1);
        output2.add(0);
        output2.add(1);
        SpiderWebContest spiderWeb2 = new SpiderWebContest();

        assertEquals(output2, spiderWeb2.solve(4, 2, input2));
    }
}
