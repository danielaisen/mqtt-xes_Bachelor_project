/**
 * @author Daniel Max Aisen (s171206)
 **/

package fileConstructorXES;

import Helpers.FilesHelper;
import org.json.simple.JSONObject;


import java.io.FileNotFoundException;

public class CreateXesMain {


    public static void main(String[] args) throws FileNotFoundException, java.text.ParseException {
        System.out.println("Staring with creating of the XES log");

        String fileReader = "traceByLineObject";
        String fileNameXes = "file_XES2";

        if (args.length != 0) {
            fileReader = args[0];
            fileNameXes = args[1];
        }

        JSONObject jsonObject = FilesHelper.readJSONObjectFile(fileReader);


        new CreateXES_File(fileNameXes, jsonObject);

        System.out.println("Done with creating of the XES log");
    }

}
