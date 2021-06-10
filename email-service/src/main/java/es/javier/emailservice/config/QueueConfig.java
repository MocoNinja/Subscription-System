package es.javier.emailservice.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Component that creates listening queues on start, so the application starts without the need of the queue
 *   to be created in the broker
 */
@Component
public class QueueConfig {

    @Value("${spring.rabbitmq.queue.in}")
    private String queueName;

    private AmqpAdmin amqpAdmin;

    public QueueConfig(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    @PostConstruct
    public void createQueues() {
        amqpAdmin.declareQueue(new Queue(queueName, true));
    }
}