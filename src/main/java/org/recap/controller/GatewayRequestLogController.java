package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.model.jpa.ItemRequestReceivedInformationEntity;
import org.recap.repository.ItemRequestInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Dinakar N created on 07/12/22
 */
@RestController
@Slf4j
public class GatewayRequestLogController {

    private static final String EMAIL_BODY_FOR = "emailBodyFor";

    @Value("${" + ScsbConstants.GATEWAY_REQUESTS_EMAIL_TO + "}")
    private String emailRequestsLogFailed;

    @Value("${" + PropertyKeyConstants.REQUEST_PENDING_LIMIT + "}")
    private Integer MAX_RECORDS_COUNT;

    @Value("${" + ScsbConstants.GATEWAY_REQUESTS_LOG_FREQUENCY_CHECK_IN_SEC + "}")
    private Integer GATEWAY_REQUEST_LOG_FREQUENCY_CHECK_IN_SEC;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private ItemRequestInformationRepository itemRequestInformationRepository;

    @GetMapping(value = "/requestsEmailNotification", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> requestsLogEmailNotification() {
        Integer count = 0;
        Date date = new Date(System.currentTimeMillis() - GATEWAY_REQUEST_LOG_FREQUENCY_CHECK_IN_SEC * 1000);

        Optional<List<ItemRequestReceivedInformationEntity>> entityList = null;
        try {
            entityList = itemRequestInformationRepository.findAllByDateAndStatus(date, ScsbConstants.FAILED);
            count = entityList != null ? entityList.isPresent() ? entityList.get().size() : 0 : 0;
            if(count > 0)
                sendEmailNotification(entityList.get());
        } catch (Exception e) {
            log.info(ScsbConstants.GATEWAY_REQUST_LOG_EXCEPTION_MESSAGE);
            return new ResponseEntity<>(ScsbConstants.FAILED, HttpStatus.OK);
        }
        return new ResponseEntity<>(ScsbConstants.SUCCESS, HttpStatus.OK);
    }

    private void sendEmailNotification(List<ItemRequestReceivedInformationEntity> entityList) {
        StringBuilder message = new StringBuilder();
        message.append(ScsbConstants.GATEWAY_REQUESTS_LOG_BODY_MESSAGE);
        if (entityList.size() <= MAX_RECORDS_COUNT) {
            MAX_RECORDS_COUNT = entityList.size();
        }
        for (int i = 0; i <= MAX_RECORDS_COUNT - 1; i++) {
            message.append(System.getProperty(ScsbConstants.LINE_SEPERATOR));
            message.append(ScsbConstants.BARCODE).append(entityList.get(i).getRequestedItemBarcode()).append("\t\t")
                    .append(ScsbConstants.REQUEST_CREATE_DATE).append(entityList.get(i).getDate());
        }
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setMessage(message.toString());
        emailPayLoad.setTo(emailRequestsLogFailed);
        producer.sendBodyAndHeader(ScsbConstants.EMAIL_Q, emailPayLoad, EMAIL_BODY_FOR, ScsbConstants.EMAIL_REQUEST_LOG);
    }
}
