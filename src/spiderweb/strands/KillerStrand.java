package spiderweb.strands;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class KillerStrand extends Strand {

    public KillerStrand(Point start, Point end, String color) {
        super(start, end, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
        spiderWeb.killSpider();
    }

    @Override
    public String toString() {
        return String.format("Color: %s - Type: %s", color, Strand.Types.KILLER.getType());
    }
    @Override
    public Types getType() {
        return Types.KILLER;
    }
    @Override
    public Strand copy() {
        return new KillerStrand(this.start, this.end, this.color);
    }
}
