package spiderweb.bridges;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class FixedBridge extends Bridge {
    public FixedBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
    }

    @Override
    public Types getType() {
        return Types.FIXED;
    }

    @Override
    public FixedBridge copy() {
        return new FixedBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }
}
