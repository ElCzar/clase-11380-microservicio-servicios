package microoservicios.service.microo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.function.context.FunctionCatalog;

@Configuration
public class StreamConfig implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(StreamConfig.class);
    private final FunctionCatalog functionCatalog;

    public StreamConfig(FunctionCatalog functionCatalog) {
        this.functionCatalog = functionCatalog;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("=================================================================");
        logger.info("🚀 Spring Cloud Stream Configuration Loaded");
        logger.info("=================================================================");

        logger.info("📋 Registered Functions:");
        functionCatalog.getNames(null).forEach(name -> {
            logger.info("   ✅ Function: {}", name);
        });

        logger.info("=================================================================");
        logger.info("🔔 Expected Consumer Functions:");
        logger.info("   - commentResponse (binding: commentResponse-in-0)");
        logger.info("   - serviceRequest (binding: serviceRequest-in-0)");
        logger.info("=================================================================");
        logger.info("📡 Kafka Topics Configuration:");
        logger.info("   - Consumer: comments-response (group: services-comment-consumer-group)");
        logger.info("   - Consumer: service-request-topic");
        logger.info("   - Producer: service-response-topic");
        logger.info("   - Producer: marketplace.service.events");
        logger.info("=================================================================");
    }
}
