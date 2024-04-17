package spiderweb.bridges;

import spiderweb.main.SpiderWeb;
import utilities.MessageHandler;

import java.awt.*;
import java.util.ArrayList;

public class FunnyBridge extends Bridge {

    public FunnyBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {

        ArrayList<Bridge> bridgesCopy = new ArrayList<>(spiderWeb.getBridges());

        for (Bridge bridge : bridgesCopy) {
            Bridge reference = bridge.copy();
            spiderWeb.removeBridge(bridge.getColor());

            int newInitialStrand = (int) (Math.random() * (spiderWeb.getStrandCount() - 1));
            int newDistance;

            do {
                newDistance = (int) (reference.getDistance() * (Math.random() + 0.5));
            } while (newDistance >= spiderWeb.getRadio());

            spiderWeb.addBridge(reference.getColor(), newDistance, newInitialStrand, Types.FUNNY);
        }
    }

    @Override
    public Types getType() {
        return Types.FUNNY;
    }

    @Override
    public Bridge copy() {
        return new FunnyBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }
}
