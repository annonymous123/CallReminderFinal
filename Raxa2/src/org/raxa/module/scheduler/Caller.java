/*
 * Call Manager AMI and place a cal
 */

package org.raxa.module.scheduler;
import org.raxa.module.MedicalInformation.MedicineInformer;
import org.raxa.module.soundConverter.EnglishTTS;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.raxa.module.ami.Outgoing;
import org.raxa.module.database.Alert;
import org.raxa.module.database.HibernateUtil;
import org.raxa.module.variables.VariableSetter;
import org.hibernate.Session;
import java.util.Properties;

public class Caller implements Runnable,VariableSetter,schedulerInterface {

	private MedicineInformer patient;
	private String patientDirectory;
	private int msgId;
	
	public Caller(MedicineInformer patient){
		this.patient=patient;
		msgId=patient.getMsgId();
		setParentDirectory();
	}
	
	public void setParentDirectory(){
		Properties prop = new Properties();
		try{
		prop.load(Caller.class.getClassLoader().getResourceAsStream("config.properties"));
		patientDirectory=prop.getProperty("Voice_Directory");
		}
		catch (IOException ex) {
    		ex.printStackTrace();
    		patientDirectory="/home/Desktop/PatientVoice";
        }
	}
	
	public void run(){
		Logger logger = Logger.getLogger(Caller.class);
		String downloadLocation=patientDirectory+"/"+String.valueOf(msgId);
		if(checkIfTheVoiceFolderExist(downloadLocation) || downloadVoice(downloadLocation)){
			if(callManager(String.valueOf(msgId)));
				updateAlertCount();
		}
		else logger.error("In org.raxa.module.scheduler.Caller.java:Unable To download voice for Patient with msg ID"+String.valueOf(patient.getMsgId()));
	}
	/*
	 * This will check if the sound folder for that patient exist.
	 * For many Languages it will check database.
	 */
	public boolean checkIfTheVoiceFolderExist(String downloadLocation){
		File f=new File(downloadLocation);
		if(f.exists() && f.isDirectory())
			return true;
		else return false;
	}
	/*
	 * This will download English voice for the medicine and store it in downloadLocation.
	 */
	public boolean downloadVoice(String downloadLocation){
		EnglishTTS obj=new EnglishTTS();
		if(obj.convertToSpeech(patient.getMedicineInformation(),downloadLocation))
			return true;
		else return false;
	}
	
	public boolean callManager(String msgId){
		return (new Outgoing()).callPatient(patient.getPhoneNumber(),msgId,patient.getMedicineInformation().size(),String.valueOf(patient.getAlertId()));
	}
	
	/*
	 * This will increment the retry_Count after each call;
	 */
	
	public void updateAlertCount(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Alert alert = (Alert) session.get(Alert.class,patient.getAlertId());
		int retryCount=alert.getretryCount()+1;
		alert.setretryCount(retryCount);
		session.update(alert);
		session.getTransaction().commit();
		session.close();
	}
	
}
