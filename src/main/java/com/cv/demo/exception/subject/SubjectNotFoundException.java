package com.cv.demo.exception.subject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Subject wasn't found")
public class SubjectNotFoundException extends Exception {
}
