package edu.du.garret.sugarbaker.primitives;

import java.awt.*;
import java.io.Serializable;

public abstract class PaintingPrimitive implements Serializable{
    public static final int LINE = 0, CIRCLE = 1, GARFIELD = 2;


    private final boolean isPreview;
    private final Color color;
    protected final int x1, x2, y1, y2;

    public int getMaxX(){
        return Math.max(x1,x2);
    }

    public int getMaxY() {
        return Math.max(y1,y2);
    }

    private final String owner;
    public PaintingPrimitive(String owner, Color c, int x1, int y1, int x2, int y2, boolean isPreview) {
        this.owner = owner;
        this.color = c;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.isPreview = isPreview;
    }

    public final void draw(Graphics g) {
        g.setColor(this.color);
        drawGeometry(g);
    }

    public boolean isPreview(){
        return isPreview;
    }

    public String getOwner() {
        return owner;
    }

    protected abstract void drawGeometry(Graphics g);

    public static PaintingPrimitive getPrimitiveFromOrdinal(int ordinal, String owner, Color color, int x1, int y1, int x2, int y2, boolean isPreview) {
        switch (ordinal) {
            case LINE:
                return new Line(owner, color, x1, y1, x2, y2, isPreview);
            case CIRCLE:
                return new Circle(owner, color, x1, y1, x2, y2, isPreview);
            case GARFIELD:
                return new GarfieldImage(owner, color, x1, y1, x2, y2, isPreview);
            default:
                throw new IllegalStateException("Invalid PaintingPrimitive ordinal " + ordinal);
        }
    }
}
