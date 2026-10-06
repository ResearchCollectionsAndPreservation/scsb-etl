package org.recap.model.jpa;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XmlRecordEntityUT extends BaseTestCaseUT {

    @Test
    public void getXmlRecordEntity() {

        XmlRecordEntity xmlRecordEntity = new XmlRecordEntity();

        xmlRecordEntity.setId(1);
        xmlRecordEntity.setXml(new byte[1]);
        xmlRecordEntity.setDataLoaded(new Date());
        xmlRecordEntity.setXmlFileName("XML");
        xmlRecordEntity.setOwningInstBibId("1");
        xmlRecordEntity.setOwningInst("1");

        assertNotNull(xmlRecordEntity.getId());
        assertNotNull(xmlRecordEntity.getXml());
        assertNotNull(xmlRecordEntity.getDataLoaded());
        assertNotNull(xmlRecordEntity.getOwningInst());
        assertNotNull(xmlRecordEntity.getOwningInstBibId());
        assertNotNull(xmlRecordEntity.getXmlFileName());
    }
}
