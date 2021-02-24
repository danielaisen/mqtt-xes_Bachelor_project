/**
 * @author Daniel Max Aisen (s171206)
 **/

package fileConstructorXES;

import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TempMain {


    public static void main(String[] args) throws FileNotFoundException {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        Path path = Paths.get("").toAbsolutePath();

        File folder = new File(String.valueOf(path));
        File[] listOfFiles = folder.listFiles();
        System.out.println(listOfFiles);

        try (FileReader reader = new FileReader(path  +"\\testFolder" + "\\"  + "JSON_file_try04.json")){
            Object object = jsonParser.parse(reader);
            jsonObject = (JSONObject) object;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        new CreateXES_File("file", jsonObject);


    }
}
