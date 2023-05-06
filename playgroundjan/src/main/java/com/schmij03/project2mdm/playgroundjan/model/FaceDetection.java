package com.schmij03.project2mdm.playgroundjan.model;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FaceDetection {

    public DetectedObjects detectFaces(byte[] imageBytes) throws IOException, ModelException, TranslateException {
        int[] steps = {8, 16, 32};
        int[][] scales = {
                {32, 32},
                {64, 64},
                {128, 128},
                {256, 256},
                {512, 512}
        };
        double[] variance = {0.1, 0.2};
        int topK = 200;
        double confThresh = 0.95;
        double nmsThresh = 0.3;

        FaceDetectionTranslator translator = new FaceDetectionTranslator(confThresh, nmsThresh, variance, topK, scales, steps);

        Path modelPath = Paths.get("models/face_detection");
        if (Engine.getInstance().getEngineName().equals("MXNet")) {
            Criteria<Image, DetectedObjects> criteria =
                    Criteria.builder()
                            .optApplication(Application.CV.OBJECT_DETECTION)
                            .setTypes(Image.class, DetectedObjects.class)
                            .optTranslator(translator)
                            .optModelPath(modelPath)
                            .optModelUrls("https://mlrepo.djl.ai/model/cv/ssd/ultralight/main/0.0.1/mxnet_resnet18-symbol.json")
                            .optProgress(new ProgressBar())
                            .build();

            try (ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(criteria)) {
                try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
                    try (InputStream is = new ByteArrayInputStream(imageBytes)) {
                        Image inputImage = ImageFactory.getInstance().fromInputStream(is);
                        return predictor.predict(inputImage);
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("Model only supports MXNet engine");
        }
    }
}
