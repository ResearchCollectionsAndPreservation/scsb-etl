package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@Data
@CsvRecord(separator = ",", quoting = true, crlf = "UNIX",skipFirstLine = true)
public class DataExportFailureReport {
    @DataField(pos = 1, columnName = "OwningInstitutionBibId")
    private String owningInstitutionBibId;

    @DataField(pos = 2, columnName = "Failure Reason")
    private String failureReason;

    private String filename;

    private String reportType;

    private String requestingInstitutionCode;
}
