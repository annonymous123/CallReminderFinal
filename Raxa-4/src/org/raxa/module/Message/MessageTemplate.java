/*
 * This class does the following:
 * Input:raw message String or Template message String.
 * 1)Parse the Alert Message and checks if the the medicine,number and name Audio file are present.
 * If not,checks if use Of TTS is allowed by the Hospital Authority
 * 2)Then it templatize the message and returns a list of Strings that should be played by the AGI
 * 4)mode-1 will play using TTS and mode-2 will play using Audio Files provided the location of  audio file is in preferLanguage.properties and 
 * is stored as tagName. eg in hindi.properties we have header1=/home/atul/Documents/voice/greetings/hindi/header1.mp3
 * 5)if mode not found default is 1
 * 6)considering mode of dose and medicine name to be 1.
 * 
 * CAUTION:
 * 		1)Any type of symbols like ',' '.' is highly discouraged as it will make the tts to stop executig that line
 * 		2)Medicine name should be send as lowercase otherwise the TTS will pronounce each Letter of the word.
 *      3)For more clarity its advise to use number as word rather than symbol like 'six' instead of '6'.
 *
 */

package org.raxa.module.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.raxa.module.scheduler.CallSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.raxa.module.variables.*;

public class MessageTemplate implements VariableSetter {
		
		Properties prop = new Properties();
		private Logger logger = Logger.getLogger(this.getClass());
		private List<String> Tmessage;
		private String medicineDose;
		private String medicine;			//should be in lowerCase
		private String tabletOrVolume;  // either "tablet" or volume;
		private String name; 
		private boolean isTablet;
		public MessageTemplate(){
			
		}
		/*
		 * For now Assuming Template of Raw Message is "Take x of y" as the template of Raxa Alert is not Ready
		 * Even Assuming that y is always a doze as the template as of now does not provide that information.
		 * Tags:
		 * <header1>:Greetings(eg.Welcome to Raxa.Good Morning)
		 * <name>:Name of the Patient
		 * <header2>:Introductory Line (eg.Today You have to take)
		 * <dose>:doze of Medicine(Solid)
		 *<tabletOrVolume>
		 * <footer1>:Language Specific (eg:(in Hindi) lena hai)
		 * <ending>:Good-Bye Message (eg.We hope you will get well soon.GoodBye)
		 */
		
