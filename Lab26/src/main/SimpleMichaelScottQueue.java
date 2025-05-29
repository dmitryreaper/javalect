package main;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleMichaelScottQueue<T> {
    private static class Node<T> {
        T value;
        AtomicReference<Node<T>> next = new AtomicReference<>(null);

        Node(T value) {
            this.value = value;
        }
    }

    private final Node<T> initialNode = new Node<>(null);
    private AtomicReference<Node<T>> head = new AtomicReference<>(initialNode);
    private AtomicReference<Node<T>> tail = new AtomicReference<>(initialNode);

    public void enqueue(T value) {
        if (value == null) throw new NullPointerException("Невозможно добавить элемент: значение не может быть null");

        Node<T> newNode = new Node<>(value);

        while (true) {
            Node<T> currentTail = tail.get();
            if (currentTail.next.compareAndSet(null, newNode)) {
                // Удалось добавить новый узел после tail
                tail.compareAndSet(currentTail, newNode); // продвигаем tail
                System.out.println("Добавлен элемент: " + value);
                return;
            } else {
                // Другой поток уже добавил элемент, помогаем продвинуть tail
                tail.compareAndSet(currentTail, currentTail.next.get());
            }
        }
    }

    public T dequeue() {
        while (true) {
            Node<T> currentHead = head.get();
            Node<T> firstNode = currentHead.next.get();

            if (firstNode == null) {
                // Очередь пустая
                System.out.println("Очередь пуста, извлечение невозможно");
                return null;
            }

            T value = firstNode.value;

            // Продвигаем голову на следующий узел
            if (head.compareAndSet(currentHead, firstNode)) {
                System.out.println("Извлечен элемент: " + value);
                return value;
            }
        }
    }

    public boolean isEmpty() {
        return head.get().next.get() == null;
    }

    // Тестирование
    public static void main(String[] args) throws InterruptedException {
        SimpleMichaelScottQueue<Integer> queue = new SimpleMichaelScottQueue<>();

        Thread enqueuer = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                queue.enqueue(i);
            }
            System.out.println("Добавление завершено");
        });

        Thread dequeuer = new Thread(() -> {
            int count = 0;
            while (count < 10_000) {
                Integer val = queue.dequeue();
                if (val != null) {
                    count++;
                }
            }
            System.out.println("Завершено извлечение: " + count + " элементов");
        });

        enqueuer.start();
        dequeuer.start();

        enqueuer.join();
        dequeuer.join();

        System.out.println("Очередь пуста? " + queue.isEmpty());
    }
}