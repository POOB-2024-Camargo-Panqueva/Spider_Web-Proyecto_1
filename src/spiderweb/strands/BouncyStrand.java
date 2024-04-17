package spiderweb.strands;

import spiderweb.bridges.Bridge;
import spiderweb.bridges.NormalBridge;
import spiderweb.main.SpiderWeb;

import java.awt.*;

public class BouncyStrand extends Strand {

    public BouncyStrand(Point start, Point end, String color) {
        super(start, end, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
        int lastStrand = spiderWeb.getCurrentStrand();
        int nextStrand = spiderWeb.getCurrentStrand() + 1;
        if (nextStrand == spiderWeb.getStrandCount()) {
            nextStrand = 0;
        }

        Point initialPoint = spiderWeb.getStrands().get(lastStrand).getScaledPoint((double) spiderWeb.getCurrentDistance() / spiderWeb.getRadio());
        Point finalPoint = spiderWeb.getStrands().get(nextStrand).getScaledPoint((double) spiderWeb.getCurrentDistance() / spiderWeb.getRadio());

        spiderWeb.setCurrentStrand(nextStrand);

        Bridge moveTo = new NormalBridge(spiderWeb.getCurrentDistance(), lastStrand, nextStrand, initialPoint, finalPoint, "temp");
        spiderWeb.getSpider().moveTo(moveTo.getFinalPoint());
    }

    @Override
    public String toString() {
        return String.format("Color: %s - Type: %s", color, Strand.Types.BOUNCY.getType());
    }

    @Override
    public Types getType() {
        return Types.BOUNCY;
    }

    @Override
    public Strand copy() {
        return new BouncyStrand(this.start, this.end, this.color);
    }

}
