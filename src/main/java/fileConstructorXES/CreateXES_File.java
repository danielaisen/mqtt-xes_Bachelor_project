/**
 * @author Daniel Max Aisen (s171206)
 **/

package fileConstructorXES;


import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import temp.DateHelper;


import javax.json.JsonObject;
import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class CreateXES_File {
    private FileOutputStream fileXESGZ;
    private FileOutputStream fileXES;


    CreateXES_File(String fileName, JSONObject jsonObject) throws FileNotFoundException, ParseException {
        Path path = Paths.get("").toAbsolutePath();
        fileXESGZ = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes.gz");
        fileXES = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        Date a = new Date();
//    public static void main(String[] args) throws IOException {
//        String name = (String) jsonObject.get("XES_Type");
        XLog logHelper = XLogHelper.generateNewXLog("log");
//
//        fileXESGZ = new FileOutputStream("a" + ".xes.gz");
//        fileXES = new FileOutputStream("a" + ".xes");

int debug =0;

//        XTrace xTrace = null;
//        XEvent xEvent;
        int caseID =0;
        JSONArray jsonArray = (JSONArray) jsonObject.get("Traces");

//        System.out.println("adding test stuff"); //todo delete this line
//
//
//        XTrace t1 = XLogHelper.insertTrace(logHelper, "case100");
//        XEvent e11 = XLogHelper.insertEvent(t1, "X", new Date());
//        XEvent e12 = XLogHelper.insertEvent(t1, "X", new Date());
//
//        XTrace t2 = XLogHelper.insertTrace(logHelper, "case200");
//
////        JSONObject jsonObjectEvent1234 = (JSONObject) ((JSONArray) ((JSONObject) jsonArray.get(0)).get("Events")).get(0);
//        Date timestamp1234 = DateHelper.getDate(jsonObjectEvent1234.get("time:timestamp"));
//        XEvent e1 = XLogHelper.insertEvent(t1, (String) jsonObjectEvent1234.get("Event_name"),timestamp1234);
//        XLogHelper.insertEvent(t1, (String) jsonObjectEvent1234.get("Event_name"),timestamp1234);
//        XEvent e231 = XLogHelper.insertEvent(t2, (String) jsonObjectEvent1234.get("Event_name"),timestamp1234);
//        e231 = XLogHelper.insertEvent(t2, (String) jsonObjectEvent1234.get("Event_name"),new Date());
//        XEvent e21 = XLogHelper.insertEvent(t2, "A", timestamp1234);
//        XLogHelper.decorateElement(e21, (String) "time:timestamp", new Date());
//        XEvent e22 = XLogHelper.insertEvent(t2, "B", new Date());
////        for (int i = 0; i < 5; i++) {
////            XLogHelper.insertEvent(t2, i + "B", new Date());
////
////            XLogHelper.insertEvent(t1, i+ "B", new Date(2021,02,20));
////
////            XLogHelper.insertEvent(t1, i + "A", new Date());
////
////
////        }
////        System.out.println("finsinh adding test stuff");


        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject JSONObject_TraceWithEvents = (JSONObject) jsonArray.get(i);
            if (JSONObject_TraceWithEvents.get("XES_Type").equals("Trace_Info")) {
                caseID++;
                XTrace xTrace = XLogHelper.insertTrace(logHelper, "case" + caseID);
                for (Object keysTraceData : JSONObject_TraceWithEvents.keySet()) {
                    if (keysTraceData.equals("Events")) {
                        JSONArray jsonArrayEvents = (JSONArray) JSONObject_TraceWithEvents.get(keysTraceData);
                        for (int j = 0; j < jsonArrayEvents.size(); j++) {

//                            System.out.println(debug);debug++;

                            JSONObject jsonObjectEvent = (JSONObject) jsonArrayEvents.get(j);
                            Date timestamp = DateHelper.getDate(jsonObjectEvent.get("time:timestamp"));

                            XEvent xEvent = XLogHelper.insertEvent(xTrace, (String) jsonObjectEvent.get("Event_name"),timestamp);
                            for (Object attributeName : jsonObjectEvent.keySet()) {
                                if (attributeName.equals("time:timestamp")) {
                                    continue;
                                }
                                String attributeNameString = (String) attributeName;

                                if (attributeNameString.startsWith("Original_data")) {
                                    //todo create a list for the original data
                                }

                                if (attributeName.equals("Event_name")) {
                                    continue;
                                }
                                String value = String.valueOf(jsonObjectEvent.get(attributeName));
                                XLogHelper.decorateElement(xEvent, (String) attributeName, value);
                            }
                        }
                    } else {
                        XLogHelper.decorateElement(xTrace, (String) keysTraceData, (String) JSONObject_TraceWithEvents.get(keysTraceData));
                    }
                }
            }  else {
                System.out.println("problem in the XES generator");
                System.exit(101);
            }
        }






        XesXmlSerializer serializer = new XesXmlSerializer();
        XesXmlGZIPSerializer xesXmlGZIPSerializer = new XesXmlGZIPSerializer();


            try {
                serializer.serialize(logHelper, fileXES);
                xesXmlGZIPSerializer.serialize(logHelper, fileXESGZ);
            } catch (IOException e) {
                e.printStackTrace();
            }


    //    }
    }

}
