package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by peris on 11/8/16.
 */
public class XmlFormatterUT extends BaseTestCaseUT {
    
    
    @Test
    public void format() throws Exception {
        XmlFormatter xmlFormatter = new XmlFormatter();
        String unformattedXml = FileUtils.readFileToString(new File(getClass().getResource("scsb-sample.xml").toURI()));
        String formattedXml = xmlFormatter.prettyPrint(unformattedXml);
        assertNotNull(formattedXml);
    }
    
    @Test
    public void prettyPrintWithNullException(){
        XmlFormatter xmlFormatter = new XmlFormatter();
        String formattedXml = null;
        try {
            formattedXml = xmlFormatter.prettyPrint(null);
        }catch (NullPointerException e){
            assertNull(formattedXml);
            assertEquals("xml was null or blank in prettyPrint()",e.getMessage());   
        }
    }

    @Test
    public void prettyPrintWithRunTimeException(){
        XmlFormatter xmlFormatter = new XmlFormatter();
        String formattedXml = null;
        try {
            formattedXml = xmlFormatter.prettyPrint("test");
        }catch (RuntimeException e){
            assertNull(formattedXml);
            assertEquals("Error pretty printing xml:\n"+"test",e.getMessage());
        }
    }

}