package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.service.BookingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("bookings")
public class BookingsController {

    private final Logger LOGGER = LoggerFactory.getLogger(BookingsController.class);

    private final BookingsService bookingsService;

    public BookingsController(BookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createCustomer(@RequestBody Booking booking) {
        LOGGER.info("Processing customer creation request for customer={}", booking);

        final Booking savedBooking = bookingsService.makeBooking(booking);

        LOGGER.trace("Booking created");
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        LOGGER.info("Processing booking search request for booking id={}", id);
        Booking booking = this.bookingsService.getBookingById(id);

        if (booking != null) {
            LOGGER.trace("Found booking");
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }

        LOGGER.trace("Booking not found");
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchCustomers(@RequestBody Booking bookingRequest) {
        LOGGER.info("Processing bookings search request for request {}", bookingRequest);
        List<Booking> bookings = null;

        if(bookingRequest.getCustomerId() != null && bookingRequest.getReferenceNumber() == null)
            bookings = bookingsService.getBookingsByCustomerId(bookingRequest.getCustomerId());
        else if(bookingRequest.getReferenceNumber() != null && bookingRequest.getCustomerId() == null)
            bookings = bookingsService.getBookingsByReferenceNumber(bookingRequest.getReferenceNumber());

        if (bookings != null) {
            return ResponseEntity.ok(bookings);
        }

        LOGGER.trace("Bookings not found");
        return ResponseEntity.notFound().build();
    }
}