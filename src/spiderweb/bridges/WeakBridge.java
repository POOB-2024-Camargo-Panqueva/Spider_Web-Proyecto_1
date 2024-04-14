package spiderweb.bridges;

import spiderweb.main.SpiderWeb;

import java.awt.*;
import java.util.function.Consumer;

public class WeakBridge extends Bridge {
    public WeakBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
        spiderWeb.removeBridge(this.color);
    }

    @Override
    public WeakBridge copy() {
        return new WeakBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    public Consumer<SpiderWeb> triggerAction;
}
