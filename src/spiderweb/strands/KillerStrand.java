package spiderweb.strands;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class KillerStrand extends Strand {


    public KillerStrand(Point start, Point end) {
        super(start, end);
    }

    public KillerStrand(Point start, Point end, String color) {
        super(start, end, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
        spiderWeb.killSpider();
    }
}
