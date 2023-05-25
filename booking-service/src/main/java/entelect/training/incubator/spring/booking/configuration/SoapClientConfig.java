package entelect.training.incubator.spring.booking.configuration;

import entelect.training.incubator.spring.booking.rewards.client.RewardsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class SoapClientConfig {
    @Bean
    public Jaxb2Marshaller marshaller(){
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath("entelect.training.incubator.spring.loyalty.ws.model");
        return jaxb2Marshaller;
    }

    @Bean
    public RewardsClient rewardsClient(Jaxb2Marshaller jaxb2Marshaller) {
        RewardsClient rewardsClient = new RewardsClient();
        rewardsClient.setDefaultUri("http://localhost:8208/ws");
        rewardsClient.setMarshaller(jaxb2Marshaller);
        rewardsClient.setUnmarshaller(jaxb2Marshaller);
        return rewardsClient;
    }
}
