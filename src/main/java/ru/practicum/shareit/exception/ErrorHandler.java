package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserValidationException(final UserValidationException e) {
        log.error("ошибка handleUserValidationException c HttpStatus CONFLICT)");
        log.debug("информация для отладки: {}, {}, {}", e.getMessage(), e.getCause(), e.getStackTrace());
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("ошибка handleNotFoundException c HttpStatus NOT_FOUND)");
        log.debug("информация для отладки: {}, {}, {}", e.getMessage(), e.getCause(), e.getStackTrace());
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final ForbiddenException e) {
        log.error("ошибка handleForbiddenException c HttpStatus FORBIDDEN)");
        log.debug("информация для отладки: {}, {}, {}", e.getMessage(), e.getCause(), e.getStackTrace());
        return new ErrorResponse("error", e.getMessage());
    }
}
