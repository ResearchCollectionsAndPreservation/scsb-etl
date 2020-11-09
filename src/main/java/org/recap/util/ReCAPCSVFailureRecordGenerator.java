package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.RecapConstants;
import org.recap.model.csv.FailureReportReCAPCSVRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SheikS on 8/8/2016.
 */
public class ReCAPCSVFailureRecordGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReCAPCSVFailureRecordGenerator.class);

    /**
     * Prepare failure records csv report for initial data load.
     *
     * @param reportEntity the report entity
     * @return the failure report re capcsv record
     */
    public FailureReportReCAPCSVRecord prepareFailureReportReCAPCSVRecord(ReportEntity reportEntity) {

        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();

        FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new FailureReportReCAPCSVRecord();

        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerName = report.getHeaderName();
            String headerValue = report.getHeaderValue();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(failureReportReCAPCSVRecord, headerValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error(RecapConstants.ERROR,e);
                }
            }
        }
        return failureReportReCAPCSVRecord;
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
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, FailureReportReCAPCSVRecord.class));
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
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, FailureReportReCAPCSVRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.ERROR,e);
        }
        return null;
    }
}
