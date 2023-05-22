package entelect.training.incubator.spring.booking.model;

import lombok.Data;
@Data
public class BookingsSearchRequest {
    private Integer customerId;
    private Integer flightId;

    private String referenceNumber;
}
