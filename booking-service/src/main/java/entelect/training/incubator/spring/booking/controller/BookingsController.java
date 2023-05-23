package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.rewards.client.RewardsClient;
import entelect.training.incubator.spring.booking.service.BookingsService;
import entelect.training.incubator.spring.customer.model.Customer;
import entelect.training.incubator.spring.flight.model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("bookings")
public class BookingsController {
    @Value("${entelect.spring-incubator.customer.service}")
    private String customerServiceUrl;

    @Value("${entelect.spring-incubator.flights.service}")
    private String flightsServiceUrl;

    private final Logger LOGGER = LoggerFactory.getLogger(BookingsController.class);

    private final BookingsService bookingsService;

    @Autowired
    RewardsClient rewardsClient;

    @Autowired
    RestTemplate restTemplate;

    public BookingsController(BookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createCustomer(@RequestBody Booking booking) {
        LOGGER.info("Processing customer creation request for customer={}", booking);

        final Booking savedBooking = bookingsService.makeBooking(booking);

        Customer customer = restTemplate.getForEntity("http://localhost:" + customerServiceUrl + savedBooking.getCustomerId(), Customer.class).getBody();
        Flight flight = restTemplate.getForEntity("http://localhost:" + flightsServiceUrl + savedBooking.getFlightId(), Flight.class).getBody();

        rewardsClient.sendRewards(customer.getPassportNumber(), new BigDecimal(flight.getSeatCost()));

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