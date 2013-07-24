/*
 * Return a list of string of fileLocation of the translated voice in regional Language.
 * 
 */

package org.raxa.module.soundConverter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;



public class RegionalLanguageParser {
	private Logger logger = Logger.getLogger(this.getClass());
	private String MedicineNameLocation;
	private String NumberLocation;
	private String TabletLocation;						//pronounciation of tablet
	private String VolumeLocation;
	private String PatientNameLocation;
	private List<String> RegionalVoicefileLocation;
	
	public RegionalLanguageParser(){
		MedicineNameLocation=getFileLocation("Medicine_Name_Location");
		NumberLocation=getFileLocation("Number_Location");
		TabletLocation=getFileLocation("Volume_Location");
		PatientNameLocation=getFileLocation("Patient_Name_Location");
		
	}
	/*the first one will be greeting to the patient(Its neglected here,will come in use if we use some other tts in future)
	 * 
	 * the second is number of tablet/no of volumes  (can be null)
	 * the third is name Of medicine
	 * 
	 * the fourth is again  number of tablet/no of volumes
	 * the fifth is name of Medicine
	 *
	 * pattern continues
	 */
	public List<String> getVoiceFileLocation(List<String> msgContent,String pname){
		RegionalVoicefileLocation=new ArrayList<String>();
		String greeting1=getFileLocation("greeting1");
		String greeting2=getFileLocation("greeting2");
		String name=getPatientNameLocation(pname);
		RegionalVoicefileLocation.add(greeting1);
		RegionalVoicefileLocation.add(name);
		RegionalVoicefileLocation.add(greeting2);
	
		for(int i=0;i<msgContent.size()-1;i++){
			if(i%2==0){     // means it contains dose
				String amountOfdoseOrVolume=NumberLocation+String.valueOf(Integer.parseInt(msgContent.get(i)));
				String tabletOrVolume=checkIfTabletOrVolume(i);
				RegionalVoicefileLocation.add(amountOfdoseOrVolume);
				RegionalVoicefileLocation.add(tabletOrVolume);
			}
			else{
				String medicineName=MedicineNameLocation+msgContent.get(i);
				RegionalVoicefileLocation.add(medicineName);
			}
		}
		RegionalVoicefileLocation.add(getFileLocation("EndGreeting"));
	}
	
	
	public String checkIfTabletOrVolume(int i){
		//If tablet return TabletLocation
		//if volume return VolumeLocation
		return null;
	}
	
	
	public String getFileLocation(String key){
		Properties prop = new Properties();
		try{
			prop.load(this.getClass().getClassLoader().getResourceAsStream("FileLocation.properties"));
			return prop.getProperty(key);
			}
			catch (IOException ex) {
	    		ex.printStackTrace();
	    		logger.error("Unable to Set Regional Folder Directory");
	    		return null;
	        }
	}
	/*
	 * pname may be retreived from TTS.From now on its assumed that it exist
	 */
	public String getPatientNameLocation(String pname){
		File f = new File(PatientNameLocation+pname+".mp3");
		if(f.exists())
		return PatientNameLocation+pname;
		else {
			// download it and store it in that location
			return PatientNameLocation+pname;
		}
	}
	

}
