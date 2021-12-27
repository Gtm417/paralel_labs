package com.example.task1;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class CasMutex extends Object {

    private AtomicReference<Runnable> currThread = new AtomicReference<>();
    private LinkedBlockingQueue<Runnable> waitingThreads;

    public CasMutex(LinkedBlockingQueue<Runnable> waitingThreads) {
        this.waitingThreads = waitingThreads;
    }

    public void lock(){
        if (Thread.currentThread().equals(currThread.get())) {
            throw new RuntimeException("Object already locked");
        }

        while (!currThread.compareAndSet(null, Thread.currentThread())) {
            Thread.yield();
        }
        System.out.println("Object locked by: " + Thread.currentThread().getName());
    }

    public void unlock() {
        if (!Thread.currentThread().equals(currThread.get())) {
            throw new RuntimeException("Tried unlock without have lock");
        }

        System.out.println("Object unlocked by: " + Thread.currentThread().getName());
        currThread.set(null);
    }


    public void casWait() throws InterruptedException {
        Thread thread = Thread.currentThread();
        if (!thread.equals(currThread.get())) {
            throw new RuntimeException("Using wait method without lock");
        }

        waitingThreads.put(thread);
        System.out.println("Waiting: " + Thread.currentThread().getName());
        unlock();

        while (waitingThreads.contains(thread)) {
            Thread.yield();
        }

        lock();
        System.out.println("No waiting any more: " + Thread.currentThread().getName());
    }

    public void casNotify() throws InterruptedException {
        waitingThreads.take();
        System.out.println("Notify: " + Thread.currentThread().getName());
    }

    public void casNotifyAll() {
        waitingThreads.clear();
        System.out.println("Notify all: " + Thread.currentThread().getName());
    }
}
