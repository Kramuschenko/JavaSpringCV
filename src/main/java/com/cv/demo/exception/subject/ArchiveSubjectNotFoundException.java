package com.cv.demo.exception.subject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "There is no Archive subject in repository")
public class ArchiveSubjectNotFoundException extends Exception {
}
