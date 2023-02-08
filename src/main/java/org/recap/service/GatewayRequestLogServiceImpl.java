package org.recap.service;

import lombok.extern.slf4j.Slf4j;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.ItemRequestReceivedInformationEntity;
import org.recap.repository.ItemRequestInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dinakar N created on 14/01/23
 */
@Service
@Slf4j
public class GatewayRequestLogServiceImpl implements GatewayRequestLogService {


    @Autowired
    private ItemRequestInformationRepository itemRequestInformationRepository;

    @Value("${" + PropertyKeyConstants.GATEWAY_VALIDATION_EXCEPTIONS+ "}")
    private String validationExceptions;

    @Override
    public void updateGatewayRequestLogRequests(Date date) {
        Integer count = 0;

        Optional<List<ItemRequestReceivedInformationEntity>> entityList = null;
        try {
            entityList = itemRequestInformationRepository.findAllByDateAndStatus(date, ScsbConstants.SUCCESS);
            count = entityList != null ? entityList.isPresent() ? entityList.get().size() : 0 : 0;
            if(count > 0)
                updateRecentGatewayRequests(entityList.get());
        } catch (Exception e) {
            log.info(ScsbConstants.GATEWAY_REQUST_LOG_EXCEPTION_MESSAGE);
        }
    }

    private void updateRecentGatewayRequests(List<ItemRequestReceivedInformationEntity> itemRequestReceivedInformationEntities) {
         HashSet<Integer> failureIdsSet = new HashSet<>();
        HashSet<Integer> successIdsSet = new HashSet<>();
        for (ItemRequestReceivedInformationEntity entity: itemRequestReceivedInformationEntities) {
            Boolean isValidationException = false;
            if(entity.getResponseMessage() != null && !entity.getResponseMessage().isEmpty() && !entity.getResponseMessage().isBlank()) {
                isValidationException = checkRequestFailedwithValidationExcpetion(entity.getResponseMessage());
            }
            if(isValidationException)
                failureIdsSet.add(entity.getId());
            else
                successIdsSet.add(entity.getId());
        }
        log.info(ScsbConstants.GATEWAY_VALIDATION_LOG_MESSAGE + ScsbConstants.GATEWAY_VALIDATION_FAILURE, failureIdsSet);
        log.info(ScsbConstants.GATEWAY_VALIDATION_LOG_MESSAGE + ScsbConstants.GATEWAY_VALIDATION_SUCCESS, successIdsSet);
        try {
            itemRequestInformationRepository.updateAllByIdIn(ScsbConstants.GATEWAY_VALIDATION_FAILURE,failureIdsSet);
            itemRequestInformationRepository.updateAllByIdIn(ScsbConstants.GATEWAY_VALIDATION_SUCCESS,successIdsSet);
        } catch (Exception e) {
            log.info(ScsbConstants.GATEWAY_EXCEPTION_UPDATE,e.getMessage());
        }

    }

    private Boolean checkRequestFailedwithValidationExcpetion(String responseMessage) {
        List<String> validataionExceptionsList = List.of(validationExceptions.split("\\|"));
        log.info(ScsbConstants.RESPONSE_LOG, responseMessage);
        return validataionExceptionsList.stream().anyMatch(a -> a.contains(responseMessage));
    }
}
