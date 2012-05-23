package ru.spbstu.telematics.chat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Account {
	public Socket socket = null;
	public String userName;
	private ObjectOutputStream oos;
	public Account(String userName, Socket socket) {
		this.socket = socket;
		this.userName = userName;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Message m) throws IOException {
		oos.writeObject(m);
	}
	
	public String getIpString() {
		return socket.getInetAddress().toString();
	}
}

