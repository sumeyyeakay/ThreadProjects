package com.sumeyyeakay.producer;

import java.util.LinkedList;

public class Main
{

    public static void main(String[] args) throws InterruptedException
    {
        LinkedList<Integer> list = new LinkedList<>();

        Producer producer = new Producer(list, 3);
        Consumer consumer = new Consumer(list);

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Program sonlandı!");
    }

}
