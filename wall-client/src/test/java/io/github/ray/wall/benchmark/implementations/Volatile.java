package io.github.ray.wall.benchmark.implementations;

import io.github.ray.wall.benchmark.Counter;

public class Volatile implements Counter
{
	private volatile long counter;
	
	public long getCounter()
	{
		return counter;
	}
	
	public long increment()
	{
		++counter;
		return counter;
	}
}