package com.mickey;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

class Dekker
{
	public Dekker ()
	{
		for(int i=0;i<=25;i++)
			flag.set(i,0);
		turn = 0;
	}

	public void Pmutex(int t)
	{
		int other;
		other = 25-t;
		flag.set(t,1);
		while (flag.get(other) == 1)
		{
			if (turn == other)
			{
				flag.set(t,0);
				while (turn == other)
					;
				flag.set(t,1);
			}
		}
	}

	public void Vmutex(int t)
	{
		turn = 25-t;
		flag.set(t,0);
	}
	private volatile int turn;
	private AtomicIntegerArray flag = new AtomicIntegerArray(26);
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
		MyThreads.sort();
		MyThreads.printResults();
	}
}

class Casca implements Comparable<Casca>
{
	String id;
	int price;
	String name;
	String url;

	@Override
	public String toString()
	{
		System.out.println("ID:"+id+" Price:"+price+" Name:"+name+" URL:"+url);
		return super.toString();
	}

	public int compareTo(Casca a)
	{
		return a.price - this.price;
	}
}

class MyThreads implements Runnable
{
	private int threadNumber;
	private volatile static ArrayList<Casca>results= new ArrayList<>();
	volatile static Dekker dekker= new Dekker();
	private static volatile int nrCel=0,nrEmag=0;
	public static void printResults()
	{
		System.out.println("We have a total of:"+results.size()+" items:"+nrCel+" items from Cel and "+nrEmag+" items from Emag.");
		for(Casca item:results)
		{
			System.out.println(item);
		}
	}

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
				idCasti.add(id.attr("id").substring(1,id.attr("id").length()-2));
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
		//System.out.println(idCasti.size()+" "+urlCasti.size()+" "+preturiCasti.size()+" "+numeCasti.size());

		for(int i=0;i<=idCasti.size()-1;i++)
		{
			Casca item= new Casca();
			item.id=idCasti.get(i);
			item.price=Integer.parseInt(preturiCasti.get(i));
			item.name=numeCasti.get(i);
			item.url=urlCasti.get(i);
			synchronized (this)
			{
				//dekker.Pmutex(threadNumber);
				//Critical area.
				results.add(item);
				nrCel++;
				//dekker.Vmutex(threadNumber);
			}
		}

	}

	public void doEmag() throws IOException
	{
		String url = "https://www.emag.ro/casti-pc/p"+threadNumber+"/c";
		System.out.println("Fetching from url:" + url);
		Document doc = Jsoup.connect(url).get();
		Elements linksAndUrls = doc.select("a[class='product-title js-product-url']");
		Elements ids = doc.select("input[name='product[]']");
		Elements prices = doc.select("p[class='product-new-price']");

		List<String>idCasti=new ArrayList<String>();
		List<String>urlCasti=new ArrayList<String>();
		List<String>preturiCasti=new ArrayList<String>();
		List<String>numeCasti=new ArrayList<String>();
		for (Element id : ids)
		{
			idCasti.add(id.attr("value"));
		}

		for (Element link : linksAndUrls)
		{
			urlCasti.add(link.attr("href"));
			numeCasti.add(link.text());
		}

		for (Element price : prices)
		{
			if(price.childNodeSize()!=0)
				preturiCasti.add(price.textNodes().get(0).toString().replaceAll("[^A-Za-z0-9]", ""));
		}
		//System.out.println(idCasti.size()+" "+urlCasti.size()+" "+preturiCasti.size()+" "+numeCasti.size());

		for(int i=0;i<=idCasti.size()-1;i++)
		{
			Casca item= new Casca();
			item.id=idCasti.get(i);
			item.price=Integer.parseInt(preturiCasti.get(i));
			item.name=numeCasti.get(i);
			item.url=urlCasti.get(i);
			synchronized (this)
			{
				//dekker.Pmutex(threadNumber);
				//Critical area.
				results.add(item);
				nrEmag++;
				//dekker.Vmutex(threadNumber);
			}
		}

	}

	@Override
	public void run()
	{
		try
		{
			doCel();
			if(threadNumber<=11)
				doEmag();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public static void sort()
	{
		Collections.sort(results);
		printResults();
	}
}
