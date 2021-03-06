package io.github.ray.wall.benchmark.implementations;

import java.util.concurrent.locks.StampedLock;

import io.github.ray.wall.benchmark.Counter;

public class Stamped implements Counter {

	private StampedLock rwlock = new StampedLock();
	
	private long counter;
	public long s, t;
	
	public long getCounter()
	{
		long stamp = rwlock.tryOptimisticRead();
		
		try
		{
			long result = counter;
			
			if (rwlock.validate(stamp))
			{
				return result;
			}
			
			stamp = rwlock.readLock();
			
			result = counter;
			
			rwlock.unlockRead(stamp);
			
			return counter;
		}
		finally
		{
			
		}
	}
	
	public long increment()
	{
		long stamp = rwlock.writeLock();
		
		try
		{	
			++counter;
			return counter;
		}
		finally
		{
			rwlock.unlockWrite(stamp);
		}
	}
}
