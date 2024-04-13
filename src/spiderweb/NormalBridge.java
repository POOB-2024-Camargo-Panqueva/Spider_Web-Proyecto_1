package spiderweb;

import java.awt.*;

public class NormalBridge extends Bridge {
    public NormalBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public Bridge copy() {
        return null;
    }

    @Override
    public Bridge createInstance(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        return new NormalBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }
}
