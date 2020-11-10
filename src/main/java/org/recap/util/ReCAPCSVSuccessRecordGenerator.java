package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.RecapConstants;
import org.recap.model.csv.SuccessReportReCAPCSVRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by angelind on 18/8/16.
 */
public class ReCAPCSVSuccessRecordGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReCAPCSVSuccessRecordGenerator.class);

    /**
     * Prepare success records csv report for initial data load.
     *
     * @param reportEntity the report entity
     * @return the success report re capcsv record
     */
    public SuccessReportReCAPCSVRecord prepareSuccessReportReCAPCSVRecord(ReportEntity reportEntity) {

        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();

        SuccessReportReCAPCSVRecord successReportReCAPCSVRecord = new SuccessReportReCAPCSVRecord();

        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerName = report.getHeaderName();
            String headerValue = report.getHeaderValue();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(successReportReCAPCSVRecord, headerValue);
                } catch (Exception e) {
                    logger.error(RecapConstants.ERROR,e);
                }
            }
        }
        return successReportReCAPCSVRecord;
    }

    /**
     * Gets setter method for the given name.
     *
     * @param propertyName the property name
     * @return the setter method
     */
    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, SuccessReportReCAPCSVRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.ERROR,e);
        }
        return null;
    }

    /**
     * Gets getter method for the given name.
     *
     * @param propertyName the property name
     * @return the getter method
     */
    public Method getGetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, SuccessReportReCAPCSVRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.ERROR,e);
        }
        return null;
    }

}
