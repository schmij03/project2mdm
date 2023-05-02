package com.schmij03.project2mdm.playgroundjan.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.schmij03.project2mdm.playgroundjan.model.ImageClassifier;

@RestController
public class ServiceController {

    private ImageClassifier classifier = new ImageClassifier();

    @PostMapping("/classify")
    public String classify(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty");
        }
        var result = classifier.classify(file.getInputStream());
        return result.toJson();
    }
}
