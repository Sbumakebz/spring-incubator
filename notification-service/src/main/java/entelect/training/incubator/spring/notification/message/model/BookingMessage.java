package entelect.training.incubator.spring.notification.message.model;

public class BookingMessage {
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
