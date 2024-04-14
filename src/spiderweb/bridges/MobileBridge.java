package spiderweb.bridges;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class MobileBridge extends Bridge {
    public MobileBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color) {
        super(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
        int newDistance = (int) (this.getDistance() * 1.2);
        int newStrand = Math.max(initialStrand, finalStrand);

        if (newDistance > spiderWeb.getRadio()){
            return;
        }

        if ((finalStrand == 0 && initialStrand == spiderWeb.getStrandCount() - 1) || initialStrand == 0 && finalStrand == spiderWeb.getStrandCount() - 1) {
            newStrand = 0;
        }

        spiderWeb.removeBridge(this.getColor());
        spiderWeb.addBridge(this.color, newDistance, newStrand, Types.MOBILE);
    }

    @Override
    public MobileBridge copy() {
        return new MobileBridge(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
    }

}
