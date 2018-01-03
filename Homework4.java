package com.mickey;

import java.util.concurrent.Semaphore;

public class Main
{
	private static int i=0;
	public static void increaseCounter()
	{
		i++;
		System.out.println(i);
	}

	public static boolean valueEqualsEnd()
	{
		return i==60;
	}

    public static void main(String[] args)
    {
		Semaphore sem = new Semaphore(1,true);
		Thread myThreads[] = new Thread[5];
		for(int j=1;j<=4;j++)
		{
			myThreads[j] = new Thread(new myThread(j,sem));
			myThreads[j].start();
		}
		for(int j=1;j<=4;j++)
			try
			{
				myThreads[j].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		System.out.println("Value of i is:"+i);
	}
}

class myThread implements Runnable
{
	Semaphore sem;
	int nrThread;
	myThread(int nrThr,Semaphore semaphore)
	{
		sem=semaphore;
		nrThread = nrThr;
	}
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				sem.acquire();
				if(Main.valueEqualsEnd())
				{
					sem.release();
					return;
				}
				System.out.println("Therad "+nrThread+" increased the counter.");
				Main.increaseCounter();
				sem.release();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
