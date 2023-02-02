package com.cv.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST , reason = "you can't update archive subject with archive of projects")
public class UpdatingArchiveSubjectException extends Exception{
}
