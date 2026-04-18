package dev.simoncodes.ledger.common.exception;

import dev.simoncodes.ledger.auth.UnverifiedEmailException;
import dev.simoncodes.ledger.auth.jwt.JwtException;
import dev.simoncodes.ledger.auth.refresh.RefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    @ResponseBody
    public ErrorResponse handleConflictException(ConflictException ex) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    @ExceptionHandler(UnverifiedEmailException.class)
    @ResponseBody
    public ErrorResponse handleUnverifiedEmailException(UnverifiedEmailException ex) {
        return new ErrorResponse(
                HttpStatus.PRECONDITION_REQUIRED.value(),
                ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(RefreshTokenException.class)
    @ResponseBody
    public ErrorResponse handleRefreshTokenException(RefreshTokenException ex) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage()
        );
    }
}
