/*
 * Assumption:The patient with a valid uuid contains name of patient and phone number.
 * Don't use AlertType as 0;
 */
package org.raxa.module.registration;
import org.raxa.module.database.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.raxa.module.scheduler.RestCall;
import org.raxa.module.scheduler.TimeSetter;
import org.raxa.module.variables.VariableSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Register implements VariableSetter {
	protected Logger logger = Logger.getLogger(this.getClass());
	private Properties prop = new Properties();
	private String contactUUID;
	private String patientQuery;
	private String patientFullQuery;
	public Register(){
		try {
            prop.load(TimeSetter.class.getClassLoader().getResourceAsStream("restCall.properties"));
            String urlbase=prop.getProperty("restBaseUrl","");
            String username=prop.getProperty("username","");
            String password=prop.getProperty("password","");
            contactUUID=prop.getProperty("contactuuid","");
            patientQuery=prop.getProperty("patientQuery","");
            patientFullQuery=prop.getProperty("patientFullQuery","");
			RestCall.setURLBase(urlbase);
	        RestCall.setUsername(username);
	        RestCall.setPassword(password);
		}
		catch(IOException ex){
			logger.error("IMPORTANT:UNABLE TO CONNECT TO THE REST CALL");
		}
	}

	/*
	 * Check if patient is register for the alert.
	 * If no,register pateint for the Alert
	 * Now check if the patient information is available(patient table)
	 * if no,addPatient information;
	 * 
	 * Patient will be able to listen the alert from next day onwards (after midnight when the reset reminder will update the alert Table)
	 */
	public boolean addReminder(String pid,String preferLanguage,int alertType){
		
		if(checkIfPatientExist(pid,alertType)){
			logger.info("Patient Already Exist");
			return false;
		}
		else{
			if(!addPateintToAlert(pid,alertType))
				return false;
		}
		
		if((getPatient(pid)!=null) || addPatient(pid,preferLanguage)){
			return true;
		}
		
		return false;
	}
	
	public boolean checkIfPatientExist(String pid,int alertType){
		List list=getpatientAlert(pid,alertType);
		if(list!=null && list.size()>0)
			return true;
		else return false;
	}
	
	public boolean addPateintToAlert(String pid,int alertType){
		try{
			PAlert patientAlert=new PAlert(pid,alertType);
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			int id=(Integer)session.save(patientAlert);
			patientAlert.setPatientAlertId(id);
			session.getTransaction().commit();
			session.close();
			return true;
		}
		catch(Exception ex){
			logger.error("Unable to add patient in the alert");
			
		}
		return false;
	}
	
	public boolean addPatient(String pid,String preferLanguage){   //set Name Also
		List<String> info=getPatientNameAndNumberFromRest(pid);  //info.get(1) will be name info.get(2) will be   //UNMARK THIS
		Patient patient=null;
		if(!(info.equals(null)&& info.size()>1)){
			try{
				if(info.size()==2)							//if we don't have secondary number
					patient=new Patient(pid,info.get(0),info.get(1),preferLanguage);
				else patient=new Patient(pid,info.get(0),info.get(1),info.get(2),preferLanguage);  //if we have secondary number information of the patient
				Session session = HibernateUtil.getSessionFactory().openSession();
				session.beginTransaction();
				session.save(patient);
				session.getTransaction().commit();
				session.close();
				return true;
			}
			catch(Exception ex){
				logger.error("Unable to add Patient with id "+ pid);
				return false;
			}
		}
		logger.error("Unable to add Patient with id "+ pid);
		return false;
	}
	
	
			
	public boolean deleteReminder(String pid,int alertType){
		boolean deletePatient=false;PAlert patientAlert=null;
		Patient patient=getPatient(pid);
		List list=getpatientAlert(pid,alertType);
		if(list!=null && list.size()>0)
			patientAlert=(PAlert) list.get(0);
		else{
			logger.info("Patient with pid:"+pid+"\t is not register for this alert");
			return false;
		}
		
		List allList=getpatientAllAlert(pid);
		
		if(allList.size()==1 && patient!=null)
			deletePatient=true;
		try{
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			if(patientAlert!=null){
				session.delete(patientAlert);
				if(deletePatient)
					session.delete(patient);
			}
			session.getTransaction().commit();
			session.close();
			return true;
		}
		catch(Exception ex){
			logger.error("unable to delete patient");
			return false;
		}
	}
	

	/*
	 * have to work while making incoming call
	 * May return many ids
	 * Have to ask patient whom they want to register;
	 */
	public List<String> getpatientId(String pnumber){
		
		return null;
	}
	
	public List<String> getPatientNameAndNumberFromRest(String pid){
		try{
			List<String> a=new ArrayList<String>();
			String query=patientQuery+pid+patientFullQuery;
			ObjectMapper m=new ObjectMapper();
			JsonNode rootNode = m.readTree(RestCall.getRequestGet(query));
			JsonNode patient = rootNode.get("result");
				
			try{
				a.add(patient.path("person").path("display").textValue());
			}
			catch(Exception ex){
				logger.warn("name not found for patient with uuid "+pid);
				a.add(null);
			}
			
			JsonNode attribute=patient.path("person").get("attributes");
			for(int i=0;i<attribute.size();i++)
				if((attribute.get(i).path("attributeType").path("uuid").textValue()).equals(contactUUID))
					a.add(attribute.get(i).path("value").textValue());
			return a;
		}
		
		catch(Exception ex){
			logger.error("Some error while making rest call on patient with id "+pid);
			return null;
		}
		
	}
	
	public Patient getPatient(String pid){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Patient patient = (Patient) session.get(Patient.class,pid);
		session.getTransaction().commit();
		session.close();
		return patient;
	}
	
	public List getpatientAlert(String pid,int alertType){
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql="from PAlert where pid=:pid and alertType=:alertType";
		session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setString("pid", pid);
		query.setInteger("alertType", alertType);
		List list=query.list();
		session.getTransaction().commit();
		session.close();
		return list;
	}
	
	public List getpatientAllAlert(String pid){
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql="from PAlert where pid=:pid";
		session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setString("pid", pid);
		List list=query.list();
		session.getTransaction().commit();
		session.close();
		return list;
	}
}