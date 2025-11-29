package com.nourproject.backend.exceptions;
import com.nourproject.backend.dtos.Response;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(Exception ex) {
        Response response=Response.builder()
                .status(HttpStatus.SC_NOT_FOUND)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(response);
    }
    @ExceptionHandler(InvalidBookingStateOrDate.class)
    public ResponseEntity<Response> handleInvalidBookingException(Exception ex) {
        Response response=Response.builder()
                .status(HttpStatus.SC_BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleMethodArgumentNotValidException(Exception ex) {
        Response response=Response.builder()
                .status(HttpStatus.SC_BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(response);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleHttpMessageNotReadableException(Exception ex) {
        Response response=Response.builder()
                .status(HttpStatus.SC_BAD_REQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleUnkounException(Exception ex) {
        Response response=Response.builder()
                .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(response);
    }
}