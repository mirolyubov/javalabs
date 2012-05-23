package ru.spbstu.telematics.chat;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String text;
	public String userName;
	public Date timeSent;
	public String status;
	public Message(String text, String userName, String status) {
		this.text = text;
		this.userName = userName;
		this.timeSent = new Date();
		this.status = status;
	}
	@Override
	public String toString() {
		return this.status+" "+this.userName+" says: "+this.text+" "+this.timeSent.toString();
	}
}

