package org.recap.model.jaxb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.recap.model.jaxb.marc.MatchingInstitutionBibIdType;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MatchingInstitutionBibIdTypeUT {

    MatchingInstitutionBibIdType matchingInstitutionBibId;

    @BeforeEach
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
