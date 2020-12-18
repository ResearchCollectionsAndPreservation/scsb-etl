package org.recap.camel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenchulakshmig on 15/9/16.
 */
@Data
public class EmailPayLoad implements Serializable{

    private Integer itemCount;
    private Integer failedCount;
    private Integer count;

    private String location;
    private String to;
    private String cc;
    private String subject;
    private String fetchType;
    private String requestingInstitution;
    private String transmissionType;
    private String outputFileFormat;

    private List<String> institutionsRequested;
    private List<String> institutions;
    private List<Integer> collectionGroupIds;
}
