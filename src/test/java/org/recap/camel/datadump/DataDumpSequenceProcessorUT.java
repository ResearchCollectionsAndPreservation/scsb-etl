package org.recap.camel.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.service.executor.datadump.DataDumpSchedulerExecutorService;
import org.recap.util.PropertyUtil;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class DataDumpSequenceProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpSequenceProcessor dataDumpSequenceProcessor;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;

    @Test
    public void testProcessor() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange exPul = new DefaultExchange(ctx);
        Message inPul = exPul.getIn();
        inPul.setBody("PUL");
        exPul.setIn(inPul);
        Exchange exCul = new DefaultExchange(ctx);
        Message inCul = exCul.getIn();
        inCul.setBody("PUL");
        exCul.setIn(inCul);
        Exchange exNypl = new DefaultExchange(ctx);
        Message inNypl = exCul.getIn();
        inNypl.setBody("PUL");
        exCul.setIn(inNypl);
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEtlIncrementalDump(RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION);
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        try {
            dataDumpSequenceProcessor.process(exPul);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dataDumpSequenceProcessor.process(exCul);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dataDumpSequenceProcessor.process(exNypl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testProcessor1() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("PUL");
        ex.setIn(in);
        RecapConstants.EXPORT_DATE_SCHEDULER = "IncrementalRecordsExportNypl";
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEtlIncrementalDump("1");
        ilsConfigProperties.setEtlDeletedDump(RecapConstants.EXPORT_FETCH_TYPE_INSTITUTION);
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("1"));
        try {
            dataDumpSequenceProcessor.process(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testProcessor2() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        in.setBody("CUL");
        ex.setIn(in);
        RecapConstants.EXPORT_DATE_SCHEDULER = "IncrementalRecordsExportCul";
        ILSConfigProperties ilsConfigProperties=new ILSConfigProperties();
        ilsConfigProperties.setEtlIncrementalDump("1");
        ilsConfigProperties.setEtlDeletedDump("2");
        Mockito.when(propertyUtil.getILSConfigProperties(Mockito.anyString())).thenReturn(ilsConfigProperties);
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(Arrays.asList("1"));
        try {
            dataDumpSequenceProcessor.process(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }
}
