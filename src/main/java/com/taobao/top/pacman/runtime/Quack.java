package com.taobao.top.pacman.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public abstract class Quack<T> {
	private Class<T> type;

	T[] items;

	// First element when items is not empty
	int head;
	// Next vacancy when items are not full
	int tail;
	// Number of elements.
	int count;

	public Quack() {
		this.type = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.items = (T[]) Array.newInstance(this.type, 4);
	}

	public Quack(T[] items) {
		// Fx.Assert(items != null, "This shouldn't get called with null");
		// Fx.Assert(items.Length > 0, "This shouldn't be called with a zero length array.");

		this.items = items;

		// The default value of 0 is correct for both
		// head and tail.

		this.count = this.items.length;
	}

	public int count() {
		return this.count;
	}

	public T get(int index) {
		// Fx.Assert(index < this.count, "Index out of range.");

		int realIndex = (this.head + index) % this.items.length;

		return this.items[realIndex];
	}

	public T[] toArray() {
		// Fx.Assert(this.count > 0, "We should only call this when we have items.");

		T[] compressedItems = (T[]) Array.newInstance(this.type, this.count);

		for (int i = 0; i < this.count; i++) {
			compressedItems[i] = this.items[(this.head + i) % this.items.length];
		}

		return compressedItems;
	}

	public void pushFront(T item) {
		if (this.count == this.items.length) {
			enlarge();
		}

		if (--this.head == -1) {
			this.head = this.items.length - 1;
		}
		this.items[this.head] = item;

		++this.count;
	}

	public void enqueue(T item) {
		if (this.count == this.items.length) {
			enlarge();
		}

		this.items[this.tail] = item;
		if (++this.tail == this.items.length) {
			this.tail = 0;
		}

		++this.count;
	}

	public T dequeue() {
		// Fx.Assert(this.count > 0, "Quack is empty");

		T removed = this.items[this.head];
		this.items[this.head] = null;
		if (++this.head == this.items.length) {
			this.head = 0;
		}

		--this.count;

		return removed;
	}

	public boolean remove(T item) {
		int found = -1;

		for (int i = 0; i < this.count; i++)
		{
			int realIndex = (this.head + i) % this.items.length;
			if (item.equals(this.items[realIndex]))
			{
				found = i;
				break;
			}
		}

		if (found == -1)
		{
			return false;
		}
		else
		{
			remove(found);
			return true;
		}
	}

	public void remove(int index)
	{
		// Fx.Assert(index < this.count, "Index out of range");

		for (int i = index - 1; i >= 0; i--)
		{
			int sourceIndex = (this.head + i) % this.items.length;
			int targetIndex = sourceIndex + 1;

			if (targetIndex == this.items.length)
			{
				targetIndex = 0;
			}

			this.items[targetIndex] = this.items[sourceIndex];
		}

		--this.count;
		++this.head;

		if (this.head == this.items.length)
		{
			this.head = 0;
		}
	}

	void enlarge()
	{
		// Fx.Assert(this.items.Length > 0, "Quack is empty");

		int capacity = this.items.length * 2;
		this.setCapacity(capacity);
	}

	void setCapacity(int capacity)
	{
		// Fx.Assert(capacity >= this.count, "Capacity is set to a smaller value");

		T[] newArray = (T[]) Array.newInstance(this.type, capacity);
		if (this.count > 0)
		{
			if (this.head < this.tail)
			{
				System.arraycopy(this.items, this.head, newArray, 0, this.count);
			}
			else
			{
				System.arraycopy(this.items, this.head, newArray, 0, this.items.length - this.head);
				System.arraycopy(this.items, 0, newArray, this.items.length - this.head, this.tail);
			}
		}

		this.items = newArray;
		this.head = 0;
		this.tail = (this.count == capacity) ? 0 : this.count;
	}
}
