package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.practicum.shareit.error.exception.DataConflictException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.error.exception.ValidationException;


@RestControllerAdvice
public class ErrorHandler {

    //@ExceptionHandler
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    //public ErrorResponse handleInvalidParamException(final InvalidParamException e) {
    //    return new ErrorResponse(String.format("Ошибка с полем \"%s\".", e.getParameter()));
    //}

    @ExceptionHandler (value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidArgumentException(final MethodArgumentNotValidException e) {
        System.out.println("Invalid argument exception : " );
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

    @ExceptionHandler (value = {ValidationException.class,
                                InvalidEntityException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        //StringBuilder message = new StringBuilder("Некорректный объект: ");
        //var errors = e.getAllErrors();
        //for (int i = 0; i < errors.size(); i++) {
        //    message.append(errors.get(i).getDefaultMessage());
        //    if (i < errors.size() - 1) {
        //        message.append(" | ");
        //    }
        //}
        return new ErrorResponse(e.getMessage());
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
            NumberFormatException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final Exception e) {
        return new ErrorResponse("Некорректный запрос.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(final RuntimeException e) {
        //return Map.of("Not found", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }





    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        System.out.println(e.getClass());
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
