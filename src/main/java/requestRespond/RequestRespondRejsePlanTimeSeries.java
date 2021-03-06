package requestRespond;

import Helpers.FilesHelper;
import requestRespond.specificalApI.TimeSeries_RejsePlanCall;

import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Max Aisen (s171206)
 *
 * the attribute value
 **/

public class RequestRespondRejsePlanTimeSeries {


    public static void main(String[] args) throws Exception { //todo add the option to work with &format=xml

        int callsForEachUrl = 1;
        String fileName = "timeSeriesJSON3";
        int limitRoutes = 4;
        int howManyCalls = 1;

        if (args.length != 0) {
            callsForEachUrl = Integer.parseInt(args[0]);
            limitRoutes = Integer.parseInt(args[1]);
            howManyCalls = Integer.parseInt(args[2]);
            fileName = args[3];
        }

        org.json.simple.JSONArray timeSeriesJSONMain = new org.json.simple.JSONArray();
        TimeSeries_RejsePlanCall callToRejsePlan = new TimeSeries_RejsePlanCall();

        for (int i = 0; i < howManyCalls; i++) {
            String rejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/departureBoard?id=8600626&format=json";

            String name1= "callForFindingRouts";
            FilesHelper file1 = new FilesHelper(name1);

            List<String> urlList =  RequestRespondCall.setInTxtAndReturnHttpList(file1.file, rejsePlan);
            urlList.remove(0); urlList.remove(0);


    //        String[] urls = urlList.toArray(new String[0]);
            if (limitRoutes == 0) {
                limitRoutes = urlList.size();
            }

            if (urlList.size() > limitRoutes) {
                urlList = urlList.subList(0, limitRoutes);
            }
            for (String url : urlList) {
                System.out.println("getting the information from: \n" + url);
            }

            callToRejsePlan.updateTimeSeries(timeSeriesJSONMain,urlList, callsForEachUrl);

            FilesHelper.createFileToJSONSimple(fileName, timeSeriesJSONMain);
        }



//        String wiki =  "https://stream.wikimedia.org/v2/stream/recentchange";
//        RequestRespondCall.getAndWriteLineByLineToTxt(new FilesHelper("forWiki").file, wiki);

    }


}
