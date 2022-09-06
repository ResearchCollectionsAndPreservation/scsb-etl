package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;

public class XMLFileLoadValidatorUT extends BaseTestCaseUT {

    @InjectMocks
    XMLFileLoadValidator xmlFileLoadValidator;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Test
    public void process() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "sample.txt");
        ReportEntity reportEntity = getReportEntity();
        Mockito.when(reportDetailRepository.findByFileName(any())).thenReturn(Arrays.asList(reportEntity));
        xmlFileLoadValidator.process(ex);
    }
    @Test
    public void processTest() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "sample.txt");
        ReportEntity reportEntity = new ReportEntity();
        Mockito.when(reportDetailRepository.findByFileName(any())).thenReturn(Arrays.asList(reportEntity));
        xmlFileLoadValidator.process(ex);
    }

    private ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(ScsbConstants.HEADER_FETCH_TYPE);
        reportDataEntity.setHeaderValue(ScsbConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }
}
