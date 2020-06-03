package org.recap.model.etl;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * Created by rajeshbabuk on 22/6/16.
 */
@Data
public class EtlLoadRequest {
    private String fileName;
    private Integer batchSize;
    private MultipartFile file;
    private String userName;
    private String owningInstitutionName;
    private String reportFileName;
    private String reportType;
    private String operationType;
    private String transmissionType;
    private String reportInstitutionName;
    private Date dateFrom;
    private Date dateTo;
}
