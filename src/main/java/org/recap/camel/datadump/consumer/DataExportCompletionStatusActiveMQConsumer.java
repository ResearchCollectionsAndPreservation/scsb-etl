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

    public void onCompletionTopicMessage(@Body String body){
        logger.info("Topic - Completion Message {}",body);
    }

}
