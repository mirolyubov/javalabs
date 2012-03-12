package ru.spbstu.telematics.stu.collections;

public interface IArrayQueue<T> {
	/**
     * Добавляет объект в очередь
     * @param o
     */
    void add(T o);

    /**
     * Забирает объект из очереди. Возвращаеммый объект удаляется.
     * @return
     */
    T get();

    /**
     * Возвращает размер очереди
     * @return
     */
    int size();

    /**
     * Циклический сдвиг в массиве на заданное количество элементов
     */
    void out();	// Для теста
    void rotate(int posNum);
}
