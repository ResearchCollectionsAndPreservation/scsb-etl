package org.recap.camel;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.model.jpa.XmlRecordEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.repository.ImsLocationDetailsRepository;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.repository.ItemStatusDetailsRepository;
import org.recap.util.DBReportUtil;
import org.recap.util.MarcUtil;
import org.recap.util.PropertyUtil;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
public class RecordProcessorTest {

    @InjectMocks
    private RecordProcessor recordProcessor;

    @Mock
    private ProducerTemplate producer;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Mock
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    private BibDataProcessor bibDataProcessor;

    @Mock
    private DBReportUtil dbReportUtil;

    @Mock
    private ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    private MarcUtil marcUtil;

    @Mock
    private PropertyUtil propertyUtil;

    @Mock
    private Future<Map<String, String>> future;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        Page<XmlRecordEntity> xmlRecordEntities = mock(Page.class);
        when(xmlRecordEntities.iterator()).thenReturn(new ArrayList<XmlRecordEntity>().iterator());
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        recordProcessor.setExecutorService(executorService);
        List<Future<Map<String, String>>> futures = new ArrayList<>();
        futures.add(future);
        try {
            recordProcessor.process(xmlRecordEntities);
        } finally {
            executorService.shutdown();
        }
    }
}
