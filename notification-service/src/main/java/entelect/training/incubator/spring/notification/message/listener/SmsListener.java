package entelect.training.incubator.spring.notification.message.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entelect.training.incubator.spring.notification.message.model.BookingMessage;
import entelect.training.incubator.spring.notification.sms.client.impl.MoloCellSmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class SmsListener {
    private final Logger LOGGER = LoggerFactory.getLogger(SmsListener.class);
    @Autowired
    MoloCellSmsClient moloCellSmsClient;
    @JmsListener(destination = "ENTELECT.INCUBATOR.SMS")
    public void receiveMessage(String message) throws JsonProcessingException {//implicit message type conversion
        LOGGER.info("received: " + message);//TODO to be removed
        ObjectMapper objectMapper = new ObjectMapper();
        BookingMessage bookingMessage = objectMapper.readValue(message, BookingMessage.class);

        moloCellSmsClient.sendSms(bookingMessage.getPhoneNumber(), bookingMessage.getMessage());
    }
}
