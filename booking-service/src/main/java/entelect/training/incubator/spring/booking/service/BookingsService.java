package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.repository.BookingsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

@Service
public class BookingsService {

    private final BookingsRepository bookingsRepository;

    public BookingsService(BookingsRepository bookingsRepository) {
        this.bookingsRepository = bookingsRepository;
    }

    public Booking makeBooking(Booking bookingRequest) {
        return bookingsRepository.save(bookingRequest);
    }

    public List<Booking> getBookingsByReferenceNumber(String referenceNumber) {
        return bookingsRepository.findByReferenceNumber(referenceNumber);
    }

    public List<Booking> getBookingsByCustomerId(Integer customerId) {
        return bookingsRepository.findByCustomerId(customerId);
    }

    public Booking getBookingById(Integer id) {
        Optional<Booking> bookingOptional = bookingsRepository.findById(id);
        return bookingOptional.orElse(null);
    }
}
