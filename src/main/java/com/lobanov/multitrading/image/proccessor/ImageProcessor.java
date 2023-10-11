package com.lobanov.multitrading.image.proccessor;

import java.awt.image.BufferedImage;

public interface ImageProcessor {

    int[][] getImageIntensityParallel(BufferedImage image);

    int[][] performErosion(int[][] binaryMatrix);

    BufferedImage restoreErodedImageParallel(int[][] erodedMatrix);
}
