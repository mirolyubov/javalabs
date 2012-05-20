package ru.spbstu.telemamics.chat.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;

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
		AppFrame frame = new AppFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

}

class AppFrame extends JFrame {
	public AppFrame() {
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
		JTextField userNameField = new JTextField();
		JButton loginButton = new JButton("Log In!");
		header.add(userNameField);
		header.add(loginButton);
		add(header, BorderLayout.NORTH);
		
		JTextArea textArea = new JTextArea(23, 25);
		textArea.setEditable(false);
		JScrollPane scroller = new JScrollPane(textArea);
		add(scroller, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		footer.setLayout(new GridLayout(2,2));
		JTextField messageField = new JTextField();
		JButton sendButton = new JButton("Send");
		footer.add(sendButton);
		footer.add(messageField);
		add(footer, BorderLayout.SOUTH);
	}
	public static final int DEFAULT_WIDTH = 640;
	public static final int DEFAULT_HEIGHT = 480;
}
