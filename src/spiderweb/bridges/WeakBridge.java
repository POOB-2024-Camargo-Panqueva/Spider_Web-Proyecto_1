package spiderweb.bridges;

import java.awt.*;

public class WeakBridge extends Bridge {
    public WeakBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public WeakBridge copy() {
        return new WeakBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }
}
