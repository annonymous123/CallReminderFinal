/*
 * Outgoing Call Context here sets the following varial
 * totalItem;item0,item1....
 */

package org.raxa.module.agi;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.module.Message.ContentFormat;
import org.raxa.module.database.HibernateUtil;
import org.raxa.module.scheduler.Caller;
import org.raxa.module.variables.VariableSetter;

import com.fasterxml.jackson.databind.ObjectMapper;


public class Incoming extends BaseAgiScript implements VariableSetter
{
	private AgiRequest request;
	private AgiChannel channel;
	private Logger logger = Logger.getLogger(this.getClass());
	
    public void service(AgiRequest request, AgiChannel channel)
            throws AgiException
    {
    	answer();
    	this.request=request;
    	this.channel=channel;
    	if(request.getContext().equals("incoming-call"))
        	handleIncomingCall();
        if(request.getContext().equals("outgoing-call"))
        	provideMedicalInfo();
        
        return;
        
    }
    
    public void handleIncomingCall(){      //will be implemneted once succesfully updated the database.
    	//request.get
    }
   
    public void provideMedicalInfo() throws AgiException{
    	
    	while(true){
	    	int readItemSoFar=Integer.parseInt(channel.getVariable("count"));
	    	
	    	if(readItemSoFar==0){
	    		int msgId=Integer.parseInt(request.getParameter("msgId"));
	            
	            try{
	            	List content=getMessageContent(msgId);
	            	int totalItem=content.size();
	            	channel.setVariable("totalItem",String.valueOf(totalItem));
	            	for(int i=0;i<totalItem;i++){
	            		String item="item"+i;
	            		channel.setVariable(item,(String)content.get(i));
	            	}
	            }
	            catch(Exception ex){
	            	logger.error("IMPORTANT:ERROR OCCURED WHILE IN CALL.CHECK THE ISSUE");
	            	channel.hangup();
	            	return;
	            }
	    	}
	    	
	    	
	    	if(readItemSoFar>=Integer.parseInt(channel.getVariable("totalItem"))){
	    		channel.hangup();
	    		int par1=Integer.parseInt(request.getParameter("msgId"));
	    		String aid=request.getParameter("aid");
	    		String serviceInfo=channel.getName();//Doubt
	    		CallSuccess obj=new CallSuccess(par1,aid,true,serviceInfo);
	    		obj.updateAlert();
	    		return;
	    	}
	    	
	    	updateCount(readItemSoFar);
	    	
	    	String itemNumber="item"+readItemSoFar;
	    	String itemContent=channel.getVariable(itemNumber);
	    	ContentFormat message=parseString(itemContent);
	    	String preferLanguage=(request.getParameter("language")).toLowerCase();
	    	String ttsNotation=request.getParameter("ttsNotation");
	    	
	    	if(message==null || (message.getContent())==null){
	    		provideMedicalInfo();
	    		return;
	    	}
	    		
	    	
	    	if(message.getMode()==TTS_MODE){
	    		logger.info("Playing "+message.getContent()+" in TTS");
	    		channel.setVariable("message", message.getContent().toLowerCase());
	    		channel.setVariable("language",ttsNotation);
	    		return;
	    	}
	    	
	    	else if(message.getMode()==AUDIO_MODE){
		    		Properties prop = new Properties();
		    		try{
		    			logger.info("Searching for "+preferLanguage+".properties file");
			    		prop.load(Caller.class.getClassLoader().getResourceAsStream(preferLanguage+".properties"));
			    		String fileLocation=prop.getProperty(message.getField())+"/"+message.getField();    //if want to put un fromatted location then remove "/"+message.getField() 
			    		logger.info("Playing "+message.getField()+" in from audio Folder with file location "+fileLocation);
			    		channel.streamFile(fileLocation);
			    	}
		    		catch (IOException ex) {
		        		ex.printStackTrace();
		        		logger.error("Some error while playing AudioFile returning back");
		        		
		            }
		    	}
    	}
    }
    	
	public void updateCount(int count) throws AgiException{
		++count;
		channel.setVariable("count",String.valueOf(count));
	}
    
    
	public ContentFormat parseString(String itemContent){
		try{
			logger.info("Parsing the content of msgId(Json String)");
    		ObjectMapper mapper = new ObjectMapper();
    		return (mapper.readValue(itemContent, ContentFormat.class));
		}
		catch(Exception ex){
			return null;
		}
	}
    	
	public List getMessageContent(int msgId) throws Exception{
		logger.info("Getting content for medicine Reminder haveing msgId"+msgId);
		String hql="select content from IvrMsg where ivrId=:msgId order by itemNumber";
		Session session = HibernateUtil.getSessionFactory().openSession();
    	session.beginTransaction();
    	Query query=session.createQuery(hql);
    	query.setInteger("msgId", msgId);
    	List content=query.list();
    	session.getTransaction().commit();
    	session.close();
    	logger.info("Successfully retreived msg content from database with msgId"+msgId);
    	return content;
	}
    	
}
    
 