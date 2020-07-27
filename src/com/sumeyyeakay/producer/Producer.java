package com.sumeyyeakay.producer;

import java.util.Deque;

public class Producer extends Thread
{

    private final Deque<Integer> list;
    private final int capacity;

    public Producer(Deque<Integer> list, int capacity)
    {
        this.list = list;
        this.capacity = capacity;

        setDaemon(true);
        setName("producer");
    }

    @Override
    public void run()
    {
        int number = 1;
        while (true)
        {
            synchronized (list)
            {
                try
                {
                    if (list.size() == capacity)
                    {
                        list.wait();
                    }

                    System.out.println("Eklendi: " + number);
                    list.addLast(number);
                    number++;

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
