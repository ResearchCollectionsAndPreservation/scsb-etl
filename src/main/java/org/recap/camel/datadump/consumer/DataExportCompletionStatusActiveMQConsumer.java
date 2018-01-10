package org.recap.camel.datadump.consumer;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by sudhishk on 6/7/17.
 */
@Component
public class DataExportCompletionStatusActiveMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DataExportCompletionStatusActiveMQConsumer.class);

    public void pulOnCompletionTopicOnMessage(@Body String body){
        logger.info("PUL Topic - Completion Message {}",body);
    }

    public void culOnCompletionTopicOnMessage(@Body String body){
        logger.info("CUL Topic - Completion Message {}",body);
    }

    public void nyplOnCompletionTopicOnMessage(@Body String body){
        logger.info("NYPL Topic - Completion Message {}",body);
    }
}
