package entelect.training.incubator.spring.booking.rewards.client;

import entelect.training.incubator.spring.loyalty.ws.model.CaptureRewardsRequest;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.math.BigDecimal;

public class RewardsClient extends WebServiceGatewaySupport {
    public void sendRewards(String passportNumber, BigDecimal amount) {
        CaptureRewardsRequest captureRewardsRequest = new CaptureRewardsRequest();
        captureRewardsRequest.setPassportNumber(passportNumber);
        captureRewardsRequest.setAmount(amount);

        getWebServiceTemplate().marshalSendAndReceive(captureRewardsRequest);
    }
}
