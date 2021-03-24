/**
 * @author Daniel Max Aisen (s171206)
 **/

package collectData.specificalApI;

//import org.json.JSONArray;
import Helpers.JSONSimpleHelper;
import org.json.simple.parser.JSONParser;
import Helpers.DateHelper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.*;

public class TimeSeries_RejsePlanCall {

    public void updateTimeSeries(org.json.simple.JSONArray timeSeriesJSONMain, List<String> urls, int numberOfCalls, int sleepTime) throws InterruptedException, ParseException, org.json.simple.parser.ParseException {

        double totalTime = 0;
        double numberOfStops = 0;
        int inValidUrl = 0;

        for (int index =0; index < urls.size(); index++) {
            String url = urls.get(index);
            int []timeStopsInvalid = getTimeIntervals(url);
            totalTime = totalTime +timeStopsInvalid[0];
            numberOfStops = numberOfStops + timeStopsInvalid[1];
            if (timeStopsInvalid[2] == 1) {
                inValidUrl++ ;
                urls.remove(index);
                index--;
            }

        }

        int numberOfValid = urls.size();
        int averageStops = (int) Math.ceil(numberOfStops/(numberOfValid));
        int minutesBetweenCalls = (int) Math.ceil(totalTime/numberOfStops);


        System.out.printf("Received %d of journey options, out of them %d are valid. %n",
                (numberOfValid + inValidUrl), (numberOfValid));
        System.out.printf("There will be %d calls, with %d minutes (fixed time) waiting between them. In total this task will take: %d minutes %n"
                , numberOfCalls, minutesBetweenCalls, (numberOfCalls-1)*minutesBetweenCalls);
        System.out.println("The time now is " + DateHelper.nowShort());

        for (int i = 0; i < numberOfCalls; i++) {
            for (String uri : urls) {

                org.json.simple.JSONObject requestData = getRejseplanRawData(uri);
                String timeNow = DateHelper.nowShort();
                checkTimeExistenceAndAdd(requestData, timeSeriesJSONMain, timeNow);
            }
            if (i < numberOfCalls-1) {
                System.out.printf("sleeping for %d min %n", minutesBetweenCalls);
                Thread.sleep( minutesBetweenCalls *60 * 1000 );
            }
            Thread.sleep(sleepTime);
        }

    }

    private int[] getTimeIntervals(String uri) throws org.json.simple.parser.ParseException, ParseException {
        org.json.simple.JSONObject jsonObject = getRejseplanRawData(uri);
        org.json.simple.JSONArray stops = (org.json.simple.JSONArray) jsonObject.get("Stop");
        int timeBetweenStops =0;
        int numberOfStops =0;
        int noValue =0;
        int size;
        try {
            if (stops.get(0) != null) {
                size = stops.size();
                Date start = DateHelper.getDateHHMM(String.valueOf(((org.json.simple.JSONObject) stops.get(0)).get("depTime")));
                Date end = DateHelper.getDateHHMM(String.valueOf(((org.json.simple.JSONObject) stops.get(size - 1)).get("arrTime")));
                numberOfStops = size - 1;
                timeBetweenStops = (int) (end.getTime() - start.getTime()) / (60 * 1000);
                if (timeBetweenStops <= 0) {
                    timeBetweenStops = Math.abs(numberOfStops);
                }
            }
        } catch (NullPointerException e) {
            noValue = 1;
        }

        return new int[]{timeBetweenStops,numberOfStops, noValue};
    }


    private org.json.simple.JSONObject getRejseplanRawData(String url) throws org.json.simple.parser.ParseException {
        org.json.simple.JSONObject journeyDetail = extractRequestIntoJSON(requestRespondCall(url));
        return journeyDetail;
    }

    private void checkTimeExistenceAndAdd(org.json.simple.JSONObject requestData, org.json.simple.JSONArray timeSeriesJSON, String timeNow) {
        org.json.simple.JSONObject JSONObjectTimeObject = new org.json.simple.JSONObject();
        org.json.simple.JSONArray JSONArrayDataArray = new org.json.simple.JSONArray();
        try  {
            Boolean found = false;
            while (!found) {
                for(int i = 0; i < timeSeriesJSON.size(); i++) {
                    if (found = ((org.json.simple.JSONObject) timeSeriesJSON.get(i)).get("time").equals(timeNow)) {
                        JSONObjectTimeObject = (org.json.simple.JSONObject) timeSeriesJSON.get(i);
                        JSONArrayDataArray = (org.json.simple.JSONArray) JSONObjectTimeObject.get("raw_data");

                        requestData.put("time:timestamp", DateHelper.nowFull());
                        JSONArrayDataArray.add(requestData);

                        timeSeriesJSON.remove(i);
                        break;
                    }
                }
                if (!found) {
                    requestData.put("time:timestamp", DateHelper.nowFull());
                    JSONArrayDataArray.add(requestData);

                    JSONObjectTimeObject.put("time", timeNow);
                    JSONObjectTimeObject.put("raw_data", JSONArrayDataArray);
                }

                JSONObjectTimeObject.put("raw_data" , JSONArrayDataArray);
                break;
            }

            timeSeriesJSON.add(JSONObjectTimeObject);
        }
        catch (NullPointerException e) {
            return;
        }
    }

    public String requestRespondCall(String url) {

        org.json.simple.JSONArray tripDetails = new org.json.simple.JSONArray();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        String s = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        return s;
    }

    private org.json.simple.JSONObject extractRequestIntoJSON(String responseBody) throws org.json.simple.parser.ParseException {
        String refinedRespond = JSONSimpleHelper.deleteFirstChar(responseBody);
        JSONParser parser = new JSONParser();
        org.json.simple.JSONObject mainJSONobject = (org.json.simple.JSONObject) parser.parse(refinedRespond);

        return (org.json.simple.JSONObject) mainJSONobject.get("JourneyDetail");
    }

}