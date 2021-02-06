package requestRespond;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class RequestRespondMain {

    private static HttpURLConnection connection;
    private static File inputFile = new File("C:\\Users\\danie\\code\\restrequest\\src\\main\\text.txt");

    private static File outPutFile = new File("C:\\Users\\danie\\code\\restrequest\\src\\main\\outPut.txt");


    public static void main(String[] args) throws Exception {

        String rejsePlan = "http://xmlopen.rejseplanen.dk/bin/rest.exe/" +
                "trip?destId=8600664&originId=6584&useTog=0&useBus=1&useMetro=0&type=json";

//        method 2: java.net.http.HttpClient
//        WriteAndRead w_a_r = new WriteAndRead();
//
//        WriteAndRead.readingAndWritingUsingChar(inputFile,outPutFile);
//        WriteAndRead.readingAndWritingUsingBuffers(inputFile,outPutFile);
//
        String name1= "test326";
        CreateTxtFile file1 = new CreateTxtFile(name1);

        List<String> urlList =  RequestRespondCall.methodCombine12(file1.file, rejsePlan);
        String first = urlList.get(0);
        String anObject = "http://www.w3.org/2001/XMLSchema-instance";
        System.out.println(first.equals(anObject));


        CreateTxtFile file2 = new CreateTxtFile("getting2");

        RequestRespondCall.methodCombine12(file2.file, urlList.get(3));
//        RequestRespondCall.requestRespondPrint("http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=965961%2F335618%2F8440%2F317768%2F86%3Fdate%3D04.02.21%26station_evaId%3D6033");
//        RequestRespondCall.requestRespondPrint("http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=892029%2F306142%2F300476%2F147105%2F86%3Fdate%3D06.02.21%26station_evaId%3D50460");
        System.out.println("closed");

    }


}
