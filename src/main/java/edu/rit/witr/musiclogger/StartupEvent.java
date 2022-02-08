package edu.rit.witr.musiclogger;

import edu.rit.witr.musiclogger.database.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupEvent implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupEvent.class);

    private final IndexingService service;

    public StartupEvent(IndexingService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            service.initiateIndexing();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to reindex entities ",e);
        }
    }
}
