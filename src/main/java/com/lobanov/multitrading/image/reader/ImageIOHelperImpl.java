package com.lobanov.multitrading.image.reader;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageIOHelperImpl implements ImageIOHelper {

    @Override
    public BufferedImage readImage(File image) {
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public void saveImage(BufferedImage image, String outputFileName) {
        try {
            File outputImage = new File(outputFileName + ".png");
            ImageIO.write(image, "png", outputImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
