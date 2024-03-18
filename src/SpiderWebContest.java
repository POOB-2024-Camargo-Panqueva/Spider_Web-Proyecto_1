import java.util.ArrayList;

public class SpiderWebContest {

    private int nextIndex(int x, int c, int n) {
        return (x + c) % n;
    }

    private int previousIndex(int x, int c, int n) {
        return (x - c + n) % n;
    }

    private void update(int[] c, int x, int y) {
        x += 1;

        while (x < c.length) {
            c[x] += y;
            x += x & -x;
        }
    }

    private int queryCounter(int[] c, int x) {
        int res = 0;
        x += 1;

        while (x > 0) {
            res += c[x];
            x -= x & -x;
        }

        return res;
    }

    private void processUpdates(int[] c, int l, int r, int x) {
        update(c, l, x);
        update(c, r + 1, -x);
    }

    private void processWork(int[] c, int l, int r, int n) {
        if (l <= r) {
            processUpdates(c, l, r, -1);
        } else {
            processUpdates(c, l, n - 1, -1);
            processUpdates(c, 0, r, -1);
        }
    }

    public ArrayList<Integer> solve(int numOfStrands, int favoriteStrand, int[][] bridges) {
        int n = numOfStrands;
        int m = bridges.length;
        int s = favoriteStrand - 1;

        int[] strandCounts = new int[n + 1];

        ArrayList<int[]> lines = new ArrayList<>();

        for (int[] bridge : bridges) {
            int d = bridge[0];
            int t = bridge[1] - 1;

            lines.add(new int[]{d, t});
        }

        for (int i = 0; i < n; i++) {
            processUpdates(strandCounts, i, i, Math.min(Math.abs(s - i), n - Math.abs(s - i)));
        }

        lines.sort((a, b) -> Integer.compare(b[0], a[0]));

        for (int[] line : lines) {
            int d = line[0];
            int target = line[1];
            int nx = nextIndex(target, 1, n);
            int v1 = queryCounter(strandCounts, target);
            int v2 = queryCounter(strandCounts, nx);

            assert Math.abs(v1 - v2) <= 1;

            if (v1 == v2) {
                continue;
            }

            if (v1 > v2) {
                processUpdates(strandCounts, target, target, -1);
                int cur = 0;

                for (int i = 18; i >= 0; i--) {
                    if (cur + (1 << i) <= n - 2 && queryCounter(strandCounts, previousIndex(target, cur + (1 << i), n)) == v1 + cur + (1 << i)) {
                        cur += 1 << i;
                    }
                }

                if (cur != 0) {
                    processWork(strandCounts, previousIndex(target, cur, n), previousIndex(target, 1, n), n);
                }

                if (queryCounter(strandCounts, nextIndex(nx, 1, n)) != v2 - 1) {
                    processUpdates(strandCounts, nx, nx, 1);
                }
            } else {
                processUpdates(strandCounts, nx, nx, -1);
                int cur = 0;

                for (int i = 18; i >= 0; i--) {
                    if (cur + (1 << i) <= n - 2 && queryCounter(strandCounts, nextIndex(nx, cur + (1 << i), n)) == v2 + cur + (1 << i)) {
                        cur += 1 << i;
                    }
                }

                if (cur != 0) {
                    processWork(strandCounts, nextIndex(nx, 1, n), nextIndex(nx, cur, n), n);
                }

                if (queryCounter(strandCounts, previousIndex(target, 1, n)) != v1 - 1) {
                    processUpdates(strandCounts, target, target, 1);
                }
            }
        }

        ArrayList<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            result.add(queryCounter(strandCounts, i));
        }
        return result;
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
//        while(true){
//            clockwiseNeighbor = currentStrand - 1;
//            counterclockwiseNeighbor = currentStrand + 1;
//            if (currentStrand == 1){clockwiseNeighbor = strands;}
//            else if (currentStrand == strands) {counterclockwiseNeighbor = 1;}
//
//            int distanceToNearestBridge = bridgesByStrands.get(currentStrand).get(0).getDistance();
//
//            ArrayList<Bridge> possibleClockwiseNeighborsInRange = new ArrayList<Bridge>();
//            ArrayList<Bridge> possibleCounterclockwiseNeighborsInRange = new ArrayList<Bridge>();
//
//            int index = 0;
//            while (true){
//                if(bridgesByStrands.get(clockwiseNeighbor).isEmpty()){
//                    break;
//                }
//                Bridge possibleNeighbor = bridgesByStrands.get(clockwiseNeighbor).get(index);
//
//                if(possibleNeighbor.getDistance() < distanceToNearestBridge){
//                    possibleClockwiseNeighborsInRange.add(possibleNeighbor);
//                } else {
//                    break;
//                }
//                index++;
//            }
//
//            index = 0;
//            while (true){
//                if(bridgesByStrands.get(counterclockwiseNeighbor).isEmpty()){
//                    break;
//                }
//                Bridge possibleNeighbor = bridgesByStrands.get(counterclockwiseNeighbor).get(index);
//
//                if(possibleNeighbor.getDistance() < distanceToNearestBridge){
//                    possibleCounterclockwiseNeighborsInRange.add(possibleNeighbor);
//                } else {
//                    break;
//                }
//                index++;
//            }
//
//
//        }
//
//    };

}
