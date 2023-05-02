package com.schmij03.project2mdm.playgroundjan.model;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.LoggerFactory;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ch.qos.logback.classic.Logger;

public class ImageClassifier {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ImageClassifier.class);

    public ImageClassifier() {
    }

    public Classifications classify(InputStream imageInputStream) throws MalformedModelException, ModelNotFoundException, IOException, TranslateException {
        logger.info("Classifying image...");

        Criteria<InputStream, Classifications> criteria = Criteria.builder().setTypes(InputStream.class, Classifications.class)
                .optApplication(Application.CV.IMAGE_CLASSIFICATION).optDevice(Device.cpu()).optFilter("backbone", "resnet50")
                .optFilter("dataset", "imagenet").optProgress(new ProgressBar()).build();

        Path tempImage = Files.createTempFile("temp-image", ".jpg");
        Files.copy(imageInputStream, tempImage, StandardCopyOption.REPLACE_EXISTING);
        try (ZooModel<InputStream, Classifications> model = ModelZoo.loadModel(criteria);
                Predictor<InputStream, Classifications> predictor = model.newPredictor()) {
            return predictor.predict(Files.newInputStream(tempImage));
        }
    }
}
