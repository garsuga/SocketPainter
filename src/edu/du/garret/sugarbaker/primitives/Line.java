package edu.du.garret.sugarbaker.primitives;

import java.awt.*;

public class Line extends PaintingPrimitive{
    Line(String owner, Color c, int x1, int y1, int x2, int y2, boolean isPreview) {
        super(owner, c, x1, y1, x2, y2, isPreview);
    }

    @Override
    protected void drawGeometry(Graphics g) {
        g.drawLine(x1, y1, x2, y2);
    }
}
