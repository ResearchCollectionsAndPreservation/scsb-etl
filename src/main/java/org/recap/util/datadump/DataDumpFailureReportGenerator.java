package org.recap.util.datadump;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.RecapConstants;
import org.recap.model.csv.DataDumpFailureReport;
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
 * Created by premkb on 30/9/16.
 */
public class DataDumpFailureReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpFailureReportGenerator.class);

    /**
     * Prepare data dump csv failure record data dump failure report.
     *
     * @param reportEntity the report entity
     * @return the data dump failure report
     */
    public DataDumpFailureReport prepareDataDumpCSVFailureRecord(ReportEntity reportEntity) {

        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();

        DataDumpFailureReport dataDumpFailureReport = new DataDumpFailureReport();

        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerName = report.getHeaderName();
            String headerValue = report.getHeaderValue();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(dataDumpFailureReport, headerValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error(RecapConstants.ERROR,e);
                }
            }
        }
        return dataDumpFailureReport;
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
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, DataDumpFailureReport.class));
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
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, DataDumpFailureReport.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.ERROR,e);
        }
        return null;
    }
}
