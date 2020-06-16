package org.recap.util;

import org.junit.Test;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by peris on 8/10/16.
 */
public class FailureReportUtil_UT {


    String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><collection><record><leader>01506cam a2200325 a 4500</leader><controlfield tag=\"001\">115610</controlfield><controlfield tag=\"005\">20160503221157.0</controlfield><controlfield tag=\"008\">820316s1979    gw       b    00010dger d</controlfield><datafield tag=\"020\" ind1=\" \" ind2=\" \"><subfield code=\"a\">3880703175</subfield></datafield><datafield tag=\"035\" ind1=\" \" ind2=\" \"><subfield code=\"a\">(OCoLC)6837393</subfield></datafield><datafield tag=\"035\" ind1=\" \" ind2=\" \"><subfield code=\"a\">(CStRLIN)NJPG82-B5840</subfield></datafield><datafield tag=\"035\" ind1=\" \" ind2=\" \"><subfield code=\"9\">AAT0318TS</subfield></datafield><datafield tag=\"040\" ind1=\" \" ind2=\" \"><subfield code=\"a\">NjP</subfield><subfield code=\"c\">NjP</subfield></datafield><datafield tag=\"100\" ind1=\"1\" ind2=\" \"><subfield code=\"a\">Grisebach, Eberhard,</subfield><subfield code=\"d\">1880-1945.</subfield><subfield code=\"0\">(uri)http://id.loc.gov/authorities/names/nr98024681</subfield></datafield><datafield tag=\"240\" ind1=\"1\" ind2=\"0\"><subfield code=\"a\">Correspondence.</subfield><subfield code=\"k\">Selections</subfield></datafield><datafield tag=\"245\" ind1=\"1\" ind2=\"0\"><subfield code=\"a\">Philosophie und Theologie in realer Dialektik :</subfield><subfield code=\"b\">Briefwechsel E. Grisebach - Fr. Gogarten, 1921/22 /</subfield><subfield code=\"c\">Herausgegeben von Dr. Michael Freyer.</subfield></datafield><datafield tag=\"260\" ind1=\" \" ind2=\" \"><subfield code=\"a\">Rheinstetten :</subfield><subfield code=\"b\">Schindele,</subfield><subfield code=\"c\">c1979.</subfield></datafield><datafield tag=\"300\" ind1=\" \" ind2=\" \"><subfield code=\"a\">155 p. ;</subfield><subfield code=\"c\">21 cm.</subfield></datafield><datafield tag=\"504\" ind1=\" \" ind2=\" \"><subfield code=\"a\">Bibliography: p. 150-152.</subfield></datafield><datafield tag=\"650\" ind1=\" \" ind2=\"0\"><subfield code=\"a\">Dialectical theology.</subfield><subfield code=\"0\">(uri)http://id.loc.gov/authorities/subjects/sh85037525</subfield></datafield><datafield tag=\"600\" ind1=\"1\" ind2=\"0\"><subfield code=\"a\">Gogarten, Friedrich,</subfield><subfield code=\"d\">1887-1967.</subfield><subfield code=\"0\">(uri)http://id.loc.gov/authorities/names/n50033141</subfield></datafield><datafield tag=\"600\" ind1=\"1\" ind2=\"0\"><subfield code=\"a\">Griesebach, Eberhard,</subfield><subfield code=\"d\">1880-1945.</subfield></datafield><datafield tag=\"650\" ind1=\" \" ind2=\"0\"><subfield code=\"a\">Theologians</subfield><subfield code=\"z\">Germany</subfield><subfield code=\"x\">Correspondence.</subfield><subfield code=\"0\">(uri)http://id.loc.gov/authorities/subjects/sh2010116441</subfield></datafield><datafield tag=\"650\" ind1=\" \" ind2=\"0\"><subfield code=\"a\">Philosophers</subfield><subfield code=\"z\">Germany</subfield><subfield code=\"x\">Correspondence.</subfield><subfield code=\"0\">(uri)http://id.loc.gov/authorities/subjects/sh2008109210</subfield></datafield><datafield tag=\"700\" ind1=\"1\" ind2=\" \"><subfield code=\"a\">Freyer, Michael.</subfield></datafield><datafield tag=\"700\" ind1=\"1\" ind2=\"2\"><subfield code=\"a\">Gogarten, Friedrich,</subfield><subfield code=\"d\">1887-1967.</subfield><subfield code=\"t\">Correspondence.</subfield><subfield code=\"k\">Selections.</subfield><subfield code=\"f\">1979.</subfield><subfield code=\"0\">(uri)http://id.loc.gov/authorities/names/n50033141</subfield></datafield><datafield tag=\"998\" ind1=\" \" ind2=\" \"><subfield code=\"a\">05/12/93</subfield><subfield code=\"s\">9114</subfield><subfield code=\"n\">NjP</subfield><subfield code=\"w\">NJPG82B5840</subfield><subfield code=\"d\">03/16/82</subfield><subfield code=\"c\">SBY</subfield><subfield code=\"b\">SLT</subfield><subfield code=\"i\">930512</subfield><subfield code=\"l\">NJPG</subfield></datafield><datafield tag=\"948\" ind1=\" \" ind2=\" \"><subfield code=\"a\">AACR2</subfield></datafield><datafield tag=\"911\" ind1=\" \" ind2=\" \"><subfield code=\"a\">19930520</subfield></datafield><datafield tag=\"912\" ind1=\" \" ind2=\" \"><subfield code=\"a\">19930512000000.0</subfield></datafield></record></collection>";

    @Test
    public void generateFailureReportEntity() throws Exception {
        DBReportUtil DBReportUtil = new DBReportUtil();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(123);
        bibliographicEntity.setContent(content.getBytes());
        bibliographicEntity.setCreatedBy(new SimpleDateFormat("mm-dd-yyyy").format(new Date()));
        bibliographicEntity.setOwningInstitutionBibId("PUL-Bib-123");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setHoldingsId(3123);
        holdingsEntity.setOwningInstitutionHoldingsId("PUL-Holdings-123");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCreatedDate(new Date());
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setBarcode("Barcode-123");
        List<ReportDataEntity> reportDataEntity = DBReportUtil.generateBibHoldingsAndItemsFailureReportEntities(bibliographicEntity, holdingsEntity, itemEntity);
        assertNotNull(reportDataEntity);
    }

}