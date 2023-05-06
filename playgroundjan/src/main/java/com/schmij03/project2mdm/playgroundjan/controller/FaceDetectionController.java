package com.schmij03.project2mdm.playgroundjan.controller;

import com.schmij03.project2mdm.playgroundjan.model.FaceDetection;
import ai.djl.modality.cv.output.DetectedObjects;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/facedetection")
public class FaceDetectionController {

    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DetectedObjects detectFaces(MultipartFile file) throws Exception {
        FaceDetection faceDetection = new FaceDetection();
        byte[] imageBytes = file.getBytes();
        return faceDetection.detectFaces(imageBytes);
    }
}
