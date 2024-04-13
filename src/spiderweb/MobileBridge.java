package spiderweb;

import java.awt.*;

public class MobileBridge extends Bridge {
    public MobileBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public Bridge copy() {
        return null;
    }

    @Override
    public Bridge createInstance(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        return new MobileBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }
}
