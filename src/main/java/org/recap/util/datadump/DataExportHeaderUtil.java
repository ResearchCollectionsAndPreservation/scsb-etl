package org.recap.util.datadump;

import org.recap.model.export.DataDumpRequest;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by peris on 11/5/16.
 */
@Component
public class DataExportHeaderUtil {
    /**
     * Gets value for given key from headers.
     *
     * @param batchHeaderString the batch header string
     * @param key               the key
     * @return the value for
     */
    public String getValueFor(String batchHeaderString, String key) {
        StringTokenizer stringTokenizer = new StringTokenizer(batchHeaderString, ";");
        while(stringTokenizer.hasMoreTokens()){
            StringTokenizer stringTokenizer1 = new StringTokenizer(stringTokenizer.nextToken(), "#");
            if(stringTokenizer1.nextToken().equals(key)){
                return stringTokenizer1.nextToken();
            }
        }
        return null;
    }

    /**
     * Gets batch header string.
     *
     * @param totalPageCount   the total page count
     * @param currentPageCount the current page count
     * @param folderName       the folder name
     * @param fileName         the file name
     * @param dataDumpRequest  the data dump request
     * @return the batch header string
     */
    public String getBatchHeaderString(Integer totalPageCount, Integer currentPageCount, String folderName, String fileName, DataDumpRequest dataDumpRequest) {
        StringBuilder headerString = new StringBuilder();
        headerString.append("totalPageCount")
                .append("#")
                .append(totalPageCount)
                .append(";")
                .append("currentPageCount")
                .append("#")
                .append(currentPageCount)
                .append(";")
                .append("folderName")
                .append("#")
                .append(folderName)
                .append(";")
                .append("fileName")
                .append("#")
                .append(fileName)
                .append(";")
                .append("institutionCodes")
                .append("#")
                .append(getInstitutionCodes(dataDumpRequest.getInstitutionCodes()))
                .append(";")
                .append("imsDepositoryCodes")
                .append("#")
                .append(getInstitutionCodes(dataDumpRequest.getImsDepositoryCodes()))
                .append(";")
                .append("fileFormat")
                .append("#")
                .append(dataDumpRequest.getOutputFileFormat())
                .append(";")
                .append("fetchType")
                .append("#")
                .append(dataDumpRequest.getFetchType())
                .append(";")
                .append("exportFromDate")
                .append("#")
                .append(dataDumpRequest.getDate())
                .append(";")
                .append("collectionGroupIds")
                .append("#")
                .append(getCollectionGroupIds(dataDumpRequest))
                .append(";")
                .append("transmissionType")
                .append("#")
                .append(dataDumpRequest.getTransmissionType())
                .append(";")
                .append("toEmailId")
                .append("#")
                .append(dataDumpRequest.getToEmailAddress())
                .append(";")
                .append("dateTimeString")
                .append("#")
                .append(dataDumpRequest.getDateTimeString())
                .append(";")
                .append("requestingInstitutionCode")
                .append("#")
                .append(dataDumpRequest.getRequestingInstitutionCode())
                .append(";")
                .append("exportFormat")
                .append("#")
                .append(dataDumpRequest.getOutputFileFormat())
                .append(";")
                .append("requestId")
                .append("#")
                .append(dataDumpRequest.getRequestId())
                .append(";")
                .append("etlRequestId")
                .append("#")
                .append(dataDumpRequest.getEtlRequestId())
                .append(";")
                .append("isRequestFromSwagger")
                .append("#")
                .append(dataDumpRequest.isRequestFromSwagger());

        return headerString.toString();
    }

    /**
     * Gets a string with values appended by comma from data dump request.
     * @param values
     * @return
     */
    private static String getInstitutionCodes(List<String> values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Iterator<String> iterator = values.iterator(); iterator.hasNext(); ) {
            String code = iterator.next();
            stringBuilder.append(code);
            if(iterator.hasNext()){
                stringBuilder.append("*");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Gets a string with collection group ids appended by comma from data dump request.
     * @param dataDumpRequest
     * @return
     */
    private static String getCollectionGroupIds(DataDumpRequest dataDumpRequest) {
        List<Integer> institutionCodes = dataDumpRequest.getCollectionGroupIds();
        StringBuilder stringBuilder = new StringBuilder();
        for (Iterator<Integer> iterator = institutionCodes.iterator(); iterator.hasNext(); ) {
            String code = String.valueOf(iterator.next());
            stringBuilder.append(code);
            if(iterator.hasNext()){
                stringBuilder.append("*");
            }
        }
        return stringBuilder.toString();
    }

}
