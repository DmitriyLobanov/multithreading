package com.lobanov.multitrading.image.tasks.taskA;

import com.lobanov.multitrading.image.dto.VectorRecord;
import com.lobanov.multitrading.image.proccessor.ImageProcessor;
import com.lobanov.multitrading.image.reader.ImageIOHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;

@Component
@RequiredArgsConstructor
public class TaskBService {
    private final ImageIOHelper imageIOHelper;
    private final ImageProcessor imageProcessor;


    public void doBTask() {
        var file = new File("C:\\Users\\guard\\OneDrive\\Рабочий стол\\МНОГОПОТОЧКА\\multitrading\\src\\main\\resources\\images\\cat.jpg");
        BufferedImage bufferedImage = imageIOHelper.readImage(file);

        var imageIntensity = imageProcessor.getRGBMatrix(bufferedImage);
        var erodedImage = imageProcessor.transferImageToVector(imageIntensity, new VectorRecord(100, 100));
        var blurMatrixWithImage = imageProcessor.blurMatrixWithImage(erodedImage);
        var restoreErodedImage = imageProcessor.restoreRGBImage(blurMatrixWithImage);

        imageIOHelper.saveImage(restoreErodedImage, "output");
    }
}
