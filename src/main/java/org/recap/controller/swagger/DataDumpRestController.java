package org.recap.controller.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.recap.RecapConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.preprocessor.DataDumpExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by premkb on 19/8/16.
 */
@RestController
@RequestMapping("/dataDump")
@Api(value="dataDump", description="Export data dump", position = 1)
public class DataDumpRestController {


    @Autowired
    private DataDumpExportService dataDumpExportService;

    @Autowired
    private DynamicRouteBuilder dynamicRouteBuilder;

    /**
     * Gets data dump export service.
     *
     * @return the data dump export service
     */
    public DataDumpExportService getDataDumpExportService() {
        return dataDumpExportService;
    }

    /**
     * Gets dynamic route builder.
     *
     * @return the dynamic route builder
     */
    public DynamicRouteBuilder getDynamicRouteBuilder() {
        return dynamicRouteBuilder;
    }

    /**
     * API to initiate the data dump export.
     *
     * @param institutionCodes          the institution codes
     * @param requestingInstitutionCode the requesting institution code
     * @param fetchType                 the fetch type
     * @param outputFormat              the output format
     * @param date                      the date
     * @param toDate                    the toDate
     * @param collectionGroupIds        the collection group ids
     * @param transmissionType          the transmission type
     * @param emailToAddress            the email to address
     * @return string
     */
    @GetMapping(value="/exportDataDump")
    @ApiOperation(value = "exportDataDump",
            notes = "Export datadumps to institutions", nickname = "exportDataDump", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = RecapConstants.DATADUMP_PROCESS_STARTED)})
    @ResponseBody
    public String exportDataDump(@ApiParam(value = "Code of institutions whose shared collection updates are requested. Use PUL for Princeton, CUL for Columbia and NYPL for NYPL." , required = true, name = "institutionCodes") @RequestParam String institutionCodes,
                                         @ApiParam(value = "Code of insitituion who is requesting. Use PUL for Princeton, CUL for Columbia and NYPL for NYPL. ",required=true, name = "requestingInstitutionCode") @RequestParam String requestingInstitutionCode,
                                         @ApiParam(value = "Type of export - Incremental (use 1) or Deleted (use 2)" , required = true , name = "fetchType") @RequestParam String fetchType,
                                         @ApiParam(value = "Type of format - Marc xml (use 0) or SCSB xml (use 1), for deleted records only json format (use 2)",required=true, name = "outputFormat") @RequestParam String outputFormat,
                                         @ApiParam(value = "Get updates to middleware collection since the date provided. Default will be updates since the previous day. Date format will be a string (yyyy-MM-dd HH:mm)", name = "date") @RequestParam(required=false) String date,
                                         @ApiParam(value = "Get updates to middleware collection until the date provided. Default will be updates since the previous day. Date format will be a string (yyyy-MM-dd HH:mm)", name = "toDate") @RequestParam(required=false) String toDate,
                                         @ApiParam(value = "Collection group id will get the relevant info based on the id provided. Default will get both shared and open information - Shared (use 1), Open (use 2), Both (use 1,2)", name = "collectionGroupIds") @RequestParam(required=false) String collectionGroupIds,
                                         @ApiParam(value = "Type of transmission - FTP (use 0), HTTP Response (use 1) this parameter is not considered for full dump. Default will be ftp ", name = "transmissionType")@RequestParam(required=false) String transmissionType,
                                         @ApiParam(value = "Email address to whom we need to send an email" , name = "emailToAddress")@RequestParam(required=false) String emailToAddress
    ){
        RecapConstants.EXPORT_SCHEDULER_CALL = false;
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        getDynamicRouteBuilder().addDataDumpExportRoutes();
        getDataDumpExportService().setDataDumpRequest(dataDumpRequest,fetchType,institutionCodes,date, toDate, collectionGroupIds,transmissionType,requestingInstitutionCode,emailToAddress,outputFormat);
        String responseMessage = getDataDumpExportService().validateIncomingRequest(dataDumpRequest);
        if(responseMessage!=null) {
            return responseMessage;
        }
        responseMessage = getDataDumpExportService().startDataDumpProcess(dataDumpRequest);
        return responseMessage;
    }
}
