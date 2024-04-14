package spiderweb.bridges;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class TransformerBridge extends Bridge {
    public TransformerBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
    }

    @Override
    public TransformerBridge copy() {
        return new TransformerBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }
}
