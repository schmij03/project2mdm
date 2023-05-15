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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
public class BigGANService {

    private static final Logger logger = LoggerFactory.getLogger(BigGANService.class);

    public List<String> generateAndSaveImages(int classId, int numImages) throws ModelException, TranslateException, IOException {
        Image[] generatedImages = generate(classId, numImages);
        logger.info("Using PyTorch Engine. {} images generated.", generatedImages.length);
        return saveImages(generatedImages);
    }

    private List<String> saveImages(Image[] generatedImages) throws IOException {
        Path outputPath = Paths.get(new ClassPathResource("static/images").getURI());
        Files.createDirectories(outputPath);
        List<String> imagePaths = new ArrayList<>();

        for (int i = 0; i < generatedImages.length; ++i) {
            Path imagePath = outputPath.resolve("image" + i + ".png");
            generatedImages[i].save(Files.newOutputStream(imagePath), "png");
            imagePaths.add("/images/image" + i + ".png");
        }
        logger.info("Generated images have been saved in: {}", outputPath);

        return imagePaths;
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
        ClassPathResource resource = new ClassPathResource("static/imagenet1000.txt");
        Scanner scanner = new Scanner(resource.getFile());
        Map<Integer, String> classes = new HashMap<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            if (parts.length >= 2) {
                String indexStr = parts[0].trim().replace("{", "");
                String className = parts[1].trim().replace("'", "");
                int index = Integer.parseInt(indexStr);
                classes.put(index, className);
            }
        }

        return classes;
    }
}
