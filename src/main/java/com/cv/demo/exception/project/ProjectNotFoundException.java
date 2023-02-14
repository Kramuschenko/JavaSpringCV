package com.cv.demo.exception.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Project wasn't found")
public class ProjectNotFoundException extends Exception {
}
