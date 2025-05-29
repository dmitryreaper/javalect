package main;

import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.List;

public class TreiberStack<T> {

    // Узел стека
    private static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    // Атомарная ссылка на вершину стека
    private final AtomicReference<Node<T>> top = new AtomicReference<>(null);

    // Добавление элемента в стек
    public void push(T value) {
        Node<T> newHead = new Node<>(value);
        Node<T> currentHead;
        do {
            currentHead = top.get();
            newHead.next = currentHead;
        } while (!top.compareAndSet(currentHead, newHead));
    }

    // Извлечение элемента из стека
    public T pop() {
        Node<T> currentHead;
        Node<T> newHead;
        do {
            currentHead = top.get();
            if (currentHead == null) {
                throw new IllegalStateException("Стек пуст");
            }
            newHead = currentHead.next;
        } while (!top.compareAndSet(currentHead, newHead));
        return currentHead.value;
    }

    // Проверка на пустоту стека
    public boolean isEmpty() {
        return top.get() == null;
    }

    // === Точка входа ===
    public static void main(String[] args) throws InterruptedException {
        TreiberStack<Integer> stack = new TreiberStack<>();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                stack.push(i);
                System.out.println("Добавлено: " + i);
            }
            System.out.println("Поток добавления завершён.");
        });

        Thread t2 = new Thread(() -> {
            List<Integer> poppedValues = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                try {
                    Integer value = stack.pop();
                    poppedValues.add(value);
                    System.out.println("Извлечено: " + value);
                } catch (Exception e) {
                    System.out.println("Ошибка при извлечении: " + e.getMessage());
                    break;
                }
            }
            System.out.println("Извлеченные значения: " + poppedValues);
        });

        // Запуск потоков
        t1.start();
        t2.start();

        // Ожидание завершения потоков
        t1.join();
        t2.join();

        // Проверка, пуст ли стек после операций
        System.out.println("Стек " + (stack.isEmpty() ? "пуст" : "не пуст"));
    }
}