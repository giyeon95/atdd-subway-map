package nextstep.subway.ui.handler;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Void> badRequestExceptionHandler(HttpServletRequest request, BadRequestException ex) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> notfoundExceptionHandler(HttpServletRequest request, NotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

}
