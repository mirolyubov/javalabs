package ru.stu.telematics.lab3;

public class Connection {
	private final static Object lock = new Object();
	private int termNum;
	private String clientName;
	private String result;
	
	public String call(int termNum, String clientName) {
		synchronized(lock) {			
			this.termNum = termNum;
			this.clientName = clientName;
			lock.notify();
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void service() {
		synchronized (lock) {
			this.result = "Request from terminal " + this.termNum + " with client " + this.clientName;
			lock.notify();
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
