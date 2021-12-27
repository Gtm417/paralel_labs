package task2;

import com.example.task2.SkipList;
import org.junit.Test;

import java.util.ArrayList;

public class SkipListTest {


    @Test
    public void testAllTogether() throws InterruptedException {
        SkipList<String> skipList = new SkipList<>(17, 0.5);
        ArrayList<Thread> threads = new ArrayList<>();
        int i = 0;
        for (; i < 10; i++) {
            threads.add(i, new Thread(() -> {
                String currThreadName = Thread.currentThread().getName();
                System.out.println("Add " + currThreadName + ": " + skipList.add(currThreadName));
            }));
            threads.get(i).start();
        }
        threads.add(i, new Thread(() -> {
            System.out.println("Remove " + "Thread-2" + ": " + skipList.remove("Thread-2"));
        }));
        threads.get(i).start();

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Contains: " + skipList.contains("Thread-1"));
        skipList.printAllList();
    }
}