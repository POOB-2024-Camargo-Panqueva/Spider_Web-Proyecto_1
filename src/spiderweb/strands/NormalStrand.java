package spiderweb.strands;

import spiderweb.main.SpiderWeb;

import java.awt.*;

public class NormalStrand extends Strand {

    public NormalStrand(Point start, Point end) {
        super(start, end);
    }

    public NormalStrand(Point start, Point end, String color) {
        super(start, end, color);
    }

    @Override
    public void triggerAction(SpiderWeb spiderWeb) {
    }

    @Override
    public Types getType() {
        return Types.NORMAL;
    }

    @Override
    public String toString() {
        return String.format("Color: %s - Type: %s", color, Strand.Types.NORMAL.getType());
    }

    @Override
    public Strand copy() {
        return new NormalStrand(this.start, this.end, this.color);
    }
}
