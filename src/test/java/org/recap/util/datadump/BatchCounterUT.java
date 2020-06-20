package org.recap.util.datadump;

import org.junit.Test;
import org.recap.BaseTestCase;


import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 20/4/17.
 */
public class BatchCounterUT extends BaseTestCase{

    @Test
    public void testBatchCounter(){
        BatchCounter.setCurrentPage(1);
        BatchCounter.setTotalPages(1);
        assertNotNull(BatchCounter.getCurrentPage());
        assertNotNull(BatchCounter.getTotalPages());
        BatchCounter.reset();

    }

}