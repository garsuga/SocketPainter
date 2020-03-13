package edu.du.garret.sugarbaker.primitives;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GarfieldImage extends PaintingPrimitive {
    private static BufferedImage[] images = new BufferedImage[8];

    private int imageIndex = 0;
    private static int imageCounter = 0;

    public GarfieldImage(String owner, Color c, int x1, int y1, int x2, int y2, boolean isPreview) {
        super(owner, c, x1, y1, x2, y2, isPreview);

        imageIndex = (imageCounter++)%images.length;
    }

    public void setImageIndex(int index) {
        this.imageIndex = index;
    }

    public int getImageIndex(){
        return imageIndex;
    }

    private BufferedImage getGarfieldImage(int index) {
        if(images[index] == null) {
            try {
                try {
                    images[index] = ImageIO.read(GarfieldImage.class.getResourceAsStream("/garfield" + index + ".jpg"));
                }catch (IllegalArgumentException ex) {
                    images[index] = ImageIO.read(new File("resources/garfield" + index + ".jpg"));
                }
            }catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        return images[index];
    }

    @Override
    protected void drawGeometry(Graphics g) {
        BufferedImage image = getGarfieldImage(imageIndex);

        int ux = Math.min(x1, x2);
        int uy = Math.min(y1, y2);

        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        g.drawImage(image, ux, uy, width, height, null);
    }
}
