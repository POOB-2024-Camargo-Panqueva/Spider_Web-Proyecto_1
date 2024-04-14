package spiderweb.strands;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class NormalStrand extends Strand{

    public NormalStrand(Point start, Point end) {
        super(start, end);
    }

    public NormalStrand(Point start, Point end, String color) {
        super(start, end, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
        return;
    }

}
