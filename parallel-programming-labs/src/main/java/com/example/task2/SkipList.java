package com.example.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SkipList<T extends Comparable<? super T>> {

    private final int maxLevel;
    private final double probability;
    private final Node<T> head;
    public static final int FIRST_LEVEL = 1;


    static class Node<T> {
        public T data;
        public AtomicReference<Node<T>> rightNode;
        public Node<T> down;
        public AtomicReference<Thread> markedToRemove = new AtomicReference<>(null);

        public Node(T data, AtomicReference<Node<T>> rightNode, Node<T> down) {
            this.data = data;
            this.rightNode = rightNode;
            this.down = down;
        }
    }

    public SkipList(int maxLevel, double probability) {
        this.maxLevel = maxLevel;
        this.probability = probability;

        Node<T> initHeadNode = new Node<>(null, new AtomicReference<>(null), null);
        head = initHeadNode;

        for (int i = 0; i < maxLevel - 1; i++) {
            Node<T> newElementHead = new Node<>(null, new AtomicReference<>(null), null);
            initHeadNode.down = newElementHead;
            initHeadNode = newElementHead;
        }
    }

    public boolean remove(T data) {
        if (isNull(data)) {
            throw new IllegalArgumentException("Data can not be null");
        }

        Node<T> currentNode = head;
        int currentLevel = maxLevel;
        boolean isRemoveHappened = false;

        while (currentLevel >= FIRST_LEVEL) {
            Node<T> rightNode = currentNode.rightNode.get();
            if (rightNode != null && rightNode.data.compareTo(data) == 0) {
                Node<T> afterRightNode = rightNode.rightNode.get();
                Node<T> toDeleteNode = rightNode;
                while (true) {
                    if (toDeleteNode.markedToRemove.compareAndSet(null, Thread.currentThread())) {
                        Thread.yield();
                    } else if (toDeleteNode.markedToRemove.get().equals(Thread.currentThread())) {
                        toDeleteNode.rightNode.compareAndSet(toDeleteNode.rightNode.get(), null);
                        currentNode.rightNode.compareAndSet(rightNode, afterRightNode);
                        isRemoveHappened = true;
                        break;
                    } else {
                        return false;
                    }
                }
            }

            if (rightNode != null && rightNode.data.compareTo(data) < 0) {
                currentNode = rightNode;
            } else {
                currentNode = currentNode.down;
                currentLevel--;
            }
        }

        return isRemoveHappened;
    }

    public boolean add(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data can not be null");
        }

        final List<Node<T>> leftNodes = new ArrayList<>();
        final List<Node<T>> rightNodes = new ArrayList<>();
        final int elementLevel = getElementLevel();

        setLeftAndRightNodes(data, leftNodes, rightNodes, elementLevel);

        return addElements(data, leftNodes, rightNodes);
    }

    private boolean addElements(T data, List<Node<T>> leftNodes, List<Node<T>> rightNodes) {
        Node<T> DownNode = null;
        for (int i = leftNodes.size() - 1; i >= 0; i--) {
            Node<T> newNode = new Node<>(data, new AtomicReference<>(rightNodes.get(i)), null);

            if (DownNode != null) {
                newNode.down = DownNode;
            }

            if (!leftNodes.get(i).rightNode.compareAndSet(rightNodes.get(i), newNode)) {
                return false;
            }

            DownNode = newNode;
        }

        return true;
    }

    private void setLeftAndRightNodes(T data, List<Node<T>> leftNodes, List<Node<T>> rightNodes, int elementLevel) {
        Node<T> currentNode = head;
        int currentLevel = maxLevel;
        while (currentLevel > 0) {
            Node<T> rightNode = currentNode.rightNode.get();

            if (currentLevel <= elementLevel) {
                if (isNull(rightNode) || rightNode.data.compareTo(data) >= 0) {
                    leftNodes.add(currentNode);
                    rightNodes.add(rightNode);
                }
            }

            if (nonNull(rightNode) && rightNode.data.compareTo(data) < 0) {
                currentNode = rightNode;
            } else {
                currentNode = currentNode.down;
                currentLevel--;
            }
        }
    }

    public boolean contains(T data) {
        Node<T> currentNode = head;

        while (currentNode != null) {
            Node<T> rightNode = currentNode.rightNode.get();
            if (currentNode.data != null && currentNode.data.compareTo(data) == 0) {
                return true;
            } else if (nonNull(rightNode) && rightNode.data.compareTo(data) <= 0) {
                currentNode = rightNode;
            } else {
                currentNode = currentNode.down;
            }
        }

        return false;
    }

    public void printAllList() {
        Node<T> downLeftNode = findFirstElement(head);
        printAllElementsFromCurrent(downLeftNode);
    }

    private void printAllElementsFromCurrent(Node<T> current) {
        current = current.rightNode.get();
        while (nonNull(current)) {
            System.out.println(current.data);
            current = current.rightNode.get();
        }
    }

    private Node<T> findFirstElement(Node<T> current) {
        while (current.down != null) {
            current = current.down;
        }
        return current;
    }

    private int getElementLevel() {
        int level = FIRST_LEVEL;
        while (level < maxLevel && Math.random() < probability) {
            level++;
        }
        return level;
    }
}
