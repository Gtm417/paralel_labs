package task3;

import com.example.task3.MichaelAndScottQueue;
import org.junit.Test;

import java.util.ArrayList;

public class MichaelAndScottQueueTest {

    @Test
    public void test() throws InterruptedException {
        MichaelAndScottQueue<String> michaelAndScottQueue = new MichaelAndScottQueue<>();

        Thread[] arr = new Thread[20];

        for (int i = 0; i < 20; i++) {
            arr[i] = new Thread(newThreadTask(michaelAndScottQueue));
            arr[i].start();
        }

        for (int i = 0; i < 20; i++) {
            arr[i].join();
        }

        michaelAndScottQueue.print();
    }

    static Runnable newThreadTask(MichaelAndScottQueue<String> queue){
        return () -> {
            queue.add(Thread.currentThread().getName());
            if (Math.random() > 0.5) {
                System.out.println("Remove: " + queue.pull());
            }
        };
    }
}