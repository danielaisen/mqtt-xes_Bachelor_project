/**
 * @author Daniel Max Aisen (s171206)
 **/

package requestRespond.specificalApI;

import org.json.JSONArray;
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

public class TimeSeries_RejsePlanCall {

    public static void main(String[] urls) throws InterruptedException, ParseException, org.json.simple.parser.ParseException {
        org.json.simple.JSONArray timeSeriesJSONMain = new org.json.simple.JSONArray(); //todo change it to be a variable going in

        String fileName =urls[0];
        urls = Arrays.copyOfRange(urls, 1, urls.length);

        double totalTime = 0;
        double numberOfStops = 0;
        double inValidUrl = 0;

        for (String url : urls) {
            int []timeStopsInvalid = getTimeIntervals(url);
            totalTime    =   totalTime +timeStopsInvalid[0];
            numberOfStops = numberOfStops + timeStopsInvalid[1];
            inValidUrl = inValidUrl +timeStopsInvalid[2];
        }

        int numberOfValid = urls.length - (int) inValidUrl;
        int averageStops = (int) Math.ceil(numberOfStops/(numberOfValid));
        int minutesBetweenCalls = (int) Math.ceil(totalTime/numberOfStops);

        int fixed =2;
        int howManyCalls = fixed;

        System.out.printf("Received %d of journey options, out of them %d are valid. %n",
                 urls.length, (numberOfValid));
        System.out.printf("There will be %d calls, with %d minutes (fixed time) waiting between them. In total this task will take: %d minutes %n"
                , howManyCalls, minutesBetweenCalls, (howManyCalls-1)*minutesBetweenCalls);
        System.out.println("The time now is " + DateHelper.nowShort());

        for (int i = 0; i < howManyCalls; i++) {
            for (String uri : urls) {

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
        org.json.simple.JSONArray JSONArrayDataArray = new org.json.simple.JSONArray();
        if (timeSeriesJSON.isEmpty()) {
            JSONObjectTimeObject.put("time", timeNow); //if it doesnt exist in the time-series object
            requestData.put("time:timestamp", DateHelper.nowFull());
            JSONArrayDataArray.add(requestData);
            JSONObjectTimeObject.put("raw_data", JSONArrayDataArray);

        } else {
            Boolean found = false;
            while (!found) {
                for(int i = 0; i < timeSeriesJSON.size(); i++) {
                    if (found = ((org.json.simple.JSONObject) timeSeriesJSON.get(i)).get("time").equals(timeNow)) {
                        JSONObjectTimeObject = (org.json.simple.JSONObject) timeSeriesJSON.get(i);
                        JSONArrayDataArray = (org.json.simple.JSONArray) JSONObjectTimeObject.get("raw_data");
                        JSONArrayDataArray.add(requestData);
                        timeSeriesJSON.remove(i);
                        break;
                    }
                }
                if (!found) {
                    JSONObjectTimeObject.put("time", timeNow);

                    requestData.put("time:timestamp", DateHelper.nowFull());
                    JSONArrayDataArray.add(requestData);
                    JSONObjectTimeObject.put("raw_data", JSONArrayDataArray);

                    JSONObjectTimeObject.put("time:timestamp", DateHelper.nowFull());
                }
                int j = JSONObjectTimeObject.size()-1;
                JSONObjectTimeObject.put("raw_data" +j, requestData);
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

                retrieveInformationFromObjectUSINGSIMPLE(key, hashMap.get(key), traceInfo);
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