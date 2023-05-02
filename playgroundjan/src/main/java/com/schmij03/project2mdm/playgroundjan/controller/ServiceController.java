package com.schmij03.project2mdm.playgroundjan.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import com.schmij03.project2mdm.playgroundjan.model.ImageClassifier;

import java.io.IOException;

@RestController
public class ServiceController {

    private ImageClassifier classifier = new ImageClassifier();

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }

    @GetMapping("/ping")
    public String ping() {
        return "Image classification app is up and running!";
    }

    @PostMapping("/classify")
    public String classify(@RequestParam(name = "file", required = true) MultipartFile file) throws IOException {
        var result = classifier.classify(file);
        return result.toJson();
    }
}
