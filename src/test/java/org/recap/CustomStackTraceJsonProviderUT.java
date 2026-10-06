package org.recap;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

public class CustomStackTraceJsonProviderUT extends BaseTestCaseUT{

    @InjectMocks
    CustomStackTraceJsonProvider customStackTraceJsonProvider;

    @Mock
    JsonGenerator jsonGenerator;

    @Mock
    ILoggingEvent event;

    @Mock
    IThrowableProxy iThrowableProxy;

    @Mock
    ThrowableHandlingConverter converter;

    @Test
    public void writeTo() throws Exception {
        Mockito.when(event.getThrowableProxy()).thenReturn(iThrowableProxy);
        Mockito.when(converter.convert(event)).thenReturn("message");
        customStackTraceJsonProvider.writeTo(jsonGenerator, event);
    }
}
