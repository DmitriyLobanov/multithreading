package com.lobanov.multitrading.image.tasks;

import com.lobanov.multitrading.image.proccessor.ImageProcessor;
import com.lobanov.multitrading.image.reader.ImageIOHelper;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class TaskAService {

    private final ImageIOHelper imageIOHelper;
    private final ImageProcessor imageProcessor;

    public TaskAService(ImageIOHelper imageIOHelper, ImageProcessor imageProcessor) {
        this.imageIOHelper = imageIOHelper;
        this.imageProcessor = imageProcessor;
    }

    public void doATask() {
        var file = new File("C:\\Users\\guard\\OneDrive\\Рабочий стол\\МНОГОПОТОЧКА\\multitrading\\src\\main\\resources\\images\\cat.jpg");
        BufferedImage bufferedImage = imageIOHelper.readImage(file);

        var imageIntensity = imageProcessor.getImageIntensityParallel(bufferedImage);
        var erodedImage = imageProcessor.performErosion(imageIntensity);
        var restoreErodedImage = imageProcessor.restoreErodedImageParallel(erodedImage);

        imageIOHelper.saveImage(restoreErodedImage, "cat_erroded.jpg");
    }
}
