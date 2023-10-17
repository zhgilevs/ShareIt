package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
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
            UnsupportedStatusException.class})
    public ResponseEntity<ErrorMessage> handleBadRequest(Exception e) {
        var response = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage(), Arrays.toString(e.getStackTrace())));
        log.error(e.getMessage(), Arrays.toString(e.getStackTrace()));
        return response;
    }
}