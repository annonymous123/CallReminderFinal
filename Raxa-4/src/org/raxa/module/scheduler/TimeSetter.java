/*
 * This Class will ping database after every two mins(DATABASE_PINGING_INTERVAL) and schedule all thread to call 
 * patient where patient scheduleTime falls between that time Zone.Pinging time can be changed easily 
 * by changing DATABASE_PINGING_INTERVAL 
 * 
 */

package org.raxa.module.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.io.IOException;
import java.util.Properties;

public class TimeSetter{

	 public static void main(String args[]){
		Properties prop = new Properties();
		int THREAD_POOL_DATABASE=1;int DATABASE_POLLING_INTERVAL=2;
    	try {
            prop.load(TimeSetter.class.getClassLoader().getResourceAsStream("config.properties"));
    		THREAD_POOL_DATABASE=Integer.parseInt(prop.getProperty("Thread_Poll_Database"));
    		DATABASE_POLLING_INTERVAL=Integer.parseInt(prop.getProperty("Database_Poll_Interval"));
    	} 
    	catch (IOException ex) {
    		ex.printStackTrace();
        }
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_DATABASE);
		Runnable callSetter = new CallSetter(new Date());
		try{
			executor.scheduleWithFixedDelay(callSetter,0,DATABASE_POLLING_INTERVAL,TimeUnit.SECONDS);
		}
		catch(Exception e){
		   e.printStackTrace();
		 }
	} 
}
