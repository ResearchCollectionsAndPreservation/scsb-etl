package org.recap.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

/**
 * Created by peris on 11/8/16.
 */
@Component
public class XmlFormatter {
    /**
     * Formats the given xml string.
     *
     * @param xml the xml
     * @return the string
     */
    public String prettyPrint(final String xml) {
        if (StringUtils.isBlank(xml)) {
            throw new NullPointerException("xml was null or blank in prettyPrint()");
        }

        final StringWriter sw;

        try {
            final OutputFormat format = OutputFormat.createPrettyPrint();
            final org.dom4j.Document document = DocumentHelper.parseText(xml);
            sw = new StringWriter();
            final XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
        } catch (Exception e) {
            throw new RuntimeException("Error pretty printing xml:\n" + xml, e);
        }
        String[] xmlArray = StringUtils.split(sw.toString(), '\n');
        Object[] xmlContent = ArrayUtils.subarray(xmlArray, 1, xmlArray.length);
        StringBuilder xmlStr = new StringBuilder();
        for (Object object : xmlContent) {
            xmlStr.append(object.toString());
            xmlStr.append('\n');
        }
        return xmlStr.toString();
    }
}
