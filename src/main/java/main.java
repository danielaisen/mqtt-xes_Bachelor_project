import fileConstructorXES.CreateXesMain;
import mqttxes.PublisherXES;
import requestRespond.RequestRespondRejsePlanTimeSeries;
import temp.RearangeSJONToProcessAware;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class main {


    public static void main(String[] args) throws Exception {

        System.out.println("starting main method");

        if (args.length == 0) {
            System.out.println("no arguments entered. constant values are used instead.");
            String callsForEachUrl = "10";
            String limitTheRoutes = "5";
            String howManyCalls = "20";
            String timeSeriesFileName = "fullRun07";

            String nameFile = "traceByLineObject_07";
            String nameFileXES = "file_XES_07";
            String logTime = "10";
            args = new String[]{callsForEachUrl, limitTheRoutes, howManyCalls, timeSeriesFileName, nameFile, nameFileXES, logTime};
        }


//        RequestRespondRejsePlanTimeSeries.main(args);
//        Thread.sleep(70000);
//
//        RearangeSJONToProcessAware.main(new String[]{args[3], args[4]});


        CreateXesMain.main(new String[]{args[4], args[5]});

        PublisherXES.main(new String[]{args[5], args[6]});

        System.out.println("DONE");

    }
}
