package org.recap.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultMessage;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by peris on 8/20/16.
 */
@Component
public class XMLFileLoadValidator implements Processor {

    /**
     * The Report detail repository.
     */
    @Autowired
    ReportDetailRepository reportDetailRepository;

    /**
     * Check to see if the xml file has been loaded already. If so, set empty body such that the file doesn't get
     * processed again.
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String camelFileName = (String) exchange.getIn().getHeader("CamelFileName");

        List<ReportEntity> reportEntity =
                reportDetailRepository.findByFileName(camelFileName);

        if(!CollectionUtils.isEmpty(reportEntity)){
            DefaultMessage defaultMessage = new DefaultMessage(exchange);
            defaultMessage.setBody("");
            exchange.setIn(defaultMessage);
            exchange.setMessage(defaultMessage);
        }
    }
}
