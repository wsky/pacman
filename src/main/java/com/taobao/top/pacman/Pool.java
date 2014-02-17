package com.taobao.top.pacman;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;

public abstract class Pool<T> {
	static final int DefaultPoolSize = 10;

	private Class<T> type;
	private T[] items;
	private int count;
	private int poolSize;

	public Pool() {
		this(DefaultPoolSize);
	}

	@SuppressWarnings("unchecked")
	public Pool(int poolSize) {
		this.type = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.items = (T[]) Array.newInstance(this.type, poolSize);
		this.poolSize = poolSize;
	}

	public T acquire() {
		if (this.count <= 0)
			return this.createNew();
		this.count--;
		return this.items[this.count];
	}

	public void release(T item) {
		if (this.count >= this.poolSize)
			return;
		this.items[this.count] = item;
		this.count++;
	}

	protected abstract T createNew();
}