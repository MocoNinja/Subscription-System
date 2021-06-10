package es.javier.emailservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.javier.emailservice.exception.InvalidEmailException;
import es.javier.emailservice.model.Subscription;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Services that reads subscriptions from a queue.
 * If any error happens reading the message, it is sent to another queue for revision, simulating a DLQ check queue.
 */
@Service
public class MessageQueueService {

    @Autowired
    EmailService emailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routingkey}")
    private String routingkey;

    @RabbitListener(queues = {"${spring.rabbitmq.queue.in}"})
    public void read(String subscriptionJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Subscription subscription = objectMapper.readValue(subscriptionJson, Subscription.class);

            emailService.checkValidity(subscription);
            emailService.sendEmail(subscription);
        } catch (JsonProcessingException e) {
            rabbitTemplate.convertAndSend(exchange, routingkey, subscriptionJson);
        } catch (InvalidEmailException e) {
            rabbitTemplate.convertAndSend(exchange, routingkey, subscriptionJson);
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(exchange, routingkey, subscriptionJson);
        }

    }
}