package org.raxa.module.Message;

import java.util.Date;
import java.util.List;

public class Reminder {

	private String aid;
	private String rawmessage;
	private Date time;
	private List<String> templatizeMessage;
	
	public Reminder(String aid,String rawmessage,Date time){
		this.aid=aid;
		this.time=time;
		this.rawmessage=rawmessage;
	}
 
	public String getAlertId(){
		return aid;
	}
	
	public String getrawmessage(){
		return rawmessage;
	}
	
	public Date getTime(){
		return time;
	}
	
	public void setTemplatizeMessage(List<String> message){
		templatizeMessage=message;
	}
	
	public List<String> getTemplatizeMessage(){
		return templatizeMessage;
	}
	
	
	
}
