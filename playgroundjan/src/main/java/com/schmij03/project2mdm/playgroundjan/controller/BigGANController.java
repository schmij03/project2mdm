package com.schmij03.project2mdm.playgroundjan.controller;

import com.schmij03.project2mdm.playgroundjan.model.BigGANService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ai.djl.ModelException;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class BigGANController {

    private static final Logger logger = LoggerFactory.getLogger(BigGANController.class);

    private final BigGANService bigGANService;

    public BigGANController(BigGANService bigGANService) {
        this.bigGANService = bigGANService;
    }

    @GetMapping("/")
    public String index(Model model) throws IOException {
        Map<Integer, String> classes = bigGANService.loadClasses();
        model.addAttribute("classes", classes);
        return "index.html";
    }

    @GetMapping("/classes")
    @ResponseBody
    public ResponseEntity<Map<Integer, String>> getClasses() {
        try {
            Map<Integer, String> classes = bigGANService.loadClasses();
            return new ResponseEntity<>(classes, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Error loading classes: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<List<String>> generate(@RequestParam int classId, @RequestParam int numImages) {
        try {
            List<String> imagePaths = bigGANService.generateAndSaveImages(classId, numImages);
            return new ResponseEntity<>(imagePaths, HttpStatus.OK);
        } catch (ModelException | TranslateException | IOException e) {
            logger.error("Error generating images: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
