package com.mickey;

import java.util.Scanner;

public class Main
{
	private static int carsLeft,carsRight,bridgeCapacity;
	private static void readInput()
	{
		Scanner input = new Scanner(System.in);
		System.out.print("The number of cars on the LEFT side is:");
		carsLeft = input.nextInt();
		System.out.print("The number of cars on the RIGHT side is:");
		carsRight = input.nextInt();
		System.out.print("The number of maximum cars on bridge at one point is:");
		bridgeCapacity = input.nextInt();
	}
    public static void main(String[] args)
    {
    	readInput();

		Thread cars[] = new Thread[100];

        for(int i=1;i<=carsLeft;i++)
		{
			cars[i] = new Thread(new Car("LEFT"));
			cars[i].start();
		}
		for(int i=carsLeft+1;i<=carsRight+carsLeft;i++)
		{
			cars[i] = new Thread(new Car("RIGHT"));
			cars[i].start();
		}
		Thread controlCenter = new Thread(new ControlCenter(bridgeCapacity));
		controlCenter.start();
		try
		{
			controlCenter.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
        try
        {
            for (int i = 1; i <= carsLeft+carsRight; i++)
            {
                cars[i].join();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }
}
