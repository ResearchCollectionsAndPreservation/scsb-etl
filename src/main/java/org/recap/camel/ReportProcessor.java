package org.recap.camel;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by peris on 8/10/16.
 */
@Component
public class ReportProcessor implements Processor {

    /**
     * The Report detail repository.
     */
    @Autowired
    ReportDetailRepository reportDetailRepository;

    /**
     * This method is invoked by route to persist the report entity.
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        ReportEntity reportEntity = (ReportEntity) exchange.getIn().getBody();
        reportDetailRepository.save(reportEntity);
    }
}
