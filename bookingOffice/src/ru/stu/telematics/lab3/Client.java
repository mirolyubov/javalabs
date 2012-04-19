package ru.stu.telematics.lab3;

import java.util.ArrayList;
import java.util.Random;

////////////////////////////
/*
 * ������� 1 ������ ��� ����������! � ���� �� ���.
 * ��� ������� 1 ������ ��� ���������� � 1 ������� ��� ����������
 */

public class Client implements Runnable{
	private String clientName;
	private Terminal terminal;
	static Object lock = new Object();
	public Client(String name, Terminal terminal) {
		this.setClientName(name);
		this.terminal = terminal;
	}
	@Override
	public void run() {
		System.out.println("Client " + clientName + " is waiting for operator on terminal " + terminal.getTermNumber());
		try {
			terminal.use();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			boolean ticketConfigm = false;
			while (!ticketConfigm) {
			int orderedSeat = ticketChose();
				synchronized (lock) {	
					ticketConfigm = terminal.operator.book(this, orderedSeat);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	private int ticketChose() {
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		tickets = terminal.operator.showTickets();
		Random rand = new Random();
		int orderedNumber = Math.abs(rand.nextInt()%tickets.size());
		int orderedSeat = tickets.get(orderedNumber).getSeat();
		System.out.println(clientName + ": I ordered ticket " + orderedSeat);
		return orderedSeat;	// ���������� ��������� �����
	}
	
}
