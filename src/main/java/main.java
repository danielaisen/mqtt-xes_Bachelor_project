import fileConstructorXES.CreateXesMain;
import mqttxes.PublisherXES;
import requestRespond.RequestRespondMain;
import temp.RearangeSJONToProccesAware;

/**
 * @author Daniel Max Aisen (s171206)
 **/

public class main {


    public static void main(String[] args) throws Exception {

        RequestRespondMain.main(args);
        RearangeSJONToProccesAware.main(args);
        CreateXesMain.main(args);
        PublisherXES.main(args);

    }
}