		public List<String> templatize(String message,String preferLanguage,String name,String pid){
			this.name=name;
			Properties prop = new Properties();
			parseMessage(message);
			String propertyFile=preferLanguage.toLowerCase()+"properties";
			Tmessage=new ArrayList<String>();
			String header1,header2,footer1,ending;
			int header1mode,header2mode,footer1mode,endingmode,tabletOrVolumemode;
			try{
				logger.info("Trying to get mode in which the words should be spoken from "+propertyFile);
				prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyFile));
				header1mode=Integer.parseInt(prop.getProperty("header1mode",String.valueOf(TTS_MODE)));  //If headerMode not provided it will use TTS
				header2mode=Integer.parseInt(prop.getProperty("header2mode",String.valueOf(TTS_MODE)));
				footer1mode=Integer.parseInt(prop.getProperty("footer1mode",String.valueOf(TTS_MODE)));
				endingmode=Integer.parseInt(prop.getProperty("endingmode",String.valueOf(TTS_MODE)));
				tabletOrVolumemode=Integer.parseInt(prop.getProperty("tabletOrVolume",String.valueOf(TTS_MODE)));
			}
			catch(Exception ex3){
				logger.error("Unable to set mode,making all mode to 1 for patient ID  "+pid);
				header1mode=header2mode=footer1mode=endingmode=tabletOrVolumemode=TTS_MODE;
			}
			
			try{
				logger.info("Setting the content of the message"+propertyFile);
				prop.load(this.getClass().getClassLoader().getResourceAsStream("english.properties"));
				header1=prop.getProperty("header1Text",null);
				header2=prop.getProperty("header2Text",null);
				footer1=prop.getProperty("footer1Text",null);
				ending=prop.getProperty("endingText",null);
				Tmessage.add(convertToJsonString(new ContentFormat("header1",header1,header1mode)));
				Tmessage.add(convertToJsonString(new ContentFormat("name",name,TTS_MODE)));
				Tmessage.add(convertToJsonString(new ContentFormat("header2",header2,header2mode)));
				Tmessage.add(convertToJsonString(new ContentFormat("dose",medicineDose,TTS_MODE)));
				if(isTablet)
					tabletOrVolume="tablet";
				else tabletOrVolume="volume";
					
				Tmessage.add(convertToJsonString(new ContentFormat(tabletOrVolume,tabletOrVolume+ "of",tabletOrVolumemode)));
				Tmessage.add(convertToJsonString(new ContentFormat("medicine",medicine.toLowerCase(),TTS_MODE)));
				Tmessage.add(convertToJsonString(new ContentFormat("footer1",footer1,footer1mode)));
				Tmessage.add(convertToJsonString(new ContentFormat("ending",ending,endingmode)));
			}
			catch(IOException ex) {
				logger.error("Unable to set the content of the message fro patient"+pid+" with message "+message);
				Tmessage=null;
				return null;
			}
		
			return Tmessage;
			
		}
		
		public void parseMessage(String message){
			String separator=" ";
			String[] content=message.split(separator);
			medicineDose=content[1];
			String lastWordBeforeMedicineName=" of "; // of y
			int indexOfMedicine=message.lastIndexOf(lastWordBeforeMedicineName)+lastWordBeforeMedicineName.length();   //Since medicine can be of two or more words
			medicine=message.substring(indexOfMedicine, message.length());
			isTablet=checkIfTabletOrVolume(medicine);
		}
		
		/*
		 * return either "tablet" or "volume".Right now the template does not provide the info.
		 * Will change according to template of message;
		 */
		public boolean checkIfTabletOrVolume(String medicine){
			//return fales if volume
			return true;					
		}
		
		public String convertToJsonString(ContentFormat format){
			ObjectMapper m = new ObjectMapper();
			ObjectNode jString=m.createObjectNode();
			jString.put("field", format.getField());
			jString.put("content",format.getContent());
			jString.put("mode",format.getMode());
			return jString.toString();
		}
		
}		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		
		
		
		public List<String> templatize(String message,String preferLanguage,String name,String pid){
			this.name=name;
			Properties prop = new Properties();
			Tmessage=new ArrayList<String>();
			String propertyFile=preferLanguage+".properties";
			try{
				parseMessage(message);
				if(medicine.equals(null) || medicineDose.equals(null)) return null;    //If unsuccesful to parse or some error occured.
				
				else if(numberExist(medicineDose,preferLanguage) && medicineExist(medicine,preferLanguage) && addMessageToAlert(preferLanguage)){}
				
				else if(!defaultVoiceLanguage.equals(null) && numberExist(medicineDose,preferLanguage) && medicineExist(medicine,preferLanguage) && addMessageToAlert(defaultVoiceLanguage)){}
					
				else if(isTTSAllowed && checkifTTSSupport(preferLanguage) && addMessageToAlert(preferLanguage)){}
				
				else if(isTTSAllowed && checkifTTSSupport(defaultVoiceLanguage) && addMessageToAlert(defaultVoiceLanguage)){}
			
				else return null;
			}
			catch(Exception ex){
				logger.error("Unable to set alert for patient with id "+pid);
				return null;
			}
			return Tmessage;
		}
		
		public boolean addMessageToAlert(String language){
			Tmessage=null;
			String propertyFile=language+".properties";
			String greeting1,greeting2,footer1,ending;
			try{
				prop.load(this.getClass().getClassLoader().getResourceAsStream(propertyFile));
				greeting1=prop.getProperty("greeting1Text");
				greeting2=prop.getProperty("greeting2Text");
				footer1=prop.getProperty("footer1Text");
				ending=prop.getProperty("endingText");
			}
			catch(IOException ex) {
				Tmessage=null;
				return false;
			}
			Tmessage.add(convertToJsonString("header1",greeting1));
			Tmessage.add(convertToJsonString("name",name));
			Tmessage.add(convertToJsonString("header2",greeting2));
			Tmessage.add(convertToJsonString("dose",medicineDose));
			Tmessage.add(convertToJsonString(tabletOrVolume,medicineDose));
			Tmessage.add(convertToJsonString("medicine",medicine));
			Tmessage.add(convertToJsonString("footer1",footer1));
			Tmessage.add(convertToJsonString("ending",ending));
			return true;
			}
		
		public void parseMessage(String message){
			String[] content=message.split(" ");
			medicineDose=content[1];
			int indexOfMedicine=(content[0].length()+1+content[1].length()+1)-1;
			medicine=message.substring(indexOfMedicine, message.length());
			tabletOrVolume=checkIfTabletOrVolume(medicine);
		}
		
		/*
		 * return either "tablet" or "volume".Right now the template does not provide the info.
		 * Will change according to template of message;
		 */
/*		public String checkIfTabletOrVolume(String medicine){
			
			return "tablet";
		}
		
		public boolean numberExist(String medicineDose,String language){
			
			return false;
		}
		
		public boolean medicineExist(String medicineDose,String language){
			
			return false;
		}
		
		public boolean checkifTTSSupport(String language){
			return false;
		}
		
		public String convertToJsonString(String fieldName,String content){
			ObjectMapper m = new ObjectMapper();
			ObjectNode jString=m.createObjectNode();
			jString.put("field", fieldName);
			jString.put("content", content );
			return jString.toString();
		}
			
}
*/