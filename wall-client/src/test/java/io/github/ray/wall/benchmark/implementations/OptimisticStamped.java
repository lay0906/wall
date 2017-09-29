package io.github.ray.wall.benchmark.implementations;

import java.util.concurrent.locks.StampedLock;

import io.github.ray.wall.benchmark.Counter;

public class OptimisticStamped implements Counter {

	private StampedLock rwlock = new StampedLock();
	
	private long counter;
	private long success;

	private long total;
	
	
	public long getCounter()
	{
		long stamp = rwlock.tryOptimisticRead();
		
		total++;
		
		if (rwlock.validate(stamp))
		{
			success++;
			return counter;
		}
		
		return counter;
		
		
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

	public long getSuccess() {
		return success;
	}
	

	public long getTotal() {
		return total;
	}
}
