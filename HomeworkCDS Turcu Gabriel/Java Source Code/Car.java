package com.mickey;

import java.util.Date;

import static java.lang.Thread.sleep;

public class Car implements Runnable
{
	private final static Object tunnelWaitLeft = new Object();
	private final static Object tunnelWaitRight = new Object();
	String direction;

	/**
	 * Constructor that sets the direction of the car.
	 * @param direction the direction of the car. Can be LEFT or RIGHT.
	 */
	public Car(String direction)
	{
		synchronized (tunnelWaitRight)
		{
			this.direction = direction;
		}
	}

	/**
	 * Notifies one car standing in queue at LEFT side.
	 */
	public static void wakeLeft()
	{
		synchronized (tunnelWaitLeft)
		{
			tunnelWaitLeft.notify();
		}
	}

	/**
	 * Notifies one car standing in queue at RIGHT side.
	 */
	public static void wakeRight()
	{
		synchronized (tunnelWaitRight)
		{
			tunnelWaitRight.notify();
		}
	}

	@Override
	public void run()
	{
		/**
		 * The for is used to represent the car doing the same process a number of times.
		 * As was requested in the problem.
		 */
		for (int j = 1; j <= 3; j++)
		{
			/**
			 * Tells the control center that a car has arrived and is waiting to be woken up.
			 */
			if (direction == "LEFT")
			{
				synchronized (this)
				{
					ControlCenter.carWaitingLeft();
				}
			} else
			{
				synchronized (this)
				{
					ControlCenter.carWaitingRight();
				}
			}

			if (direction == "LEFT")
			{
				/**
				 * Waiting to be awoken by the control center.
				 */
				try
				{
					synchronized (tunnelWaitLeft)
					{
						tunnelWaitLeft.wait();
					}
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				/**
				 * After the process is notified. It now enters the tunnel.
				 */
				System.out.println("Car coming from direction LEFT entering tunnel.");

				try
				{
					sleep(5000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				ControlCenter.carExitsTunnel(direction);
				System.out.println("Car coming from direction LEFT exiting tunnel.");
			}
			else
			{
				/**
				 * Waiting to be awoken by the control center.
				 */
				try
				{
					synchronized (tunnelWaitRight)
					{
						tunnelWaitRight.wait();
					}
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				/**
				 * After the process is notified. It now enters the tunnel.
				 */
				System.out.println("Car coming from direction RIGHT entering tunnel.");
				try
				{
					sleep(5000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				ControlCenter.carExitsTunnel(direction);
				System.out.println("Car coming from direction RIGHT exiting tunnel.");
			}
		}
	}
}
