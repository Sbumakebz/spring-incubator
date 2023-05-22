package entelect.training.incubator.spring.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 7)
    private String referenceNumber;
    private Integer customerId;
    private Integer flightId;
}
