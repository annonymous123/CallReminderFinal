/*
 * Caution:Change of any String variable name isn't advised unless a change in the message Template is made
 * as the mapping by jackson will won't work
 */

package org.raxa.module.Message;

public class ContentFormat {

	private String field;
	private String content;
	private int mode;
	
	public ContentFormat(String field,String content,int mode){
		this.field=field;
		this.content=content;
		this.mode=mode;
	}
	
	public ContentFormat(){}
	
	public String getField(){
		return field;
	}
	
	public String getContent(){
		return content;
	}
	
	public int getMode(){
		return mode;
	}
	

}
