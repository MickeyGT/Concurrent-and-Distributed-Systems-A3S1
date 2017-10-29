package com.mickey;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

class Dekker
{
	public Dekker ()
	{
		flag.set(0,0); flag.set(1,0); turn = 0;
	}

	public void Pmutex(int t)
	{
		int other;

		other = 1-t;
		flag.set(t,1);
		while (flag.get(other) == 1) {
			if (turn == other) {
				flag.set(t,0);
				while (turn == other)
					;
				flag.set(t,1);
			}
		}
	}

	public void Vmutex(int t)
	{
		turn = 1-t;
		flag.set(t,0);
	}

	private volatile int turn;
	private AtomicIntegerArray flag = new AtomicIntegerArray(2);
}

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Thread myThreads[] = new Thread[26];
		try
		{
			for (int i = 1; i <= 25; i++)
			{
				myThreads[i] = new Thread(new MyThreads(i));
				myThreads[i].start();
			}

			for (int i = 1; i <= 25; i++)
			{
				myThreads[i].join();
			}
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}

class Casca
{
	String id;
	int price;
	String name;
	String url;
}

class MyThreads implements Runnable
{
	private int threadNumber;

	public MyThreads(int thrNumber)
	{
		threadNumber = thrNumber;
	}

	public void doCel() throws IOException
	{
		String url = "http://www.cel.ro/casti/0a-" + threadNumber;
		System.out.println("Fetching from url:" + url);
		Document doc = Jsoup.connect(url).get();
		Elements linksAndUrls = doc.select("a[class=\"productListing-data-b product_link product_name\"]");
		Elements ids = doc.select("span[id]");
		Elements prices = doc.select("b[itemprop='price']");

		List<String>idCasti=new ArrayList<String>();
		List<String>urlCasti=new ArrayList<String>();
		List<String>preturiCasti=new ArrayList<String>();
		List<String>numeCasti=new ArrayList<String>();
		for (Element id : ids)
		{
			if (id.attr("id").endsWith("-0") && id.attr("id").startsWith("s"))
			{
				idCasti.add(id.attr("id"));
			}
		}

		for (Element link : linksAndUrls)
		{
			urlCasti.add(link.attr("abs:href"));
			numeCasti.add(link.text());
		}

		for (Element price : prices)
		{
			preturiCasti.add(price.attr("content"));
		}
		System.out.println(idCasti.size()+" "+urlCasti.size()+" "+preturiCasti.size()+" "+numeCasti.size());
		Casca item= new Casca();
		for(int i=0;i<=idCasti.size()-1;i++)
		{
			item.id=idCasti.get(i);
			item.price=Integer.parseInt(preturiCasti.get(i));
			item.name=numeCasti.get(i);
			item.url=urlCasti.get(i);

		}
		Dekker dekker= new Dekker();
		dekker.Pmutex(threadNumber);

		dekker.Vmutex(threadNumber);
	}

	@Override
	public void run()
	{
		try
		{
			doCel();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
