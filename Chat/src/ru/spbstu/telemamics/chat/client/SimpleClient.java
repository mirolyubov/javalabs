package ru.spbstu.telemamics.chat.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SimpleClient {
	public static void main(String[] args) throws IOException {
	    long start = System.currentTimeMillis();
	    Socket s = new Socket("localhost", 10002);
	    ObjectOutputStream oos = new ObjectOutputStream(
	        s.getOutputStream());
	    Person p = new Person("Heinz", "Kabutz", 0);
	    for (int age=0; age < 1500 * 1000; age++) {
	      p.setAge(age);
	      oos.writeObject(p);
	    }
	    long end = System.currentTimeMillis();
	    System.out.println("That took " + (end-start) + "ms");
	  }
}
