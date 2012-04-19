package ru.stu.telematics.lab3;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainComputer {
	private Lock orderLock;
	private ArrayList<Ticket> tickets;
	public MainComputer(ArrayList<Ticket> tickets) {
		this.orderLock = new ReentrantLock();
		this.tickets = tickets;
	}
	public boolean order(String clientName, int orderedSeat) {
		orderLock.lock();
		boolean result;
		try {
			//Random rand = new Random();
			//int orderedNumber = Math.abs(rand.nextInt()%getTickets().size());
			Ticket chosenTicket = new Ticket(orderedSeat);
			if (getTickets().contains(chosenTicket)) {
				int chosenNum = getTickets().indexOf(chosenTicket);
				System.out.println(clientName + " ordered ticket for seat " + orderedSeat);
				getTickets().remove(chosenNum);
				result = true;
			}
			else
			{
				System.out.println("No such ticket ("+ orderedSeat + "), "+ clientName);
				result = false;
			}
			
		} finally {
			orderLock.unlock();
		}
		return result;
	}
	
	public ArrayList<Ticket> getTickets() {
		return tickets;
	}
	

}
