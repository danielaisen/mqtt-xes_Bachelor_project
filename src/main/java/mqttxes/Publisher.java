package mqttxes;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import mqttxes.lib.UpdatedPublisher;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import mqttxes.lib.XesMqttEvent;
//import mqttxes.lib.XesMqttProducer;

public class Publisher {

	private static XFactory factory = new XFactoryNaiveImpl();

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.out.println("Use java -jar mqtt-xes.jar LOG.XES.GZ MILLISECONDS");
			System.exit(1);
		}


		System.out.print("Parsing log... ");
		XLog log = new XesXmlGZIPParser(factory).parse(new File(args[0])).get(0);
		System.out.println("here");
		String logName = XConceptExtension.instance().extractName(log);
		System.out.println("Done");

		List<XTrace> events = log2events(log);
		int millis = 10;
		int deviding_time = Integer.parseInt(args[1]);

		XTrace first = events.get(0);
		Date startDate = event_handler(logName, first).getTime();

		System.out.print("Streaming... ");
		UpdatedPublisher client = new UpdatedPublisher("pmcep");
//		XesMqttProducer client = new XesMqttProducer( "localhost", "pmcep");

		System.out.println("start");
		client.connect();
		int i = 0 ;
		for (XTrace trace : events) { //todo Figure out why it doesnt send the first element
			System.out.println(i);
			XesMqttEvent event = event_handler(logName, trace);

			Date secondDate = event.getTime();
//			System.out.println(secondDate);
			int diffInMillis = time_interval(startDate, secondDate, deviding_time);
			startDate = secondDate;
			System.out.println(diffInMillis);

			event.removeEventAttribute("time:timestamp");
			client.send(Integer.toString(i));
			client.send(event); //todo create an option to separate between different topics
			Thread.sleep(millis);
//			Thread.sleep(diffInMillis);

			i++;

		}
		client.disconnect();
		System.out.println("done");

	}

	private static int time_interval(Date startDate, Date secondDate, int deviding_time) {

		int abs = Math.round(Math.abs(secondDate.getTime() - startDate.getTime())/deviding_time); //todo delete the Math.abs
//todo Warning:(81, 24) 'Math.abs(secondDate.getTime() - startDate.getTime())/deviding_time': integer division in floating-point context
		return abs;
	}

	private static XesMqttEvent event_handler(String logName, XTrace trace) {
		String caseId = XConceptExtension.instance().extractName(trace);
		String activity = XConceptExtension.instance().extractName(trace.get(0));
		XesMqttEvent event = new XesMqttEvent(logName, caseId, activity);

		event.addAllTraceAttributes(trace.getAttributes());
		event.addAllEventAttributes(trace.get(0).getAttributes());
		return event;
	}

	private static List<XTrace> log2events(XLog log) throws Exception { //todo Warning:(96, 58) Exception 'java.lang.Exception' is never thrown in the method
		System.out.print("Parsing the events for streaming... ");
		List<XTrace> events = new LinkedList<XTrace>(); //todo Warning:(98, 40) Explicit type argument XTrace can be replaced with <>
		for(XTrace trace : log) {
			for (XEvent event: trace) {
				XTrace t = factory.createTrace(trace.getAttributes());
				t.add(event);
				events.add(t);
			}
		}
		System.out.println("Done");
		System.out.print("Sorting the events for streaming... ");
		events.sort(new Comparator<XTrace>() { //todo Warning:(108, 15) Anonymous new Comparator<XTrace>() can be replaced with lambda
			@Override
			public int compare(XTrace o1, XTrace o2) {
				Date d1 = XTimeExtension.instance().extractTimestamp(o1.get(0));
				Date d2 = XTimeExtension.instance().extractTimestamp(o2.get(0));
				return d1.compareTo(d2);
			}
		});
		System.out.println("Done");
		return events;
	}
}
