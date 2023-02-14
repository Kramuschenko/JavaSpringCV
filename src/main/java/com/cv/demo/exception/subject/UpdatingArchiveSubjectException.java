package com.cv.demo.exception.subject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "You can't update archive subject with archive of projects")
public class UpdatingArchiveSubjectException extends Exception {
}
