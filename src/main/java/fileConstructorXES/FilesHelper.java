package fileConstructorXES;

import mqttxes.lib.XesMqttEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class FilesHelper {

    public Path path;
    public String name;
    public File file;
//    XesMqttEvent e;
    public FilesHelper(String name){
        this.path = Paths.get("").toAbsolutePath();
        createFile(name);
//        XesMqttEvent e = new XesMqttEvent("me", "trying","mybest");

    }

    public static void createFileToJSONSimple(String name, JSONObject tripDetails) {
        Path path = Paths.get("").toAbsolutePath();
        try (FileWriter file = new FileWriter(path  +"\\testFolder" + "\\" + name + ".json")){
            file.write(String.valueOf(tripDetails));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void createFileToJSONSimple(String name, org.json.simple.JSONObject tripDetails) {
        Path path = Paths.get("").toAbsolutePath();
        try (FileWriter file = new FileWriter(path  +"\\testFolder" + "\\" + name + ".json")){
            file.write(String.valueOf(tripDetails));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void createFileToJSONSimple(String name, org.json.simple.JSONArray tripDetails) {
        Path path = Paths.get("").toAbsolutePath();
        try (FileWriter file = new FileWriter(path  +"\\testFolder" + "\\" + name + ".json")){
            file.write(String.valueOf(tripDetails));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static org.json.simple.JSONObject readJSONObjectFile(String fileReader) {
        JSONParser jsonParser = new JSONParser();
        org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
        Path path = Paths.get("").toAbsolutePath();

        try (FileReader reader = new FileReader(path  +"\\testFolder" + "\\"  + fileReader +".json")){
            Object object = jsonParser.parse(reader);
            jsonObject = (org.json.simple.JSONObject) object;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static org.json.simple.JSONArray readJSONArrayFile(String fileReader) {
        JSONParser jsonParser = new JSONParser();
        org.json.simple.JSONArray jsonArray = new org.json.simple.JSONArray();
        Path path = Paths.get("").toAbsolutePath();

        try (FileReader reader = new FileReader(path  +"\\testFolder" + "\\"  + fileReader +".json")){
            Object object = jsonParser.parse(reader);
            jsonArray = (org.json.simple.JSONArray) object;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
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
