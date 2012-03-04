package ru.spbstu.telematics.stu.collections;
public class QueueTest {

	public static void main (String args[]) {		
		IArrayQueue q = new ArrayQueue();
		for (int i=1; i<11; i++)
			q.add(i);
		q.out();
		//System.out.println(q.size());
		//q.rotate(3);
		//q.out();
		int a = (int)q.get();
		System.out.println(q.size());
		
		//q.out();		
	}
}
