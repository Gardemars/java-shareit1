package ru.practicum.shareit.core.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.core.exception.*;
import ru.practicum.shareit.core.exception.model.ErrorResponse;
import ru.practicum.shareit.core.exception.model.ValidationErrorResponse;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleBindException(MethodArgumentNotValidException exp) {

        Map<String, String> errors = exp.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        Objects.requireNonNull(DefaultMessageSourceResolvable::getDefaultMessage)));
        log.error(errors.toString());
        return new ValidationErrorResponse(errors);
    }

    @ExceptionHandler(value = {ItemNotAvailableException.class, ValidationException.class, StatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final RuntimeException exp) {
        log.error(exp.getMessage());
        return new ErrorResponse(exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmailException(final DuplicateEmailException exp) {
        log.error(exp.getMessage());
        return new ErrorResponse(exp.getMessage());
    }

    @ExceptionHandler(value = {FailIdException.class, EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFailId(final RuntimeException exp) {
        log.error(exp.getMessage());
        return new ErrorResponse(exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRequestHeaderNotFound(final MissingRequestHeaderException exp) {
        log.error(exp.getMessage());
        return new ErrorResponse(exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable exp) {
        log.error("Произошла непредвиденная ошибка.{}", exp.getMessage(), exp);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
