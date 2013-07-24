package org.raxa.module.soundConverter;
import java.util.List;
import java.util.ArrayList;
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
/*		List<String> a=new ArrayList<String>();
		a.add("hello Atul Good Morning");
		a.add("Today you have to take");
		a.add("3 tablets of");
		a.add("Crocin");
		String s="/home/atul/Desktop/Patient";
		EnglishTTS b=new EnglishTTS();
		if(b.convertToSpeech(a, s))
			System.out.println("Suuccess");
*/		String a="aadada";
		String c=a.replace(" ", "+");
		System.out.println(c);
		String[] b=a.split(" ");
		System.out.println(b[0]);
	}

}
