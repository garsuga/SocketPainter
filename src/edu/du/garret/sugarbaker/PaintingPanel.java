package edu.du.garret.sugarbaker;

import edu.du.garret.sugarbaker.primitives.Cursor;
import edu.du.garret.sugarbaker.primitives.GarfieldImage;
import edu.du.garret.sugarbaker.primitives.PaintingPrimitive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class PaintingPanel extends JPanel implements MouseListener, MouseMotionListener {
    private final Object colorLock = new Object();
    private Color paintColor;
    private final Object drawModeLock = new Object();
    private int drawMode = -1;
    private final HashMap<String, Cursor> cursors = new HashMap<>();
    private final HashMap<String, PaintingPrimitive> previews = new HashMap<>();

    private final Painter parent;

    // Use layer so objects dont have to hang around and be redrawn every time if they are permanent
    private BufferedImage drawLayer;

    PaintingPanel(Painter parent) {
        super();
        this.parent = parent;
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // Method to support resizing by expanding the image
    private BufferedImage getDrawLayer(int width, int height) {
        if(drawLayer == null) {
            drawLayer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        } else if(width > drawLayer.getWidth() || height > drawLayer.getHeight()) {
            BufferedImage oldImage = drawLayer;
            drawLayer = new BufferedImage(Math.max(width, drawLayer.getWidth()), Math.max(height, drawLayer.getHeight()), BufferedImage.TYPE_4BYTE_ABGR_PRE);
            drawLayer.getGraphics().drawImage(oldImage, 0, 0, oldImage.getWidth(), oldImage.getHeight(), new Color(0, 0, 0, 0), null);
        }

        return drawLayer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage drawLayer = getDrawLayer(getWidth(), getHeight());
        g.drawImage(drawLayer, 0, 0, drawLayer.getWidth(), drawLayer.getHeight(), new Color(0,0,0,0), null);

        synchronized (previews) {
            for(PaintingPrimitive primitive : previews.values()) {
                primitive.draw(g);
            }
        }

        synchronized (cursors) {
            for(Cursor val : cursors.values()) {
                if(val.isEnabled())
                    val.draw(g);
            }
        }
    }

    void addPrimitive(PaintingPrimitive primitive) {
        if(!primitive.isPreview()) {
            primitive.draw(getDrawLayer(Math.max(getWidth(), primitive.getMaxX()), Math.max(getHeight(), primitive.getMaxY())).getGraphics());
            synchronized (previews) {
                previews.remove(primitive.getOwner());
            }
        } else {
            synchronized (previews) {
                previews.put(primitive.getOwner(), primitive);
            }
        }
        repaint();
    }

    void setPaintColor(Color color){
        synchronized (colorLock){
            this.paintColor = color;
        }
    }

    void setDrawMode(int drawMode) {
        synchronized (drawModeLock) {
            this.drawMode = drawMode;
        }
    }

    void setCursor(Cursor cursor) {
        synchronized (cursors) {
            cursors.put(cursor.getOwner(), cursor);
            repaint();
        }
    }

    private int startMouseX = 0;
    private int startMouseY = 0;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        startMouseX = e.getX();
        startMouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(drawMode < 0 || paintColor == null)
            return;
        synchronized (colorLock) {
            synchronized (drawModeLock) {
                PaintingPrimitive primitive = PaintingPrimitive.getPrimitiveFromOrdinal(drawMode, parent.getUsername(), paintColor, startMouseX, startMouseY, e.getX(), e.getY(), false);

                if(primitive instanceof GarfieldImage) {
                    synchronized (previews) {
                        if (previews.containsKey(parent.getUsername())) {
                            Object preview = previews.get(parent.getUsername());
                            if (preview instanceof GarfieldImage) {
                                ((GarfieldImage)primitive).setImageIndex(((GarfieldImage) preview).getImageIndex());
                            }
                        }
                    }
                }

                addPrimitive(primitive);
                try {
                    ObjectOutputStream os = parent.getOutputStream();
                    os.writeObject(primitive);
                } catch(IOException ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        ObjectOutputStream os = parent.getOutputStream();
        try {
            os.writeObject(new Cursor(parent.getUsername(), -1, -1, false));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ObjectOutputStream os = parent.getOutputStream();
        try {
            os.writeObject(new Cursor(parent.getUsername(), (int)e.getPoint().getX(), (int)e.getPoint().getY(), true));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        if(drawMode < 0 || paintColor == null)
            return;

        PaintingPrimitive prim = PaintingPrimitive.getPrimitiveFromOrdinal(drawMode, parent.getUsername(), paintColor, startMouseX, startMouseY, e.getX(), e.getY(), true);

        if(prim instanceof GarfieldImage) {
            synchronized (previews) {
                if (previews.containsKey(parent.getUsername())) {
                    Object preview = previews.get(parent.getUsername());
                    if (preview instanceof GarfieldImage) {
                        ((GarfieldImage)prim).setImageIndex(((GarfieldImage) preview).getImageIndex());
                    }
                }
            }
        }


        addPrimitive(prim);

        try {
            os.writeObject(prim);
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ObjectOutputStream os = parent.getOutputStream();
        try {
            os.writeObject(new Cursor(parent.getUsername(), (int)e.getPoint().getX(), (int)e.getPoint().getY(), true));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
