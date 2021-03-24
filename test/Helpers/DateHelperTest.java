package Helpers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class DateHelperTest {
    DateHelper dateHelper = new DateHelper();
    Date date;
    String year;
    String month;
    String day;
    String hour;
    String minutes;
    String seconds;
    String milliseconds;
    String time;
    long millis;

    @BeforeEach
    void setUp() {
        millis=System.currentTimeMillis();
        date=new java.util.Date(millis);
        year = String.valueOf(date.getYear()+1900);
        month =  String.valueOf((date.getMonth()+1));
        if (month.length() == 1) {
            month = "0" + month;
        }
        day = String.valueOf(date.getDate());
        if (day.length() == 1) {
            day = "0"+day;
        }

        hour = String.valueOf(date.getHours());
        if (hour.length() == 1) {
            hour = "0"+hour;
        }



        minutes = String.valueOf(date.getMinutes());
        if (minutes.length() == 1) {
            minutes = "0"+minutes;
        }
        seconds = String.valueOf(date.getSeconds());
        if (seconds.length() == 1) {
            seconds = "0"+seconds;
        }
        milliseconds = String.valueOf(millis %1000);
        while (milliseconds.length() < 3) {
            milliseconds = "0"+milliseconds;
        }
        time = year + "-" + month + "-" + day + 'T' +
                hour + ":" + minutes + ":" + seconds + "." + milliseconds + "+01:00";

    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void getDate2() throws ParseException {
        Date d29 = dateHelper.getDate(time);
        assertEquals(d29, date);
    }

    @Test
    void getDate() throws ParseException {





        String time22 = year + "-" + month + "-" + day + 'T' +
                hour + ":" + minutes + "+01:00";
        Date d22 = dateHelper.getDate(time22);
        assertEquals(d22.getYear(), date.getYear());
        assertEquals(d22.getMonth(), date.getMonth());
        assertEquals(d22.getDate(), date.getDate());
        assertEquals(d22.getHours(), date.getHours());
        assertEquals(d22.getMinutes(), date.getMinutes());


        String time17 = year + "-" + month + "-" + day + "T " +
                hour + ":" + minutes ;
        Date d17 = dateHelper.getDate(time17);
        assertEquals(d17.getYear(), date.getYear());
        assertEquals(d17.getMonth(), date.getMonth());
        assertEquals(d17.getDate(), date.getDate());
        assertEquals(d17.getHours(), date.getHours());
        assertEquals(d17.getMinutes(), date.getMinutes());


        String time16 = year + "-" + month + "-" + day + "T" +
                hour + ":" + minutes ;
        Date d16 = dateHelper.getDate(time16);
        assertEquals(d16.getYear(), date.getYear());
        assertEquals(d16.getMonth(), date.getMonth());
        assertEquals(d16.getDate(), date.getDate());
        assertEquals(d16.getHours(), date.getHours());
        assertEquals(d16.getMinutes(), date.getMinutes());


        String time5 = hour + ":" + minutes ;
        Date d5 = dateHelper.getDate(time5);
        assertEquals(d5.getHours(), date.getHours());
        assertEquals(d5.getMinutes(), date.getMinutes());


        String timeElse = year + "-" + month +"-" + day +  'T' +
                hour + ":" + minutes + ":" + seconds  + "+01:00" ;
        Date dElse = dateHelper.getDate(timeElse);
        assertEquals(dElse.getYear(), date.getYear());
        assertEquals(dElse.getMonth(), date.getMonth());
        assertEquals(dElse.getDate(), date.getDate());
        assertEquals(dElse.getHours(), date.getHours());
        assertEquals(dElse.getMinutes(), date.getMinutes());

    }

    @Test
    void getDate1() throws ParseException {
        Object object = year + "-" + month + "-" + day + 'T' +
                hour + ":" + minutes + ":" + seconds + "." + milliseconds + "+01:00";
        Date d29 = dateHelper.getDate(object);
        assertEquals(d29, date);

    }

    @Test
    void getDateHHMM() throws ParseException {
        Date hhmm = dateHelper.getDateHHMM(hour+ ":" + minutes);
        assertEquals( Integer.valueOf(hour) ,hhmm.getHours());
        assertEquals(hhmm.getMinutes(), Integer.valueOf(minutes));

        Date hhmm2 = dateHelper.getDateHHMM(date);
        assertEquals( Integer.valueOf(hour) ,hhmm2.getHours());
        assertEquals(hhmm2.getMinutes(), Integer.valueOf(minutes));


    }

    @Test
    void getDateMMDDYYYY() throws ParseException {

        Date mmDDYY = dateHelper.getDateMMDDYYYY(month + "." + day + "."+ year);
        assertEquals( Integer.valueOf(year),2021);
        assertEquals(mmDDYY.getMonth(),  date.getMonth());
        assertEquals(mmDDYY.getDate(), date.getDate());

    }

    @Test
    void getDateYYYYMMDDHHMM() throws ParseException {

        Date hhMMDDYY = dateHelper.getDateYYYYMMDDHHMM(year + "-" + month + "-" + day + 'T' +
                hour + ":" + minutes);
        assertEquals( Integer.valueOf(year),2021);
        assertEquals(hhMMDDYY.getMonth(),  date.getMonth());
        assertEquals(hhMMDDYY.getDate(), date.getDate());
        assertEquals( Integer.valueOf(hour) ,hhMMDDYY.getHours());
        assertEquals(hhMMDDYY.getMinutes(), Integer.valueOf(minutes));

    }

    @Test
    void nowShort() {
        String nowShort= DateHelper.nowShort();
        String a = nowShort.substring(0,nowShort.length()-6);
        String b =time.substring(0, nowShort.length() - 6);

        assertEquals(a,b);
    }

    @Test
    void nowFull() {
        String nowFull= DateHelper.nowFull();
        String a = nowFull.substring(0,nowFull.length()-10);
        String b =time.substring(0, nowFull.length() -10);

        assertEquals(a,b);

        String aa = nowFull.substring(nowFull.length()-5);
        String bb =time.substring(nowFull.length() -5);

        assertEquals(aa,bb);
    }

    @Test
    void getTimeValue() throws ParseException {

        long b = DateHelper.getTimeValue(time);
        assertEquals(millis, b);


        millis =System.currentTimeMillis();
        date=new java.util.Date(millis);

        long a = DateHelper.getTimeValue(date);

        assertEquals(a, millis);





    }
}