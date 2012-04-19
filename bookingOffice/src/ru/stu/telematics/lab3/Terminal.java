package ru.stu.telematics.lab3;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Terminal {
	
	public class BookingOperator {
		
		public boolean book(Client client, int orderedSeat) throws InterruptedException {
			//Terminal.this.inUse = true;
			System.out.println("Operator on terminal " + Terminal.this.getTermNumber() + " is booking for client " + client.getClientName());
			boolean result = mainComputer.order(client.getClientName(), orderedSeat);
			Thread.sleep(1000);
			Terminal.this.free();
			return result;
		}
		
		public ArrayList<Ticket> showTickets() {
			return mainComputer.getTickets();
		}
	}
	
	private int termNumber;
	private Lock termLock;
	private MainComputer mainComputer;
	private Condition operatorResponse;
	public BookingOperator operator;
	public boolean inUse = false;
	public Terminal(int termNumber, MainComputer mainComputer) {
		// TODO Auto-generated constructor stub
		this.setTermNumber(termNumber);
		this.operator = new BookingOperator();
		this.termLock = new ReentrantLock();
		this.mainComputer = mainComputer;
		this.operatorResponse = this.termLock.newCondition();
	}
	
	public void use() throws InterruptedException
	{
		termLock.lock();
			try {
				if (inUse)
					operatorResponse.await();
				else
					inUse = true;
			} finally {
				termLock.unlock();
			}
		//inUse = true;
	}
	
	public void free() throws InterruptedException {
		termLock.lock();
		try {
			inUse = false;
			operatorResponse.signal();
		} finally {
			termLock.unlock();
		}
	}

	public int getTermNumber() {
		return termNumber;
	}

	public void setTermNumber(int termNumber) {
		this.termNumber = termNumber;
	}

}
