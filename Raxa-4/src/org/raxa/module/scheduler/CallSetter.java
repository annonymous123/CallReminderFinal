/*
 * Its run method set all threads to run Caller object and provide patient information to the caller object
 * 
 * It will take into account both alertTypes i.e IVR AND SMS
 * 
 */

package org.raxa.module.scheduler;
import org.raxa.module.variables.VariableSetter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.raxa.module.database.*;
import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.module.MedicalInformation.AlertInfo;
import org.raxa.module.MedicalInformation.MedicineInformer;

import java.io.IOException;
import java.util.Properties;

public class CallSetter implements Runnable,VariableSetter{
	static Logger logger = Logger.getLogger(CallSetter.class);
	private Date today;
	
	public CallSetter(Date today){
		this.today=today;
	}
	
	public void run(){
		if(!isSameDay()){
			resetDatabase();
			today=new Date();
		}
		
		
		List<AlertInfo> listOfIVRCaller=(new MedicineInformer()).getPatientInfoOnTime(new Date(),IVR_TYPE);
	//	List<AlertInfo> listOfSMSCaller=(new MedicineInformer()).getPatientInfoOnTime(new Date(),SMS_TYPE);
		
		if(listOfIVRCaller!=null)
			setIVRThread(listOfIVRCaller);
		else logger.info("In CallSetter:run-No IVRTuple found for the next interval");
		
//		if(listOfSMSCaller!=null)
//			setSMSThread(listOfSMSCaller);
//		else logger.info("In CallSetter:run-No SMSTuple found for the next interval");
		
	}
	
	public boolean isSameDay(){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(today);
		cal2.setTime(new Date());
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)&&
		                  cal1.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH);
		return sameDay;
	}
	
	/*
	 * Update the database and set isExecuted to no and retry_count to false each day(at midnight).
	 */
	public void resetDatabase(){
		  ReminderUpdate r=new ReminderUpdate();
		  Session session = HibernateUtil.getSessionFactory().openSession();
		  session.beginTransaction();
		  String hql="select p.pname,p.preferLanguage,pa.pid,pa.alertType from Patient p,PAlert pa where p.pid=pa.pid";
		  Query query=session.createQuery(hql);
		  Iterator results=query.list().iterator();
		  session.getTransaction().commit();
		  session.close();
		  try{
				Object[] row=(Object[]) results.next();
				
				while(true){
				  String pname=(String) row[0];
				  String preferLanguage=(String) row[1];
				  String pid=(String) row[2];
				  int alertType=(Integer) row[3];
				  r.resetReminder(pid, pname, preferLanguage, alertType);
				  if(results.hasNext())
					  row=(Object[]) results.next();
				  else break;
				}
			}
			catch(Exception ex){
				logger.error("unable to get patientList on "+new Date());
				
			}
		  session.getTransaction().commit();
		  session.close();
	}
	
	public void setIVRThread(List<AlertInfo> list){
		Properties prop = new Properties();
		int THREAD_POOL_CALLER=50;
		try{
			prop.load(CallSetter.class.getClassLoader().getResourceAsStream("config.properties"));
			THREAD_POOL_CALLER=Integer.parseInt(prop.getProperty("Thread_Pool_Caller"));
		}
		catch (IOException ex) {
    		ex.printStackTrace();
		}
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_CALLER);
		int count=0;
	    while(count<list.size()){
	    	AlertInfo a;
			a=list.get(count);
			Caller caller=new Caller(a);
			try{
				executor.schedule(caller,0,TimeUnit.SECONDS);
			}
			catch(Exception ex){
				logger.error("In function setIVRThread:Error Occured");
			}
			finally{
				count++;
			}
		}
	}
		
	public void setSMSThread(List<AlertInfo> list){
		Properties prop = new Properties();
		int THREAD_POOL_MESSAGER=50;
		try{
			prop.load(CallSetter.class.getClassLoader().getResourceAsStream("config.properties"));
			THREAD_POOL_MESSAGER=Integer.parseInt(prop.getProperty("Thread_Pool_Messager"));
		}
		catch (IOException ex) {
    		ex.printStackTrace();
        }
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_MESSAGER);
		int count=0;
	    while(count<list.size()){
	    	AlertInfo a;
			a=list.get(count);
			Messager messager=new Messager(a);
			try{
				executor.schedule(messager,1,TimeUnit.SECONDS);
			}
			catch(Exception ex){
				logger.error("In function setSMSThread:Error Occured");
			}
			finally{
				count++;
			}
		}
	}

}
		
		
		
		
		
		
		
		
		
	
