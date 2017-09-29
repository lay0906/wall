package io.github.ray.wall.benchmark.implementations;

import java.util.concurrent.atomic.AtomicLong;

import io.github.ray.wall.benchmark.Counter;

public class Atomic implements Counter
{
	private final AtomicLong atomic = new AtomicLong();
	
	public long getCounter()
	{
		return atomic.get();
	}
	
	public long increment()
	{
		return atomic.incrementAndGet();
	}
}
