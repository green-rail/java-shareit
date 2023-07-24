package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.practicum.shareit.error.exception.DataConflictException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.item.exception.InvalidCommentAuthorException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.exception.OwnerMismatchException;
import ru.practicum.shareit.user.exception.UserNotFoundException;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler (value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidArgumentException(final MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder("Некорректный объект: ");
        var errors = e.getAllErrors();
        for (int i = 0; i < errors.size(); i++) {
            message.append(errors.get(i).getDefaultMessage());
            if (i < errors.size() - 1) {
                message.append(" | ");
            }
        }
        return new ErrorResponse(message.toString());
    }

    @ExceptionHandler (MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatch(final RuntimeException e) {
        var ex = (MethodArgumentTypeMismatchException)e;
        return new ErrorResponse("Unknown state: " + ex.getValue());
    }

    @ExceptionHandler (value = {
            InvalidEntityException.class,
            ItemUnavailableException.class,
            InvalidCommentAuthorException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(OwnerMismatchException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerMismatchException(OwnerMismatchException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeaderException(MissingRequestHeaderException e) {
        return new ErrorResponse(String.format("отсутствует заголовок %s", e.getHeaderName()));
    }

    @ExceptionHandler (value = {DataConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConfilictException(final DataConflictException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler (value = {
            HttpMessageNotReadableException.class,
            NoHandlerFoundException.class,
            MethodNotAllowedException.class,
            HttpRequestMethodNotSupportedException.class,
            NumberFormatException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final Exception e) {
        return new ErrorResponse("некорректный запрос");
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("произошла непредвиденная ошибка");
    }
}
