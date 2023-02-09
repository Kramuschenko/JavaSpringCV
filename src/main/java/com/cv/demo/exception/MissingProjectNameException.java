package com.cv.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "You need to add name (not null) to the project and try again")
public class MissingProjectNameException extends Exception {
}
