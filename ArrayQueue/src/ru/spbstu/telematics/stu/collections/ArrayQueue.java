package ru.spbstu.telematics.stu.collections;
import java.util.Iterator;
//import java.util.ArrayList;
import java.util.NoSuchElementException;
public class ArrayQueue<T> implements IArrayQueue<T>, Iterable<T>  {
	private int elemCount = 0;				// Число элементов в очереди
	private T[] arr;	// Основной массив очереди. Пусть их будет 10 изначально

	public ArrayQueue(int num)
	{
		arr = (T[]) new Object[num];
	}
	
	public ArrayQueue()
	{
		arr = (T[]) new Object[1];
	}
	public void add(T o)				// Метод добавляет объект в конец очереди
	{
		if (elemCount==arr.length)
			enlarge();
		
		arr[elemCount] = o;
		elemCount++;
		
	}
	
	private void enlarge()					// Метод увеличивает массив очереди в двое
	{
		T tmp[] = (T[]) new Object[arr.length*2];
		System.arraycopy(arr, 0, tmp, 0, arr.length);
		arr = tmp;
	}
	
	public T get()						// Метод извлекает первый объект из очереди
	{
		if (elemCount > 0)
		{
			T tmp[] = (T[]) new Object[arr.length-1];
			System.arraycopy(arr, 1, tmp, 0, arr.length-1);
			T first = arr[0];
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
		T[] tmp = (T[]) new Object[elemCount];
		System.arraycopy(arr, posNum, tmp, 0, elemCount-posNum);
		System.arraycopy(arr, 0, tmp, elemCount-posNum, posNum);
		// Но так в итоге получили массив с числом элементов elemCount, а не arr.legth - пустые не учтены
		T[] tmp1 = (T[]) new Object[arr.length];	// Вернем их
		System.arraycopy(tmp, 0, tmp1, 0, tmp.length);
		arr = tmp1;
	}
	
	//Реализация интерфейса iterator
	private class Itr implements Iterator<T>
	{
		private int index = 0;
		@Override
		public boolean hasNext() {
			if (index<elemCount)
				return true;
			else
			return false;
		}
		
		@Override
		public T next() {
			if (index==elemCount)
				throw new NoSuchElementException();
			else
			{
				T result = arr[index];
				index++;
				return result;
			}
		}
		
		@Override
		public void remove() {
			// TODO Auto-generated method stub
			T[] tmp = (T[]) new Object[arr.length-1];
			if (index > 0)
				System.arraycopy(arr, 0, tmp, 0, index);
			System.arraycopy(arr, index+1, tmp, index, arr.length-index-1);
			if (elemCount>0) 
				elemCount--;
			arr = tmp;
		}
		
	}
	
	public Iterator<T> iterator(){
		return new Itr();
	}
	
	
	

	
	
}
