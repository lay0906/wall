package io.github.ray.wall.benchmark.implementations;



import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.github.ray.wall.benchmark.Counter;

public class Locks implements Counter
{
	private Lock lock = new ReentrantLock();

	
	private long counter;
	
	public long getCounter()
	{
		try
		{
			lock.lock();
			return counter;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public long increment()
	{
		try
		{
			lock.lock();
			++counter;
			return counter;
		}
		finally
		{
			lock.unlock();
		}
	}
}
