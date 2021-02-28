package temp;

import fileConstructorXES.FilesHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.HashMap;

import static requestRespond.specificalApI.RejsePlanCall.*;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class RearangeSJONToProccesAware {

    public static void main(String[] args) {


        JSONArray jsonArrayTimeSeries = FilesHelper.readJSONArrayFile("timeSeriesJSON2");
        for (Object object : jsonArrayTimeSeries) {
            JSONObject objectTimeSeries = (JSONObject) object;
            System.out.println(jsonArrayTimeSeries.size());
            Object date = objectTimeSeries.get("time");
            objectTimeSeries.remove("time");
            HashMap<String , String> myTEST = new HashMap<>();

            for (Object key : objectTimeSeries.keySet()) {
                JSONObject event = (JSONObject) objectTimeSeries.get(key);

                String string = event.toString();

                parseRejsePlanReturnStopsAndTraceSimple(string);
                System.out.println(string);
//                retrieveInformationFromObjectUSINGSIMPLE(null, event, myTEST); //not working jet. gather all the data.


            }




        }

        int i;



    }




}
