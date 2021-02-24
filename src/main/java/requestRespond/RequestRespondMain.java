package requestRespond;

import org.json.JSONObject;
import requestRespond.specificalApI.RejsePlanCall;

import java.util.List;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class RequestRespondMain {


    public static void main(String[] args) throws Exception { //todo add the option to work with &format=xml

//        String OldrejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/" +
//                "trip?destId=8600664&originId=6584&useTog=0&useBus=1&useMetro=0";
        String rejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/departureBoard?id=8600626&format=json";
//        String albums = "http://jsonplaceholder.typicode.com/posts";
        String wiki =  "https://stream.wikimedia.org/v2/stream/recentchange";
//        String rejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/departureBoard?id=8600626&format=json";

//        String rejsePlan = "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=418719%2F140597%2F32724%2F123213%2F86%3Fdate%3D10.02.21%26format%3Djson";
//        String Rej = "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=650091%2F234465%2F263768%2F84816%2F86%3Fdate%3D10.02.21%26format%3Djson";

//        method 2: java.net.http.HttpClient
//        WriteAndRead w_a_r = new WriteAndRead();
//
//        WriteAndRead.readingAndWritingUsingChar(inputFile,outPutFile);
//        WriteAndRead.readingAndWritingUsingBuffers(inputFile,outPutFile);
//
        String name1= "callForFindingRouts";
        CreateTxtFile file1 = new CreateTxtFile(name1);

        List<String> urlList =  RequestRespondCall.setInTxtAndReturnHttpList(file1.file, rejsePlan);
//        String first = urlList.get(0);
//        String anObject = "http://www.w3.org/2001/XMLSchema-instance";
//        System.out.println(first.equals(anObject));


        CreateTxtFile file2 = new CreateTxtFile("eventLog");

        JSONObject jsonObject = RequestRespondCall.getAndWriteLineByLineToTxt(file2.file, urlList.get(2));
        RequestRespondCall.setInTxtAndReturnHttpList(new CreateTxtFile("beforeEventLog").file, urlList.get(2));
//        RequestRespondCall.requestRespondPrint("http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=965961%2F335618%2F8440%2F317768%2F86%3Fdate%3D04.02.21%26station_evaId%3D6033");
//        RequestRespondCall.requestRespondPrint("http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=892029%2F306142%2F300476%2F147105%2F86%3Fdate%3D06.02.21%26station_evaId%3D50460");
        System.out.println("closed");

//        ClientRR.mainClientRR(urlList.get(2));
        RejsePlanCall.main(new String[]{urlList.get(2)});


        RequestRespondCall.getAndWriteLineByLineToTxt(new CreateTxtFile("forWiki").file, wiki);



    }


}
