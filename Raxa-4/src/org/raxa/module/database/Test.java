package org.raxa.module.database;
import org.hibernate.Query;
import org.hibernate.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import org.raxa.module.MedicalInformation.*;


public class Test {

	public static void main(String[] args) {
		   
		  System.out.println("I am in");
		  
		  Session session = HibernateUtil.getSessionFactory().openSession();
		 
/*
		   	String hql="from Patient,";
		   	Query query = session.createQuery(hql);
		   	List<Patient> results=query.list();
		   
		   	System.out.println((results.get(0)).getPatientName());
	*/
		/*
		   int count=0;
		   String hql="select a1.pid from Patient as p1,Alert as a1 where p1.pid=a1.pid";
		   	Query query = session.createQuery(hql);
		   	List<String> results=query.list();
		  
		   	System.out.println(results.get(2));
		  */
		   
		   /*
		   String p="126";
		   String hql="select p1.pnumber,i1.content,a1.scheduleTime from Patient p1,Alert a1,IvrMsg i1 where p1.pid=:id and p1.pid=a1.pid and a1.alertType=1 and a1.msgId=i1.ivrId";
		   	Query query = session.createQuery(hql);
		   	query.setString("id", p);
		   	Iterator results=query.list().iterator();
		   	
		   	List<String> content=new ArrayList<String>();
		   	Object[] row = (Object[]) results.next();
		   	String pnumber = (String) row[0];
		   	Time time=(Time) row[2];
		   	String temp=(String) row[1];
		   	content.add(temp);
		   	while ( results.hasNext() ) {
		   		row = (Object[]) results.next();
		   		content.add((String)row[1]);
		   	    
		   	}
		   	
		   	System.out.println(pnumber+" "+time);int count=0;
		   	while(count<content.size()){
		   
		   		System.out.println(content.get(count++));
		   	}

		    */
		   
		/*   java.util.Date date = new Date();
			Time lowertime=new Time(date.getTime());
			lowertime.setHours(lowertime.getHours()-16);
			 System.out.println(lowertime);
			 Time uppertime=new Time(lowertime.getTime());
			 uppertime.setMinutes(uppertime.getMinutes()+58);
			 System.out.println(uppertime);
			 System.out.println(lowertime);
			 String pid="125";
			 boolean isExecuted=true;
			 java.sql.Timestamp a=new java.sql.Timestamp(date.getTime());
			 int msgId=6;
			 int alertType=1;
			 Record record=new Record(pid,alertType,msgId,a,isExecuted,"UnKnown");
			 String hqlAlert="from Alert where msgId=:msgId and alertType=:alertType";
			 session.beginTransaction();
				 
				Query query=session.createQuery(hqlAlert);
				query.setInteger("msgId", msgId);
				query.setInteger("alertType", alertType);
				
				
				 Alert alert = (Alert)query.list().get(0);
				 
				 alert.setIsExecuted(isExecuted);
				 alert.setLastTried(a);
				 session.update(alert);
				 
			       int id = (Integer) session.save(record);
			       record.setRecordId(id);
			     
			    session.getTransaction().commit();
			    session.close();
		*/
		 /*
		  List<MedicineInformer> listOfPatients=new ArrayList<MedicineInformer>();
		  session.beginTransaction();
		  String hql="select p1.pnumber,i1.content,i1.ivrId,p1.pid,a1.aid from Patient p1,Alert a1,IvrMsg i1 where p1.pid=a1.pid and a1.alertType=:alertType and a1.msgId=i1.ivrId and a1.scheduleTime<=:systemTime and a1.isExecuted=:isExecuted and a1.retryCount<:retryCount order by i1.id,i1.itemNumber";
		  Time lowertime=new Time(new Date().getTime());
		  Query query=session.createQuery(hql);
		  query.setInteger("alertType", 1);
		  query.setBoolean("isExecuted",false);
		  query.setTime("systemTime",lowertime);
		  query.setInteger("retryCount",3);
		  
		  System.out.println(query.list().size());
		  Iterator results=query.list().iterator();
		  if(!(results.hasNext()))
				return ;
		  
		  Object[] row=(Object[]) results.next();
		  int flag=0;
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
		 
		 int count=0;
			System.out.println(listOfPatients.size());
			while(count<listOfPatients.size()){
				MedicineInformer a=listOfPatients.get(count++);
				int count2=0;
				System.out.println(a.getPhoneNumber()+" "+" " +a.getMsgId()+" "+a.getPatientId()+" "+a.getAlertId());
				List<String> b=a.getMedicineInformation();
					while(count2<b.size()){
						System.out.println(b.get(count2));
						count2++;
					}
				System.out.println("**************************************");
			}
			*/
		  /*
		  session.beginTransaction();
		  String queryString = "update Alert a set a.retryCount=3,a.isExecuted=:a where aid=:aid";
		  Query query = session.createQuery(queryString);
		  query.setBoolean("a", true);
		  query.setInteger("aid", 17);
		  query.executeUpdate();
		  session.getTransaction().commit();
		  session.close();
		  */
		  /*
		  	Timestamp time=new Timestamp(new Date().getTime());
			Session session = HibernateUtil.getSessionFactory().openSession();
		  	session.beginTransaction();
			Alert alert = (Alert) session.get(Alert.class,16);
			Record record=new Record(alert.getPatientId(),1,3,time,true,"UnKnown");
			int id = (Integer) session.save(record);    
		    record.setRecordId(id);
		    session.getTransaction().commit();
			session.close();
		*/
		/*
			session.beginTransaction();
			Alert alert = (Alert) session.get(Alert.class,20 );
			int retryCount=alert.getretryCount()+1;
			alert.setretryCount(retryCount);
			session.update(alert);
			session.getTransaction().commit();
			session.close();
		*/
		  
		 /* 
			String s="2013-07-17T03:58:00.000Z";
			Date date=null;
			try{
				TimeZone tz = TimeZone.getTimeZone("Asia/Calcutta");
				Calendar cal = Calendar.getInstance(tz);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				sdf.setCalendar(cal);
				cal.setTime(sdf.parse("2013-07-17T03:58:00.000Z"));
				date = cal.getTime();
				System.out.println(date);
			}
			catch(Exception ex){
				System.out.println("Dsdf");
			}
			
		  session.beginTransaction();
		  String hql=("from Alert where lastTry<:time");
		  Query query = session.createQuery(hql);
		  query.setTimestamp("time", date);
		  List<Object> a=query.list();
		  if(a.equals(null)){
			  System.out.println("null");
		  }
		  for(int i=0;i<a.size();i++){
			  Alert r=(Alert) a.get(i);
			  System.out.println(r.getAlertId()+" "+r.getMessageId()+"\t"+r.getLastTried());
		  }
	*/
		/*  session.beginTransaction();
		 String hql="select max(a.id) from IvrMsg a";
		 Query query = session.createQuery(hql);
		 List list = session.createQuery(hql).list(); 
		 int maxID = ( (Integer)list.get(0) ).intValue();
		 System.out.println(maxID);
		  session.getTransaction().commit();
		  session.close();
		 */	String pid="1";
		/* 	int alertType=1;
			
			String hql="from PAlert where pid=:pid and alertType=:alertType";
			session.beginTransaction();
			Query query = session.createQuery(hql);
			query.setString("pid", pid);
			query.setInteger("alertType", alertType);
			List list=query.list();
			if(list.size()>0)
					System.out.print(2);
			else System.out.print(23);
		*/
		 session.beginTransaction();
		 Query query=session.createQuery("select p1.pnumber,p1.preferLanguage,i1.ivrId,a1.aid from Patient p1,Alert a1,IvrMsg i1 where p1.pid=a1.pid and a1.alertType=:alertType and a1.msgId=i1.ivrId and a1.scheduleTime<=:systemTime and a1.isExecuted=:isExecuted and a1.retryCount<:retryCount");
		 query.setTimestamp("systemTime", new Date());
		 query.setBoolean("isExecuted",false);
		 query.setInteger("alertType",1);
		 query.setInteger("retryCount",4);
		 List results=query.list();
		 System.out.println(results.toString());
		 session.getTransaction().commit();
}
}



/*
 * 
 *public List<MedicineInformer> getPatientList(Time lowertime,int alertType){
Session session = HibernateUtil.getSessionFactory().openSession();

//	Time uppertime=new Time(lowertime.getTime());
	
	//uppertime.setMinutes(uppertime.getMinutes()+IVR_PINGING_INTERVAL+ACOOUNT_DELAY);   //3 minutes more to keep account of processing
	
	
	//session.beginTransaction();
	
//	if(alertType==IVR_TYPE)
//		  hql=IVR_MEDICINE_QUERY_DATE;
//	if(alertType==SMS_TYPE)
	//	  hql=SMS_MEDICINE_QUERY_DATE;
		  
   // Query query=session.createQuery(hql);
    //query.setTime("lowerTime",lowertime);
    //query.setTime("upperTime", uppertime);

*/