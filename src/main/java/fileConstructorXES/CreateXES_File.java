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


import javax.json.JsonObject;
import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateXES_File {
    private FileOutputStream fileXESGZ;
    private FileOutputStream fileXES;


    CreateXES_File(String fileName, JSONObject jsonObject) throws FileNotFoundException, ParseException {
        Path path = Paths.get("").toAbsolutePath();
        fileXESGZ = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes.gz");
        fileXES = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

//    public static void main(String[] args) throws IOException {
//        String name = (String) jsonObject.get("XES_Type");
        XLog logHelper = XLogHelper.generateNewXLog("log");
//
//        fileXESGZ = new FileOutputStream("a" + ".xes.gz");
//        fileXES = new FileOutputStream("a" + ".xes");



        XTrace xTrace = null;
        XEvent xEvent;
        int caseID =0;
        for (Object keys : jsonObject.keySet()) {

            JSONArray jsonArray = (JSONArray) jsonObject.get(keys);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject JSONObject_TraceInfoOrEvents = (JSONObject) jsonArray.get(i);
                if (JSONObject_TraceInfoOrEvents.get("XES_Type").equals("Trace_Info")) {
                    caseID++;
                    xTrace = XLogHelper.insertTrace(logHelper, "case" + caseID);
                    for (Object keysTraceData : JSONObject_TraceInfoOrEvents.keySet()) {
                        XLogHelper.decorateElement(xTrace, (String) keysTraceData, (String) JSONObject_TraceInfoOrEvents.get(keysTraceData));
                    }
                } else if (JSONObject_TraceInfoOrEvents.get("XES_Type").equals("Events")) {
                    for (Object keysTraceData : JSONObject_TraceInfoOrEvents.keySet()) {
                        if (keysTraceData.equals("Events")) {
                            JSONArray jsonArrayAllEvents = (JSONArray) JSONObject_TraceInfoOrEvents.get(keysTraceData);
                            for (int j = 0; j < jsonArrayAllEvents.size(); j++) {

                                JSONObject jsonObjectEvent = (JSONObject) jsonArrayAllEvents.get(j);

                                xEvent = XLogHelper.insertEvent(xTrace, (String) jsonObjectEvent.get("Event_Name")); //todo decide on name

                                for (Object attributeName : jsonObjectEvent.keySet()) {
                                    if (attributeName.equals("Event_Name")) {
                                        continue;
                                    }
                                    String value = String.valueOf(jsonObjectEvent.get(attributeName));
                                    XLogHelper.decorateElement(xEvent, (String) attributeName, value);
                                }
                            }
                        }
//                      }      for (JsonObject jsonObjectEvent : jsonArrayAllEvents)
//                                xEvent = XLogHelper.insertEvent(xTrace, "X", new Date());
//                            XLogHelper.decorateElement(xEvent, (String) "Activity name", (String) "Value");
                    }

                } else {
                    System.out.println("problem in the XES generator");
                    System.exit(101);
                }
            }

        }
        System.out.println("adding test stuff"); //todo delete this line


//        XTrace t1 = XLogHelper.insertTrace(logHelper, "case100");
//        XEvent e11 = XLogHelper.insertEvent(t1, "X", new Date());
//        XEvent e12 = XLogHelper.insertEvent(t1, "X", new Date());

        XTrace t2 = XLogHelper.insertTrace(logHelper, "case200");

        XEvent e21 = XLogHelper.insertEvent(t2, "A");
        XLogHelper.decorateElement(e21, (String) "time:timesptamp", new Date());
//        XEvent e22 = XLogHelper.insertEvent(t2, "B", new Date());
//            for (int i = 0; i < 5; i++) {
//                XLogHelper.insertEvent(t2, i + "B", new Date());
//
//                XLogHelper.insertEvent(t1, i+ "B", new Date(2021,02,20));
//
//                XLogHelper.insertEvent(t1, i + "A", new Date());
//
//
//            }

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
