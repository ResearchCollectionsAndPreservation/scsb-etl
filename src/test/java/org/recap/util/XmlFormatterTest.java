package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

/**
 * Created by peris on 11/8/16.
 */
public class XmlFormatterTest {
    @Test
    public void format() throws Exception {
        XmlFormatter xmlFormatter = new XmlFormatter();
        String unformattedXml = FileUtils.readFileToString(new File(getClass().getResource("scsb-sample.xml").toURI()));
        String formattedXml = xmlFormatter.prettyPrint(unformattedXml);
        System.out.println(formattedXml);
    }

}