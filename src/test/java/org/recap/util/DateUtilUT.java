package org.recap.util;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by premkb on 20/8/16.
 */
public class DateUtilUT extends BaseTestCase {

    @Test
    public void getDateFromString() {
        Calendar cal = Calendar.getInstance();
        System.out.print(cal);
        Date inputDate = cal.getTime();
        DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_MMDDYYY);
        String inputDateString = df.format(inputDate);
        Date outputDate = DateUtil.getDateFromString(inputDateString, RecapConstants.DATE_FORMAT_MMDDYYY);
        assertEquals(inputDate.getDate(), outputDate.getDate());
        assertEquals(inputDate.getMonth(), outputDate.getMonth());
        assertEquals(inputDate.getYear(), outputDate.getYear());
    }

   /* @Test
    public void testDateUtil() {
        DateUtil DateUtil = new DateUtil();
        Date inputDate = DateUtil.getDateFromString("2016-09-02 12:00", RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        assertNotNull(inputDate);
    }
    @Test
    public void testDateUtilCase() {
        DateUtil DateUtil = new DateUtil();
        try{
        Date inputDate = DateUtil.getDateFromString("2016-09", RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
    }catch (Exception e){
    }
        assertTrue(true);
    }
    @Test
    public void testgetDateTimeFromString() {
        DateUtil DateUtil = new DateUtil();
        Date inputDate = DateUtil.getDateTimeFromString("2016-09-02 12:00", RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        assertNotNull(inputDate);
    }
    @Test
    public void testgetDateTimeFromStringCase() {
        DateUtil DateUtil = new DateUtil();
        try {
            Date inputDate = DateUtil.getDateTimeFromString("2016-09-02", RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        }catch (Exception e){
        }
        assertTrue(true);
    }*/
}
