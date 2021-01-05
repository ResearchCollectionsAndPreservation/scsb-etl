package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by premkb on 20/8/16.
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private DateUtil() {}

    /**
     * Get date from string date.
     *
     * @param inputDateString the input date string
     * @param dateFormat      the date format
     * @return the date
     */
    public static Date getDateFromString(String inputDateString,String dateFormat){
        Date outputDate=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            if(inputDateString != null) {
                outputDate = sdf.parse(inputDateString);
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return outputDate;
    }

    /**
     * Get date time from string date.
     *
     * @param inputDateTimeString the input date time string
     * @param dateTimeFormat      the date time format
     * @return the date
     */
    public static Date getDateTimeFromString(String inputDateTimeString, String dateTimeFormat){
        Date outputDateTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        try{
            LocalDateTime parsedDateTime = LocalDateTime.parse(inputDateTimeString, formatter);
            outputDateTime = Date.from(parsedDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }catch (Exception e){
            logger.error(RecapConstants.ERROR,e);
        }
        return outputDateTime;
    }

    /**
     * Gets UTC formatted date string.
     *
     * @param inputDateString the input date string
     * @return the formatted date string
     */
    public static String getFormattedDateString(String inputDateString, String inputToDateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        String dateString = null;
        try {
            DateFormat format = new SimpleDateFormat(RecapCommonConstants.UTC_DATE_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone(RecapCommonConstants.UTC));

            Date fromDate = simpleDateFormat.parse(inputDateString);
            String fromDateStr = format.format(fromDate);
            dateString = fromDateStr + RecapCommonConstants.SOLR_DATE_RANGE_TO_NOW;
            if (StringUtils.isNotBlank(inputToDateString)) {
                Date toDate = simpleDateFormat.parse(inputToDateString);
                String toDateStr = format.format(toDate);
                dateString = fromDateStr + " TO " + toDateStr;
            }
            logger.info("Date range for solr : {} " , dateString);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return dateString;
    }
}
