/**
 * @author Daniel Max Aisen (s171206)
 **/

package requestRespond;


//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;


import org.json.JSONArray;
import org.json.JSONObject;

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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ClientRR {


    public static void mainClientRR(String uri) {
        System.out.println("getting the information from: \n" +  uri + "\n");

        //method 2: java.net.http.HttpClient

//    String uri = "http://jsonplaceholder.typicode.com/posts";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(ClientRR::parseRejsePlanJourneyDetails)
//                .thenAccept(System.out::println)
                .join();
    }


    public static String parseRejsePlanJourneyDetails(String responseBody) {

//        String respond = deleteFirstRow(responseBody);
//
//        JSONObject one = new JSONObject(respond);

//        JSONArray events = new JSONArray(respond);
//        for (int i = 0; i < events.length(); i++) {
//            JSONObject eventJSON = event.getJSONObject(i);
//            int id = eventJSON.getInt("id");
//            String title = eventJSON.getString("name");
//            System.out.println(id + " " + title );
//        }
//                    int JourneyLine = one.getInt("JourneyLine");

        String refinedRespond = deleteFirstChar(responseBody);
        System.out.println(refinedRespond);

        JSONObject mainJSONobject = new JSONObject(refinedRespond);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String timestamp = dateFormat.format(new Date());

        JSONObject journeyDetail = mainJSONobject.getJSONObject("JourneyDetail");
        List<String> namesJourneyDetail = new ArrayList<String>(journeyDetail.keySet());
        System.out.println(namesJourneyDetail); //todo delete this print
        JSONArray tripDetails = new JSONArray();
        JSONObject traceObject = new JSONObject();
        HashMap<String, String> traceInfo = new HashMap<>();
        HashMap<String, String> traceInfo2 = new HashMap<>();
        JSONObject stopsObject = new JSONObject();
        stopsObject.put("time:timestamp", timestamp);
        for (String keys : journeyDetail.keySet()) {
            if (keys.equals("Stop")) {
                JSONArray stops = journeyDetail.getJSONArray("Stop");
                arrangeStopData(stops);
                stopsObject.put("Type", "Event");
                stopsObject.put("Stops", stops);
            }
            else if (keys.equals("noNamespaceSchemaLocation")){} //delete this object
            else {
                Object tempObject = journeyDetail.get(keys);
                retrieveInformationFromObject(keys, tempObject, traceInfo2);
                if (tempObject instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) tempObject;
                    for (Object object : jsonArray) {
                        JSONObject jsonObject = (JSONObject) object;
                        for (String key : jsonObject.keySet()) {
                            if (jsonObject.get(key) instanceof String){
                                if (!(traceInfo.containsKey(key) && traceInfo.get(key).equals(jsonObject.get(key))))  {
                                    traceInfo.put(key, String.valueOf(jsonObject.get(key)));
                                }
                            }
                            else{
                                Object a = jsonObject.get(key);
                                a.getClass();
                            }

                        }
                    }
                }
                if (tempObject instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) tempObject;
                    for (String key : (jsonObject.keySet())) {
                        if (!traceInfo.containsKey(key)) {
                            traceInfo.put(key, String.valueOf(jsonObject.get(key)));
                        }
                    }
                }
            }
            System.out.println(traceInfo.equals(traceInfo2));
        }


        tripDetails.put(traceInfo2);
        tripDetails.put(stopsObject);



        CreateTxtFile file = new CreateTxtFile("TryingToJSON");

        BufferedWriter bufferedWriter;
        try {
            bufferedWriter= new BufferedWriter(new FileWriter(file.file));
            bufferedWriter.write(tripDetails.toString());
//            bufferedWriter.write(stops.getJSONObject(1).toString());
            System.out.println("wrote to file");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;

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
                if (jsonObject.get(key) instanceof String){
                    if (traceInfo.containsKey(key)) {
                        if (!traceInfo.get(key).equals(jsonObject.get(key))) {
                            traceInfo.put(key + "_addition", String.valueOf(jsonObject.get(key)));
                        }
                        else { } //do nothing
                    }
                }
                else {
                    retrieveInformationFromObject(key, jsonObject.get(key), traceInfo);
                    System.out.println("\n inside the JSON object found" + jsonObject.get(key).getClass());
                    System.out.println();
                }
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

        }
        else if (original instanceof HashMap) {

        }
        else{
            System.out.println("\n error has occur in retrieveInformationFromObject method");
            System.out.println(original.getClass() + "has been found instead");
        }


    }

    private static void arrangeStopData(JSONArray stops) {
        System.out.print("updating the stop objects ");
        SimpleDateFormat hoursMinFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
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
                    String s = stop.getString(attribute);
                    Date firstDate = dateFormat.parse(s.substring(0,6) +"20" + s.substring(6));
                    s = stop.getString("arrDate");
                    Date secondDate = dateFormat.parse(s.substring(0,6) +"20" + s.substring(6));
                    daysArrivalDiff = (int) TimeUnit.DAYS.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()));
                }
                if (attribute.equals("rtDepDate")) {
                    String s = stop.getString(attribute);
                    Date firstDate = dateFormat.parse(s.substring(0,6) +"20" + s.substring(6));
                    s = stop.getString("depDate");
                    Date secondDate = dateFormat.parse(s.substring(0,6) +"20" + s.substring(6));
                    daysDepartureDiff = (int) TimeUnit.DAYS.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()));
                }
                if (attribute.equals("rtArrTime")) {

                    Date firstDate = hoursMinFormat.parse(stop.getString(attribute));
                    Date secondDate = hoursMinFormat.parse(stop.getString("arrTime"));
                    arrivalDiff = (int) TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime())); //60 second, 1000 mili seconds
                }

                if (attribute.equals("rtDepTime")) {
                    Date firstDate = hoursMinFormat.parse(stop.getString(attribute));
                    Date secondDate = hoursMinFormat.parse(stop.getString("depTime"));
                    departureDiff = (int) (TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()))); //60 second, 1000 mili seconds
                }
                else if (attribute.equals("arrDate") || attribute.equals("arrTime") || attribute.equals("depTime")) {    //do nothing
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

            stop.put("daysArrivalDiff", daysArrivalDiff);
            stop.put("arrivalDiff", arrivalDiff);
            stop.put("daysDepartureDiff", daysDepartureDiff);
            stop.put("departureDiff", departureDiff);
        }



    }

    /*
    the first row exist of:
       {
            "JourneyDetail":{
                "noNamespaceSchemaLocation":"http://webapp.rejseplanen.dk/xml/rest/hafasRestJourneyDetail.xsd"
     Instead we add '[' as a start of a JSON object.
     */

    private static String deleteFirstRow(String responseBody) {

        String s;

        s = '{' + responseBody.substring(119);
//        System.out.println(s);

        return s;
    }
    private static String deleteFirstChar(String responseBody) {

        String s;

        s = '{' + responseBody.substring(2);
//        System.out.println(s);

        return s;
    }

}