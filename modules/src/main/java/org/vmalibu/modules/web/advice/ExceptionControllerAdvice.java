package org.vmalibu.modules.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.vmalibu.modules.module.exception.PlatformException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;

import java.util.function.Function;

@RestControllerAdvice(basePackages = "org.vmalibu")
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = PlatformException.class)
    public ResponseEntity<?> handle(PlatformException e) {
        log.error("Rest controller platform exception", e);
        Function<HttpStatus, ResponseEntity<?>> generateResponse = status -> new ResponseEntity<>(e, status);
        String code = e.getCode();
        if (GeneralExceptionFactory.INTERNAL_SERVER_ERROR_CODE.equals(code)) {
            return generateResponse.apply(HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (GeneralExceptionFactory.INVALID_ARGUMENT_CODE.equals(code)) {
            return generateResponse.apply(HttpStatus.BAD_REQUEST);
        } else {
            return generateResponse.apply(HttpStatus.CONFLICT);
        }
    }

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<?> handle(Throwable e) {
        log.error("Rest controller exception", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
