package com.sumeyyeakay.producer;

import java.util.Deque;

public class Consumer extends Thread
{

    private final Deque<Integer> list;

    public Consumer(Deque<Integer> list)
    {
        this.list = list;

        setName("consumer");
        setDaemon(true);
    }

    @Override
    public void run()
    {
        while (true)
        {
            synchronized (list)
            {
                try
                {
                    if (list.isEmpty())
                    {
                        list.wait();
                    }

                    int number = list.removeFirst();
                    System.out.println("Çıkarıldı: " + number);
                    list.notify();

                    Thread.sleep(1000L);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
