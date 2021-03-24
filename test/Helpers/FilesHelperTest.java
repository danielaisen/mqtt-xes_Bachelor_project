package Helpers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FilesHelperTest {
    FilesHelper filesHelper = new FilesHelper("file");
    FilesHelper filesHelper2 = new FilesHelper("file");

    org.json.simple.JSONObject jsonObject;
    JSONArray list;

    @BeforeEach
    void setUp() {
        jsonObject = new JSONObject();
        jsonObject.put("name", "daniel");
        jsonObject.put("age", 25);

        list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        list.add("msg 3");
        jsonObject.put("list", list);

//    jsonObject =

    }



    @Test
    void readJSONObjectFile() throws ParseException {

        JSONParser jsonParser = new JSONParser();
        filesHelper.createFileToJSONSimple("JSON_test_object",jsonObject);
        JSONObject object = filesHelper.readJSONObjectFile("JSON_test_object");
        Object o = (Object) jsonParser.parse(String.valueOf(object));
        Object original = (Object) jsonParser.parse(String.valueOf(jsonObject));
        assertTrue(o.equals(original));
    }

    @Test
    void readJSONArrayFile() {


        filesHelper.createFileToJSONSimple("JSON_test_array",list);
        JSONArray array = filesHelper.readJSONArrayFile("JSON_test_array");
        assertEquals(array, list);

    }

    @Test
    void addPathXESGZ() {

        String name = filesHelper.addPathXESGZ("xes name");
        assertEquals(name, Paths.get("").toAbsolutePath() + "\\testFolder" + "\\" +"xes name.xes.gz");

    }
}
