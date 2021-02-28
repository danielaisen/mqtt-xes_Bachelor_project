/**
 * @author Daniel Max Aisen (s171206)
 **/

package temp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    static Date myDate;
    static String myDateString;

    public static Date getDate(String time) throws ParseException {

        if (time.length() == 29) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            dateFormat.parse(time);
            myDate = dateFormat.parse(time);
        }
        else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            dateFormat.parse(time);
            myDate = dateFormat.parse(time);
        }
        return myDate;
    }

    public static Date getDate(Object object) throws ParseException {
        return getDate((String) object);
    }

    public static Date getDateHHMM(Object object) throws ParseException {
        SimpleDateFormat hoursMinFormat = new SimpleDateFormat("HH:mm");
        return hoursMinFormat.parse((String) object);
    }
    public static Date getDateMMDDYYYY(Object object) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
        return dateFormat.parse((String) object);
    }


    public String getNowShort() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX");
        myDateString =  dateFormat.format(new Date());
        return myDateString;
    }
    public String getNowFull() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        myDateString =  dateFormat.format(new Date());
        return myDateString;
    }


    public Date shortDate(Object time) {

        Date date  = new Date();
        return myDate;
    }






}
