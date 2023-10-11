package com.lobanov.multitrading.image.proccessor;

import com.lobanov.multitrading.image.configuration.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageProcessorImpl implements ImageProcessor {


    private final ApplicationProperties applicationProperties;

    @Override
    public int[][] getImageIntensityParallel(BufferedImage image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int[][] intensityMatrix = new int[imageWidth][imageHeight];
        try (ExecutorService executor = Executors.newFixedThreadPool(applicationProperties.getThreadsNumber())) {

            for (int i = 0; i < imageWidth; i++) {
                final int column = i;

                executor.execute(() -> {
                    for (int j = 0; j < imageHeight; j++) {
                        var rgbPixel = image.getRGB(column, j);
                        var redComponent = (rgbPixel >> 16) & 0x000000FF;
                        var greenComponent = (rgbPixel >> 8) & 0x000000FF;
                        var blueComponent = (rgbPixel) & 0x000000FF;

                        var intensity = (redComponent + greenComponent + blueComponent) / 3;
                        var intensityNormalized = intensity < applicationProperties.getTrashHold() ? 0 : 1;

                        intensityMatrix[column][j] = intensityNormalized;
                    }
                });
            }

            executor.shutdown();
        }

        return intensityMatrix;
    }

    @Override
    public int[][] performErosion(int[][] inputMatrix) {
        int numRows = inputMatrix.length;
        int numCols = inputMatrix[0].length;

        int[][] resultMatrix = new int[numRows][numCols];
        var step = applicationProperties.getErrosianStep();

        for (int i = step; i < numRows - step; i++) {
            for (int j = step; j < numCols - step; j++) {
                boolean erode = true;

                for (int x = -step; x <= step && erode; x++) {
                    for (int y = -step; y <= step && erode; y++) {
                        if (inputMatrix[i + x][j + y] != 1) {
                            erode = false;
                        }
                    }
                }

                resultMatrix[i][j] = erode ? 1 : 0;
            }
        }

        return resultMatrix;
    }

    @Override
    public BufferedImage restoreErodedImageParallel(int[][] erodedMatrix) {
        int width = erodedMatrix.length;
        int height = erodedMatrix[0].length;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        try (ExecutorService executor = Executors.newFixedThreadPool(applicationProperties.getThreadsNumber())) {

            for (int i = 0; i < width; i++) {
                final int row = i;

                executor.execute(() -> {
                    for (int j = 0; j < height; j++) {
                        int pixelValue = (erodedMatrix[row][j] == 1) ? 0xFFFFFF : 0x000000;
                        bufferedImage.setRGB(row, j, pixelValue);
                    }
                });
            }

            executor.shutdown();
        }

        return bufferedImage;
    }
}
