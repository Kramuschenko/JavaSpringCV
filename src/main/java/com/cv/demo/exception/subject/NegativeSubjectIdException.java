package com.cv.demo.exception.subject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Id of subject can't be negative")
public class NegativeSubjectIdException extends Exception{
}
