package ru.spbstu.telematics.chat;

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
	private static ArrayList<Account> clients;	
	static FileOutputStream fos;
	static DataOutputStream dos;
	static Object clientsLock = new Object();	
	static Object fileLock = new Object();		
	
	public static void main(String[] args) throws IOException {
		servSock = new ServerSocket(10002);
		fos = new FileOutputStream("serverOutput.txt");		
		dos = new DataOutputStream(fos);
		clients = new ArrayList<Account>(); 
		while(true) {
			try {
				final Socket client = servSock.accept();	
				pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							serveAll(client);	// Main things are done here
						} catch (ClassNotFoundException | IOException e) {
							errorExit(client);
						}
					}
				});
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	private static void serveAll(Socket client) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		boolean exitFlag = false;
		Account someOne = new Account("someone", client);
		while (true)
		{
			Message m = (Message)ois.readObject();
			switch (m.status) {
				case "LOGIN": {		// In case it is a LOGIN request
					if (!logIn(m.userName, someOne)) {	// logIn synchronized
						Message resp = new Message("User already exists", "server", "MESSAGE");
						someOne.send(resp);
					}
					else {			// if login successful - print to log
						synchronized (fileLock) {
							try {
								dos.writeUTF(m.toString()+"\r\n");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					break;
				}
				case "LOGOUT": {	// logout request
					synchronized (clientsLock) {
						logOut(m.userName);
						Message resp = new Message("User "+m.userName+" left the chat", "server", "MESSAGE");
						deliverMessage(resp);	// tell everyone
					}
					synchronized (fileLock) {	// write to log
						try {
							dos.writeUTF(m.toString()+"\r\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				}
				case "CLOSE": {		// in case it is a close signal
					ois.close();
					exitFlag = true;
					break;
				}
				case "MESSAGE": {					// an ordinary message
					if (checkAuth(m.userName)){		// check if user is logged in 
						synchronized (fileLock) {
							try {
								dos.writeUTF(m.toString()+"\r\n");	// to log
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						Message resp = new Message(m.text, m.userName, "MESSAGE");
						synchronized (clientsLock) {
							deliverMessage(resp);	// tell everyone
						}
					}
					else {		// in case of unauthorized user
						synchronized (fileLock) {
							try {
								dos.writeUTF("UNAUTHORIZED"+m.toString()+"\r\n");	// Â ëîã
							} catch (Exception e) {
								e.printStackTrace();
							}					
						}
						Message resp = new Message("You are unauthorized, please log in to chat", "server", "MESSAGE");
						someOne.send(resp);		 
					}
					break;
				}
			}
			if (exitFlag)
				break;
		}
	}
	
	private static void deliverMessage(Message m){
		for (int i = 0; i < clients.size(); i++) {
			try {
				clients.get(i).send(m);
			} catch (Exception e) {
				try {
					dos.writeUTF("ERROR: can't deliver message to "+clients.get(i).getIpString()+"\r\n");	
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static void errorExit(Socket client){	
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).socket == client) {
				synchronized (fileLock) {
					try {
						dos.writeUTF("Client "+clients.get(i).getIpString()+" exited badly\r\n");
					} catch (IOException e) {
						e.printStackTrace();
					}							
				}
				clients.remove(i);
				break;
			}
		}
	}
	
	private static boolean logIn(String userName, Account client) throws IOException{
		synchronized (clientsLock) {
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).userName.equals(userName))
					return false;
			}
			client.userName = userName;
			clients.add(client);
			Message resp = new Message("Welcome to the chat server, "+userName, "server", "LOGINCONFIGM");
			client.send(resp);
			return true;
		}
	}
	
	private static boolean checkAuth(String userName) {
		synchronized (clientsLock) {
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).userName.equals(userName))
					return true;
			}
			return false;
		}
	}
	
	private static void logOut(String userName) throws IOException {
		for (int i = 0; i < clients.size(); i++){
			if (clients.get(i).userName.equals(userName)) {
				Message resp = new Message("Good By, "+clients.get(i).userName, "server", "MESSAGE");
				clients.get(i).send(resp);
				clients.remove(i);
				break;
			}
		}
	}

}
