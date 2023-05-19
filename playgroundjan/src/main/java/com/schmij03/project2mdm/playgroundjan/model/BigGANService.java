package com.schmij03.project2mdm.playgroundjan.model;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Base64;

@Service
public class BigGANService {

    private static final Logger logger = LoggerFactory.getLogger(BigGANService.class);

    public ResponseEntity<List<String>> generateAndReturnImages(int classId, int numImages)
            throws ModelException, TranslateException, IOException {
        // Ensure that no more than 6 images are requested.
        if (numImages > 6) {
            throw new IllegalArgumentException("Cannot generate more than 6 images at a time.");
        }

        Image[] generatedImages = generate(classId, numImages);
        logger.info("Using PyTorch Engine. {} images generated.", generatedImages.length);
        return convertImagesToResponse(generatedImages);
    }

    private ResponseEntity<List<String>> convertImagesToResponse(Image[] generatedImages) throws IOException {
        List<String> imageBlobs = new ArrayList<>();
        for (Image image : generatedImages) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.save(outputStream, "png");
            imageBlobs.add(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(imageBlobs);
    }

    public Image[] generate(int classId, int numImages) throws IOException, ModelException, TranslateException {
        Criteria<int[], Image[]> criteria = Criteria.builder()
                .optApplication(Application.CV.IMAGE_GENERATION)
                .setTypes(int[].class, Image[].class)
                .optFilter("size", "256")
                .optArgument("truncation", 0.4f)
                .optEngine("PyTorch")
                .optProgress(new ProgressBar())
                .build();

        int[] input = new int[numImages];
        for (int i = 0; i < numImages; i++) {
            input[i] = classId;
        }

        try (ZooModel<int[], Image[]> model = criteria.loadModel();
                Predictor<int[], Image[]> generator = model.newPredictor()) {
            return generator.predict(input);
        }
    }

    public Map<Integer, String> loadClasses() throws IOException {
        Map<Integer, String> classes = new HashMap<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/imagenet1000.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String indexStr = parts[0].trim().replace("{", "");
                    String className = parts[1].trim().replace("'", "");
                    int index = Integer.parseInt(indexStr);
                    classes.put(index, className);
                }
            }
        }
        return classes;
    }
}
