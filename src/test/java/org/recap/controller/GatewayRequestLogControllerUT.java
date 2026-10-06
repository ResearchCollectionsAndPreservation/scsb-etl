package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.jpa.ItemRequestReceivedInformationEntity;
import org.recap.repository.ItemRequestInformationRepository;
import org.recap.service.GatewayRequestLogServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * @author Charan Raj C created on 30/03/23
 */
public class GatewayRequestLogControllerUT extends BaseTestCaseUT {

    @InjectMocks
    GatewayRequestLogController gatewayRequestLogController;


    @Mock
    ProducerTemplate producer;

    @Mock
    GatewayRequestLogServiceImpl gatewayRequestLogServiceImpl;

    @Mock
    ItemRequestInformationRepository itemRequestInformationRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRequestsLogEmailNotification() {
        ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
        ReflectionTestUtils.setField(gatewayRequestLogController, "GATEWAY_REQUEST_LOG_FREQUENCY_CHECK_IN_SEC", 60);
        ReflectionTestUtils.setField(gatewayRequestLogController, "emailRequestsLogFailed", "test@gmail.com");
        Date date = new Date();
        ResponseEntity<String> response = gatewayRequestLogController.requestsLogEmailNotification();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gatewayRequestLogServiceImpl, times(0)).updateGatewayRequestLogRequests(date);
    }

    @Test
    public void testRequestsLogEmailNotificationException() {
        ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
        ReflectionTestUtils.setField(gatewayRequestLogController, "GATEWAY_REQUEST_LOG_FREQUENCY_CHECK_IN_SEC", 60);
        ReflectionTestUtils.setField(gatewayRequestLogController, "emailRequestsLogFailed", "test@gmail.com");
        Date date = new Date();
        doNothing().when(gatewayRequestLogServiceImpl).updateGatewayRequestLogRequests(date);
        Optional<List<ItemRequestReceivedInformationEntity>> entityList = getItemRequestReceivedInformationEntityTest();
        Mockito.when(itemRequestInformationRepository.findAllByDateAndStatus(date, ScsbConstants.FAILURE)).thenReturn(entityList);
        ResponseEntity<String> response = gatewayRequestLogController.requestsLogEmailNotification();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gatewayRequestLogServiceImpl, times(0)).updateGatewayRequestLogRequests(date);
    }


    @Test
    public void testSendEmailNotificationWithLessThanMaxRecordsCount()  {

        try {
            ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
            ReflectionTestUtils.setField(gatewayRequestLogController, "emailRequestsLogFailed", "test@gmail.com");
            List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ItemRequestReceivedInformationEntity entity = new ItemRequestReceivedInformationEntity();
                entity.setRequestedItemBarcode("barcode_" + i);
                entity.setDate(new Date());
                entityList.add(entity);
            }
            ReflectionTestUtils.invokeMethod(gatewayRequestLogController, "sendEmailNotification", entityList);
            verify(producer, times(1)).sendBodyAndHeader(ArgumentMatchers.anyString(), any(), any(), any());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testSendEmailNotificationWithMoreThanMaxRecordsCount() {
        ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
        ReflectionTestUtils.setField(gatewayRequestLogController, "emailRequestsLogFailed", "test@gmail.com");
        List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            ItemRequestReceivedInformationEntity entity = new ItemRequestReceivedInformationEntity();
            entity.setRequestedItemBarcode("barcode_" + i);
            entity.setDate(new Date());
            entityList.add(entity);
        }
        ReflectionTestUtils.invokeMethod(gatewayRequestLogController, "sendEmailNotification", entityList);
        verify(producer, times(1)).sendBodyAndHeader(ArgumentMatchers.anyString(), any(), any(), any());
    }

    private Optional<List<ItemRequestReceivedInformationEntity>> getItemRequestReceivedInformationEntityTest() {
        List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();

        ItemRequestReceivedInformationEntity entity = new ItemRequestReceivedInformationEntity();
        entity.setRequestInstitution("requestInstitution");
        entity.setItemOwningInstitution("itemOwningInstitution");
        entity.setRequestRecieved("requestRecieved");
        entity.setRequestedItemBarcode("requestedItemBarcode");
        entity.setResponseMessage("message");
        entity.setItemOwningInstitution("itemOwningInstitution");
        entity.setValidationStatus("available");
        entity.setDate(new Date());
        entity.setStatusId(1);
        entity.setValidationStatus("available");
        entityList.add(entity);

        return Optional.of(entityList);
    }


    private List<ItemRequestReceivedInformationEntity> getItemRequestReceivedInformationEntity() {
        List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
        ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
        itemRequestReceivedInformationEntity.setRequestInstitution("TEST");
        itemRequestReceivedInformationEntity.setItemOwningInstitution("TEST");
        itemRequestReceivedInformationEntity.setRequestedItemBarcode("12445");
        itemRequestReceivedInformationEntity.setDate(new Date());
        itemRequestReceivedInformationEntity.setStatus("FAILED");
        itemRequestReceivedInformationEntity.setId(2);
        itemRequestReceivedInformationEntity.setRequestRecieved("{\"itemBarcodes\":[\"465654\"],\"titleIdentifier\":\"\\u003cPushto poems. 2002-2003\\u003e.      \",\"itemOwningInstitution\":\"TEST\",\"patronBarcode\":\"8765124321\",\"emailAddress\":\"\",\"requestingInstitution\":\"TEST\",\"requestType\":\"RETRIEVAL\",\"deliveryLocation\":\"TEST\",\"requestNotes\":\"\",\"author\":\"\",\"startPage\":\"\",\"endPage\":\"\",\"chapterTitle\":\"\",\"username\":\"dinakartest\",\"issue\":\"\",\"volume\":\"\"}");
        entityList.add(itemRequestReceivedInformationEntity);
        return entityList;
    }
}
