package org.recap.util;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by premkb on 20/8/16.
 */
public class DateUtilUT extends BaseTestCaseUT {

    @Test
    public void getDateFromString() {
        Calendar cal = Calendar.getInstance();
        Date inputDate = cal.getTime();
        DateFormat df = new SimpleDateFormat(ScsbConstants.DATE_FORMAT_MMDDYYY);
        String inputDateString = df.format(inputDate);
        Date outputDate = DateUtil.getDateFromString(inputDateString, ScsbConstants.DATE_FORMAT_MMDDYYY);
        assertNotNull(outputDate);
    }

    @Test
    public void testgetDateTimeFromString() {
        Date inputDate = DateUtil.getDateTimeFromString("2016-09-02 12:00", ScsbCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        assertNotNull(inputDate);
    }

    @Test
    public void testgetDateTimeFromStringCase() {
        try {
            Date inputDate = DateUtil.getDateTimeFromString("2016-09-02", ScsbCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
            assertNull(inputDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }


    @Test
    public void getFormattedDateString() {
        Calendar cal = Calendar.getInstance();
        Date inputDate = cal.getTime();
        DateFormat df = new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        String inputDateString = df.format(inputDate);
        String dateString = DateUtil.getFormattedDateString(inputDateString, inputDateString);
        assertNotNull(dateString);
    }

    @Test
    public void getFormattedDateStringException() {
        String dateString = DateUtil.getFormattedDateString(new Date().toString(), new Date().toString());
        assertNull(dateString);
    }
}
