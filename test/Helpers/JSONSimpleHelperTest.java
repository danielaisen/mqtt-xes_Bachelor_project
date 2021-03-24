package Helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class JSONSimpleHelperTest {

    JSONSimpleHelper jsonSimpleHelper = new  JSONSimpleHelper();
    JSONObject jsonObject;
    JSONObject jsonObject2;
    JSONArray list;
    HashMap<String, String> traceInfo;
    Object tempObject;
    JSONParser jsonParser = new JSONParser();


    @BeforeEach
    void setUp() {
        jsonObject = new JSONObject();
        jsonObject.put("name", "daniel");
        jsonObject.put("age", 25);

        list = new JSONArray();
        list.add("msg 1");
        list.add("msg 2");
        jsonObject.put("list", list);
        jsonObject2 = new JSONObject();


    }

    @Test
    void retrieveInformationFromObjectUSINGSIMPLE() throws ParseException, IOException {
        traceInfo = new HashMap<>();
        traceInfo.put("routeIdxTo", "38");
        traceInfo.put("routeIdxFrom", "0");
        traceInfo.put("type", "IC");

        jsonObject2.put("routeIdxTo", "38");
        jsonObject2.put("routeIdxFrom", "0");

        tempObject = new HashMap<>();
        ((HashMap) tempObject).put("text", "some text");

        jsonSimpleHelper.retrieveInformationFromObjectUSINGSIMPLE(null, tempObject, traceInfo);
        jsonSimpleHelper.retrieveInformationFromObjectUSINGSIMPLE("something1", jsonObject2, traceInfo);


        JSONObject o = new JSONObject();

        o.put("routeIdxTo", "38");
        o.put("routeIdxFrom", "0");
        o.put("type", "IC");
        o.put("text", "some text");

        org.json.simple.JSONObject traceObject = new org.json.simple.JSONObject(traceInfo);


        ObjectMapper mapper = new ObjectMapper();
        JsonNode expected = mapper.readTree(String.valueOf(o));
        JsonNode actual = mapper.readTree(String.valueOf(traceObject));
        assertEquals(expected, actual);


        jsonSimpleHelper.retrieveInformationFromObjectUSINGSIMPLE("something2", list, traceInfo);
        traceObject = new org.json.simple.JSONObject(traceInfo);


        o.put("class java.lang.String", "msg 1");
        o.put("class java.lang.String_addition","msg 2");


        expected = mapper.readTree(String.valueOf(o));
        actual = mapper.readTree(String.valueOf(traceObject));
        assertEquals(expected, actual);

    }

    @Test
    void deleteFirstChar() {

        String string = "";

        String r = jsonSimpleHelper.deleteFirstChar(string);
        assertEquals(r, "{}");


        System.out.println(jsonObject);
        String string2 = "{ This is a part of the Json}";
        String r2 = jsonSimpleHelper.deleteFirstChar(string2);

        assertEquals("{This is a part of the Json}", r2);
    }
}