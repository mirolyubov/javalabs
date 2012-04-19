package ru.stu.telematics.lab3;

public class bookingOperator {
	private Terminal terminal;
	public bookingOperator(Terminal terminal) {
		this.terminal = terminal;
	}
	
	public synchronized void book(Client client) throws InterruptedException {
		terminal.inUse = true;
		System.out.println("Operator on terminal " + terminal.getTermNumber() + " is booking for client " + client.getClientName());
		
		Thread.sleep(1000);
		terminal.free();
	}
}
