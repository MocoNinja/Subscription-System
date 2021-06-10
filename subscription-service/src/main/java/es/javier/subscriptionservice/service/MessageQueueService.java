package es.javier.subscriptionservice.service;

import es.javier.subscriptionservice.config.RabbitConfig;
import es.javier.subscriptionservice.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service to easily enqueue a message in the configured queue (Config: {@link RabbitConfig}
 */
@Service
public class MessageQueueService {

    private static final Logger logger = LoggerFactory.getLogger(MessageQueueService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routingkey}")
    private String routingkey;

    public void send(Subscription subscription) {
        rabbitTemplate.convertAndSend(exchange, routingkey, subscription);
        logger.debug("[x] Sent subscription: '{}'", subscription);
    }

}