package collectData;

import Helpers.FilesHelper;
import collectData.specificalApI.TimeSeries_RejsePlanCall;

import java.util.List;

/**
 * @author Daniel Max Aisen (s171206)
 *
 * the attribute value
 **/

public class RequestRespondRejsePlanTimeSeries {


    public static void main(String[] args) throws Exception { //todo add the option to work with &format=xml

        System.out.println("making a call to RejsePlan");

        int callsForEachUrl = 4;
        String fileName = "timeSeriesJSON";
        int limitRoutes = 5;
        int howManyCalls = 2;
        int sleepTime = 10;

        if (args.length != 0) {
            callsForEachUrl = Integer.parseInt(args[0]);
            limitRoutes = Integer.parseInt(args[1]);
            howManyCalls = Integer.parseInt(args[2]);
            sleepTime = Integer.parseInt(args[3]);
            fileName = args[4];
        }

        org.json.simple.JSONArray timeSeriesJSONMain = new org.json.simple.JSONArray();
        TimeSeries_RejsePlanCall callToRejsePlan = new TimeSeries_RejsePlanCall();

        for (int i = 0; i < howManyCalls; i++) {
            String rejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/departureBoard?id=8600626&format=json";

            String name1= "callForFindingRouts_" + i;
            FilesHelper file1 = new FilesHelper(name1);

            List<String> urlList =  RequestRespondCall.setInTxtAndReturnHttpList(file1.file, rejsePlan);
            urlList.remove(0);
//            urlList.remove(0);


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

            callToRejsePlan.updateTimeSeries(timeSeriesJSONMain,urlList, callsForEachUrl,sleepTime);
            Thread.sleep(sleepTime);
        }
        FilesHelper.createFileToJSONSimple(fileName, timeSeriesJSONMain);
        System.out.println("Done call to Rejseplanen");

//        String wiki =  "https://stream.wikimedia.org/v2/stream/recentchange";
//        RequestRespondCall.getAndWriteLineByLineToTxt(new FilesHelper("forWiki").file, wiki);

    }


}
