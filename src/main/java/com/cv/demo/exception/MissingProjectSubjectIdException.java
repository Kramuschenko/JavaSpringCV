package com.cv.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "you need to add Missing information to the project and try again")
public class MissingProjectSubjectIdException extends Exception {
}