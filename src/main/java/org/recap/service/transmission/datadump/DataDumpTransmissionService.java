package org.recap.service.transmission.datadump;

import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by premkb on 28/9/16.
 */
@Service
public class DataDumpTransmissionService {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpTransmissionService.class);

    private List<DataDumpTransmissionInterface> dataDumpTransmissionInterfaceList;

    @Autowired
    private DataDumpFileSystemTranmissionService dataDumpFileSystemTranmissionService;

    @Autowired
    private DataDumpS3TransmissionService DataDumpS3TransmissionService;


    /**
     * Starts transmitting data dump files to a specified path.
     *
     * @param dataDumpRequest the data dump request
     * @param routeMap        the route map
     */
    public void startTranmission(DataDumpRequest dataDumpRequest, Map<String,String> routeMap){
        for(DataDumpTransmissionInterface dataDumpTransmissionInterface:getTransmissionService()){
            if(dataDumpTransmissionInterface.isInterested(dataDumpRequest)){
                try {
                    dataDumpTransmissionInterface.transmitDataDump(routeMap);
                } catch (Exception e) {
                    logger.error(RecapConstants.ERROR,e);
                }
            }
        }
    }

    /**
     * Get transmission service list.
     *
     * @return the list
     */
    public List<DataDumpTransmissionInterface> getTransmissionService(){
        if(CollectionUtils.isEmpty(dataDumpTransmissionInterfaceList)){
            dataDumpTransmissionInterfaceList = new ArrayList<>();
            dataDumpTransmissionInterfaceList.add(dataDumpFileSystemTranmissionService);
            dataDumpTransmissionInterfaceList.add(DataDumpS3TransmissionService);
        }
        return dataDumpTransmissionInterfaceList;
    }
}
