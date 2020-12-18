package org.recap.util.datadump;

import org.recap.RecapConstants;
import org.springframework.stereotype.Component;

@Component
public class DataDumpUtil {

    public String getFetchType(String fetchTypeNumber) {
        String fetchType ="";
        switch (fetchTypeNumber) {
            case RecapConstants.DATADUMP_FETCHTYPE_FULL:
                fetchType= RecapConstants.EXPORT_TYPE_FULL;
                break;
            case RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL:
                fetchType= RecapConstants.INCREMENTAL;
                break;
            case RecapConstants.DATADUMP_FETCHTYPE_DELETED:
                fetchType= RecapConstants.DELETED;
                break;
            default:
                fetchType= "Export";
        }
        return fetchType;
    }

    public String getOutputformat(String outputFileFormat) {
        String format ="";
        switch (outputFileFormat) {
            case RecapConstants.DATADUMP_XML_FORMAT_MARC:
                format= RecapConstants.MARC;
                break;
            case RecapConstants.DATADUMP_XML_FORMAT_SCSB:
                format= RecapConstants.SCSB;
                break;
            case RecapConstants.DATADUMP_DELETED_JSON_FORMAT:
                format= RecapConstants.JSON;
                break;
        }
        return format;
    }

    public String getTransmissionType(String transmissionType) {
        String type ="";
        switch (transmissionType) {
            case RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP:
                type= "FTP";
                break;
            case RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP:
                type= "HTTP";
                break;
            case RecapConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM:
                type= "Filesystem";
                break;
        }
        return type;
    }
}
