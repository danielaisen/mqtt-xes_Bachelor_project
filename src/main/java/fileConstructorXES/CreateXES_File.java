/**
 * @author Daniel Max Aisen (s171206)
 **/

package fileConstructorXES;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XSerializerRegistry;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.json.JSONObject;
import requestRespond.XLogHelper;


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
        XLog logHelper = XLogHelper.generateNewXLog("firstLog");
//
//        fileXESGZ = new FileOutputStream("a" + ".xes.gz");
//        fileXES = new FileOutputStream("a" + ".xes");
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
