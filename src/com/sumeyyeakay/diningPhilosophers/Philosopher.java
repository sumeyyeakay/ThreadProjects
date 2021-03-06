package com.sumeyyeakay.diningPhilosophers;

import java.util.Random;

public class Philosopher extends Thread
{
    private final Random random = new Random();

    private final int number;
    private final Fork leftFork;
    private final Fork rightFork;

    private PhilosopherEventHandler philosopherEventHandler = null;

    public Philosopher(int number, Fork leftFork, Fork rightFork)
    {
        this.number = number;
        this.leftFork = leftFork;
        this.rightFork = rightFork;

        setName("philosopher-" + number);
        setDaemon(true);
    }

    public int getNumber()
    {
        return number;
    }

    public PhilosopherEventHandler getPhilosopherEventHandler()
    {
        return philosopherEventHandler;
    }

    public void setPhilosopherEventHandler(PhilosopherEventHandler philosopherEventHandler)
    {
        this.philosopherEventHandler = philosopherEventHandler;
    }

    private void sleepMe(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private void eat()
    {
        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.eating(this);
        }

        sleepMe(2500L + random.nextInt(5000));

        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.thinking(this);
        }
    }

    private void forkTaken(Fork fork, Direction direction)
    {
        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.forkTaken(this, fork, direction);
        }
    }

    private void forkReleased(Fork fork, Direction direction)
    {
        if (philosopherEventHandler != null)
        {
            philosopherEventHandler.forkReleased(this, fork, direction);
        }
    }

    /**
     * Thread DEADLOCK'a dusmesini engellemek icin lock olusturuyoruz.
     * Olusturulan run metodunda kullandigimiz synchronized:(kilit mekanizmasi) ile birlikte tek bir threadin buraya girmesini sagliyoruz.
     * parametre olarak kilit olarak kullanilacak nesneyi yaziyoruz. (leftFork)
     * Ayni anda sadece 1 threadin calismasini saglamak icin bu yapiyi kullaniyoruz boylelikle deadLock durumundan kurtulmus oluyoruz.
     *
     *      Yazdigimiz metod ile 2 filozof yemek yerken diger 3 filozof dusunecektir. Onlarda yemegini bitirdikten sonra dongu bu sekilde
     *      devam edecektir.
     */

    @Override
    public void run()
    {
        while (true)
        {
            synchronized (leftFork)
            {
                // Soldaki çatalım elimde
                forkTaken(leftFork, Direction.LEFT);

                synchronized (rightFork)
                {
                    // Sağdaki çatalım elimde
                    forkTaken(rightFork, Direction.RIGHT);

                    // Yemek yiyebilirim
                    eat();

                    // Sağdaki çatalı bırakıyorum..
                    forkReleased(rightFork, Direction.RIGHT);
                }

                // Soldaki çatalı bırakıyorum..
                forkReleased(leftFork, Direction.LEFT);
            }

            sleepMe(2000L);
        }
    }

}
