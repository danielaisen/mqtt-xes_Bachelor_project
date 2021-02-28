package temp;

import fileConstructorXES.FilesHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static requestRespond.specificalApI.RejsePlanCall.*;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class RearangeSJONToProccesAware {

    public static void main(String[] args) throws ParseException {
        int i = 0, j =0;

    JSONArray jsonArrayRearangedArray = new JSONArray();
    JSONArray V1jsonArrayRearangedArray = new JSONArray();

    JSONArray jsonArrayTimeSeries = FilesHelper.readJSONArrayFile("timeSeriesJSON2");
        for (Object object : jsonArrayTimeSeries) {
            JSONObject jsonObjectTimeSeriesOrdered = new JSONObject();
            JSONObject V1jsonObjectTimeSeriesOrdered = new JSONObject();

            if (i == 2) {
                i=0;
                break;
            } i++;

            JSONObject objectTimeSeries = (JSONObject) object;
//            System.out.println(jsonArrayTimeSeries.size());
            Date date = DateHelper.getDate(objectTimeSeries.get("time"));
            objectTimeSeries.remove("time");
            HashMap<String , String> myTEST = new HashMap<>();
            jsonObjectTimeSeriesOrdered.put("time",date);
            for (Object key : objectTimeSeries.keySet()) {

                if (j == 2) {
                    j=0;
                    break;
                } j++;


                JSONObject event = (JSONObject) objectTimeSeries.get(key);



                String string = event.toString();

                ArrayList<JSONObject> eventAndTraceOrTopic =parseRejsePlanReturnStopsAndTraceSimple(string);
                ArrayList<JSONObject> V1eventAndTraceOrTopic =V1parseRejsePlanReturnStopsAndTraceSimple(string);
//                for (JSONObject a : eventAndTraceOrTopic) {
//                    System.out.println(a);
//                }

                findRelevatStations((JSONObject) V1jsonObjectTimeSeriesOrdered.get(1), date);

//                retrieveInformationFromObjectUSINGSIMPLE(null, event, myTEST); //not working jet. gather all the data.

                jsonObjectTimeSeriesOrdered.put(key, eventAndTraceOrTopic);
                V1jsonObjectTimeSeriesOrdered.put(key, V1eventAndTraceOrTopic);
            }

            jsonArrayRearangedArray.add(jsonObjectTimeSeriesOrdered);
            V1jsonArrayRearangedArray.add(jsonObjectTimeSeriesOrdered);


        }



        System.out.println("done RearangeSJONToProccesAware");

    }


    public static ArrayList<org.json.simple.JSONObject> V1parseRejsePlanReturnStopsAndTraceSimple(String responseBody) {
        ArrayList<org.json.simple.JSONObject> objects = new ArrayList<org.json.simple.JSONObject>();



        org.json.simple.JSONObject journeyDetail = (org.json.simple.JSONObject) JSONValue.parse(responseBody);


//        org.json.simple.JSONObject journeyDetail = (org.json.simple.JSONObject) mainJSONObject.get("JourneyDetail");
        List<String> namesJourneyDetail = new ArrayList<String>(journeyDetail.keySet());
//        System.out.println(namesJourneyDetail); //todo delete this print


        HashMap<String, String> traceInfo = new HashMap<>();
        org.json.simple.JSONObject stopsObject = new org.json.simple.JSONObject();

        for (Object keys : journeyDetail.keySet()) {
            if (keys.equals("Stop")) {
                org.json.simple.JSONArray stops = (org.json.simple.JSONArray) journeyDetail.get("Stop");
                V1arrangeStopData(stops);
                stopsObject.put("XES_Type", "Events"); //todo add activity name
                stopsObject.put("Events", stops);
            }
            else if (keys.equals("noNamespaceSchemaLocation")){} //delete this object
            else {
                Object tempObject = journeyDetail.get(keys);
                retrieveInformationFromObjectUSINGSIMPLE((String) keys, tempObject, traceInfo);
            }
        }
        org.json.simple.JSONObject traceObject = new org.json.simple.JSONObject(traceInfo);
        traceObject.put("XES_Type", "Trace_Info");
        objects.add(traceObject);
        objects.add(stopsObject);
        return objects;
    }



    private static void V1arrangeStopData(org.json.simple.JSONArray stops) {
        System.out.print("updating the stop objects ");

//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
//        SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        for (int i = 0; i < stops.size(); i++) {
            org.json.simple.JSONObject stop = (org.json.simple.JSONObject) stops.get(i);
            List<String> stopAttributes = new ArrayList<String>(stop.keySet());
            int daysArrivalDiff = 0;
            int daysDepartureDiff = 0;
            int departureDiff =0;
            int arrivalDiff = 0;
            for (String attribute : stopAttributes) {
                try {
                    if (attribute.equals("rtArrDate")) {
                        daysArrivalDiff = V1clearingDateDaysAttributes(stop, attribute, "arrDate");
                        continue;
                    }
                    if (attribute.equals("rtDepDate")) {
                        daysDepartureDiff = V1clearingDateDaysAttributes( stop, attribute, "depDate");
                        continue;
                    }
                    if (attribute.equals("rtArrTime")) {
                        arrivalDiff = V1cleaningMinutesAttribute(stop, attribute, "arrTime");
                        continue;
                    }

                    if (attribute.equals("rtDepTime")) {
                        departureDiff = V1cleaningMinutesAttribute(stop, attribute, "depTime");
                        continue;
                    }
                    else if (attribute.equals("arrDate") || attribute.equals("arrTime") || attribute.equals("depTime")) {    //do nothing
                        continue;
                    }
                    else { //delete all empty
                        if (((String) stop.get(attribute)).length() ==0){
                            stop.remove(attribute);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            stop.put("time:timestamp", ""); //todo figure our time stamp in the original file creator
            stop.put("Event_Name", "stop");
            stop.put("daysArrivalDiff", daysArrivalDiff);
            System.out.println();
            stop.put("arrivalDiff", arrivalDiff);
            stop.put("daysDepartureDiff", daysDepartureDiff);
            stop.put("departureDiff", departureDiff);
        }
    }

    private static int V1cleaningMinutesAttribute(org.json.simple.JSONObject stop, String attribute, String depTime) throws ParseException {
        int departureDiff;
        Date firstDate = DateHelper.getDateHHMM(stop.get(attribute));
//        Date firstDate = hoursMinFormat.parse((String) stop.get(attribute));
//        Date secondDate = hoursMinFormat.parse((String) stop.get(depTime));
        Date secondDate = DateHelper.getDateHHMM(stop.get(depTime));
        departureDiff = (int) (TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()))); //60 second, 1000 mili seconds
        stop.remove(attribute);
        return departureDiff;
    }

    private static int V1clearingDateDaysAttributes( org.json.simple.JSONObject stop, String attribute, String depDate) throws ParseException {
        int daysDepartureDiff;
        String s = (String) stop.get(attribute);
        Date firstDate = DateHelper.getDateMMDDYYYY(s.substring(0, 6) + "20" + s.substring(6));
        s = (String) stop.get(depDate);
        Date secondDate = DateHelper.getDateMMDDYYYY(s.substring(0, 6) + "20" + s.substring(6));
        daysDepartureDiff = (int) TimeUnit.DAYS.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()));
        stop.remove(attribute);
        return daysDepartureDiff;
    }


    private static void findRelevatStations(JSONObject stops, Date time) {

        if (false) {
            //todo add a check that it is the same calender date
        }


        int depTime;
        int arrTime;

        for (int i = 0; i < stops.size(); i++) {
            if (i == 0) {
                depTime = (int) ((Date)((JSONObject) stops.get(0)).get("depTime")).getTime();
            }



        }
    }


}
