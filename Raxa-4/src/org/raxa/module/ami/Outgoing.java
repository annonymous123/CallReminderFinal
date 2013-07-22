/*Call Patient Number and set Extension to magId of the patient;
 * 
 */
package org.raxa.module.ami;
import java.io.IOException;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.response.ManagerResponse;
import org.apache.log4j.Logger;
import org.raxa.module.scheduler.Caller;
import org.raxa.module.scheduler.TimeSetter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Outgoing{
	
    private ManagerConnection managerConnection;
    private String CONTEXT;
    private String CALLERID;
    private Long TIMEOUT;
    private String ASTERISK_SERVER_URL;
    private String MANAGER_USERNAME;
    private String MANAGER_PASSWORD;
    private String EXTENSION;
    Logger logger = Logger.getLogger(Outgoing.class);
    
    public Outgoing(){
    	   setProperties();
    	   
    	   ManagerConnectionFactory factory = new ManagerConnectionFactory(
    			   ASTERISK_SERVER_URL, MANAGER_USERNAME, MANAGER_PASSWORD);

           this.managerConnection = factory.createManagerConnection();
           
    }
    
    public void setProperties(){
 	    ASTERISK_SERVER_URL=null;
 	    MANAGER_USERNAME=null;
 	    MANAGER_PASSWORD=null;
 	    CONTEXT=null;
 	    CALLERID=null;
 	    TIMEOUT=null;
 	    EXTENSION=null;
 	   try {
 		   	Properties prop = new Properties();
    		prop.load(Outgoing.class.getClassLoader().getResourceAsStream("config.properties"));
    		ASTERISK_SERVER_URL=prop.getProperty("Asterisk_URL");
    		MANAGER_USERNAME=prop.getProperty("Manager_Username");
    		MANAGER_PASSWORD=prop.getProperty("Manager_Password");
    		CONTEXT=prop.getProperty("MedRemind_Context");
     	    CALLERID=prop.getProperty("MedRemind_CallerId");
     	    TIMEOUT=Long.parseLong(prop.getProperty("MedRemind_TimeOut"),10);
     	    EXTENSION=prop.getProperty("MedRemind_Extension");
    	   } 
    	catch (IOException ex) {
    		ex.printStackTrace();
    		logger.error("Some error occur while retreiving information from config.properties.Unable to forward call to asterisk");
        }
    }
    
    
    public void setContext(String s){
    	CONTEXT=s;
    }
    
    public void setCallerId(String s){
    	CALLERID=s;
    }
    
    public void setTimeout(Long l){
    	TIMEOUT=l;
    }
    		
    
    public boolean callPatient(String pnumber,String msgId,String aid,String preferLanguage){	
    	
    	pnumber="SIP/1000abc"; //will change to phonenumber once we have outgoing call facility.
    	Map<String,String> var=new HashMap<String,String>();
    	var.put("msgId",msgId);
    	var.put("aid",aid);
    	var.put("preferLanguage", preferLanguage.toLowerCase());
    	var.put("ttsNotation", getTTSNotation(preferLanguage));
    	try{
	        OriginateAction originateAction=new OriginateAction();
	        ManagerResponse originateResponse=new ManagerResponse();
	        managerConnection.login();
	        originateAction = new OriginateAction();
	        originateAction.setCallerId(CALLERID);
	        originateAction.setChannel(pnumber);
	        originateAction.setContext(CONTEXT);
	        originateAction.setExten(EXTENSION);
	        originateAction.setPriority(new Integer(1));
	        originateAction.setTimeout(TIMEOUT);
	        originateAction.setAsync(true);
	        originateAction.setVariables(var);
	        originateResponse = managerConnection.sendAction(originateAction,10000);
	        logger.info(originateResponse.getResponse());
	        managerConnection.logoff();
	        return true;
        
    	}
    	catch(AuthenticationFailedException ex){
    		logger.error("In org.raxa.module.ami.Outgoing.java:Authentication Failure");
    		return false;
    	}
    	catch(TimeoutException ex){
    		logger.error("In org.raxa.module.ami.Outgoing.java:TimeOut Exception");
    		return false;
    	}
    	catch(Exception ex){
    		logger.error("In org.raxa.module.ami.Outgoing.java:Some Error Occured");
    		return false;
    	}
    }
    
    /*
     * here if TTS doesnot support the prefer Language,It will pass English as defaultLanguage.But then the header and footer should be played
     * in default language if the mode is AudioFolder.all language should be in lower case in prop file.
     * 
     */
    public String getTTSNotation(String preferLanguage){
    	String defaultLanguage=null;
    	Properties prop = new Properties();
		try{
		prop.load(Caller.class.getClassLoader().getResourceAsStream("tts.properties"));
		defaultLanguage=prop.getProperty("default");
		return(prop.getProperty(preferLanguage.toLowerCase(),defaultLanguage));
		}
		catch (IOException ex) {
    		ex.printStackTrace();
    		logger.error("Unable to set prefer language");
    		return defaultLanguage;
        }
    }
 }
