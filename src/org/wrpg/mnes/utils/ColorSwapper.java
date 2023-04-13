package org.wrpg.mnes.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ColorSwapper {

    public static void main(String[] args) throws Exception {
        BufferedImage image =  ImageIO.read(new File(""));
        image.getHeight();
        BufferedImage image1 = new BufferedImage(256, 2, 2);

        Color color = new Color(1, 1, 1);
        image1.setRGB(1, 1, color.getRGB());

    }

}
