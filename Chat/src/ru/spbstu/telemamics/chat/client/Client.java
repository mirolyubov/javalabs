package ru.spbstu.telemamics.chat.client;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		final Socket s = new Socket();
		s.connect(new InetSocketAddress("localhost", 10002));
		ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		(new Runnable() {
			
			@Override
			public void run() {
				try {
					recieveMessages(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).run();
		for (int i = 0; i < 10000; i++) {
			Message m = new Message("Hey yo!"+i, "kite");
			oos.writeObject(m);
			//Message resp = (Message) ois.readObject();
			//System.out.println(resp);
		}
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
}
