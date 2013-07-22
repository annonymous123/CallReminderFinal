package org.raxa.module.scheduler;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import org.raxa.module.Message.Reminder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetJson {

	private TimeZone tz;
	private SimpleDateFormat sdf;
	private Calendar cal;
	private ObjectMapper m ;
	private String alertQuery;
	private Logger logger = Logger.getLogger(this.getClass());
	
	public GetJson(){
		Properties prop = new Properties();
		try {
            prop.load(TimeSetter.class.getClassLoader().getResourceAsStream("restCall.properties"));
            String urlbase=prop.getProperty("restBaseUrl","");
            String username=prop.getProperty("username","");
            String password=prop.getProperty("password","");
            alertQuery=prop.getProperty("raxaalert","");
			RestCall.setURLBase(urlbase);
	        RestCall.setUsername(username);
	        RestCall.setPassword(password);
		}
		catch(IOException ex){
			logger.error("IMPORTANT:UNABLE TO CONNECT TO THE REST CALL");
		}
        m=new ObjectMapper();
        tz = TimeZone.getTimeZone("Asia/Calcutta");
        cal = Calendar.getInstance(tz);
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setCalendar(cal);
	}
	
	public List<Reminder> getAlert(String pid){
		Date today=new Date();
		List<Reminder> reminder=new ArrayList<Reminder>();
		String query=alertQuery+pid;
		try{
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			JsonNode results = rootNode.get("results");
			for(JsonNode alert:results){
				String description=alert.path("description").textValue();	//description returns date in T,Z format.
				Date date=getDate(description);
				if(isSameDay(date,today)){
					String aid=alert.path("uuid").textValue();
					String message=alert.path("name").textValue();
					Date time=date;
					reminder.add(new Reminder(aid,message,time));
				}
			}
		}
		catch(IOException io){
			io.printStackTrace();
			logger.error("Unable to set alert for patient with id "+pid);
			return null;
		}
		return reminder;
	}
	
	public boolean isSameDay(Date d1,Date d2){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(d1);
		cal2.setTime(d2);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)&&
		                  cal1.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH);
		return sameDay;
	}
	 
	/*
	 * getDate convert date in TZ format to java Simple Date Format
	 */
	public Date getDate(String isoFormat){
		Date date=null;
		try{
			cal.setTime(sdf.parse(isoFormat));
			date = cal.getTime();
		}
		catch(Exception ex){
			logger.error("Unable to convert in Date Format");
		}
		return date;
	}
}
