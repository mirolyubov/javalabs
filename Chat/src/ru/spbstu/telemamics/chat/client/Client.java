package ru.spbstu.telemamics.chat.client;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	final static Socket s = new Socket();
	static ObjectOutputStream oos;
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		s.connect(new InetSocketAddress("localhost", 10002));
		oos = new ObjectOutputStream(s.getOutputStream());
		new Thread((new Runnable() {
			
			@Override
			public void run() {
				try {
					recieveMessages(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		})).start();
		logIn("mike");
		for (int i = 0; i < 5; i++) {
			Message m = new Message("Hey yo!"+i, "mike", "MESSAGE");
			oos.writeObject(m);
			//Message resp = (Message) ois.readObject();
			//System.out.println(resp);
		}
		logOut("mike");
	}
	
	private static void recieveMessages(Socket s) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
		while(true) {
			try {
				Message resp = (Message) ois.readObject();
				System.out.println(resp);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void logIn(String userName) throws IOException{
		Message m = new Message("", userName, "LOGIN");
		oos.writeObject(m);
	}
	
	private static void logOut(String userName) throws IOException{
		Message m = new Message("", userName, "LOGOUT");
		oos.writeObject(m);
	}
}
