package ru.spbstu.telemamics.chat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		ServerSocket ss = new ServerSocket(7000);
	    Socket socket = ss.accept();
	    ObjectInputStream ois = new ObjectInputStream(
	        socket.getInputStream());
	    int count=0;
	    while(true) {
	      Person p = (Person) ois.readObject();
	      if (count++ % 1000 == 0) {
	        System.out.println(p);
	      }
	    }
	}

}
