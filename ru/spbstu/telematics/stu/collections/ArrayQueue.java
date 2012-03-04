package ru.spbstu.telematics.stu.collections;

public class ArrayQueue implements IArrayQueue{
	private int elemCount = 0;				// Число элементов в очереди
	private Object[] arr = new Object[10];	// Основной массив очереди. Пусть их будет 10 изначально

	public void add(Object o)				// Метод добавляет объект в конец очереди
	{
		if (elemCount==arr.length)
			enlarge();
		
		arr[elemCount] = o;
		elemCount++;
		
	}
	
	private void enlarge()					// Метод увеличивает массив очереди в двое
	{
		Object tmp[] = new Object[arr.length*2];
		System.arraycopy(arr, 0, tmp, 0, arr.length);
		arr = tmp;
	}
	
	public Object get()						// Метод извлекает первый объект из очереди
	{
		if (elemCount > 0)
		{
			Object tmp[] = new Object[arr.length-1];
			System.arraycopy(arr, 1, tmp, 0, arr.length-1);
			Object first = arr[0];
			arr = tmp;
			elemCount--;
			return first;
		}
		else return null;
	}
	
	public int size()						// Метод возвращает число объектов в очереди
	{
		return elemCount;
	}
	
	public void out()	// Для теста
	{
		for (int i=0; i<elemCount; i++)
			System.out.println(arr[i]);
	}
	
	public void rotate(int posNum)			// Метод осуществляет циклический сдвиг очереди
	{
		Object[] tmp = new Object[arr.length];
		System.arraycopy(arr, posNum, tmp, 0, arr.length-posNum);
		System.arraycopy(arr, 0, tmp, arr.length-posNum, posNum);
		arr = tmp;
	}
	
}
