import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;

public final class SpiderWeb {

    private final Line[] strandLines;
    private final Spider spider;

    private final int strands;
    private final int radio;

    private boolean isVisible;

    public SpiderWeb(int strands, int radio) {
        this.strands = strands;
        this.radio = radio;

        this.isVisible = false;

        this.spider = new Spider(new Point(Canvas.CENTER));
        this.strandLines = new Line[strands];

        this.generateStrandLines();
    }

    public void generateStrandLines() {
        for (int index = 0; index < this.strands; index++) {
            double angle = Math.toRadians((double) 360 / this.strands * index);
            int x = (int) (this.radio * Math.cos(angle));
            int y = (int) (this.radio * Math.sin(angle));
            this.strandLines[index] = new Line(new Point(Canvas.CENTER), new Point(Canvas.CENTER.x + x, Canvas.CENTER.y + y));
        }
    }

    public void makeVisible() {
        this.isVisible = true;
        this.draw();
    }

    private void draw() {
        if (this.isVisible) {
            Arrays.stream(this.strandLines).forEach(Line::draw);
            this.spider.draw();
        }
    }
}
