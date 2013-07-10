/*
 * It will provide patient content of Message that will be played or texted to the patient.
 * 
 * provide information given one of the following info
 * 1.patientId,alertType
 * 2.patientnumber,alertType
 * 3.scheduleTime,AlertType
 */

package org.raxa.module.MedicalInformation;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.module.database.HibernateUtil;
import java.util.Iterator;
import java.sql.Time;
import java.util.List;
import java.util.ArrayList;
import org.raxa.module.scheduler.TimeSetter;
import org.raxa.module.variables.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
 


public class MedicineInformer implements VariableSetter,MedicineInformerConstant {
	static Logger logger = Logger.getLogger(MedicineInformer.class);
	private String pnumber;
	private List<String> medicineInfo;
	private String pid;
	private int msgId;
	private int aid;
		
	public MedicineInformer(){
		pnumber=null;
		medicineInfo=null;
	//	PropertyConfigurator.configure("log4j.properties");
	}
	
	public MedicineInformer(String pnumber,List<String> medicineInfo,String pid,int msgId,int aid){
		this.pnumber=pnumber;
		this.medicineInfo=medicineInfo;
		this.pid=pid;
		this.msgId=msgId;
		this.aid=aid;
	}
	
	public String getPhoneNumber(){
		
		return pnumber;
	}
	
	public int getAlertId(){
		return aid;
	}
	
	public List<String> getMedicineInformation(){
		
		return medicineInfo;
	}
	
	public String getPatientId(){
		return pid;
	}
	
	public int getMsgId(){
		return msgId;
	}
	/*
	 * This will return MedicineInformer object once user pass uuid and alertType.This will be used to call
	 * via any software which has access to patient id;
	 */
	
	public List<MedicineInformer> getMedicineInfoOnId(String uuid,int alertType){
		
		String hql=null;Session session = HibernateUtil.getSessionFactory().openSession();
		List<MedicineInformer> a=null;
		if(alertType==IVR_TYPE)
			  hql=IVR_MEDICINE_QUERY_UUID;
		if(alertType==SMS_TYPE)
			  hql=SMS_MEDICINE_QUERY_UUID;
		try{
			 session.beginTransaction();
			 Query query=session.createQuery(hql);
			 query.setString("pid",uuid);
			 Iterator results=query.list().iterator();
			 a=getPatientList(results);
			 }
			 catch(Exception ex){
				 logger.error("Error in getPatientListOnTime");
				 return null;
			 }
			 session.close();
			
			 return a;
	}
	
	/*
	 * This will return MedicineInformer object once user pass phone numebr and alertType.This will be used when
	 * any incoming call,SMS is received; 
	 */
	
	public List<MedicineInformer> getMedicineInfoOnNumebr(String pnumber,int alertType){
		String hql=null;Session session = HibernateUtil.getSessionFactory().openSession();
		List<MedicineInformer> a=null;
		if(alertType==IVR_TYPE)
			  hql=IVR_MEDICINE_QUERY_NUMBER;
		if(alertType==SMS_TYPE)
			  hql=SMS_MEDICINE_QUERY_NUMBER;
		try{
			 session.beginTransaction();
			 Query query=session.createQuery(hql);
			 query.setString("pnumber",pnumber);
			 Iterator results=query.list().iterator();
			 a=getPatientList(results);
			 }
			 catch(Exception ex){
				 logger.error("Error in getPatientListOnTime");
				 return null;
			 }
			 session.close();
			
			 return a;
		
	}
	
	/*
	 * 
	 */
	public List<MedicineInformer> getPatientInfoOnTime(Time time,int alertType){
		String hql=null;Session session = HibernateUtil.getSessionFactory().openSession();
		List<MedicineInformer> a=null;
		if(alertType==IVR_TYPE)
			  hql=IVR_MEDICINE_QUERY_DATE;
		if(alertType==SMS_TYPE)
			  hql=SMS_MEDICINE_QUERY_DATE;
		
		 try{
		 session.beginTransaction();
		 Query query=session.createQuery(hql);
		 query.setTime("systemTime",time);
		 query.setBoolean("isExecuted",false);
		 query.setInteger("alertType",IVR_TYPE);
		 query.setInteger("retryCount",getMaxRetry());
		 Iterator results=query.list().iterator();
		 a=getPatientList(results);
		 }
		 catch(Exception ex){
			 logger.error("Error in getPatientListOnTime");
			 ex.printStackTrace();
			 return null;
		 }
		 session.close();
		
		 return a;
	}
	
	public int getMaxRetry(){
		Properties prop = new Properties();int MAX_TRY=3;
		try{
		prop.load(MedicineInformer.class.getClassLoader().getResourceAsStream("config.properties"));
		MAX_TRY=Integer.parseInt(prop.getProperty("Max_Retry"));
		return MAX_TRY;
		}
		catch (IOException ex) {
    		ex.printStackTrace();
    		return MAX_TRY;
        }
	}
	
	/*
	 * This will return all patient who need to be called given a time and alertType
	 * return null if no tuple found or any error occured
	 */
	
	
	public List<MedicineInformer> getPatientList(Iterator results){
		
		int flag=0;
		List<MedicineInformer> listOfPatients=new ArrayList<MedicineInformer>();
		if(!(results.hasNext()))
			return null;
		
		try{
		Object[] row=(Object[]) results.next();
		
		while(true){
		  String pnumber=(String) row[0];
		  String pid=(String) row[3];
		  int id=(Integer) row[2];
		  int aid=(Integer) row[4];
		  List<String> content=new ArrayList<String>();
		
		  while(aid==(Integer)row[4]){
			  String temp=(String) row[1];
		 	  content.add(temp);
			  if(results.hasNext())
			  row=(Object[]) results.next();
			  else{ flag=1;	break;}						
		  }
		  
		  listOfPatients.add(new MedicineInformer(pnumber,content,pid,id,aid));
		  		if(flag==1) break;
		 }
		
		}
		
	    catch(Exception ex){
			 logger.error("IN MedicineInformer:getPatientListOnTime:Error Occured");
			 return null;
		}
		
		return listOfPatients;
	}
	
}
