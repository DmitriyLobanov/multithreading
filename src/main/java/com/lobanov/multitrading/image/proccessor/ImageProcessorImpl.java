package com.lobanov.multitrading.image.proccessor;

import com.lobanov.multitrading.image.configuration.ApplicationProperties;
import com.lobanov.multitrading.image.dto.VectorRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageProcessorImpl implements ImageProcessor {

    private final ApplicationProperties applicationProperties;

    @Override
    public int[][] getImageIntensityParallel(BufferedImage image) {
        var imageWidth = image.getWidth();
        var imageHeight = image.getHeight();
        var intensityMatrix = new int[imageWidth][imageHeight];

        var numThreads = applicationProperties.getThreadsNumber();
        var chunkSize = imageWidth / numThreads;

        try (var executor = Executors.newFixedThreadPool(numThreads)) {

            var countDownLatch = new CountDownLatch(numThreads);

            for (int t = 0; t < numThreads; t++) {
                final var startColumn = t * chunkSize;
                final var endColumn = (t == numThreads - 1) ? imageWidth : (t + 1) * chunkSize;

                executor.execute(() -> {
                    for (int i = startColumn; i < endColumn; i++) {
                        for (int j = 0; j < imageHeight; j++) {

                            var rgbPixel = image.getRGB(i, j);
                            var redComponent = (rgbPixel >> 16) & 0x000000FF;
                            var greenComponent = (rgbPixel >> 8) & 0x000000FF;
                            var blueComponent = (rgbPixel) & 0x000000FF;

                            var intensity = (redComponent + greenComponent + blueComponent) / 3;
                            var intensityNormalized = intensity < applicationProperties.getTrashHold() ? 0 : 1;

                            intensityMatrix[i][j] = intensityNormalized;
                        }
                    }
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return intensityMatrix;
    }

    @Override
    public int[][] performErosionParallel(int[][] inputMatrix) {
        var numRows = inputMatrix.length;
        var numCols = inputMatrix[0].length;
        var resultMatrix = new int[numRows][numCols];
        var step = applicationProperties.getErrosianStep();

        var numThreads = applicationProperties.getThreadsNumber();

        try (var executorService = Executors.newFixedThreadPool(numThreads)) {

            try {
                var chunkSize = (numRows - 2 * step) / numThreads;
                CountDownLatch countDownLatch = new CountDownLatch(numThreads);

                for (int t = 0; t < numThreads; t++) {
                    final var startRow = step + t * chunkSize;
                    final var endRow = t == numThreads - 1 ? numRows - step : startRow + chunkSize;

                    executorService.execute(() -> {
                        for (int i = startRow; i < endRow; i++) {
                            for (int j = step; j < numCols - step; j++) {
                                boolean erode = true;

                                for (var x = -step; x <= step && erode; x++) {
                                    for (var y = -step; y <= step && erode; y++) {
                                        if (inputMatrix[i + x][j + y] != 1) {
                                            erode = false;
                                        }
                                    }
                                }

                                resultMatrix[i][j] = erode ? 1 : 0;
                            }
                        }

                        countDownLatch.countDown();
                    });
                }

                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return resultMatrix;
    }

    @Override
    public int[][] transferImageToVector(int[][] inputMatrix, VectorRecord coordinateVector) {
        int rows = inputMatrix.length;
        int cols = inputMatrix[0].length;

        int shiftX = -coordinateVector.x();
        int shiftY = -coordinateVector.y();

        int[][] resultMatrix = new int[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int newX = x + shiftX;
                int newY = y + shiftY;

                if (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
                    resultMatrix[y][x] = inputMatrix[newY][newX];
                } else {
                    // Заменить пограничные пиксели на цвет RGB = (187, 38, 73)
                    resultMatrix[y][x] = 0xFFBB2649; // RGB = (187, 38, 73) в шестнадцатеричном формате
                }
            }
        }

        return resultMatrix;
    }

    @Override
    public int[][] blurMatrixWithImage(int[][] rgbMatrix) {
        int height = rgbMatrix.length;
        int width = rgbMatrix[0].length;

        int[][] resultMatrix = new int[height][width];

        // Гауссовское ядро 5x5
        int[][] kernel = {
                {0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0},
                {0, 1, 1, 1, 0},
                {0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0}
        };

        int kernelSize = 5;
        int kernelSum = 9; // Сумма значений ядра

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = 0;
                int green = 0;
                int blue = 0;

                for (int ky = 0; ky < kernelSize; ky++) {
                    for (int kx = 0; kx < kernelSize; kx++) {
                        int pixelY = y + ky - kernelSize / 2;
                        int pixelX = x + kx - kernelSize / 2;

                        if (pixelY >= 0 && pixelY < height && pixelX >= 0 && pixelX < width) {
                            int pixel = rgbMatrix[pixelY][pixelX];
                            red += ((pixel >> 16) & 0xFF) * kernel[ky][kx];
                            green += ((pixel >> 8) & 0xFF) * kernel[ky][kx];
                            blue += (pixel & 0xFF) * kernel[ky][kx];
                        }
                    }
                }

                red /= kernelSum;
                green /= kernelSum;
                blue /= kernelSum;

                // Собираем новый RGB-пиксель
                int newPixel = (red << 16) | (green << 8) | blue;
                resultMatrix[y][x] = newPixel;
            }
        }

        return resultMatrix;
    }

    @Override
    public int[][] getRGBMatrix(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        var rgbMatrix = new int[width][height];

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                rgbMatrix[i][j] = image.getRGB(i, j);
            }
        }
        return rgbMatrix;
    }

    @Override
    public BufferedImage restoreRGBImage(int[][] rgbMatrix) {
        var width = rgbMatrix.length;
        var height = rgbMatrix[0].length;
        var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                bufferedImage.setRGB(i, j, rgbMatrix[i][j]);
            }
        }
        return bufferedImage;
    }

    @Override
    public BufferedImage restoreErodedImageParallel(int[][] erodedMatrix) {
        var width = erodedMatrix.length;
        var height = erodedMatrix[0].length;

        var numThreads = applicationProperties.getThreadsNumber();
        var chunkSize = width / numThreads;

        var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        try (var executor = Executors.newFixedThreadPool(numThreads)) {
            var countDownLatch = new CountDownLatch(numThreads);

            for (int i = 0; i < numThreads; i++) {
                final var startIndex = i * chunkSize;
                final var endIndex = (i == numThreads - 1) ? width : (i + 1) * chunkSize;

                executor.execute(() -> {
                    for (var row = startIndex; row < endIndex; row++) {
                        for (var j = 0; j < height; j++) {
                            var pixelValue = (erodedMatrix[row][j] == 1) ? 0xFFFFFF : 0x000000;
                            bufferedImage.setRGB(row, j, pixelValue);
                        }
                    }
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return bufferedImage;
    }
}
