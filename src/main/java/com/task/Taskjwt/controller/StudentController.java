package com.task.Taskjwt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {
    Logger logger = LoggerFactory.getLogger(StudentController.class);

    @GetMapping("/test")
    public String test1() {
        this.logger.warn("This is working message");
        return "Testing message";
    }

}