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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClientRR {


    public static void mainClientRR(String uri) {

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
        JSONObject two = new JSONObject(refinedRespond);

        JSONObject journeyDetail = two.getJSONObject("JourneyDetail");
        List<String> namesJourneyDetail = new ArrayList<String>(journeyDetail.keySet());
        System.out.println(namesJourneyDetail);
        JSONArray stops = journeyDetail.getJSONArray("Stop");
//        String namesStops = stops.toString();
//        System.out.println(namesStops);
//        JSONObject stop1 = stops.getJSONObject(0);
//        JSONObject stop2 = stops.getJSONObject(1);
//        Set<String> sStop2 = stop2.keySet();

//        System.out.println("stop"  + " " + stop1);
//        System.out.println("stop"  + " " + sStop2);
        arrangeStopData(stops);



        CreateTxtFile file = new CreateTxtFile("TryingToJSON");
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter= new BufferedWriter(new FileWriter(file.file));
            bufferedWriter.write(stops.toString());
//            bufferedWriter.write(stops.getJSONObject(1).toString());
            System.out.println("wrote to file");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;

    }

    private static void arrangeStopData(JSONArray stops) {
        System.out.print("updating the stop objects ");
        SimpleDateFormat hoursMinFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");
        for (int i = 0; i < stops.length(); i++) {
            JSONObject stop = stops.getJSONObject(i);
            List<String> stopAttributes = new ArrayList<String>(stop.keySet());
            int timeDifference = 0;
            int arrivalDiff = 0;
            int departureDiff =0;
            for (String attribute : stopAttributes) {
                try {
                if (attribute.equals("rtArrDate")) {
                    String s = stop.getString(attribute);
                    Date firstDate = dateFormat.parse(s.substring(0,6) +"20" + s.substring(6));
                    s = stop.getString("arrDate");
                    Date secondDate = dateFormat.parse(s.substring(0,6) +"20" + s.substring(6));
                    timeDifference = (int) TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()));
                }

                //todo add rtDepDate
                if (attribute.equals("rtArrTime")) {

                    Date firstDate = hoursMinFormat.parse(stop.getString(attribute));
                    Date secondDate = hoursMinFormat.parse(stop.getString("arrTime"));
                    timeDifference = (int) TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime())); //60 second, 1000 mili seconds
                }

                if (attribute.equals("rtDepTime")) {
                    Date firstDate = hoursMinFormat.parse(stop.getString(attribute));
                    Date secondDate = hoursMinFormat.parse(stop.getString("depTime"));
                    timeDifference = (int) (TimeUnit.MINUTES.convert(Duration.ofDays(firstDate.getTime() - secondDate.getTime()))); //60 second, 1000 mili seconds
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
            stop.put("dateDiff", timeDifference);
            stop.put("arrivalDiff", arrivalDiff);
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