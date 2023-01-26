package com.cv.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingSubjectDataException extends Exception {
    public MissingSubjectDataException(String errorMessage) {
        super(errorMessage);
    }
}
