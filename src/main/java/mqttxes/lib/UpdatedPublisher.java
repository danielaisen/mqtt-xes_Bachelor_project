/**
 * @author Daniel Max Aisen (s171206)
 **/

package mqttxes.lib;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

public class UpdatedPublisher {

    private final Mqtt5AsyncClient client;
    private String topicBase;
//    protected boolean clientIsConnected = false;
//    protected Mqtt3AsyncClient   client;
//    Mqtt3Connect connectMessage;

    public UpdatedPublisher(String topic) { //TODO change server host to the String MAYBE also topic
        topicBase = topic;
        MqttClientBuilder clientBuilder = MqttClient.builder()
//                .identifier("danielaUser1" + UUID.randomUUID().toString())
                .identifier("danielaUser1")
//                .serverHost("broker.hivemq.com")
                .serverHost("127.0.0.1")

                .serverPort(1883)
                ;

//    MqttClientBuilder clientBuilder = MqttClient.builder()
//            .identifier("daniela" + UUID.randomUUID().toString())
////                .serverHost("broker.hivemq.com")
//            .serverHost("127.0.0.1")
//            .serverPort(1883)
//            ;
//.useMqttVersion3()

        Mqtt5Client client = clientBuilder.useMqttVersion5().build();
        this.client= client.toAsync();

    }
    public void send(String message){ //todo check if the connection in on prior sending. check if the broker has disconnected
        //todo how do i specify which metaData is sent? send it
//        client.publishWith()
//                .topic("test/topic")
//                .qos(MqttQos.AT_LEAST_ONCE)
//                .payload("payload".getBytes())
//                .contentType("text/plain")
//                .send();

        client.publishWith()
                .topic(topicBase)
                .contentType("text/plain")
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(("hello mqtt start with 1, now " + message ).getBytes())
                .send();
//        client.disconnect();
    }

    public void connect() { //todo set a session expiry interval
        //todo have a print/check for Unsuccessful reason code / reason Strings
//        this.client.connectWith().cleanStart(false)
//                .willPublish()
//                .topic("test/topic")
//                .qos(MqttQos.AT_LEAST_ONCE)
//                .payload("payload".getBytes())
//                .retain(true)
//                .applyWillPublish();

        client.connect(); //todo set the client to connect first time with a clean start
    }
    public void disconnect(){
        client.disconnectWith(); //todo the result is Warning:(74, 16) Result of 'Mqtt5AsyncClient.disconnectWith()' is ignored
    }

    public void sendConnect() {
        client.publishWith().topic("test/topic").send();
    }

    public void send(XesMqttEvent event) {
//        CompletableFuture<Mqtt5ConnAck> connAckFuture = client.connect();

        client.publishWith().topic( //todo create a check that there are no wild cards in the message about to be sent
                //todo The same as wild card Never use spaces in a topic OR
                // leading forward slash
                topicBase + "/" +
                        event.getProcessName() + "/" +
                        event.getCaseId() + "/" +
                        event.getActivityName())
//                .qos(qos)
                .payload(event.getAttributes().getBytes())
                .send();

    }
}