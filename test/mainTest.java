import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class mainTest {

    @Test
    void main() throws Exception {

        System.out.println("no arguments entered. constant values are used instead.");
        String callsForEachUrl = "2";
        String limitTheRoutes = "5";
        String howManyCalls = "1";
        String sleepTime = "10";
        String timeSeriesFileName = "timeSeriesTEST";

        String nameFile = "traceByLineObjectTEST";

        String nameFileXES = "file_XESTEST";

        String logTime = "5";
        String[] args = new String[]{callsForEachUrl, limitTheRoutes, howManyCalls, sleepTime, timeSeriesFileName, nameFile, nameFileXES, logTime};


        new main().main(args);

        new main().main(new String[0]);
    }
}