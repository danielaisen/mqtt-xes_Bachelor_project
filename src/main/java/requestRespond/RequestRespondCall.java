package requestRespond;

import mqttxes.lib.XesMqttEvent;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.setAttribute;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class RequestRespondCall {

    private static HttpURLConnection connection;
    private static XFactory factory = new XFactoryNaiveImpl();


    public void requestRespondPrint() throws MalformedURLException {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();

        //Customer c = new Customer("Bastardian");

//        URL rejsePlan = new URL( "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=88506%2F39470%2F837494%2F389259%2F86%3Fdate%3D28.01.21%26station_evaId%3D50460%26format%3Djson");
        URL rejsePlan = new URL("http://xmlopen.rejseplanen.dk/bin/rest.exe/trip?destId=8600664&originId=6584&useTog=0&useBus=1&useMetro=0&format=json");
        String wikipendia = "http://.....";

        //method 1 java.net.HttpURLConnection
        try {
//            URL url = new URL("http://jsonplaceholder.typicode.com/albums");
            int status = setConnection(rejsePlan);

            appendContent(responseContent, status);
            System.out.println(responseContent.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    public static void requestRespondPrint(String urlName) throws MalformedURLException {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();


        //method 1 java.net.HttpURLConnection
        try {
            int status = setConnection(new URL(urlName));

            appendContent(responseContent, status);
            System.out.println(responseContent.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }


    private static int setConnection(URL rejsePlan) throws IOException {
        XAttributeMap traceAttributes ;

//        traceAttributes.put("trace name",new XAttribute("IsEvenNumber", false));
        factory.createTrace();
        URL url = rejsePlan;
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();
        System.out.println(status);
        return status;
    }


    private static void appendContent(StringBuffer responseContent, int status) throws IOException {
        BufferedReader reader;
        String line;
        if (status > 299) {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                responseContent.append(line);

            }
            reader.close();
        }
    }


    static List<String> oldMthodCombine12(File outPutFile) throws IOException {
        BufferedReader reader;
        String line;
//        StringBuffer responseContent = new StringBuffer();

//        URL rejsePlan = new URL( "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=88506%2F39470%2F837494%2F389259%2F86%3Fdate%3D28.01.21%26station_evaId%3D50460%26format%3Djson");
//        URL url = new URL("http://jsonplaceholder.typicode.com/albums");
        URL rejsePlan = new URL("http://xmlopen.rejseplanen.dk/bin/rest.exe/" +
                "trip?destId=8600664&originId=6584&useTog=0&useBus=1&useMetro=0&type=json");
        String wikipendia = "http://.....";
        BufferedWriter bufferedWriter;
        //method 1 java.net.HttpURLConnection
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outPutFile));
//
            int status = setConnection(rejsePlan);

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
//                    responseContent.append(line);
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
//                    responseContent.append(line);
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                reader.close();
                bufferedWriter.close();
            }
//            System.out.println(responseContent.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        List<String> http = returnHTTP(outPutFile);
        return http;
    }


    static List<String> setInTxtAndReturnHttpList(File outPutFile, String urlIn) throws IOException { //todo change to String []
        BufferedReader reader;
        String line;
       //        StringBuffer responseContent = new StringBuffer();

//        URL rejsePlan = new URL( "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=88506%2F39470%2F837494%2F389259%2F86%3Fdate%3D28.01.21%26station_evaId%3D50460%26format%3Djson");
        URL url = new URL(urlIn);

        BufferedWriter bufferedWriter;
        //method 1 java.net.HttpURLConnection

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outPutFile));
//
            int status = setConnection(url);

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    System.out.println("to high of status from http"); //todo delete this
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
                reader.close();
                bufferedWriter.close();
            }
//            System.out.println(responseContent.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        List<String> http = returnHTTP(outPutFile);
        return http;
    }

//    static JSONObject getAndWriteLineByLineToTxt(File outPutFile, String urlIn) throws IOException {
//        BufferedReader reader; //todo clean up this method
//        String line;
//        String string = "";
////        JSONObject jsonObject = new JSONObject();
//        XesMqttEvent event = new XesMqttEvent("me", "trying","my best ");
////        StringBuffer responseContent = new StringBuffer();
//
////        URL rejsePlan = new URL( "http://webapp.rejseplanen.dk/bin//rest.exe/journeyDetail?ref=88506%2F39470%2F837494%2F389259%2F86%3Fdate%3D28.01.21%26station_evaId%3D50460%26format%3Djson");
//        URL url = new URL(urlIn);
//
//        BufferedWriter bufferedWriter;
//        //method 1 java.net.HttpURLConnection
//
//        try {
//            bufferedWriter = new BufferedWriter(new FileWriter(outPutFile));
////
//            int lineNumber = 0;
//            int status = setConnection(url);
//
//            if (status > 299) {
//                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
//                while ((line = reader.readLine()) != null) {
//                    event.addEventAttribute("000" + line.substring(1,4), line); //todo clean up event attributes
//                    string += line;
//                }
//                reader.close();
//            } else {
//                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                while ((line = reader.readLine()) != null) {
//                    string += line;
//                    event.addEventAttribute( "00000" + lineNumber , line);
//                    bufferedWriter.write(lineNumber);
//                    lineNumber++;
//                }
//                bufferedWriter.write(event.getAttributes());
//                reader.close();
//                bufferedWriter.close();
//            }
////            System.out.println(responseContent.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            connection.disconnect();
//        }
//        System.out.println("finish writing the data on the file. looking for any Http");
//        List<String> http = returnHTTP(outPutFile);
////        return http;
//        JSONObject jsonObject = new JSONObject(string);
//        return jsonObject;
//    }


    private static List<String> returnHTTP(File outPutFile) throws IOException {
        BufferedReader bufferedReader = null;
        List<String> https = new ArrayList<String>();
        int howmany = 0;
        try {
            bufferedReader = new BufferedReader(new FileReader(outPutFile));
            String line;
            String http;
            char start;
            int endInt;
            int startInt;

            while ((line = bufferedReader.readLine()) != null) {

                for(int i = 0; i < line.length() ; i++){

                    if (line.charAt(i)=='h' && line.charAt(i+1)=='t' && line.charAt(i+2)=='t' && line.charAt(i+3)=='p'){
                        howmany++;
                        startInt = i-1;
                        start = line.charAt(startInt);

                        while (true) { //calculate the length of the string
                            i++;
                            if (line.charAt(i) == start){
                                endInt = i;
                                break;
                            }
                        }

                        http =  line.substring(startInt+1,endInt);
                        https.add(http);
                    }
                }
//                List<String> e = extractUrls(line);
//                if (e.size() > 0) {
//                    https.add(e);
//                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            bufferedReader.close();

        }
//        System.out.println("there has been found " + howmany + " http connections");
        return https;
    }



}
