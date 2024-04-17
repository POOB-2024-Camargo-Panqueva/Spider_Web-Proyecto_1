package spiderweb.bridges;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class NormalBridge extends Bridge {
    public NormalBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
    }

    @Override
    public Types getType() {
        return Types.NORMAL;
    }

    @Override
    public NormalBridge copy() {
        return new NormalBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }


}
