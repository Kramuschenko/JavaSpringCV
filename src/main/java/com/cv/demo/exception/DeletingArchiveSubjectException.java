package com.cv.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "You can't delete archive subject with archive of projects")
public class DeletingArchiveSubjectException extends Throwable {
}
