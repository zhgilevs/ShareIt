package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler({
            MissingRequestHeaderException.class,
            MethodArgumentNotValidException.class,
            NotAvailableException.class,
            TimeValidationException.class,
            UnsupportedStatusException.class})
    public ResponseEntity<ErrorMessage> handleBadRequest(Exception e) {
        var response = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage(), Arrays.toString(e.getStackTrace())));
        log.error(e.getMessage(), Arrays.toString(e.getStackTrace()));
        return response;
    }

    @ExceptionHandler(OwnershipException.class)
    public ResponseEntity<ErrorMessage> handleForbidden(Exception e) {
        var response = ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(e.getMessage(), Arrays.toString(e.getStackTrace())));
        log.error(e.getMessage(), Arrays.toString(e.getStackTrace()));
        return response;
    }

    @ExceptionHandler({
            NotFoundException.class,
            PermissionException.class})
    public ResponseEntity<ErrorMessage> handleNotFound(Exception e) {
        var response = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage(), Arrays.toString(e.getStackTrace())));
        log.error(e.getMessage(), Arrays.toString(e.getStackTrace()));
        return response;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConflict(Exception e) {
        var response = ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage(), Arrays.toString(e.getStackTrace())));
        log.error(e.getMessage(), Arrays.toString(e.getStackTrace()));
        return response;
    }
}