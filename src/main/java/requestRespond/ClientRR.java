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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientRR {


    public static void main(String[] args) {

        String albums = "http://jsonplaceholder.typicode.com/posts";
        String wiki =  "https://stream.wikimedia.org/v2/stream/recentchange";
//        String rejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/departureBoard?id=8600626&format=json";

        String rejsePlan = "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=291381%2F115469%2F188514%2F2870%2F86%3Fdate%3D08.02.21%26format%3Djson";
//        useHttpClient(albums);
        useHttpClient(rejsePlan);


    }

    private static void useHttpClient(String uri) {
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

        String respond = deleteFirstRow(responseBody);

        JSONObject one = new JSONObject(respond);

//        JSONArray events = new JSONArray(respond);
//        for (int i = 0; i < events.length(); i++) {
//            JSONObject eventJSON = event.getJSONObject(i);
//            int id = eventJSON.getInt("id");
//            String title = eventJSON.getString("name");
//            System.out.println(id + " " + title );
//        }
//                    int JourneyLine = one.getInt("JourneyLine");

        JSONObject two = new JSONObject(deleteFirstChar(responseBody));

        JSONArray stop = one.getJSONArray("Stop");

            System.out.println("stop"  + " " + stop );

        return null;

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
        System.out.println(s);

        return s;
    }
    private static String deleteFirstChar(String responseBody) {

        String s;

        s = '{' + responseBody.substring(2);
        System.out.println(s);

        return s;
    }

}