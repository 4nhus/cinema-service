package cinema;

import cinema.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class CinemaRestController {
    private final Map<String, Seat> bookings = new HashMap<>();
    private Seat[] seats = null;
    private int income = 0;

    @GetMapping("/seats")
    public ResponseEntity<SeatsResponse> getSeats() {
        initialiseSeatsIfNull();

        return ResponseEntity.ok(new SeatsResponse(9, 9, seats));
    }

    @PostMapping("/purchase")
    public ResponseEntity<BookingResponse> bookSeat(@RequestBody BookingRequest booking) {
        initialiseSeatsIfNull();

        if (booking.row < 1 || booking.row > 9 || booking.column < 1 || booking.column > 9) {
            throw new InvalidSeatBookingException();
        }

        Seat seat = seats[(booking.row - 1) * 9 + (booking.column - 1)];
        if (!seat.isAvailable()) {
            throw new SeatAlreadyBookedException();
        }

        seat.setAvailable(false);
        String token = UUID.randomUUID().toString();
        bookings.put(token, seat);
        income += seat.getPrice();

        return ResponseEntity.ok(new BookingResponse(token, seat));
    }

    @PostMapping("/return")
    public ResponseEntity<ReturnResponse> cancelBooking(@RequestBody ReturnRequest cancellation) {
        if (!bookings.containsKey(cancellation.token)) {
            throw new InvalidTokenException();
        }

        Seat seat = bookings.get(cancellation.token);
        seat.setAvailable(true);
        bookings.remove(cancellation.token);
        income -= seat.getPrice();

        return ResponseEntity.ok(new ReturnResponse(seat));
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats(@RequestParam String password) {
        if (!password.equals("super_secret")) {
            throw new IncorrectPasswordException();
        }

        return ResponseEntity.ok(new StatsResponse(income, 81 - bookings.size(), bookings.size()));
    }

    private void initialiseSeatsIfNull() {
        if (seats == null) {
            seats = new Seat[81];

            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    seats[row * 9 + column] = new Seat(row + 1, column + 1);
                }
            }
        }
    }

    public record SeatsResponse(int rows, int columns, Seat[] seats) {
    }

    public record BookingRequest(int row, int column) {
    }

    public record BookingResponse(String token, Seat ticket) {
    }
    
    public record ReturnRequest(String token) {
    }

    public record ReturnResponse(Seat ticket) {
    }

    public record StatsResponse(int income, int available, int purchased) {
    }
}
