package ru.spbstu.telemamics.chat.client;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	public String text;
	public String userName;
	public Date timeSent;
	public Message(String text, String userName) {
		this.text = text;
		this.userName = userName;
		this.timeSent = new Date();
	}
	@Override
	public String toString() {
		return this.userName+" says: "+this.text+" "+this.timeSent.toString();
	}
}
