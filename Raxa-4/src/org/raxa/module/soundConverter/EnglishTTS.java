/*
 * Take a list of string to be converted to voice.It also takes the location where to download the song file
 * Each msgId will have its separate folder.Same folder location will be passed to the TTS.
 * If the folder not found this class will be called and it will create the folder and put the voice file.
 * eg. parameters List<String> patientmedicineContent,String folderLocation(including foldername)
 *      output 1.mp3,2.mp3,3.mp3
 */
package org.raxa.module.soundConverter;
import java.net.URL;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class EnglishTTS {
	private String URL;
	private String queryPrefix;
	private String queryPostfix;
	private String query;
	private String baseFolder;
	static Logger logger = Logger.getLogger(EnglishTTS.class);
	/*
	 * Now using http://tts-api.com.The code may have to change entirely if TTS is changed.
	 */
	public EnglishTTS(){
		URL="http://tts-api.com/tts.mp3";
		queryPrefix="?q=";
		queryPostfix="&return_url=1";
		baseFolder="n";
	}
	
	public boolean convertToSpeech(String stringToConvert,String folderLocation){
		try{
			logger.info("Downloading Voice File");
			
			String string=stringToConvert.replace(' ', '+');
			query=URL+queryPrefix+string+queryPostfix;
			URL url = new URL(getmp3DownloadURL(query));
			String fileLocation=baseFolder+"/"+folderLocation;
			FileUtils.copyURLToFile(url,new File(fileLocation));
			
		}
		catch(Exception ex){
			logger.error("Error Occured while downloading");
			return false;
		}
		return true;
	}
	
	public String getmp3DownloadURL(String string){
		try{
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(string);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			return rd.readLine();
		}
		catch(Exception ex){
				logger.error("Error:Cannot get the mp3 download Link");
				return null;
		}
	}
}
	


