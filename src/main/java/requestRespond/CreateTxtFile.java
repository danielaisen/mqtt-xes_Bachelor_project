package requestRespond;

import mqttxes.lib.XesMqttEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class CreateTxtFile {

    public Path path;
    public String name;
    public File file;
//    XesMqttEvent e;
    public CreateTxtFile(String name){
        this.path = Paths.get("").toAbsolutePath();
        createFile(name);
//        XesMqttEvent e = new XesMqttEvent("me", "trying","mybest");

    }

    public static void createFileToJSON(String name, JSONObject tripDetails) {
        Path path = Paths.get("").toAbsolutePath();
        try (FileWriter file = new FileWriter(path  +"\\testFolder" + "\\" + name + ".json")){
            file.write(String.valueOf(tripDetails));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createFile(String name) {
        this.name = name;

        try {
            Path path = Paths.get("").toAbsolutePath();
            File myObj = new File(path  +"\\testFolder" + "\\" +  name +".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                this.file = myObj;

            } else {
                System.out.println("File already exists.");
                this.path = Paths.get(name).toAbsolutePath();
                this.file = myObj;
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
