package org.recap.camel.datadump.consumer;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertTrue;

public class DataExportCompletionStatusActiveMQConsumerUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportCompletionStatusActiveMQConsumer dataExportCompletionStatusActiveMQConsumer = new DataExportCompletionStatusActiveMQConsumer();

    @Test
    public void testpulOnCompletionTopicOnMessage(){
        dataExportCompletionStatusActiveMQConsumer.onCompletionTopicMessage("Test Body");
        assertTrue(true);
    }
}
