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
            String callsForEachUrl = "4";
            String limitTheRoutes = "5";
            String howManyCalls = "3";
            String timeSeriesFileName = "fullRun13";

            String nameFile = "traceByLineObject_13";
            String nameFileXES = "file_XES_12";
            String logTime = "10";
            args = new String[]{callsForEachUrl, limitTheRoutes, howManyCalls, timeSeriesFileName, nameFile, nameFileXES, logTime};
        }

//
//        RequestRespondRejsePlanTimeSeries.main(args);
//        Thread.sleep(70000);


        RearangeSJONToProcessAware.main(new String[]{args[3], args[4]});
        Thread.sleep(70000);


        CreateXesMain.main(new String[]{args[4], args[5]});
        Thread.sleep(70000);


        PublisherXES.main(new String[]{args[5], args[6]});
        System.out.println("DONE");
        System.exit(0);
    }
}
