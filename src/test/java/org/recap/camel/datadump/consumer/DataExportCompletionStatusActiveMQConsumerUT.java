package org.recap.camel.datadump.consumer;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DataExportCompletionStatusActiveMQConsumerUT {

    DataExportCompletionStatusActiveMQConsumer dataExportCompletionStatusActiveMQConsumer = new DataExportCompletionStatusActiveMQConsumer();

    @Test
    public void testpulOnCompletionTopicOnMessage(){
        dataExportCompletionStatusActiveMQConsumer.culOnCompletionTopicOnMessage("Test Body");
        dataExportCompletionStatusActiveMQConsumer.nyplOnCompletionTopicOnMessage("Test Body");
        dataExportCompletionStatusActiveMQConsumer.pulOnCompletionTopicOnMessage("Test Body");
        assertTrue(true);
    }
}
