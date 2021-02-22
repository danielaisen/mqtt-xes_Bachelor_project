package mqttxes;

import java.io.File;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

public class Publisher {

	private static XFactory factory = new XFactoryNaiveImpl();

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.out.println("Use java -jar mqtt-xes.jar LOG.XES.GZ MILLISECONDS");
			System.exit(1);
		}


		System.out.print("Parsing log... ");
		XLog log = new XesXmlGZIPParser(factory).parse(new File(args[0])).get(0);

		String logName = XConceptExtension.instance().extractName(log);

		logName = UpdatedPublisher.containsWildCards(logName);

		System.out.println("Done");

		List<XTrace> traces = log2events(log);



		XTrace firstTrace = traces.get(0);
		Date startDate = event_handler(logName, firstTrace).getTime();
		XTrace lastTrace = traces.get(traces.size()-1);

        Date lastDate = event_handler(logName, lastTrace).getTime();;
//        int diffInMillis2 = time_interval(startDate, lastDate, dividingTime);
//        System.out.println(diffInMillis2 + "/n");
        int wishedInterval = Integer.parseInt(args[1]);

        float dividingTime = wishedTime(wishedInterval, startDate, lastDate);

		System.out.print("Streaming... ");
		UpdatedPublisher client = new UpdatedPublisher("daniel1");

		System.out.println("start");
		client.connect();
		int i = 0 ;
		for (XTrace trace : traces) { //todo Figure out why it doesnt send the first element
			System.out.println(i);
			XesMqttEvent event = event_handler(logName, trace);

			Date secondDate = event.getTime();
//			System.out.println(secondDate);
			int diffInMillis = time_interval(startDate, secondDate, dividingTime);
			startDate = secondDate;
			System.out.println(diffInMillis);

			event.removeEventAttribute("time:timestamp");
			client.send(Integer.toString(i));
			client.send(event); //todo create an option to separate between different topics
//			Thread.sleep(millis);
			Thread.sleep(diffInMillis);//todo ensure the KeepAlive specification go hand in hand with the sleeping time
//			client.connect(); //todo not totally sure if this is needed
			i++;

		}
		client.disconnect();
		System.out.println("done");

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
            time = "The original log is about: " + month + " months, and " + timeDays + " days."; //, " + hours % 24 + ": hours, " + minutes % 60 + ": minutes, and " + seconds % 60 + ": seconds ";
        }
        else{
            time = timeDays + " days, " + timeHours % 24 + " hours, " + timeMinutes % 60 + " minutes, and " + timeSeconds % 60 + " seconds";
        }

        float originalTimeInterval =  lastDate.getTime() - startDate.getTime();
        float wishedIntervalMilliseconds = wishedInterval * 60000;
        float dividingTime =  (originalTimeInterval/wishedIntervalMilliseconds);

        String message = "To receive the wished time interval the original time between the event will be divided by: " + dividingTime;
        System.out.println(time);
        System.out.println(message);
//        if (originalTimeInterval / dividingTime != wishedIntervalMilliseconds) {
//            System.out.print(originalTimeInterval / dividingTime+ " ");
//            System.out.println(wishedIntervalMilliseconds);
//            System.exit(100);
//        }
	    return dividingTime;
    }

    private static int time_interval(Date startDate, Date secondDate, float dividingTime) {

        long realTimeDifference = secondDate.getTime() - startDate.getTime();
        int abs = Math.round(Math.abs(realTimeDifference)/dividingTime);

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
