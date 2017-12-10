package com.mickey;

import java.util.Date;

import static java.lang.Thread.sleep;

public class ControlCenter implements Runnable
{
	int[] semafor ={0,0};
	static long  lastUseRight,lastUseLeft;
	static int carsin,carsWaitLeft,carsWaitRight;
	int bridgeCapacity;

	ControlCenter(int bridgeCapacity)
	{
		this.bridgeCapacity= bridgeCapacity;
	}

	public static synchronized void carWaitingLeft()
	{
		carsWaitLeft++;
	}

	public static synchronized void carWaitingRight()
	{
		carsWaitRight++;
	}

	/**
	 * This function updates the lastUse time and decreases the value for the cars on the bridge.
	 * @param direction The direction of the car. Needed to know which lastUse time to update.
	 */

	public static synchronized void carExitsTunnel(String direction)
	{
		carsin--;
		if(direction=="LEFT")
			lastUseLeft = (new Date()).getTime() / 1000;
		else
			lastUseRight =  (new Date()).getTime() / 1000;
	}

	/**
	 * This function checks if the other side is not starving.
	 * The other side is starving if no car has passed in the last 4 seconds.
	 * @return true if other side is not starving and false if the other side is starving.
	 */
	boolean otherSideNotStarving(int direction)
	{
		long currTime;
		currTime = (new Date()).getTime() / 1000;
		if (direction == 0)
		{
			if (carsWaitRight == 0)
			{
				System.out.println("No cars on right side, keeping left semaphore GREEN.");
				return true;
			}
			else
			{
				if (currTime - lastUseRight <= 4)
					return true;
				else
					return false;
			}
		}
		else
		{
			if (carsWaitLeft == 0)
			{
				System.out.println("No cars on left side, keeping right semaphore GREEN.");
				return true;
			}
			else
			{
				if (currTime - lastUseLeft <= 4)
					return true;
				else
					return false;
			}
		}
	}

	/**
	 * Function that checks if the bridge limit has been reached or not.
	 * @return true if bridge limit has not been reached and false otherwise.
	 */
	boolean bridgeLimitNotReached()
	{
		return (carsin<bridgeCapacity);
	}


	@Override
	public void run()
	{
		try
		{
			sleep(3000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		semafor[0]=1;
		lastUseLeft = (new Date()).getTime()/1000;
		lastUseRight = (new Date()).getTime()/1000;
		System.out.println("LEFT side semaphore is GREEN.");
		System.out.println("RIGHT side smeaphore is RED.");
		while(true)
		{
			if (semafor[0] == 1)
			{
				/**
				 * While other side is not starving and we still have cars waiting in line.
				 */
				while (otherSideNotStarving(0) && carsWaitLeft != 0)
				{
					if(bridgeLimitNotReached()==false)
					{
						System.out.println("Car cleared, but bridge is full. Waiting for capacity to decrease.");
						while (bridgeLimitNotReached() == false)
						{
							try
							{
								sleep(50);
							} catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
					}
					System.out.println("Clearing 1 car coming from LEFT to enter tunnel.");
					synchronized (this)
					{
						carsin++;
						carsWaitLeft--;
					}
					/**
					 * Waking up one car to go on the bridge.
					 */
					Car.wakeLeft();
					try
					{
						sleep(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					lastUseLeft = (new Date()).getTime()/1000;
				}
				semafor[0] = 0;
				System.out.println("LEFT side semaphore is RED.");
				/**
				 * While we still have cars driving on the bridge, we wait.
				 */
				while (carsin != 0)
				{
					try
					{
						sleep(50);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				if (carsWaitRight != 0)
				{
					semafor[1] = 1;
					System.out.println("RIGHT side smeaphore is GREEN.");
				}
				else if (carsWaitLeft == 0)
				{
					System.out.println("No more cars on either side. Exiting.");
					return;
				}
			}
			if (semafor[1] == 1)
			{
				/**
				 * While other side is not starving and we still have cars waiting in line.
				 */
				while (otherSideNotStarving(1) && carsWaitRight != 0)
				{
					if(bridgeLimitNotReached()==false)
					{
						System.out.println("Car cleared, but bridge is full. Waiting for capacity to decrease.");
						while (bridgeLimitNotReached() == false)
						{
							try
							{
								sleep(50);
							} catch (InterruptedException e)
							{
								e.printStackTrace();
							}
						}
					}
					System.out.println("Clearing 1 car coming from RIGHT to enter tunnel.");
					synchronized (this)
					{
						carsin++;
						carsWaitRight--;
					}
					/**
					 * Waking up one car to go on the bridge.
					 */
					Car.wakeRight();
					try
					{
						sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					lastUseRight = (new Date()).getTime()/1000;
				}
				System.out.println("RIGHT side smeaphore is RED.");
				semafor[1] = 0;
				/**
				 * While we still have cars driving on the bridge, we wait.
				 */
				while (carsin != 0)
				{
					try
					{
						sleep(50);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				if (carsWaitLeft != 0)
				{
					semafor[0] = 1;
					System.out.println("LEFT side semaphore is GREEN.");

				}
				else if (carsWaitLeft == 0)
				{
					System.out.println("No more cars on either side. Exiting.");
					return;
				}
			}
		}
	}
}
