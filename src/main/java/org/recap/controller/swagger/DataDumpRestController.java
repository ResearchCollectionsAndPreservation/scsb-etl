package org.recap.controller.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.recap.ScsbConstants;
import org.recap.controller.DataExportTriggerController;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.DataExportHelperService;
import org.recap.service.DataExportValidateService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;
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

    @Autowired private DataDumpExportService dataDumpExportService;
    @Autowired private DataExportValidateService dataExportValidateService;
    @Autowired DataExportHelperService dataExportHelperService;
    @Autowired DataDumpUtil dataDumpUtil;
    @Autowired
    private DataExportTriggerController demoServicePropertiesExample;


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
    @ApiResponses(value = {@ApiResponse(code = 200, message = ScsbConstants.DATADUMP_PROCESS_STARTED)})
    @ResponseBody
    public String exportDataDump(@ApiParam(value = "${swagger.values.institutionCodes}" , required = true, name = "institutionCodes") @RequestParam String institutionCodes,
                                         @ApiParam(value = "${swagger.values.requestingInstitutionCode}",required=true, name = "requestingInstitutionCode") @RequestParam String requestingInstitutionCode,
                                         @ApiParam(value = "${swagger.values.imsDepositoryCodes}" , required = true , name = "imsDepositoryCodes") @RequestParam String imsDepositoryCodes,
                                         @ApiParam(value = "Type of export - Incremental (use 1) or Deleted (use 2)" , required = true , name = "fetchType") @RequestParam String fetchType,
                                         @ApiParam(value = "Type of format - Marc xml (use 0) or SCSB xml (use 1), for deleted records only json format (use 2)",required=true, name = "outputFormat") @RequestParam String outputFormat,
                                         @ApiParam(value = "Get updates to middleware collection since the date provided. Default will be updates since the previous day. Date format will be a string (yyyy-MM-dd HH:mm)", name = "date") @RequestParam(required=false) String date,
                                         @ApiParam(value = "Get updates to middleware collection until the date provided. Default will be updates since the previous day. Date format will be a string (yyyy-MM-dd HH:mm)", name = "toDate") @RequestParam(required=false) String toDate,
                                         @ApiParam(value = "Collection group id will get the relevant info based on the id provided. Default will get both shared and open information - Shared (use 1), Open (use 2), Both (use 1,2)", name = "collectionGroupIds") @RequestParam(required=false) String collectionGroupIds,
                                         @ApiParam(value = "Type of transmission - S3 (use 0), HTTP Response (use 1) this parameter is not considered for full dump. Default will be S3 ", name = "transmissionType")@RequestParam(required=false) String transmissionType,
                                         @ApiParam(value = "Email address to whom we need to send an email" , name = "emailToAddress")@RequestParam(required=false) String emailToAddress,
                                         @RequestParam(required=false) String userName
    ){
        ScsbConstants.EXPORT_SCHEDULER_CALL = false;
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setRequestFromSwagger(true);
        dataDumpUtil.setDataDumpRequest(dataDumpRequest,fetchType,institutionCodes,date, toDate, collectionGroupIds,transmissionType,requestingInstitutionCode,emailToAddress,outputFormat,imsDepositoryCodes,userName);
        String responseMessage = dataExportValidateService.validateIncomingRequest(dataDumpRequest);
        if(responseMessage !=null) {
            return responseMessage;
        }
        return dataExportHelperService.checkForExistingRequestAndStart(dataDumpRequest,false);
    }

    @GetMapping(value="/exportDataDumpTriggerManually")
    @ApiOperation(value = "exportDataDumpTriggerManually",
            notes = "Export datadumps to institutions", nickname = "exportDataDumpTriggerManually", position = 2)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ScsbConstants.DATADUMP_PROCESS_STARTED)})
    @ResponseBody
    public String exportDataDumpTrigger(){
        demoServicePropertiesExample.dataExportTrigger();
        return "It will validate and if any request is waiting will start process and will send an email notification upon completion";
    }
}
