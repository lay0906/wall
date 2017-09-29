package io.github.ray.wall.benchmark.implementations;

import io.github.ray.wall.benchmark.Counter;

public class Synchronized implements Counter
{
	private Object lock = new Object();
	
	private int counter;
	
	public long getCounter()
	{
		synchronized (lock)
		{
			return counter;
		}
	}
	
	public long increment()
	{
		synchronized (lock)
		{
			++counter;
			return counter;
		}
	}
}
