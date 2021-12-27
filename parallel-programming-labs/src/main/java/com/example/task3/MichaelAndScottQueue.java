package com.example.task3;

import java.util.concurrent.atomic.AtomicReference;

public class MichaelAndScottQueue<T> {

    private Node<T> dummy = new Node<>(null, new AtomicReference<>(null));
    private AtomicReference<Node<T>> head = new AtomicReference<>(dummy);
    private AtomicReference<Node<T>> tail = new AtomicReference<>(dummy);

    static class Node<T> {

        public T data;
        public AtomicReference<Node<T>> next;

        public Node(T data, AtomicReference<Node<T>> next) {
            this.data = data;
            this.next = next;
        }
    }

    public T pull() {
        while (true) {
            Node<T> first = head.get();
            Node<T> last = tail.get();
            Node<T> nextHead = first.next.get();
            if (first == head.get()) {
                if (first == last) {
                    //очередь пустая или 2 поток не успел подвинуть хвост
                    if (nextHead == null) {
                        return null;
                    }
                    //помогаем двигать хвост
                    tail.compareAndSet(last, nextHead);
                } else {
                    T item = first.data;
                    //пробуем удалить
                    if (head.compareAndSet(first, nextHead)) {
                        return item;
                    }
                }
            }
        }
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data, new AtomicReference<>(null));

        while (true) {
            Node<T> currentTail = tail.get();
            Node<T> tailNext = currentTail.next.get();
            if (currentTail == tail.get()) {
                if (tailNext != null) {
                    tail.compareAndSet(currentTail, tailNext);
                } else {
                    if (currentTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(currentTail, newNode);
                        return;
                    }
                }
            }

        }
    }

    public void print() {
        Node<T> current = head.get();

        while (current != null) {
            System.out.println(current.data);
            current = current.next.get();
        }
    }
}
