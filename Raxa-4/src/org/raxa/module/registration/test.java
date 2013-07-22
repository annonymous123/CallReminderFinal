package org.raxa.module.registration;

import java.util.List;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	/*	MessageTemplate m=new MessageTemplate();
		List<String> a=m.templatize("Take 3 of acetamide B", "english", "Atul", "232334");
		for(String b:a){
			System.out.println(b);
		}
	*/
	
			Register r=new Register();
		System.out.println(r.addReminder("123456789", "hindi", 1));

	
	}
}