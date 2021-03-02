/**
 * @author Daniel Max Aisen (s171206)
 **/

package fileConstructorXES;

import org.json.simple.JSONObject;


import java.io.FileNotFoundException;

public class TempMain {


    public static void main(String[] args) throws FileNotFoundException, java.text.ParseException {
        String fileReader = "traceByLine";

        JSONObject jsonObject = FilesHelper.readJSONObjectFile(fileReader);

        new CreateXES_File("file_XES2", jsonObject);


    }

}
