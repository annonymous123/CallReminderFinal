/*
 * Call SMSModule and Provide Info which will thn send message to patient
 */
package org.raxa.module.scheduler;
import org.raxa.module.MedicalInformation.AlertInfo;
import org.raxa.module.MedicalInformation.MedicineInformer;
import java.util.List;
import org.apache.log4j.Logger;

public class Messager implements Runnable {

	private AlertInfo patient;
	
	public Messager(AlertInfo patient){
		this.patient=patient;
	}
	
	public void run(){
	
	}
}


