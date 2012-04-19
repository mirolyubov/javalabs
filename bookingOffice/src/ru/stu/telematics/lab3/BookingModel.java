package ru.stu.telematics.lab3;

import java.util.ArrayList;
import java.util.Random;

public class BookingModel {
	public static void main(String[] args) {
		final int terminalCount = 5;
		final int clientCount = 10;
		final int ticketCount = 20;
		Terminal terminals[] = new Terminal[terminalCount];
		Client[] clients = new Client[clientCount];
		Thread[] threads = new Thread[clientCount];
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		
		for (int i = 0; i<ticketCount; i++)
			tickets.add(new Ticket(i));
		
		MainComputer mainComputer = new MainComputer(tickets);
		
		for (int i = 0; i < terminalCount; i++) {
			terminals[i] = new Terminal(i, mainComputer);
		}
		
		System.out.println(Math.abs(new Random().nextInt()%terminalCount));
		for (int i = 0; i < clientCount; i++) {
			clients[i] = new Client("Client"+i, terminals[Math.abs(new Random().nextInt()%terminalCount)]);
			threads[i] = new Thread(clients[i]);
		}
		
		for (int i=0; i<clientCount; i++)
			threads[i].start();
		for (int i = 0; i < clientCount; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
