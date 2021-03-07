package mqttxes;

import java.io.File;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import Helpers.FilesHelper;
import mqttxes.lib.UpdatedPublisher;
//import mqttxes.lib.XesMqttProducer;
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



/**
 * This class and its updateTimeSeries method publish events that are in the log.
 * It is done by an mqtt Hive-MQ library.
 * They are published based on their timestamp.
 *
 * The updateTimeSeries method needs the following arguments to initialized:
 * [0] XES.GZ file - the log that the program should be running.
 * [1] minutes - the wished interval time for the running the log.
 *
 * If the wished interval is longer than the original, the original time is kept.
 */

public class PublisherXES {

	private static XFactory factory = new XFactoryNaiveImpl();

	public static void main(String[] args) throws Exception {
		System.out.println("Staring call to publish the log");

		if (args.length != 2) {
//			System.out.println("Use java -jar mqtt-xes.jar LOG.XES.GZ Minutes");
			System.out.println("Use LogName Minutes");
			System.exit(1);
		}

		System.out.print("Parsing log... ");

		String fileName = FilesHelper.addPathXESGZ(args[0]);
		XLog log = new XesXmlGZIPParser(factory).parse(new File(fileName)).get(0);
		List<XTrace> traces = log2events(log);

		String logName = XConceptExtension.instance().extractName(log);
		logName = UpdatedPublisher.checkForWildCards(logName);

		System.out.println("Done");


		XTrace firstTrace = traces.get(0);
		Date startDate = event_handler(logName, firstTrace).getTime();
		XTrace lastTrace = traces.get(traces.size()-1);
        Date lastDate = event_handler(logName, lastTrace).getTime();

        int wishedInterval = Integer.parseInt(args[1]);
        float dividingTime = wishedTime(wishedInterval, startDate, lastDate);

		System.out.print("Streaming... ");
		UpdatedPublisher client = new UpdatedPublisher("daniel1");

		System.out.printf("start. There will be: %d events \n", traces.size());
		client.connect();
		int i = 0 ;
		for (XTrace trace : traces) { //todo Figure out why it doesnt send the first element
			System.out.printf("event number %d is running. %n" ,i); //todo do i need this?
			XesMqttEvent event = event_handler(logName, trace);

			Date secondDate = event.getTime();
			int diffInMillis = time_interval(startDate, secondDate, dividingTime);
			startDate = secondDate;

			event.removeEventAttribute("time:timestamp");
			client.send(Integer.toString(i));//todo do i need this?
			client.send(event); //todo create an option to separate between different topics
			Thread.sleep(diffInMillis);//todo ensure the KeepAlive specification go hand in hand with the sleeping time
//			client.connect(); //todo not totally sure if this is needed
			i++; //todo do i need this?

		}
		client.disconnect();
		System.out.println("Done with the call to publish the log");
//		System.exit(2);

	}

    private static float wishedTime(int wishedInterval, Date startDate, Date lastDate) throws ParseException {
        System.out.println("The wished time interval for publishing the whole log data is "+ wishedInterval + " minutes. ");
        long timeInMilliSeconds = lastDate.getTime() - startDate.getTime();
        long timeSeconds = timeInMilliSeconds / 1000;
        long timeMinutes = timeSeconds / 60;
        long timeHours = timeMinutes / 60;
        long timeDays = timeHours / 24;
        String time;

        if (timeDays > 50) {
            long month = (long) (timeDays/30.436875);
            timeDays = (long)  (timeDays % 30.436875);
            time = "The original log is about: " + month + " months, and " + timeDays + " days.";
        }
        else{
            time ="The original log is: " + timeDays + " days, " + timeHours % 24 + " hours, " + timeMinutes % 60 + " minutes, and " + timeSeconds % 60 + " seconds";
        }

        float originalTimeInterval =  lastDate.getTime() - startDate.getTime();
        float wishedIntervalMilliseconds = wishedInterval * 60000;
        float dividingTime =  (originalTimeInterval/wishedIntervalMilliseconds);

		System.out.println(time);

		if (dividingTime < 1) {
			String message = "The wished interval is smaller than the original time interval. The original time will be preserved.";
			System.out.println(message);
			return 1;
		}

        String message = "To receive the wished time interval the original time between the event will be divided by: " + dividingTime;
        System.out.println(message);
	    return dividingTime;
    }

    private static int time_interval(Date startDate, Date secondDate, float dividingTime) {

        long realTimeDifference = secondDate.getTime() - startDate.getTime();

		float v = realTimeDifference / dividingTime;
		if (v < 0) {
			System.out.printf("found minus time on start: %d , end: %d, with diff: %d", startDate, secondDate, v);
			System.exit(-100);

		}
		int abs = Math.round(Math.abs(realTimeDifference) / dividingTime);
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

	private static List<XTrace> log2events(XLog log) {
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
