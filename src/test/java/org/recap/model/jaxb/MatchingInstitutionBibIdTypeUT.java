package org.recap.model.jaxb;

import org.junit.Before;
import org.junit.Test;
import org.recap.model.jaxb.marc.MatchingInstitutionBibIdType;

import static org.junit.Assert.assertNotNull;

public class MatchingInstitutionBibIdTypeUT {

    MatchingInstitutionBibIdType matchingInstitutionBibId;

    @Before
    public void beforeSetUp() {
        matchingInstitutionBibId = new MatchingInstitutionBibIdType();
    }

    @Test
    public void testMethods(){
        matchingInstitutionBibId.setSource("Test");
        matchingInstitutionBibId.setValue("test");
        String tsource=matchingInstitutionBibId.getSource();
        String tValue = matchingInstitutionBibId.getValue();
        assertNotNull(tsource);
        assertNotNull(tValue);
    }
}
