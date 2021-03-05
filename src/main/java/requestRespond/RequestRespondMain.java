package requestRespond;

import fileConstructorXES.FilesHelper;
import org.json.JSONObject;
import requestRespond.specificalApI.TimeSeries_RejsePlanCall;

import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Max Aisen (s171206)
 *
 * the attribute value
 **/

public class RequestRespondMain {


    public static void main(String[] args) throws Exception { //todo add the option to work with &format=xml

        String rejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/departureBoard?id=8600626&format=json";
//        String wiki =  "https://stream.wikimedia.org/v2/stream/recentchange";

//        String name = args[0];

        String name1= "callForFindingRouts";
        FilesHelper file1 = new FilesHelper(name1);

        List<String> urlList =  RequestRespondCall.setInTxtAndReturnHttpList(file1.file, rejsePlan);

        FilesHelper file2 = new FilesHelper("eventLog");

        JSONObject jsonObject = RequestRespondCall.getAndWriteLineByLineToTxt(file2.file, urlList.get(2));
        RequestRespondCall.setInTxtAndReturnHttpList(new FilesHelper("beforeEventLog").file, urlList.get(2));
        System.out.println("closed");

        urlList.remove(0);
        urlList.remove(0);

        String fileName = "timeSeriesJSON3";
        urlList.add(0,fileName);

        String[] urls = urlList.toArray(new String[0]);

        if (urls.length > 6) { //todo change to the size of it
            urls = Arrays.copyOfRange(urls, 0, 6);
        }
        for (String url : urls) {
            System.out.println("getting the information from: \n" + url);
        }

        TimeSeries_RejsePlanCall.main(urls);

//        RequestRespondCall.getAndWriteLineByLineToTxt(new FilesHelper("forWiki").file, wiki);

    }


}
