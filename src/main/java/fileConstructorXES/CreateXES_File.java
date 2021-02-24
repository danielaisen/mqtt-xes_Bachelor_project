/**
 * @author Daniel Max Aisen (s171206)
 **/

package fileConstructorXES;


import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class CreateXES_File {
    private FileOutputStream fileXESGZ;
    private FileOutputStream fileXES;


    CreateXES_File(String fileName, JSONObject jsonObject) throws FileNotFoundException {
        Path path = Paths.get("").toAbsolutePath();
        fileXESGZ = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes.gz");
        fileXES = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes");


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
            for (Object keysTrace : jsonArray) {
                //this should be JSON OBJECT and to look inside of them.




                JSONObject JSONObject_DataOrEvents = (JSONObject) JSONObject.get(keysTrace);
                if (JSONObject_DataOrEvents.get("XES_Type").equals("Trace_Info")) {
                    caseID++;
                    xTrace = XLogHelper.insertTrace(logHelper, "case"+ caseID);
                    for (Object keysTraceData : JSONObject_DataOrEvents.keySet()) {
                        XLogHelper.decorateElement(xTrace,(String) keysTraceData, (String) JSONObject_DataOrEvents.get(keysTraceData));
                    }
                } else if (JSONObject_DataOrEvents.get("XES_Type").equals("Events")) {
                    for (Object keysTraceData : JSONObject_DataOrEvents.keySet()) {
                        xEvent = XLogHelper.insertEvent(xTrace, "X", new Date());
                        XLogHelper.decorateElement(xEvent,(String) "Activity name",(String) "Value" );
                    }

                } else {
                    System.out.println("problem in the XES generator");
                    System.exit(101);
                }
            }


        }


        XTrace t1 = XLogHelper.insertTrace(logHelper, "case1");
        XEvent e11 = XLogHelper.insertEvent(t1, "X", new Date());
        XEvent e12 = XLogHelper.insertEvent(t1, "X", new Date());

        XTrace t2 = XLogHelper.insertTrace(logHelper, "case2");
        XEvent e21 = XLogHelper.insertEvent(t2, "A", new Date());
        XEvent e22 = XLogHelper.insertEvent(t2, "B", new Date());
            for (int i = 0; i < 5; i++) {
                XLogHelper.insertEvent(t2, i + "B", new Date());

                XLogHelper.insertEvent(t1, i+ "B", new Date(2021,02,20));

                XLogHelper.insertEvent(t1, i + "A", new Date());


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
