/**
 * @author Daniel Max Aisen (s171206)
 **/

package fileConstructorXES;


import Helpers.XLogHelper;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import Helpers.DateHelper;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;

public class CreateXES_File {
    private FileOutputStream fileXESGZ;
    private FileOutputStream fileXES;


    CreateXES_File(String fileName, JSONObject jsonObject) throws FileNotFoundException, ParseException {
        Path path = Paths.get("").toAbsolutePath();
        fileXESGZ = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes.gz");
        fileXES = new FileOutputStream(path  +"\\testFolder" +"\\" +fileName + ".xes");

        XLog logHelper = XLogHelper.generateNewXLog("log");

        int caseID =0;
        JSONArray jsonArray = (JSONArray) jsonObject.get("Traces");

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject JSONObject_TraceWithEvents = (JSONObject) jsonArray.get(i);
            if (JSONObject_TraceWithEvents.get("XES_Type_").equals("Trace_Info")) {
                caseID++;
                XTrace xTrace = XLogHelper.insertTrace(logHelper, "case" + caseID);
                for (Object keysTraceData : JSONObject_TraceWithEvents.keySet()) {
                    if (keysTraceData.equals("Events")) {
                        JSONArray jsonArrayEvents = (JSONArray) JSONObject_TraceWithEvents.get(keysTraceData);
                        for (int j = 0; j < jsonArrayEvents.size(); j++) {

                            JSONObject jsonObjectEvent = (JSONObject) jsonArrayEvents.get(j);
                            Date timestamp = DateHelper.getDate(jsonObjectEvent.get("time:timestamp"));

                            XEvent xEvent = XLogHelper.insertEvent(xTrace, (String) jsonObjectEvent.get("Event_name_"),timestamp);
                            for (Object attributeName : jsonObjectEvent.keySet()) {
                                if (attributeName.equals("time:timestamp")) {
                                    continue;
                                }
                                String attributeNameString = (String) attributeName;

                                if (attributeName.equals("Data_Stop_")) {
                                    JSONObject data_stop = (JSONObject) jsonObjectEvent.get(attributeName);
                                    for (Object key : data_stop.keySet()) {
                                        String value = String.valueOf(data_stop.get(key));
                                        XLogHelper.decorateElement(xEvent, (String) key, value);
                                    }
                                    continue;
                                }


                                if (attributeName.equals("Event_name_")) {
                                    continue;
                                }
                                String value = String.valueOf(jsonObjectEvent.get(attributeName));
                                if (value != null) {
                                    XLogHelper.decorateElement(xEvent, (String) attributeName, value);
                                }

                            }
                        }
                    } else {
                        String value = (String) JSONObject_TraceWithEvents.get(keysTraceData);
                        if (value != null) {
                            XLogHelper.decorateElement(xTrace, (String) keysTraceData, value);
                        }

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
