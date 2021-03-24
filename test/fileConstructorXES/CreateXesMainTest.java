package fileConstructorXES;

import Helpers.DateHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CreateXesMainTest {

    String[] emptyString = new String[]{};
    String[] string;


    @BeforeEach
    void setUp() {

        String processAware = "traceByLineObjectTEST";
        String fileNameXes = "file_XES_TEST";


        string =new String[]{processAware, fileNameXes};

    }


    @Test
    void main() throws FileNotFoundException, ParseException {

        String now = DateHelper.nowFull();
        CreateXesMain.main(string);


    }
}