package cinema.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CinemaExceptionHandler {
    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyBooked() {
        return ResponseEntity.badRequest().body(new ErrorResponse("The ticket has been already purchased!"));
    }

    @ExceptionHandler(InvalidSeatBookingException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSeatBooking() {
        return ResponseEntity.badRequest().body(new ErrorResponse("The number of a row or a column is out of bounds!"));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken() {
        return ResponseEntity.badRequest().body(new ErrorResponse("Wrong token!"));
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPassword() {
        return new ResponseEntity<>(new ErrorResponse("The password is wrong!"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingPassword() {
        return new ResponseEntity<>(new ErrorResponse("The password is wrong!"), HttpStatus.UNAUTHORIZED);
    }

    public record ErrorResponse(String error) {
    }
}
