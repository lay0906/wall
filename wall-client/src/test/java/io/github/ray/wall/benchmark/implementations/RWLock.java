package io.github.ray.wall.benchmark.implementations;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.github.ray.wall.benchmark.Counter;

public class RWLock implements Counter
{
	private ReadWriteLock rwlock = new ReentrantReadWriteLock();
	
	private Lock rlock = rwlock.readLock();
	private Lock wlock = rwlock.writeLock();
	
	private long counter;
	
	public long getCounter()
	{
		try
		{
			rlock.lock();		
			return counter;
		}
		finally
		{
			rlock.unlock();
		}
	}
	
	public long increment()
	{
		try
		{
			wlock.lock();		
			++counter;
			return counter;
		}
		finally
		{
			wlock.unlock();
		}
	}
}
