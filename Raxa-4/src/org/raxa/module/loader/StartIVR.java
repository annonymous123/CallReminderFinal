package org.raxa.module.loader;
import org.asteriskjava.fastagi.DefaultAgiServer;
import org.raxa.module.scheduler.TimeSetter;
public class StartIVR {

	/**
	 * This will start time setter and AGi SERVER.Asterisk must be running before running this method
	 */
	public void start() {
		TimeSetter timesetter=new TimeSetter();
		timesetter.startMedicineRemiderService();
		DefaultAgiServer incoming=new DefaultAgiServer();
		incoming.run();
	}

}
