package processAwareJSON;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

class RearangeSJONToProcessAwareTest {

    String[] emptyString = new String[]{};
    String[] string;


    @BeforeEach
    void setUp() {

        String readFile = "timeSeriesJSON";
        String nameFile2 = "traceByLineObject";
        string =new String[]{readFile, nameFile2};

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void main() throws ParseException {
        RearangeSJONToProcessAware.main(emptyString);
        RearangeSJONToProcessAware.main(string);
    }

    @Test
    void parseRejsePlanReturnStopsAndTraceSimple() throws ParseException {



    }

}