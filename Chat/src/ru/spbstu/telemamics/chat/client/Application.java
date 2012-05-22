package ru.spbstu.telemamics.chat.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ChatClient client = new ChatClient("localhost", 10002);
		final AppFrame frame = new AppFrame(client);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {	// На закрытие формы повесим запрос на выход из чата
				try {
					if (client.isLogedIn)
						client.logOut();	// разрегистрируемся
					client.close();			// информируем о выходе
				} catch (IOException e1) {
					//e1.printStackTrace();
				}
				finally {
					System.exit(0);
				}
			}
		});
		frame.setVisible(true);
	}

}

class ChatClient {
	private Socket s = new Socket();
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String serverAddr;
	private int port;
	
	
	private boolean connectError = false;
	private boolean internalError = false;
	public boolean isLogedIn = false;
	
	public String userName;
	
	public ChatClient(String serverAddr, int port) {
		this.serverAddr = serverAddr;
		this.port = port;
	}
	
	public boolean connect(){	// Соединяет с сервером
		try {
			s.connect(new InetSocketAddress(serverAddr, port));
		} catch (IOException e) {
			connectError = true;
			return false;
		}
		try {
			oos = new ObjectOutputStream(s.getOutputStream());	// Берем потоки
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			internalError = true;
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendMessage(String text, String from){	// Шлет сообщение
		Message m = new Message(text, from, "MESSAGE");
		try {
			oos.writeObject(m);
		} catch (IOException e) {
			internalError = true;
			return false;
		}
		return true;
	}
	
	public void logIn(String userName) throws IOException{	// Просит регистрации
		Message m = new Message("", userName, "LOGIN");
		this.userName = userName;
		oos.writeObject(m);
	}
	
	public void logOut() throws IOException{				// Просит разрегистрировать
		Message m = new Message("", this.userName, "LOGOUT");
		oos.writeObject(m);
	}
	
	public void close() throws IOException{					// Уведомляет о выходе
		Message m = new Message("", this.userName, "CLOSE");
		oos.writeObject(m);
	}
	
	public void recieveMessages(JTextArea textArea) throws IOException {	// Принимает сообщения
		//ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
		while(true) {
			try {
				Message resp = (Message) ois.readObject();	
				if (resp.status.equals("LOGINCONFIGM")) {	// Пришло подтверждение регистрации
					isLogedIn = true;
				}
				textArea.append(resp.userName+"> "+resp.text+"\n");		// Выводим сообщение
			} catch (IOException | ClassNotFoundException e) {
				textArea.append("Lost server connection"+"\n");
				break;
			}
		}
	}
}

class AppFrame extends JFrame {
	public static final int DEFAULT_WIDTH = 640;
	public static final int DEFAULT_HEIGHT = 480;
	private boolean connected = false;
	private ChatClient client;
	public Thread reciever;
	public AppFrame(ChatClient client_) {
		this.client = client_;
		connected = client.connect();
		
		setTitle("Chat");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		setSize(screenWidth/2, screenHeight/2);
		setLocation(screenWidth/4, screenHeight/4);
		
		
		JPanel header = new JPanel();
		header.setLayout(new GridLayout(1, 3));
		header.add(new JLabel("UserName", SwingConstants.LEFT));
		final JTextField userNameField = new JTextField();
		JButton loginButton = new JButton("Log In!");
		header.add(userNameField);
		header.add(loginButton);
		add(header, BorderLayout.NORTH);
		
		final JTextArea textArea = new JTextArea(23, 25);
		textArea.setEditable(false);
		JScrollPane scroller = new JScrollPane(textArea);
		add(scroller, BorderLayout.CENTER);
		
		loginButton.addActionListener(new ActionListener() {	// Обработчик нажатия кнопки Login
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (connected && !userNameField.getText().equals("")) {
					try {
						client.logIn(userNameField.getText());
					} catch (IOException e1) {
						textArea.append("Can't log in!");
					}
					client.userName = userNameField.getText();
				}
			}
		});
		
		JPanel footer = new JPanel();
		footer.setLayout(new GridLayout(2,2));
		final JTextField messageField = new JTextField();
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {	// Обработчик нажатия кнопки Send
				if (connected && !messageField.getText().equals("")) {
					if (!client.sendMessage(messageField.getText(), client.userName))
						textArea.append("Error sending message");
				}
				messageField.setText("");
			}
		});
		footer.add(sendButton);
		footer.add(messageField);
		add(footer, BorderLayout.SOUTH);
		
		reciever = new Thread(new Runnable() {	// Поток-читатель сообщений от сервера
			
			@Override
			public void run() {
				if (connected) {
					try {
						client.recieveMessages(textArea);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		reciever.start();
		
		if (!connected)		// Если не соединились
			textArea.append("Cannot connect to server\n");
	}
	
}
