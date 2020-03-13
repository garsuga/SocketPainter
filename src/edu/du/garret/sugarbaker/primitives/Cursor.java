package edu.du.garret.sugarbaker.primitives;

import java.awt.*;
import java.io.Serializable;

public class Cursor implements Serializable {
    private final String owner;
    private final int x,y;
    private final boolean enabled;
    public Cursor(String owner, int x, int y, boolean enabled) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.enabled = enabled;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(x, y-15, x, y+15);
        g.drawLine(x-15, y, x+15, y);
        Font f = g.getFont();
        int height = (int)f.getStringBounds(owner, g.getFontMetrics().getFontRenderContext()).getHeight();
        g.drawString(owner, x + 5, y + 5 + height);
    }

    public String getOwner(){
        return owner;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
