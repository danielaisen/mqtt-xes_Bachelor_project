/**
 * @author Daniel Max Aisen (s171206)
 **/

package Helpers;

import java.util.ArrayList;
import java.util.HashMap;

public class JSONSimpleHelper {


    public static void retrieveInformationFromObjectUSINGSIMPLE(Object keys, Object original, HashMap<String, String> traceInfo) {
        if (original instanceof String) {
            if (traceInfo.containsKey(keys)) {
                if (!traceInfo.get(keys).equals(original)) {
                    traceInfo.put(keys + "_addition", (String) original);
                }else { } //do nothing
            }
            else{
                traceInfo.put((String)keys, (String) original);
            }
//            traceInfo.put((String) keys, (String) original);
        }
        else if (original instanceof org.json.simple.JSONObject) {
            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) original;
            for (Object key : jsonObject.keySet()) {
                retrieveInformationFromObjectUSINGSIMPLE((String)key, jsonObject.get(key), traceInfo);

            }
        }
        else if (original instanceof org.json.simple.JSONArray) {

                for (Object object : (org.json.simple.JSONArray) original) {
                    if (!(object instanceof org.json.simple.JSONObject)) {
                        retrieveInformationFromObjectUSINGSIMPLE(String.valueOf(object.getClass()),object, traceInfo);
                        continue;
                    }
                    retrieveInformationFromObjectUSINGSIMPLE(null, object, traceInfo);
                }

        }
        else if (original instanceof ArrayList) {
            System.out.println("\n inside the retrieveInformationFromObject object found " + original.getClass());

            System.exit(1001);
        }
        else if (original instanceof HashMap) {
            HashMap hashMap = (HashMap) original;
            for (Object key : hashMap.keySet()) {

                retrieveInformationFromObjectUSINGSIMPLE(key, hashMap.get(key), traceInfo);
            }
        } else if (original == null) {
            System.out.println();
            System.out.println("\n inside the retrieveInformationFromObject object found " + null);
            System.out.println();

        } else {
            System.out.println("\n error has occur in retrieveInformationFromObject method");
            System.out.println(original.getClass() + "  has been found instead");
            System.exit(1003);
        }
    }

    public static String deleteFirstChar(String responseBody) {
        if (responseBody.length() <= 2) {
            return "{}";
        }

        responseBody = '{' + responseBody.substring(2);
        return responseBody;
    }
}
