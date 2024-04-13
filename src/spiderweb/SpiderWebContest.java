package spiderweb;

import spiderweb.Bridge;
import spiderweb.SpiderWeb;
import utilities.MessageHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class SpiderWebContest {

    private static final int GAP = 5;
    private static boolean SOLUTION_FOUND = false;

    private int nextIndex(int next, int current, int size) {
        return (next + current) % size;
    }

    private int previousIndex(int next, int current, int size) {
        return (next - current + size) % size;
    }

    private void update(int[] strandCounts, int first, int second) {
        first += 1;

        while (first < strandCounts.length) {
            strandCounts[first] += second;
            first += first & -first;
        }
    }

    private int queryCounter(int[] strandCounts, int x) {
        int res = 0;
        x += 1;

        while (x > 0) {
            res += strandCounts[x];
            x -= x & -x;
        }

        return res;
    }

    private void processUpdates(int[] strandCounts, int left, int right, int x) {
        update(strandCounts, left, x);
        update(strandCounts, right + 1, -x);
    }

    private void processWork(int[] strandCounts, int left, int right, int size) {
        if (left <= right) {
            processUpdates(strandCounts, left, right, -1);
        } else {
            processUpdates(strandCounts, left, size - 1, -1);
            processUpdates(strandCounts, 0, right, -1);
        }
    }

    private SpiderWeb copySpiderWeb(SpiderWeb spiderWeb) {
        SpiderWeb clonedSpiderWeb;
        try {
            clonedSpiderWeb = new SpiderWeb(spiderWeb.getStrandCount(), spiderWeb.getFavoriteStrand(), spiderWeb.getBridgesAsConsoleInput());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clonedSpiderWeb;
    }

    public ArrayList<Integer> solve(int strandCount, int favoriteStrand, int[][] bridges) {
        int[] strandCounts = new int[strandCount + 1];

        ArrayList<int[]> lines = new ArrayList<>();

        for (int[] bridge : bridges) {
            int d = bridge[0];
            int t = bridge[1];

            lines.add(new int[]{d, t});
        }

        for (int i = 0; i < strandCount; i++) {
            processUpdates(strandCounts, i, i, Math.min(Math.abs(favoriteStrand - i), strandCount - Math.abs(favoriteStrand - i)));
        }

        lines.sort((a, b) -> Integer.compare(b[0], a[0]));

        for (int[] line : lines) {
            int target = line[1];
            int nx = nextIndex(target, 1, strandCount);
            int v1 = queryCounter(strandCounts, target);
            int v2 = queryCounter(strandCounts, nx);

            assert Math.abs(v1 - v2) <= 1;

            if (v1 == v2) {
                continue;
            }

            if (v1 > v2) {
                processUpdates(strandCounts, target, target, -1);
                int current = 0;

                for (int i = 18; i >= 0; i--) {
                    if (current + (1 << i) <= strandCount - 2 && queryCounter(strandCounts, previousIndex(target, current + (1 << i), strandCount)) == v1 + current + (1 << i)) {
                        current += 1 << i;
                    }
                }

                if (current != 0) {
                    processWork(strandCounts, previousIndex(target, current, strandCount), previousIndex(target, 1, strandCount), strandCount);
                }

                if (queryCounter(strandCounts, nextIndex(nx, 1, strandCount)) != v2 - 1) {
                    processUpdates(strandCounts, nx, nx, 1);
                }
            } else {
                processUpdates(strandCounts, nx, nx, -1);
                int current = 0;

                for (int i = 18; i >= 0; i--) {
                    if (current + (1 << i) <= strandCount - 2 && queryCounter(strandCounts, nextIndex(nx, current + (1 << i), strandCount)) == v2 + current + (1 << i)) {
                        current += 1 << i;
                    }
                }

                if (current != 0) {
                    processWork(strandCounts, nextIndex(nx, 1, strandCount), nextIndex(nx, current, strandCount), strandCount);
                }

                if (queryCounter(strandCounts, previousIndex(target, 1, strandCount)) != v1 - 1) {
                    processUpdates(strandCounts, target, target, 1);
                }
            }
        }

        ArrayList<Integer> result = new ArrayList<>();

        for (int i = 0; i < strandCount; i++) {
            result.add(queryCounter(strandCounts, i));
        }

        return result;
    }

    public int getFinalStrandBySimulatingMovement(int initialStrand, ArrayList<Bridge> bridges) {
        ArrayList<Bridge> localBridges = new ArrayList<>(bridges);
        localBridges.sort(Comparator.comparingInt(Bridge::getDistance));

        boolean flag = true;
        int currentStrand = initialStrand;
        int currentDistance = 0;

        while (flag) {
            int candidates = 0;

            for (Bridge bridge : localBridges) {
                if ((currentStrand != bridge.getInitialStrand() && currentStrand != bridge.getFinalStrand()) || currentDistance >= bridge.getDistance()) {
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

    private Bridge buildBridge(int distance, int initialStrand, int finalStrand, SpiderWeb spiderWeb) {
        String bridgeColor = String.format("%s-%s", initialStrand, distance);
        Point initialPoint = spiderWeb.getStrands().get(initialStrand).getScaledPoint((double) distance / spiderWeb.getRadio());
        Point finalPoint = spiderWeb.getStrands().get(finalStrand).getScaledPoint((double) distance / spiderWeb.getRadio());

        return new NormalBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, bridgeColor);
        //All Bridges going to be Normals for simulate
    }

    public ArrayList<Bridge> buildBridges(int initialStrand, int finalStrand, int remainingAttempts, ArrayList<Bridge> builtBridges, SpiderWeb spiderWeb) {

        if (SOLUTION_FOUND) {
            return null;
        }

        if (remainingAttempts == 0) {
            if (getFinalStrandBySimulatingMovement(initialStrand, spiderWeb.getBridges()) == finalStrand) {
                SOLUTION_FOUND = true;
                return builtBridges;
            }

            return null;
        }

        ArrayList<Bridge> currentBridges = spiderWeb.getBridges();
        ArrayList<ArrayList<Bridge>> results = new ArrayList<>();

        if (currentBridges.stream().noneMatch(bridge -> bridge.getInitialStrand() == finalStrand || bridge.getFinalStrand() == finalStrand)) {

            int distance = spiderWeb.getRadio();
            int newBridgeInitialStrand = 0;
            int newBridgeFinalStrand = 0;

            for (int index = 0; index < 2; index++) {
                distance -= GAP;

                if (index < 1) {
                    newBridgeFinalStrand = finalStrand;
                    newBridgeInitialStrand = newBridgeFinalStrand == 0 ? spiderWeb.getStrandCount() - 1 : newBridgeFinalStrand - 1;
                } else {
                    newBridgeInitialStrand = finalStrand;
                    newBridgeFinalStrand = (newBridgeInitialStrand + 1) % spiderWeb.getStrandCount();
                }
            }

            // TODO: Remove duplication code with $1
            Bridge newBridge = this.buildBridge(distance, newBridgeInitialStrand, newBridgeFinalStrand, spiderWeb);

            ArrayList<Bridge> newBridges = new ArrayList<>(builtBridges);
            newBridges.add(newBridge);

            SpiderWeb clonedSpiderWeb = copySpiderWeb(spiderWeb);
            clonedSpiderWeb.addBridge(newBridge.getColor(), distance, newBridgeInitialStrand);

            ArrayList<Bridge> result = buildBridges(initialStrand, finalStrand, remainingAttempts - 1, newBridges, clonedSpiderWeb);
            results.add(result);
        }

        for (Bridge bridge : currentBridges) {
            for (int index = 0; index < 6; index++) {
                int distance = bridge.getDistance() + (index % 2 == 0 ? -GAP : +GAP);
                int newBridgeInitialStrand;
                int newBridgeFinalStrand;

                if (index < 2) {
                    newBridgeInitialStrand = bridge.getInitialStrand();
                    newBridgeFinalStrand = bridge.getFinalStrand();
                } else if (index < 4) {
                    newBridgeFinalStrand = bridge.getInitialStrand();
                    newBridgeInitialStrand = newBridgeFinalStrand == 0 ? spiderWeb.getStrandCount() - 1 : newBridgeFinalStrand - 1;
                } else {
                    newBridgeInitialStrand = bridge.getFinalStrand();
                    newBridgeFinalStrand = (newBridgeInitialStrand + 1) % spiderWeb.getStrandCount();
                }

                // TODO: Remove duplication code with $1
                Bridge newBridge = this.buildBridge(distance, newBridgeInitialStrand, newBridgeFinalStrand, spiderWeb);

                ArrayList<Bridge> newBridges = new ArrayList<>(builtBridges);
                newBridges.add(newBridge);

                SpiderWeb clonedSpiderWeb = copySpiderWeb(spiderWeb);
                clonedSpiderWeb.addBridge(newBridge.getColor(), distance, newBridgeInitialStrand);

                ArrayList<Bridge> result = buildBridges(initialStrand, finalStrand, remainingAttempts - 1, newBridges, clonedSpiderWeb);
                results.add(result);
            }
        }

        return results.stream().filter(Objects::nonNull).findFirst().orElse(null);
    }

    public SpiderWeb simulate(int strandCount, int favoriteStrand, int[][] bridges, int initialStrand) throws Exception {
        SOLUTION_FOUND = false;

        SpiderWeb spiderWeb = new SpiderWeb(strandCount, favoriteStrand, bridges);

        if (SpiderWeb.TEST_MODE)
            spiderWeb.makeInvisible();
        else
            spiderWeb.makeVisible();

        ArrayList<Integer> solution = solve(strandCount, favoriteStrand, bridges);
        ArrayList<Bridge> result = buildBridges(initialStrand, favoriteStrand, solution.get(initialStrand), new ArrayList<>(), spiderWeb);

        if (result == null) {
            MessageHandler.showError("No solution found");
            return null;
        }

        if (!SpiderWeb.TEST_MODE)
            MessageHandler.showInfo("Solution found", "The solution has been found and will start when this dialog is closed.");

        for (Bridge bridge : result) {
            spiderWeb.addBridge(bridge.getColor(), bridge.getDistance(), bridge.getInitialStrand());
        }

        spiderWeb.moveSpiderTo(favoriteStrand);

        return spiderWeb;
    }
}
