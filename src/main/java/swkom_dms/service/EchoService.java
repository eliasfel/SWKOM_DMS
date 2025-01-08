package swkom_dms.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import swkom_dms.RabbitMQConfig;

@Component
@Log4j2
public class EchoService {
    private final RabbitTemplate rabbit;

    @Autowired
    public EchoService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void processMessage(String message, @Header(RabbitMQConfig.ECHO_MESSAGE_COUNT_PROPERTY_NAME) int messageCount) {
        log.info("Received Message #" + messageCount+ ": " + message);
        rabbit.convertAndSend("", RabbitMQConfig.OCR_QUEUE, message);
        log.info("The following document was sent to OCR_QUEUE: " + message);
    }

    @RabbitListener(queues = RabbitMQConfig.RESULT_QUEUE)
    public void handleOCRResult(String ocrResult) {
        log.info("Received OCR result: {}", ocrResult);
    }
}
