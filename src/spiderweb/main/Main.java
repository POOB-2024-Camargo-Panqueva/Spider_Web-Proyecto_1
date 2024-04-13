package spiderweb.main;

import spiderweb.main.SpiderWebContest;

public class Main {

    public static void main(String[] args) {
        SpiderWebContest contest;
        try {

            int[][] bridges = new int[][]{{100, 4}};
            contest = new SpiderWebContest();

            contest.simulate(7, 0, bridges, 4);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
