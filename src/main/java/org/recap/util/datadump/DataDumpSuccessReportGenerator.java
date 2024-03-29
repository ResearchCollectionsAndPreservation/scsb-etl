package org.recap.util.datadump;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.ScsbConstants;
import org.recap.model.csv.DataDumpSuccessReport;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by premkb on 30/9/16.
 */
@Slf4j
public class DataDumpSuccessReportGenerator {



    /**
     * Prepare data dump csv success record data dump success report.
     *
     * @param reportEntity the report entity
     * @return the data dump success report
     */
    public DataDumpSuccessReport prepareDataDumpCSVSuccessRecord(ReportEntity reportEntity) {

        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();

        DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();

        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerName = report.getHeaderName();
            String headerValue = report.getHeaderValue();
            if(!headerName.equalsIgnoreCase(ScsbConstants.TO_EMAIL_ID)) {
                Method setterMethod = getSetterMethod(headerName);
                if (null != setterMethod) {
                    try {
                        setterMethod.invoke(dataDumpSuccessReport, headerValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error(ScsbConstants.ERROR, e);
                    }
                }
            }
        }
        return dataDumpSuccessReport;
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
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, DataDumpSuccessReport.class));
        } catch (IntrospectionException e) {
            log.error(ScsbConstants.ERROR,e);
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
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, DataDumpSuccessReport.class));
        } catch (IntrospectionException e) {
            log.error(ScsbConstants.ERROR,e);
        }
        return null;
    }
}
