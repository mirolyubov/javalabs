package ru.stu.telematics.lab3;
public class Ticket {
	private int seat;
	
	public Ticket(int seat) {
		this.seat = seat;
	}

	public int getSeat() {
		return seat;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.seat==((Ticket)obj).seat;
	}

}
