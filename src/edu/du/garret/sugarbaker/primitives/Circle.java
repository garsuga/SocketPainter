package edu.du.garret.sugarbaker.primitives;

import java.awt.*;

public class Circle extends PaintingPrimitive {
    Circle(String owner, Color c, int x1, int y1, int x2, int y2, boolean isPreview) {
        super(owner, c, x1, y1, x2, y2, isPreview);
    }

    @Override
    protected void drawGeometry(Graphics g) {
        int radius = (int)Math.round(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
        g.drawOval(x1 - radius, y1 - radius, radius * 2, radius * 2);
    }
}
