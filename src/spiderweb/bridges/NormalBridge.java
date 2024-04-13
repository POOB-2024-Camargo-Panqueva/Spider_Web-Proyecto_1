package spiderweb.bridges;

import java.awt.*;

public class NormalBridge extends Bridge {
    public NormalBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public NormalBridge copy() {
        return new NormalBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }
}
