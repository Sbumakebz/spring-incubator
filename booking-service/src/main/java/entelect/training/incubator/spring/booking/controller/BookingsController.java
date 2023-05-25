package entelect.training.incubator.spring.booking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.model.Customer;
import entelect.training.incubator.spring.booking.model.Flight;
import entelect.training.incubator.spring.booking.rewards.client.RewardsClient;
import entelect.training.incubator.spring.booking.service.BookingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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

    @Autowired
    JmsTemplate jmsTemplate;

    public BookingsController(BookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) throws JsonProcessingException {
        LOGGER.info("Processing booking creation request for customer={}", booking);

        final Booking savedBooking = bookingsService.makeBooking(booking);

        Customer customer = restTemplate.getForEntity("http://localhost:" + customerServiceUrl + savedBooking.getCustomerId(), Customer.class).getBody();
        Flight flight = restTemplate.getForEntity("http://localhost:" + flightsServiceUrl + savedBooking.getFlightId(), Flight.class).getBody();

        rewardsClient.sendRewards(customer.getPassportNumber(), new BigDecimal(flight.getSeatCost()));

        ObjectMapper objectMapper = new ObjectMapper();
        BookingMessage bookingMessage = new BookingMessage();
        bookingMessage.setPhoneNumber(customer.getPhoneNumber());
        bookingMessage.setMessage(String.format("Molo Air: Confirming flight %s booked for %s on %s.",
                flight.getFlightNumber(), customer.getFirstName() + " " + customer.getLastName(),
                flight.getDepartureTime()));
        objectMapper.writeValueAsString(bookingMessage);

        jmsTemplate.convertAndSend("ENTELECT.INCUBATOR.SMS", objectMapper.writeValueAsString(bookingMessage));

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
    public ResponseEntity<?> searchBookings(@RequestBody Booking bookingRequest) {
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

    class BookingMessage {
        private String phoneNumber;
        private String message;
        public String getPhoneNumber() {
            return phoneNumber;
        }
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
}