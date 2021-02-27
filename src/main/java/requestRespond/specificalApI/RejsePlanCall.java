/**
 * @author Daniel Max Aisen (s171206)
 **/

package requestRespond.specificalApI;


//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import requestRespond.CreateTxtFile;

import javax.json.JsonObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RejsePlanCall {

    public static void main(String[] args) throws InterruptedException, ParseException, org.json.simple.parser.ParseException {
        org.json.simple.JSONArray timeSeriesJSONMain = new org.json.simple.JSONArray();
        String uri = "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=157032%2F64911%2F574674%2F234994%2F86%3Fdate%3D26.02.21%26format%3Djson";

//        getTimeIntervals();

        int stop =2;
        for (int i = 0; i < stop; i++) {

            timeSeriesJSONMain = timeSeriesJSON(timeSeriesJSONMain, uri);
            timeSeriesJSONMain = timeSeriesJSON(timeSeriesJSONMain, uri);
            int minutes = 1;


            if (i < stop-1) {
                System.out.printf("sleeping for %d min %n", minutes);
                Thread.sleep( minutes *60 * 1000 );
            }

        }

        System.out.println("about to set into file");
        CreateTxtFile.createFileToJSON("timeSeriesJSON2", timeSeriesJSONMain);



        if (false) {
            old(args);
        }

    }

    private static void old(String[] args) {
        JSONArray tripDetails = new JSONArray();
//        String firstRequest = mainClientRR("http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=673050%2F236849%2F801108%2F176208%2F86%3Fdate%3D24.02.21%26format%3Djson");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String timestamp = dateFormat.format(new Date());

        String uri;
        if (args.length > 0) {
            uri = args[0];
        } else {
            uri = "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=789222%2F284240%2F155520%2F185315%2F86%3Fdate%3D24.02.21%26format%3Djson";
        }
        String request = mainClientRR(uri);
        JSONObject logDetails = new JSONObject();
//        logDetails.put("time:timestamp", timestamp);
//        logDetails.put("XES_Type", "Log");

        for (int i = 0; i < 1; i++) {

            ArrayList<JSONObject> stopAndTrace = parseRejsePlanReturnStopsAndTrace(request);

            for (JSONObject jsonObject : stopAndTrace) {

//                if (jsonObject.get("Type").equals("Trace")) {
//
//                    continue;
//                }
                timestamp = dateFormat.format(new Date());
                jsonObject.put("time:timestamp", timestamp);
                tripDetails.put(jsonObject);
            }
            int minutes = 15;
//            Thread.sleep( minutes *60 * 1000 );

        }

        logDetails.put("XES_trace", tripDetails);
        CreateTxtFile.createFileToJSON("JSON_file_try04",logDetails);
        createFileWithJSON(tripDetails);
    }


    private static org.json.simple.JSONArray timeSeriesJSON(org.json.simple.JSONArray timeSeriesJSON, String uri) throws ParseException, org.json.simple.parser.ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T' HH:mm");


        org.json.simple.JSONObject requestData = getRejseplanRawData(uri);

        checkTimeExistenceAndAdd(requestData, timeSeriesJSON, dateFormat);




        System.out.println();
        return timeSeriesJSON;
    }

    private static org.json.simple.JSONObject getRejseplanRawData(String uri) throws org.json.simple.parser.ParseException {
        org.json.simple.JSONObject journeyDetail = extractRequestIntoJSON(mainClientRR(uri));
        return journeyDetail;
    }

    private static void checkTimeExistenceAndAdd(org.json.simple.JSONObject requestData, org.json.simple.JSONArray timeSeriesJSON, SimpleDateFormat dateFormat) {
        org.json.simple.JSONObject JSONObjectTimeObject = new org.json.simple.JSONObject();
        String timeNow = dateFormat.format(new Date());
//        JSONObjectTimeObject.clear();
        if (timeSeriesJSON.isEmpty()) {
            addData(requestData, JSONObjectTimeObject, timeNow);

        } else {
            Boolean found = false;
            while (! found) {
                for(int i = 0; i < timeSeriesJSON.size(); i++) {
                    if (found = ((org.json.simple.JSONObject) timeSeriesJSON.get(i)).get("time").equals(timeNow)) {
                        JSONObjectTimeObject = (org.json.simple.JSONObject) timeSeriesJSON.get(i);
                        break;
                    }
                }
                addData(requestData, JSONObjectTimeObject, timeNow);
                break;
            }
        }

        System.out.println("added to the JsonObject"); //todo delete this print

        timeSeriesJSON.add(JSONObjectTimeObject);
        System.out.println("have been added to the JsonObject"); //todo delete this print
    }

    private static void addData(org.json.simple.JSONObject requestData, org.json.simple.JSONObject JSONObjectTimeObject, String timeNow) {
        JSONObjectTimeObject.put("time", timeNow); //if it doesnt exist in the time-series object
        JSONObjectTimeObject.put("raw_Data", requestData);
        //todo add all the info


    }

    public static String mainClientRR(String uri) {

        System.out.println("getting the information from: \n" +  uri + "\n");
        JSONArray tripDetails = new JSONArray();
        //method 2: java.net.http.HttpClient

//    String uri = "http://jsonplaceholder.typicode.com/posts";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();

        String s = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
//                .thenApply(RejsePlanCall::parseRejsePlanJourneyDetails)
//                .thenAccept(System.out::println)
                .join();

//        System.out.println(s); //todo delete this print
        System.out.println("t´got the the data stirng"); //todo delete

        return s;
    }

    public static ArrayList<JSONObject> parseRejsePlanReturnStopsAndTrace(String responseBody) {
        ArrayList<JSONObject> objects = new ArrayList<JSONObject>();
//
//        String refinedRespond = deleteFirstChar(responseBody);
//        System.out.println(refinedRespond);//todo delete this print
//
//        JSONObject mainJSONobject = new JSONObject(refinedRespond);
//
//
//        JSONObject journeyDetail = mainJSONobject.getJSONObject("JourneyDetail");
//        List<String> namesJourneyDetail = new ArrayList<String>(journeyDetail.keySet());
//        System.out.println(namesJourneyDetail); //todo delete this print
//
//
//        HashMap<String, String> traceInfo = new HashMap<>();
//        JSONObject stopsObject = new JSONObject();
//
//        for (String keys : journeyDetail.keySet()) {
//            if (keys.equals("Stop")) {
//                JSONArray stops = journeyDetail.getJSONArray("Stop");
//                arrangeStopData(stops);
//                stopsObject.put("XES_Type", "Events"); //todo add activity name
//                stopsObject.put("Events", stops);
//            }
//            else if (keys.equals("noNamespaceSchemaLocation")){} //delete this object
//            else {
//                Object tempObject = journeyDetail.get(keys);
//                retrieveInformationFromObject(keys, tempObject, traceInfo);
//            }
//        }
//        JSONObject traceObject = new JSONObject(traceInfo);
//        traceObject.put("XES_Type", "Trace_Info");
//        objects.add(traceObject);
//        objects.add(stopsObject);
        return objects;
    }


    private static org.json.simple.JSONObject extractRequestIntoJSON(String responseBody) throws org.json.simple.parser.ParseException {
        String refinedRespond = deleteFirstChar(responseBody);
//        System.out.println(refinedRespond);//todo delete this print

        System.out.printf("got the details.. %s%n", new Date());
        JSONParser parser = new JSONParser();

        org.json.simple.JSONObject mainJSONobject = (org.json.simple.JSONObject) parser.parse(refinedRespond);


        return (org.json.simple.JSONObject) mainJSONobject.get("JourneyDetail");
    }


    private static void createFileWithJSON(JSONArray tripDetails) {
        CreateTxtFile file = new CreateTxtFile("rejse3loops");

        BufferedWriter bufferedWriter;
        try {
            bufferedWriter= new BufferedWriter(new FileWriter(file.file));
            bufferedWriter.write(tripDetails.toString());
            System.out.println("wrote to file");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void retrieveInformationFromObject(String keys, Object original, HashMap<String, String> traceInfo) {
        if (original instanceof String) {
            if (traceInfo.containsKey(keys)) {
                if (!traceInfo.get(keys).equals(original)) {
                    traceInfo.put(keys + "_addition", (String) original);
                }else { } //do nothing
            }
            else{
                traceInfo.put(keys, (String) original);
            }
            traceInfo.put(keys, (String) original);
        }
        else if (original instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) original;
            for (String key : jsonObject.keySet()) {
                retrieveInformationFromObject(key, jsonObject.get(key), traceInfo);

            }
        }
        else if (original instanceof JSONArray) {

            for (Object object : (JSONArray) original) {
                if (!(object instanceof JSONObject)) {
                    retrieveInformationFromObject(String.valueOf(object.getClass()),object, traceInfo);
                }
                retrieveInformationFromObject(null, object, traceInfo);
            }

        }
        else if (original instanceof ArrayList) {
            System.out.println("\n inside the retrieveInformationFromObject object found" + original.getClass());
            System.out.println();
            System.exit(1001);
        }
        else if (original instanceof HashMap) {
            System.out.println("\n inside the retrieveInformationFromObject object found" + original.getClass());
            System.out.println();
            System.exit(1002);

        }
        else{
            System.out.println("\n error has occur in retrieveInformationFromObject method");
            System.out.println(original.getClass() + "has been found instead");
            System.exit(1003);
        }
    }

    private static void arrangeStopData(JSONArray stops) {
        System.out.print("updating the stop objects ");
        SimpleDateFormat hoursMinFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
        SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        for (int i = 0; i < stops.length(); i++) {
            JSONObject stop = stops.getJSONObject(i);
            List<String> stopAttributes = new ArrayList<String>(stop.keySet());
            int daysArrivalDiff = 0;
            int daysDepartureDiff = 0;
            int departureDiff =0;
            int arrivalDiff = 0;
            for (String attribute : stopAttributes) {
                try {
                if (attribute.equals("rtArrDate")) {
                    daysArrivalDiff = clearingDateDaysAttributes(dateFormat, stop, attribute, "arrDate");
                    continue;
                }
                if (attribute.equals("rtDepDate")) {
                    daysDepartureDiff = clearingDateDaysAttributes(dateFormat, stop, attribute, "depDate");
                    continue;
                }
                if (attribute.equals("rtArrTime")) {
                    arrivalDiff = cleaningMinutesAttribute(hoursMinFormat, stop, attribute, "arrTime");
                    continue;
                }

                if (attribute.equals("rtDepTime")) {
                    departureDiff = cleaningMinutesAttribute(hoursMinFormat, stop, attribute, "depTime");
                    continue;
                }
                else if (attribute.equals("arrDate") || attribute.equals("arrTime") || attribute.equals("depTime")) {    //do nothing
                    continue;
                }
                else { //delete all empty
                    if (stop.getString(attribute).length() ==0){
                        stop.remove(attribute);
                    }
                }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            String timestamp = dateFormatLong.format(new Date());
            stop.put("time:timestamp", timestamp);
            stop.put("Event_Name", "stop");
            stop.put("daysArrivalDiff", daysArrivalDiff);
            stop.put("arrivalDiff", arrivalDiff);
            stop.put("daysDepartureDiff", daysDepartureDiff);
            stop.put("departureDiff", departureDiff);
        }
    }

    private static int cleaningMinutesAttribute(SimpleDateFormat hoursMinFormat, JSONObject stop, String attribute, String depTime) throws ParseException {
        int departureDiff;
        Date firstDate = hoursMinFormat.parse(stop.getString(attribute));
        Date secondDate = hoursMinFormat.parse(stop.getString(depTime));
        departureDiff = (int) (TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()))); //60 second, 1000 mili seconds
        stop.remove(attribute);
        return departureDiff;
    }

    private static int clearingDateDaysAttributes(SimpleDateFormat dateFormat, JSONObject stop, String attribute, String depDate) throws ParseException {
        int daysDepartureDiff;
        String s = stop.getString(attribute);
        Date firstDate = dateFormat.parse(s.substring(0, 6) + "20" + s.substring(6));
        s = stop.getString(depDate);
        Date secondDate = dateFormat.parse(s.substring(0, 6) + "20" + s.substring(6));
        daysDepartureDiff = (int) TimeUnit.DAYS.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()));
        stop.remove(attribute);
        return daysDepartureDiff;
    }

    /*
    the first row exist of:
       {
            "JourneyDetail":{
                "noNamespaceSchemaLocation":"http://webapp.rejseplanen.dk/xml/rest/hafasRestJourneyDetail.xsd"
     Instead we add '[' as a start of a JSON object.
     */
    private static String deleteFirstRow(String responseBody) {
        responseBody = '{' + responseBody.substring(119);
        return responseBody;
    }
    private static String deleteFirstChar(String responseBody) {
        responseBody = '{' + responseBody.substring(2);
        return responseBody;
    }

}