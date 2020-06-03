package org.recap.model.export;

import lombok.Data;

import java.util.List;

/**
 * Created by premkb on 19/8/16.
 */
@Data
public class DataDumpRequest {
    private List<String> institutionCodes;
    private String fetchType;
    private String date;
    private String toDate;
    private boolean isRecordsAvailable;
    private List<Integer> collectionGroupIds;
    private String transmissionType;
    private String requestingInstitutionCode;
    private String toEmailAddress;
    private String outputFileFormat;
    private String dateTimeString;
    private String requestId;
}
