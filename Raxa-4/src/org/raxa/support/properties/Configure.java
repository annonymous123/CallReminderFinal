package org.raxa.support.properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Configure
{
    public static void main( String[] args )
    {
    	Properties prop = new Properties();
 
    	try {
    		prop.setProperty("Manager_Username", "manager");
    		prop.setProperty("Manager_Password", "squirrel");
    		prop.setProperty("Asterisk_URL", "127.0.0.1");
    		prop.setProperty("Thread_Poll_Database", "2");
    		prop.setProperty("Thread_Pool_Messager", "50");
    		prop.setProperty("Thread_Pool_Caller", "50");
    		prop.setProperty("Voice_Directory", "/home/atul/Documents/Asterisk/Sounds/PatientOutGoing");
    		prop.setProperty("Database_Poll_Interval", "120");   //in seconds
    		prop.setProperty("Max_Retry", "3"); 
    		prop.setProperty("MedRemind_Context", "outgoing-call");
    		prop.setProperty("MedRemind_CallerId", "Take Medicine");   //in seconds
    		prop.setProperty("MedRemind_TimeOut", "30000"); 
    		//save properties to project root folder
    		prop.store(new FileOutputStream("resource/config.properties"), null);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    }
}
