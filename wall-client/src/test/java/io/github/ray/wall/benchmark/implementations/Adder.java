package io.github.ray.wall.benchmark.implementations;

import java.util.concurrent.atomic.LongAdder;

import io.github.ray.wall.benchmark.Counter;

public class Adder implements Counter
{
	private final LongAdder adder = new LongAdder();
	
	public long getCounter()
	{
		return adder.longValue();
	}
	
	public long increment()
	{
		adder.increment();
		//return adder.sum(); //对测试不公平
		return 0;
	}

}
