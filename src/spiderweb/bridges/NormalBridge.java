package spiderweb.bridges;

import spiderweb.main.SpiderWeb;

import java.awt.*;
import java.util.function.Consumer;

public class NormalBridge extends Bridge {
    public NormalBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
        return;
    }

    @Override
    public NormalBridge copy() {
        return new NormalBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    public Consumer<SpiderWeb> triggerAction;
}
