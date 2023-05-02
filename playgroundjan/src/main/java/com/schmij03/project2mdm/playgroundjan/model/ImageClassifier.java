package com.schmij03.project2mdm.playgroundjan.model;

import org.apache.mxnet.javaapi.*;
import org.springframework.web.multipart.MultipartFile;

import ai.djl.inference.Predictor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageClassifier {

    private static final String MODEL_PATH = "resnet50_v2";
    private static final String SYNSET_PATH = "synset.txt";

    private final Predictor predictor;
    private final String[] synset;

    public ImageClassifier() throws IOException {
        String modelDirPath = System.getProperty("user.dir") + "/models";
        File modelDir = new File(modelDirPath);
        if (!modelDir.exists()) {
            modelDir.mkdir();
        }

        // Load the ResNet50_v2 model
        String modelPath = modelDirPath + "/" + MODEL_PATH;
        Symbol symbol = Symbol.load(modelPath + "-symbol.json");
        DataIter dataIter = ImageRecordIter.newBuilder()
                .setPath(modelDirPath + "/val.rec")
                .setBatchSize(1)
                .build();
        predictor = new Predictor(symbol, new NDListManager(dataIter));

        // Load the synset
        String synsetPath = modelDirPath + "/" + SYNSET_PATH;
        synset = Files.lines(new File(synsetPath).toPath())
                .map(String::trim)
                .toArray(String[]::new);
    }

    public ClassificationResult classify(MultipartFile file) throws IOException {
        // Convert MultipartFile to File
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);

        // Preprocess the image
        NDArray input = Image.imdecode(Files.readAllBytes(tempFile.toPath()), Imgcodecs.IMREAD_COLOR);
        input = Image.resize(input, new Shape(224, 224));
        input = input.transpose(new Shape(2, 0, 1)).expandDims(0).div(255);

        // Run the prediction
        NDList output = predictor.predict(new NDList(input));
        NDArray probabilities = SoftmaxActivation.apply(output.singletonOrThrow());
        float[] probabilitiesArray = probabilities.toFloatArray();
        int predictedClass = probabilities.argMax().toIntArray()[0];

        // Clean up the temporary file
        tempFile.delete();

        // Return the classification result
        return new ClassificationResult(synset[predictedClass], probabilitiesArray[predictedClass]);
    }

}
