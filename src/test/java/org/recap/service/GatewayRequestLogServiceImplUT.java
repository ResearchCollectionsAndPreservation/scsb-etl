package org.recap.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.ItemRequestReceivedInformationEntity;
import org.recap.repository.ItemRequestInformationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Charan Raj C created on 30/03/23
 */
public class GatewayRequestLogServiceImplUT extends BaseTestCaseUT {
    @InjectMocks
    GatewayRequestLogServiceImpl requestLogService;

    @Mock
    ItemRequestInformationRepository itemRequestInformationRepository;

    @Value("${" + PropertyKeyConstants.GATEWAY_VALIDATION_EXCEPTIONS + "}")
    String validationExceptions;

    @Test
    public void updateGatewayRequestLogRequestsTest() {
        Date date = new Date();
        Optional<List<ItemRequestReceivedInformationEntity>> entityList = getItemRequestReceivedInformationEntityTest();
        Mockito.when(itemRequestInformationRepository.findAllByDateAndStatus(date, ScsbConstants.SUCCESS)).thenReturn(entityList);
        requestLogService.updateGatewayRequestLogRequests(date);
    }

    @Test
    public void updateGatewayRequestLogRequestsException() {
        Date date = new Date();
        Optional<List<ItemRequestReceivedInformationEntity>> entityList = null;
        Mockito.when(itemRequestInformationRepository.findAllByDateAndStatus(date, ScsbConstants.SUCCESS)).thenReturn(entityList);
        requestLogService.updateGatewayRequestLogRequests(date);
    }

    @Test
    public void updateRecentGatewayRequestsTest() {
        Iterator<ItemRequestReceivedInformationEntity> iterator = mock(Iterator.class);
        List<ItemRequestReceivedInformationEntity> entityList = mock(List.class);
        ItemRequestReceivedInformationEntity entity = mock(ItemRequestReceivedInformationEntity.class);
        when(entityList.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(entity);
        ReflectionTestUtils.invokeMethod(requestLogService, "updateRecentGatewayRequests", getItemRequestReceivedInformationEntity());
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
