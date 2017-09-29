package io.github.ray.wall.benchmark;

public class Writer implements Runnable
{
	private final Counter counter;
	
	public Writer(Counter counter)
	{
		this.counter = counter;
	}
	
	public void run()
	{
		while (true)
		{
			if (Thread.interrupted())
			{
				break;
			}
			
			long count = counter.increment();

			if (count > Main.TARGET_NUMBER)
			{
				Main.publish(System.currentTimeMillis());
				break;
			}
		}
	}
}
