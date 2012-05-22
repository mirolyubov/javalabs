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
	static ExecutorService pool = Executors.newCachedThreadPool();	// Расширяемый пул потоков для обслуживания клиентов
	private static ServerSocket servSock;
	private static ArrayList<Account> clients;	// Список клиентов, подключенных к серверу
	static FileOutputStream fos;
	static DataOutputStream dos;
	static Object clientsLock = new Object();	// Замок для синхронизации работы со списком клиентов
	static Object fileLock = new Object();		// Замок для работы с файлом
	
	public static void main(String[] args) throws IOException {
		servSock = new ServerSocket(10002);
		fos = new FileOutputStream("serverOutput.txt");		// Здесь будет лог сервера
		dos = new DataOutputStream(fos);
		clients = new ArrayList<Account>(); 
		while(true) {
			try {
				final Socket client = servSock.accept();	// Прием
				pool.submit(new Runnable() {				// Отдаем клиенту поток
					@Override
					public void run() {
						try {
							serveAll(client);				// Главная функция сервера
						} catch (ClassNotFoundException | IOException e) {
							errorExit(client);				// Если клиент вышел не нормально
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
				case "LOGIN": {		// Если пришел запрос на регистрацию
					if (!logIn(m.userName, someOne)) {	// logIn synchronized
						Message resp = new Message("User already exists", "server", "MESSAGE");
						someOne.send(resp);
					}
					else {			// Записали вход в лог
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
				case "LOGOUT": {	// Пришел запрос на разрегистрацию
					synchronized (clientsLock) {
						logOut(m.userName);
						Message resp = new Message("User "+m.userName+" left the chat", "server", "MESSAGE");
						deliverMessage(resp);	// Разослать всем, что кто-то ушел
					}
					synchronized (fileLock) {	// И в лог
						try {
							dos.writeUTF(m.toString()+"\r\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				}
				case "CLOSE": {		// Уведомление об отключении
					ois.close();
					exitFlag = true;
					break;
				}
				case "MESSAGE": {					// Простое сообщение
					if (checkAuth(m.userName)){		// Пройдем аутентификацию
						synchronized (fileLock) {
							try {
								dos.writeUTF(m.toString()+"\r\n");	// В лог
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						Message resp = new Message(m.text, m.userName, "MESSAGE");
						synchronized (clientsLock) {
							deliverMessage(resp);	// Разослать всем
						}
					}
					else {		// Если аутентификация не пройдена
						synchronized (fileLock) {
							try {
								dos.writeUTF("UNAUTHORIZED"+m.toString()+"\r\n");	// В лог
							} catch (Exception e) {
								e.printStackTrace();
							}					
						}
						Message resp = new Message("You are unauthorized, please log in to chat", "server", "MESSAGE");
						someOne.send(resp);		// Скажем пользователю об этом 
					}
					break;
				}
			}
			if (exitFlag)
				break;
		}
	}
	
	private static void deliverMessage(Message m){		// Рассылает сообщение всем подключенным пользователям
		for (int i = 0; i < clients.size(); i++) {
			try {
				clients.get(i).send(m);
			} catch (Exception e) {
				try {	// Ошибка - в лог
					dos.writeUTF("ERROR: can't deliver message to "+clients.get(i).getIpString()+"\r\n");	
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static void errorExit(Socket client){	// Если клиент отвалился, надо удалить его аккаунт
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
	
	private static boolean logIn(String userName, Account client) throws IOException{	// Регистрирует пользователя
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
	
	private static boolean checkAuth(String userName) {		// Проверка аутентификации
		synchronized (clientsLock) {
			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).userName.equals(userName))
					return true;
			}
			return false;
		}
	}
	
	private static void logOut(String userName) throws IOException {	// Разрегистрация
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