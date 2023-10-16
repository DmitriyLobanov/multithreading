package com.lobanov.multitrading.image.proccessor;

import com.lobanov.multitrading.image.dto.VectorRecord;

import java.awt.image.BufferedImage;

public interface ImageProcessor {

    int[][] getImageIntensityParallel(BufferedImage image);

    int[][] performErosionParallel(int[][] inputMatrix);

    int[][] transferImageToVector(int[][] inputMatrix, VectorRecord coordinateVector);

    int[][] blurMatrixWithImage(int[][] rgbMatrix);

    int[][] getRGBMatrix(BufferedImage image);

    BufferedImage restoreRGBImage(int[][] rgbMatrix);

    BufferedImage restoreErodedImageParallel(int[][] erodedMatrix);
}
