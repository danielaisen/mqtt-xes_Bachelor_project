package temp;

import fileConstructorXES.FilesHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import javax.json.JsonObject;
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

    JSONArray totallSumationOfAll = new JSONArray();
    JSONArray traceIsForEachLine = new JSONArray();
//    JSONObject finalReadyXESLineLog = new JSONObject();
    JSONArray finalReadyXESLineLog = new JSONArray();
//    finalReadyXESLineLog.put("XES_Type", "Log_Line_Log");

    JSONArray jsonArrayTimeSeries = FilesHelper.readJSONArrayFile("timeSeriesJSON2");
        for (Object object : jsonArrayTimeSeries) {
//            JSONObject jsonObjectTimeSeriesOrdered = new JSONObject();
            JSONObject V1jsonObjectTimeSeriesOrdered = new JSONObject();


            JSONObject objectTimeSeries = (JSONObject) object;
//            System.out.println(jsonArrayTimeSeries.size());
            Date date = DateHelper.getDate(objectTimeSeries.get("time"));
            objectTimeSeries.remove("time");
//            HashMap<String , String> myTEST = new HashMap<>();
//            jsonObjectTimeSeriesOrdered.put("time",date);
            V1jsonObjectTimeSeriesOrdered.put("time",date);
            for (Object key : objectTimeSeries.keySet()) {
                if (key.equals("time:timestamp")) {
                    continue;
                }


//                String string = (objectTimeSeries.get(key)).toString();

//                ArrayList<JSONObject> eventAndTraceOrTopic =parseRejsePlanReturnStopsAndTraceSimple(string);


                ArrayList<Object> V1eventAndTraceOrTopic =V1parseRejsePlanReturnStopsAndTraceSimple((JSONObject) objectTimeSeries.get(key));
//                for (JSONObject a : eventAndTraceOrTopic) {
//                    System.out.println(a);
//                }
                JSONObject event = findRelevatStations((JSONArray) V1eventAndTraceOrTopic.get(0), date);
                JSONObject trace = (JSONObject) V1eventAndTraceOrTopic.get(1);

//                retrieveInformationFromObjectUSINGSIMPLE(null, event, myTEST); //not working jet. gather all the data.

//                jsonObjectTimeSeriesOrdered.put(key, V1eventAndTraceOrTopic);
                V1jsonObjectTimeSeriesOrdered.put(key+"_event0_trace_1", V1eventAndTraceOrTopic);

                if (finalReadyXESLineLog.isEmpty()) {
                    JSONArray events = new JSONArray();
                    events.add(event);
                    trace.put("Events", events);
                    finalReadyXESLineLog.add(trace);
                    continue;
                }
                int index = containTraceObjectNumber(finalReadyXESLineLog, trace);
                if (index == -1) {
                    JSONArray events = new JSONArray();
                    events.add(event);
                    trace.put("Events", events);
                    finalReadyXESLineLog.add(trace);
                } else {
                    JSONObject thisTrace = (JSONObject) finalReadyXESLineLog.get(index);
                    JSONArray events = (JSONArray) thisTrace.get("Events");
                    events.add(event);
                }

            }

            totallSumationOfAll.add(V1jsonObjectTimeSeriesOrdered);
//            traceIsForEachLine.add(jsonObjectTimeSeriesOrdered);

        }
        String nameFile = "traceByLine";
        FilesHelper.createFileToJSONSimple(nameFile, finalReadyXESLineLog);

        System.out.println("done RearangeSJONToProccesAware. Saved as " + nameFile);

    }


    public static ArrayList<Object> V1parseRejsePlanReturnStopsAndTraceSimple(JSONObject journeyDetail) {
        ArrayList<Object> objects = new ArrayList<>();


//        org.json.simple.JSONObject journeyDetail = (org.json.simple.JSONObject) mainJSONObject.get("JourneyDetail");
//        List<String> namesJourneyDetail = new ArrayList<String>(journeyDetail.keySet());
//        System.out.println(namesJourneyDetail); //todo delete this print


        HashMap<String, String> traceInfo = new HashMap<>();
        org.json.simple.JSONObject stopsObject = new org.json.simple.JSONObject();

        for (Object keys : journeyDetail.keySet()) {
            if (keys.equals("Stop")) {
                org.json.simple.JSONArray stops = (org.json.simple.JSONArray) journeyDetail.get("Stop");
                V1arrangeStopData(stops);
//                stopsObject.put("XES_Type", "Events"); //todo add activity name
                objects.add(stops);
                stopsObject.put("Events", stops);
            }
            else if (keys.equals("noNamespaceSchemaLocation")){} //delete this object
            else {
                Object tempObject = journeyDetail.get(keys);
                retrieveInformationFromObjectUSINGSIMPLE((String) keys, tempObject, traceInfo);
            }
        }
        org.json.simple.JSONObject traceObject = new org.json.simple.JSONObject(traceInfo);
        JSONArray events = (JSONArray) stopsObject.get("Events");
        traceObject.put("depTime", ((JSONObject) events.get(0) ).get("depTime"));
        traceObject.put("depDate", ((JSONObject) events.get(0) ).get("depDate"));
        traceObject.put("arrTime", ((JSONObject) events.get(stopsObject.size())).get("arrTime"));
        traceObject.put("XES_Type", "Trace_Info");
        objects.add(traceObject);
        objects.add(stopsObject);
        return objects;
    }



    private static void V1arrangeStopData(org.json.simple.JSONArray stops) {
//        System.out.print("updating the stop objects ");

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
//            System.out.println();
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
//        stop.remove(attribute);
        return departureDiff;
    }

    private static int V1clearingDateDaysAttributes( org.json.simple.JSONObject stop, String attribute, String depDate) throws ParseException {
        int daysDepartureDiff;
        String s = (String) stop.get(attribute);
        Date firstDate = DateHelper.getDateMMDDYYYY(s.substring(0, 6) + "20" + s.substring(6));
        s = (String) stop.get(depDate);
        Date secondDate = DateHelper.getDateMMDDYYYY(s.substring(0, 6) + "20" + s.substring(6));
        daysDepartureDiff = (int) TimeUnit.DAYS.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()));
