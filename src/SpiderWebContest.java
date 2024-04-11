import java.util.ArrayList;
import java.util.Comparator;

public class SpiderWebContest {

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

    public void simulate(int strandCount, int favoriteStrand, int[][] bridges, int initialStrand) {

    }
}
