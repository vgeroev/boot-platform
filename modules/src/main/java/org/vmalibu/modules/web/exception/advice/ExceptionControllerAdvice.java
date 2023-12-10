package org.vmalibu.modules.web.exception.advice;

import org.vmalibu.modules.module.exception.PlatformException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vmalibu.modules.module.exception.GeneralExceptionBuilder;

import java.util.function.Function;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = PlatformException.class)
    public ResponseEntity<?> handle(PlatformException e) {
        Function<HttpStatus, ResponseEntity<?>> generateResponse = status -> new ResponseEntity<>(e, status);
        String code = e.getCode();
        if (GeneralExceptionBuilder.INTERNAL_SERVER_ERROR_CODE.equals(code)) {
            return generateResponse.apply(HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (GeneralExceptionBuilder.INVALID_ARGUMENT_CODE.equals(code)) {
            return generateResponse.apply(HttpStatus.BAD_REQUEST);
        } else {
            return generateResponse.apply(HttpStatus.CONFLICT);
        }
    }

}
