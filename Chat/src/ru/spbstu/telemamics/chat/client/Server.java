package ru.spbstu.telemamics.chat.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	/**
	 * @param args
	 * @throws IOException 
	 */
	static ExecutorService pool = Executors.newCachedThreadPool();
	private static ServerSocket servSock;
	private static ArrayList<Socket> clients;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		servSock = new ServerSocket(10002);
		clients = new ArrayList<Socket>();
		
		while(true) {
			try {
				final Socket client = servSock.accept();
				clients.add(client);
				printClients();
				pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							serve(client);
						} catch (ClassNotFoundException | IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
				});
			}
			catch(Exception e) {}
			
		}
	}
	
	private static void serve(Socket client) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
		FileOutputStream fos = new FileOutputStream("serverOutput.txt");
		BufferedOutputStream buf = new BufferedOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(buf);
		while (true)
		{
			Message m = (Message)ois.readObject();
			dos.writeUTF(m.toString()+"\r\n");
			Message resp = new Message("I hear you, "+m.userName, "server");
			oos.writeObject(resp);
		}
	}
	
	private static void printClients() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("clientsList.txt");
			BufferedOutputStream buf = new BufferedOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(buf);
			for (int i = 0; i < clients.size(); i++) {
				//dos.writeUTF(clients.get(i).getInetAddress().toString());
				System.out.println(clients.get(i).getInetAddress().toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void serveAll(Socket client) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		FileOutputStream fos = new FileOutputStream("clientsList.txt");
		BufferedOutputStream buf = new BufferedOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(buf);
		
		while (true)
		{
			Message m = (Message)ois.readObject();
			dos.writeUTF(m.toString()+"\r\n");
			Message resp = new Message("I hear you, "+m.userName, "server");
			for (int i = 0; i < clients.size(); i++) {
				try {
					ObjectOutputStream oos = new ObjectOutputStream(clients.get(i).getOutputStream());
					oos.writeObject(resp);
				} catch (Exception e) {
				}
			}
		}
	}

}