package com.cv.demo.exception.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Id of project can't be negative")
public class NegativeProjectIdException extends Exception{
}
