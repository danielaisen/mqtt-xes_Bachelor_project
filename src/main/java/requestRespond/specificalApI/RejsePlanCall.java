/**
 * @author Daniel Max Aisen (s171206)
 **/

package requestRespond.specificalApI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import fileConstructorXES.FilesHelper;
import temp.DateHelper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RejsePlanCall {

    public static void main(String[] args) throws InterruptedException, ParseException, org.json.simple.parser.ParseException {
        org.json.simple.JSONArray timeSeriesJSONMain = new org.json.simple.JSONArray();

//        double []findAverage = new double[3];
        double totallTime = 0;
        double numberOfStops = 0;
        double unValidUrl = 0;

        String fileName =args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        for (String url : args) {
            int []averegeTimeBetweenStops = getTimeIntervals(url);
            totallTime    =   totallTime +averegeTimeBetweenStops[0]; //time
            numberOfStops = numberOfStops + averegeTimeBetweenStops[1]; //number of stops
            unValidUrl = unValidUrl +averegeTimeBetweenStops[2]; //empty url


        }
        int numberOfcalls = args.length - (int) unValidUrl;
        int fixed =5;
        int averageStops = (int) Math.ceil(numberOfStops/(numberOfcalls));
        int minutesBetweenCalls = (int) Math.ceil(totallTime/numberOfStops);

        int howManyCalls = fixed;

        System.out.printf("Received %d of journey options, out of them %d are valid. %n",
                 args.length, (int) (numberOfcalls));
        System.out.printf("There will be %d calls, with %d minutes (fixed time) waiting between them. In total this task will take: %d minutes %n"
                , howManyCalls, minutesBetweenCalls, (howManyCalls-1)*minutesBetweenCalls);
        System.out.println("The time now is " + DateHelper.nowShort());
        for (int i = 0; i < howManyCalls; i++) {
            for (String uri : args) {

                timeSeriesJSONMain = timeSeriesJSON(timeSeriesJSONMain, uri);
            }
            if (i < howManyCalls-1) {
                System.out.printf("sleeping for %d min %n", minutesBetweenCalls);
                Thread.sleep( minutesBetweenCalls *60 * 1000 );
            }
        }

        FilesHelper.createFileToJSONSimple(fileName, timeSeriesJSONMain);

    }

    private static int[] getTimeIntervals(String uri) throws org.json.simple.parser.ParseException, ParseException {
        org.json.simple.JSONObject jsonObject = getRejseplanRawData(uri);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); //todo change to DateHelper
        org.json.simple.JSONArray stops = (org.json.simple.JSONArray) jsonObject.get("Stop");
        int timeBetweenStops =0;
        int numberOfStops =0;
        int noValue =0;
        int size;
        try {
            if (stops.get(0) != null) {
                size = stops.size();
                Date start = dateFormat.parse(String.valueOf(((org.json.simple.JSONObject) stops.get(0)).get("depTime")));
                Date end = dateFormat.parse(String.valueOf(((org.json.simple.JSONObject) stops.get(size - 1)).get("arrTime")));
                numberOfStops = size - 1;
                timeBetweenStops = (int) (end.getTime() - start.getTime()) / (60 * 1000);
            }
        } catch (NullPointerException e) {
            noValue = 1;
        }

        return new int[]{timeBetweenStops,numberOfStops, noValue};
    }


    private static org.json.simple.JSONArray timeSeriesJSON(org.json.simple.JSONArray timeSeriesJSON, String uri) throws ParseException, org.json.simple.parser.ParseException {
        org.json.simple.JSONObject requestData = getRejseplanRawData(uri);
        String timeNow = DateHelper.nowShort();
        checkTimeExistenceAndAdd(requestData, timeSeriesJSON, timeNow);
        return timeSeriesJSON;
    }

    private static org.json.simple.JSONObject getRejseplanRawData(String url) throws org.json.simple.parser.ParseException {
        org.json.simple.JSONObject journeyDetail = extractRequestIntoJSON(mainClientRR(url));
        return journeyDetail;
    }

    private static void checkTimeExistenceAndAdd(org.json.simple.JSONObject requestData, org.json.simple.JSONArray timeSeriesJSON, String timeNow) {
        org.json.simple.JSONObject JSONObjectTimeObject = new org.json.simple.JSONObject();
        if (timeSeriesJSON.isEmpty()) {
            JSONObjectTimeObject.put("time", timeNow); //if it doesnt exist in the time-series object
            JSONObjectTimeObject.put("time:timestamp", DateHelper.nowFull()); //if it doesnt exist in the time-series object
            JSONObjectTimeObject.put("raw_Data0", requestData);

        } else {
            Boolean found = false;
            while (! found) {
                for(int i = 0; i < timeSeriesJSON.size(); i++) {
                    if (found = ((org.json.simple.JSONObject) timeSeriesJSON.get(i)).get("time").equals(timeNow)) {

                        JSONObjectTimeObject = (org.json.simple.JSONObject) timeSeriesJSON.get(i);
                        timeSeriesJSON.remove(i);
                        break;
                    }
                }
                if (!found) {
                    JSONObjectTimeObject.put("time", timeNow);
                    JSONObjectTimeObject.put("time:timestamp", DateHelper.nowFull());
                }
                int j = JSONObjectTimeObject.size()-1;
                JSONObjectTimeObject.put("raw_Data" +j, requestData);
                break;
            }
        }

        timeSeriesJSON.add(JSONObjectTimeObject);
    }

    public static String mainClientRR(String url) {

        JSONArray tripDetails = new JSONArray();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        String s = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        return s;
    }


    private static org.json.simple.JSONObject extractRequestIntoJSON(String responseBody) throws org.json.simple.parser.ParseException {
        String refinedRespond = deleteFirstChar(responseBody);
//        System.out.println(refinedRespond);//todo delete this print

        JSONParser parser = new JSONParser();

        org.json.simple.JSONObject mainJSONobject = (org.json.simple.JSONObject) parser.parse(refinedRespond);


        return (org.json.simple.JSONObject) mainJSONobject.get("JourneyDetail");
    }



    public static void retrieveInformationFromObject(String keys, Object original, HashMap<String, String> traceInfo) {
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

            System.exit(1001);
        }
        else if (original instanceof HashMap) {

            HashMap hashMap = (HashMap) original;
            for (Object key : hashMap.keySet()) {

                retrieveInformationFromObject((String) key, hashMap.get(key), traceInfo);
            }

        }
        else{
            System.out.println("\n error has occur in retrieveInformationFromObject method");
            System.out.println(original.getClass() + " has been found instead");
            System.exit(1002);
        }
    }
    public static void retrieveInformationFromObjectUSINGSIMPLE(Object keys, Object original, HashMap<String, String> traceInfo) {
        if (original instanceof String) {
            if (traceInfo.containsKey(keys)) {
                if (!traceInfo.get(keys).equals(original)) {
                    traceInfo.put(keys + "_addition", (String) original);
                }else { } //do nothing
            }
            else{
                traceInfo.put((String)keys, (String) original);
            }
            traceInfo.put((String) keys, (String) original);
        }
        else if (original instanceof org.json.simple.JSONObject) {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) original;
            for (Object key : jsonObject.keySet()) {
                retrieveInformationFromObjectUSINGSIMPLE((String)key, jsonObject.get(key), traceInfo);

            }
        }
        else if (original instanceof org.json.simple.JSONArray) {

            for (Object object : (org.json.simple.JSONArray) original) {
                if (!(object instanceof org.json.simple.JSONObject)) {
                    retrieveInformationFromObjectUSINGSIMPLE(String.valueOf(object.getClass()),object, traceInfo);
                }
                retrieveInformationFromObjectUSINGSIMPLE(null, object, traceInfo);
            }

        }
        else if (original instanceof ArrayList) {
            System.out.println("\n inside the retrieveInformationFromObject object found " + original.getClass());

            System.exit(1001);
        }
        else if (original instanceof HashMap) {
            HashMap hashMap = (HashMap) original;
            for (Object key : hashMap.keySet()) {

                retrieveInformationFromObject((String) key, hashMap.get(key), traceInfo);
            }
        } else if (original == null) {
            System.out.println();
            System.out.println("\n inside the retrieveInformationFromObject object found " + null);
            System.out.println();

        } else {
            System.out.println("\n error has occur in retrieveInformationFromObject method");
            System.out.println(original.getClass() + "  has been found instead");
            System.exit(1003);
        }
    }

    private static String deleteFirstChar(String responseBody) {
        responseBody = '{' + responseBody.substring(2);
        return responseBody;
    }

}