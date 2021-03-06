import fileConstructorXES.CreateXesMain;
import mqttxes.PublisherXES;
import requestRespond.RequestRespondRejsePlanTimeSeries;
import temp.RearangeSJONToProccesAware;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class main {


    public static void main(String[] args) throws Exception {

        System.out.println("starting main method");

        if (args.length == 0) {
            System.out.println("no arguments entered. constant values are used instead.");
            String callsForEachUrl = "10";
            String limitTheRoutes = "10";
            String howManyCalls = "10";
            String timeSeriesFileName = "fullRun06";

            String nameFile = "traceByLineObject";
            String nameFileXES = "file_XES";
            String logTime = "10";
            args = new String[]{callsForEachUrl, limitTheRoutes, howManyCalls, timeSeriesFileName, nameFile, nameFileXES, logTime};
        }


//        RequestRespondRejsePlanTimeSeries.main(args);
//        Thread.sleep(70000);

        RearangeSJONToProccesAware.main(new String[]{args[3], args[4]});


        CreateXesMain.main(new String[]{args[4], args[4]});

        PublisherXES.main(new String[]{args[5], args[6]});

        System.out.println("DONE");

    }
}
