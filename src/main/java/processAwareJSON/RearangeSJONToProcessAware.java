package processAwareJSON;

import Helpers.DateHelper;
import Helpers.JSONSimpleHelper;
import Helpers.FilesHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class RearangeSJONToProcessAware {

    public static void main(String[] args) throws ParseException {
        System.out.println("Making a call to rearranging json to process aware");

        String readFile = "timeSeriesJSON";
        String nameFile2 = "traceByLineObject";
//        JSONArray totallSumationOfAll = new JSONArray();
        JSONArray finalReadyToXESTracesByLine = new JSONArray();

        if (args.length ==2) {
            readFile = args[0];
            nameFile2 = args[1];
        }
//        int debug =0; int debug2= 0;

        JSONArray jsonArrayTimeSeries = FilesHelper.readJSONArrayFile(readFile);
        for (Object object : jsonArrayTimeSeries) {
//            debug++;System.out.println(debug);
            JSONObject V1jsonObjectTimeSeriesOrdered = new JSONObject();

            JSONObject objectTimeSeries = (JSONObject) object;
            Date date = DateHelper.getDate(objectTimeSeries.get("time"));
            objectTimeSeries.remove("time");
            V1jsonObjectTimeSeriesOrdered.put("time",date);
            JSONArray rawData = (JSONArray) objectTimeSeries.get("raw_data");

            for (Object data : rawData) {
//                debug2++;System.out.println(" " + debug2);
                ArrayList<Object> eventsAndTraces = parseRejsePlanReturnStopsAndTraceSimple((JSONObject) data);
                if (eventsAndTraces == null) {
                    continue;
                }
                JSONObject event = findRelevatStations((JSONArray) eventsAndTraces.get(0), date);
                Object timeStamp = ((JSONObject) data).get("time:timestamp");
                event.put("time:timestamp", timeStamp);
                JSONObject trace = (JSONObject) eventsAndTraces.get(1);

//                V1jsonObjectTimeSeriesOrdered.put(key+"_event0_trace_1", eventsAndTraces);

                if (finalReadyToXESTracesByLine.isEmpty()) {
                    JSONArray events = new JSONArray();
                    events.add(event);
                    trace.put("Events", events);
                    finalReadyToXESTracesByLine.add(trace);
                    continue;
                }
                int index = containTraceObjectNumber(finalReadyToXESTracesByLine, trace);
                if (index == -1) {
                    JSONArray events = new JSONArray();
                    events.add(event);
                    trace.put("Events", events);
                    finalReadyToXESTracesByLine.add(trace);
                } else {
                    JSONObject thisTrace = (JSONObject) finalReadyToXESTracesByLine.get(index);
                    JSONArray events = (JSONArray) thisTrace.get("Events");
                    events.add(event);
                }

            }

//            totallSumationOfAll.add(V1jsonObjectTimeSeriesOrdered);
        }
//        String nameFile = "traceByLineArray";
//        FilesHelper.createFileToJSONSimple(nameFile, finalReadyToXESTracesByLine);
        JSONObject log = new JSONObject();
        log.put("XES_Type_", "log");
        log.put("Traces", finalReadyToXESTracesByLine);
        FilesHelper.createFileToJSONSimple(nameFile2, log);

        System.out.println("done RearangeSJONToProcessAware. Saved as "  + nameFile2);
        System.out.println("Done with call to rearranging json to process aware");

    }


    public static ArrayList<Object> parseRejsePlanReturnStopsAndTraceSimple(JSONObject journeyDetail) {
        ArrayList<Object> objects = new ArrayList<>();

        HashMap<String, String> traceInfo = new HashMap<>();
        org.json.simple.JSONObject stopsObject = new org.json.simple.JSONObject();

        for (Object keys : journeyDetail.keySet()) {
            if  (keys.equals("error")) {
                return null;
            } else if (keys.equals("Stop")) {
                org.json.simple.JSONArray stops = (org.json.simple.JSONArray) journeyDetail.get(keys);
                arrangeStopData(stops);
                objects.add(stops);
                stopsObject.put("Events", stops);
            } else if (keys.equals("noNamespaceSchemaLocation")) {
            } //delete this object
            else {
                Object tempObject = journeyDetail.get(keys);
                JSONSimpleHelper.retrieveInformationFromObjectUSINGSIMPLE((String) keys, tempObject, traceInfo);
            }
        }
        org.json.simple.JSONObject traceObject = new org.json.simple.JSONObject(traceInfo);
        JSONArray events = (JSONArray) stopsObject.get("Events");
        traceObject.put("depTime", ((JSONObject) events.get(0) ).get("depTime"));
        traceObject.put("depDate", ((JSONObject) events.get(0) ).get("depDate"));
        traceObject.put("arrTime", ((JSONObject) events.get(stopsObject.size())).get("arrTime"));
        traceObject.put("XES_Type_", "Trace_Info");
        objects.add(traceObject);
//        objects.add(stopsObject);
        return objects;
    }



    private static void arrangeStopData(org.json.simple.JSONArray stops) {
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
                        daysArrivalDiff = clearingDaysAttributes(stop, attribute, "arrDate");
                        continue;
                    }
                    if (attribute.equals("rtDepDate")) {
                        daysDepartureDiff = clearingDaysAttributes( stop, attribute, "depDate");
                        continue;
                    }
                    if (attribute.equals("rtArrTime")) {
                        arrivalDiff = cleaningMinutesAttribute(stop, attribute, "arrTime");
                        continue;
                    }
                    if (attribute.equals("rtDepTime")) {
                        departureDiff = cleaningMinutesAttribute(stop, attribute, "depTime");
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
//            stop.put("Event_Name", "stop");
            stop.put("daysArrivalDiff_", daysArrivalDiff);
            stop.put("arrivalDiff_", arrivalDiff);
            stop.put("daysDepartureDiff_", daysDepartureDiff);
            stop.put("departureDiff_", departureDiff);
        }
    }

    private static int cleaningMinutesAttribute(org.json.simple.JSONObject stop, String attribute, String depTime) throws ParseException {
        int departureDiff;
        Date firstDate = DateHelper.getDateHHMM(stop.get(attribute));
        Date secondDate = DateHelper.getDateHHMM(stop.get(depTime));
        departureDiff = (int) (TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()))); //60 second, 1000 mili seconds
        return departureDiff;
    }

    private static int clearingDaysAttributes(org.json.simple.JSONObject stop, String attribute, String depDate) throws ParseException {
        int daysDepartureDiff;
        String s = (String) stop.get(attribute);
        Date firstDate = DateHelper.getDateMMDDYYYY(s.substring(0, 6) + "20" + s.substring(6));
        s = (String) stop.get(depDate);
        Date secondDate = DateHelper.getDateMMDDYYYY(s.substring(0, 6) + "20" + s.substring(6));
        daysDepartureDiff = (int) TimeUnit.DAYS.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()));
        return daysDepartureDiff;
    }


    private static JSONObject findRelevatStations(JSONArray stops, Date date) throws ParseException {

        JSONObject event = new JSONObject();
        event.put("XES_Type_", "Event");
        long time = DateHelper.getTimeValue(DateHelper.getDateHHMM(date));
        long nextArrTime;
        long lastDep;
        long depTime;
        long arrTime;

        for (int i = 0; i < stops.size(); i++) {

            if (i == 0) {
                depTime = DateHelper.getTimeValue(((JSONObject) stops.get(0)).get("depTime"));
                if (depTime > time) {
                    event.put("Status_", "Did not departure");
                    event.put("Event_name_", "Did not departure");
                    event.put("Name_station",((JSONObject) stops.get(0)).get("name"));
                    event.put("Data_Stop", stops.get(0));
                    break;
                }
            } else if ((i == stops.size()-1)) {
                if (time > DateHelper.getTimeValue(((JSONObject) stops.get(i)).get("arrTime"))){
                    event.put("Status_", "Finish journey");
                    event.put("Event_name_", "Is not on route");
                    event.put("Name_station",((JSONObject) stops.get(stops.size()-1)).get("name"));
                    event.put("Data_Stop", stops.get(stops.size()-1));
                }
                else {
                    String station = (String) ((JSONObject) stops.get(stops.size()-1)).get("name");
                    event.put("Status_", "On the way");
                    event.put("Event_name_", "On the way to "+ station);
                    event.put("Name_station",station );
                    event.put("Data_Stop", stops.get(stops.size()-1));
                }


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
                        event.put("Status_", "On the way");
                        String station = (String) ((JSONObject) stops.get(i)).get("name");
                        long arrivalDiff = Long.valueOf((int)((JSONObject) stops.get(i)).get("arrivalDiff"));
                        if (arrivalDiff == 0) {
                            event.put("Event_name_", "On the way to "+ station);
                        } else if (arrivalDiff > 0) {
                            event.put("Event_name_", "Delayed to "+ station);
                        } else if (arrivalDiff < 0) {
                            event.put("Event_name_", "Arriving earlier to "+ station);
                        }
                        event.put("Name_station", ((JSONObject) stops.get(i)).get("name"));
                        event.put("Data_Stop", stops.get(i));
                        break;
                    } else if (time <= depTime) {

                        String station = (String) ((JSONObject) stops.get(i)).get("name");
                        event.put("Status_", "At station " + station);
                        long arrivalDiff = Long.valueOf((int)((JSONObject) stops.get(i)).get("arrivalDiff"));
                        if (arrivalDiff == 0) {
                            event.put("Event_name_", "On the way to "+ station);
                        } else if (arrivalDiff > 0) {
                            event.put("Event_name_", "Delayed to "+ station);

                        } else if (arrivalDiff < 0) {
                            event.put("Event_name_", "Arriving earlier to "+ station);

                        }
                        event.put("Name_station", ((JSONObject) stops.get(i)).get("name"));
                        event.put("Data_Stop", stops.get(i));
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
        boolean equal = true;

        if ( firstObject.get("depTime") != null && secondObject.get("depTime") != null){
            equal = firstObject.get("depTime").equals(secondObject.get("depTime"));
        }else {
            equal = firstObject.get("depTime") == (secondObject.get("depTime"));
        }
        if(!equal){ return false; }

        if ( firstObject.get("arrTime") != null && secondObject.get("arrTime") != null){
            equal = firstObject.get("arrTime").equals(secondObject.get("arrTime"));
        }else {
            equal = firstObject.get("arrTime") == (secondObject.get("arrTime"));
        }
        if(!equal){ return false; }

        if ( firstObject.get("depDate") != null && secondObject.get("depDate") != null){
            equal = firstObject.get("depDate").equals(secondObject.get("depDate"));
        }else {
            equal = firstObject.get("depDate") == (secondObject.get("depDate"));
        }
        if(!equal){ return false; }

        if ( firstObject.get("routeIdxTo") != null && secondObject.get("routeIdxTo") != null){
            equal = firstObject.get("routeIdxTo").equals(secondObject.get("routeIdxTo"));
        }else {
            equal = firstObject.get("routeIdxTo") == (secondObject.get("routeIdxTo"));
        }
        if(!equal){ return false; }

        if ( firstObject.get("routeIdxFrom") != null && secondObject.get("routeIdxFrom") != null){
            equal = firstObject.get("routeIdxFrom").equals(secondObject.get("routeIdxFrom"));
        }else {
            equal = firstObject.get("routeIdxFrom") == (secondObject.get("routeIdxFrom"));
        }
        if(!equal){ return false; }

        if ( firstObject.get("line") != null && secondObject.get("line") != null){
            equal = firstObject.get("line").equals(secondObject.get("line"));
        }else {
            equal = firstObject.get("line") == (secondObject.get("line"));
        }

        return equal;
    }
}
