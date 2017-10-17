package com.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainClass
{

    public static void main(String[] args)
    {
        int numberOfThreads = 60;
        Thread myThreads[] = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++)
        {
            myThreads[i] = new Thread(new MyThreads(i,numberOfThreads));
            myThreads[i].start();
        }
        for (int i = 0; i < numberOfThreads; i++)
        {
            try
            {
                myThreads[i].join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("All threads are dead, exiting main thread");
    }

}

class MyThreads implements Runnable
{
    private int threadNumber,totalNrOfThreads;

    public MyThreads(int number,int nrOfThreads)
    {
        threadNumber=number;
        totalNrOfThreads=nrOfThreads;
    }

    private static String bytesToHex(byte[] hash)
    {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++)
        {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void run()
    {
        int start = 10000000+((90000000)/totalNrOfThreads+1)*(threadNumber);
        int end=start+((90000000)/totalNrOfThreads+1);
        String correctAnswer="88B8F3C04619DC8F4582802A9B3863FF99714F5EB5B2715EB84636DB797F97FC";
        for(int i=start;i<=end;i++)
        {
            String originalString = Integer.toString(i);
            MessageDigest digest = null;
            try
            {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }
            byte[] encodedhash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            String hashedNumber = bytesToHex(encodedhash);
            hashedNumber=hashedNumber.toUpperCase();
            if(hashedNumber.equals(correctAnswer))
            {
                System.out.println("The answer is:"+i);
                // Answer should be 24296545
            }
        }
        System.out.println("Thread "+threadNumber+" is done.");
    }

}
