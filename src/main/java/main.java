import fileConstructorXES.CreateXesMain;
import mqttxes.PublisherXES;
import requestRespond.RequestRespondRejsePlanTimeSeries;
import processAwareJSON.RearangeSJONToProcessAware;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class main {


    public static void main(String[] args) throws Exception {
        System.out.println("starting main method");

        if (args.length == 0) {
            System.out.println("no arguments entered. constant values are used instead.");
            String callsForEachUrl = "5";
            String limitTheRoutes = "4";
            String howManyCalls = "2";
            String sleepTime = "10";
            String timeSeriesFileName = "timeSeriesJSON2";


            String nameFile = "traceByLineObject3";

            String nameFileXES = "file_XES3";

            String logTime = "5";
            args = new String[]{callsForEachUrl, limitTheRoutes, howManyCalls, sleepTime, timeSeriesFileName, nameFile, nameFileXES, logTime};
        }


        RequestRespondRejsePlanTimeSeries.main(args);
        Thread.sleep(70000);


        RearangeSJONToProcessAware.main(new String[]{args[4], args[5]});
        Thread.sleep(70000);


        CreateXesMain.main(new String[]{args[5], args[6]});
        Thread.sleep(70000);


        PublisherXES.main(new String[]{args[6], args[7]});
        System.out.println("DONE");
        System.exit(0);
    }
}
