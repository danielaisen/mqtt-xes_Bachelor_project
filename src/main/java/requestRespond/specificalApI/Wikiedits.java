package requestRespond.specificalApI;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mqttxes.lib.XesMqttEvent;
import mqttxes.lib.XesMqttProducer;
import mqttxes.lib.exceptions.XesMqttClientNotConnectedException;

public class Wikiedits {

	private static Double eventSent = 0d;
	private static List<String> processesToStream = Arrays.asList("enwiki");
	private static JSONArray jsonArray = new JSONArray();

	public static void main(String[] args) throws InterruptedException {
		String brokerHost = "broker.hivemq.com";//args[0];
		while (true) {
			XesMqttProducer mqttClient = new XesMqttProducer(brokerHost, "daniel1/wiki"); //todo update to the new client

			System.out.print("Connecting... ");

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("https://stream.wikimedia.org/v2/stream/recentchange");
			mqttClient.connect();
			SseEventSource source = SseEventSource.target(target).reconnectingEvery(5, TimeUnit.SECONDS).build();

			System.out.println("Done!");


			source.register(new Consumer<InboundSseEvent>() {
				@Override
				public void accept(InboundSseEvent t) {
					String data = t.readData();

					/*
										if (data.equals("")){
						System.out.println("got empty data");
					}
					else if (data != null && !data.equals("")) {
					 */
					if ((!data.equals("") & data != null)) {
						try {
                            JSONObject jsonObject = new JSONObject(data);
                            jsonArray.put(jsonObject);

						System.out.println("created a json object: " + data);
							send(mqttClient, jsonObject, processesToStream); //todo update to the new client
						} catch (JSONException | InterruptedException | XesMqttClientNotConnectedException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("data received was empty");
					}
				}
			});
			source.open();
			System.out.print("waiting 10 sec ");
			Thread.sleep(1000 * 10 * 1); // wait a minutes
			System.out.println("starting again");
//			mqttClient.disconnect();
		}
	}

	public static void send(XesMqttProducer mqttClient, JSONObject obj, List<String> processes) throws InterruptedException, XesMqttClientNotConnectedException {
		String processName = obj.getString("wiki");
		String caseId = DigestUtils.md5Hex(obj.getString("title"));
		String activityName = obj.getString("type");

		if (processes.contains(processName)) {
			XesMqttEvent e = new XesMqttEvent(processName, caseId, activityName);

			e.addEventAttribute("org:resource", obj.getString("user"));
			e.addEventAttribute("comment", obj.getString("comment"));
			e.addTraceAttribute("title", obj.getString("title"));

			mqttClient.send(e);
			eventSent += 1;
			System.out.println(caseId + " - " + activityName);
			if (eventSent % 100 == 0) {
				System.out.println("");
				System.out.println("Sent " + eventSent.intValue() + " events");
			}
		}
	}
}