//        stop.remove(attribute);
        return daysDepartureDiff;
    }


    private static JSONObject findRelevatStations(JSONArray stops, Date date) throws ParseException {

        if (false) {
            //todo add a check that it is the same calender date
        }
        JSONObject event = new JSONObject();
        event.put("XES_Type", "Event");
        long time = DateHelper.getTimeValue(DateHelper.getDateHHMM(date));
        long nextArrTime;
        long lastDep;
        long depTime;
        long arrTime;

        for (int i = 0; i < stops.size(); i++) {

            if (i == 0) {
                depTime = DateHelper.getTimeValue(((JSONObject) stops.get(0)).get("depTime"));
                if (depTime > time) {
                    event.put("status_", "Did not departure");
                    event.put("Event_name", "Did not departure");
                    event.put("name_station",((JSONObject) stops.get(0)).get("name"));
                    event.put("Original_Data_Stop", stops.get(0));
                    break;
                }
            } else if (i == stops.size() - 1) {
                event.put("status_", "Finish journey");
                event.put("Event_name", "Is not on route");
                event.put("name_station",((JSONObject) stops.get(stops.size()-1)).get("name"));
                event.put("Original_Data_Stop", stops.get(stops.size()-1));
            } else {
                if (((JSONObject) stops.get(i + 1)).get("arrTime") == null
                    || ((JSONObject) stops.get(i - 1)).get("depTime") == null
                    || ((JSONObject) stops.get(i)).get("depTime") == null
                    || ((JSONObject) stops.get(i)).get("arrTime") == null) {
                    continue;
                }
                nextArrTime = DateHelper.getTimeValue(((JSONObject) stops.get(i + 1)).get("arrTime"));
                lastDep = DateHelper.getTimeValue(((JSONObject) stops.get(i - 1)).get("depTime"));
                if (nextArrTime >= time && time >= lastDep) {
                    depTime = DateHelper.getTimeValue(((JSONObject) stops.get(i)).get("depTime"));
                    arrTime = DateHelper.getTimeValue(((JSONObject) stops.get(i)).get("arrTime"));

                    if (arrTime > time) {
                        event.put("status_", "On the way");

                        long arrivalDiff = Long.valueOf((int)((JSONObject) stops.get(i)).get("arrivalDiff"));
                        if (arrivalDiff == 0) {
                            event.put("Event_name", "On the way");
                        } else if (arrivalDiff > 0) {
                            event.put("Event_name", "Delayed");
                        } else if (arrivalDiff < 0) {
                            event.put("Event_name", "Arriving earlier");
                        }
                        event.put("name_station", ((JSONObject) stops.get(i)).get("name"));
                        event.put("Original_Data_Stop", stops.get(i));
                        break;
                    } else if (time <= depTime) {
                        event.put("status_", "At station");

                        long arrivalDiff = Long.valueOf((int)((JSONObject) stops.get(i)).get("arrivalDiff"));
                        if (arrivalDiff == 0) {
                            event.put("Event_name", "On the way");
                        } else if (arrivalDiff > 0) {
                            event.put("Event_name", "Delayed");
                        } else if (arrivalDiff < 0) {
                            event.put("Event_name", "Arrived earlier");
                        }
                        event.put("name_station", ((JSONObject) stops.get(i)).get("name"));
                        event.put("Original_Data_Stop", stops.get(i));
                        break;
                    }

                }
            }

        }
        return event;
    }

    private static int containTraceObjectNumber(JSONArray log, JSONObject trace) {
        int i =0;
        for (Object t : log) {
            JSONObject traces = (JSONObject) t;
            if (areTracesEqual(traces, trace)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    private static boolean areTracesEqual(JSONObject firstObject, JSONObject secondObject) {
//        boolean equal= firstObject.get("routeIdxTo").equals(secondObject.get("routeIdxTo")) //todo update to this check for the trace
//                && firstObject.get("routeIdxFrom").equals(secondObject.get("routeIdxFrom"))
//                && firstObject.get("name").equals(secondObject.get("name"))

        boolean equal =  firstObject.get("depTime").equals(secondObject.get("depTime"))
                && firstObject.get("arrTime").equals(secondObject.get("arrTime"))
                && firstObject.get("depDate").equals(secondObject.get("depDate"));

        if (equal && firstObject.get("routeIdxTo") != null && (secondObject.get("routeIdxTo") != null)) {
            equal = firstObject.get("routeIdxTo").equals(secondObject.get("routeIdxTo"));
        }
        if (equal && firstObject.get("routeIdxFrom") != null && (secondObject.get("routeIdxFrom") != null)) {
            equal = firstObject.get("routeIdxFrom").equals(secondObject.get("routeIdxFrom"));
        }
        if (equal && firstObject.get("line") != null && (secondObject.get("line") != null)) {
            equal = firstObject.get("line").equals(secondObject.get("line"));
        }
        if (equal && firstObject.get("name") != null && (secondObject.get("name") != null)) {
            equal = firstObject.get("name").equals(secondObject.get("name"));
        }
        if (equal && firstObject.get("type") != null && (secondObject.get("type") != null)) {
            equal = firstObject.get("type").equals(secondObject.get("type"));
        }
        return equal;
    }


}
