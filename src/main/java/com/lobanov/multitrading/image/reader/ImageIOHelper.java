package com.lobanov.multitrading.image.reader;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ImageIOHelper {
    BufferedImage readImage(File image);

    void saveImage(BufferedImage image, String outputFileName);
}
