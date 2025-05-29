package main;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingCollectionsTask {

    public static void main(String[] args) {
        // --- ЗАДАЧА 1: Стек ---
        ConcurrentLinkedDeque<Integer> stack = new ConcurrentLinkedDeque<>();
        int N = 5;
        for (int i = 1; i <= N; i++) {
            stack.push(i); // Добавляем числа в стек
        }

        System.out.println("Стек до изменений: " + stack);

        if (stack.size() >= 3) {
            Integer second = getNthElement(stack, 1); // индекс 1 -> второй элемент
            Integer third = getNthElement(stack, 2);   // индекс 2 -> третий элемент

            int product = second * third;
            stack.push(product);
        } else {
            System.out.println("Недостаточно элементов в стеке для выполнения операции.");
        }

        System.out.println("Стек после изменений: " + stack);

        // --- ЗАДАЧА 2: Очередь ---
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        queue.add(5);
        queue.add(3);
        queue.add(4);
        queue.add(2);
        queue.add(6);
        queue.add(1);
        queue.add(7);

        System.out.println("\nОчередь до удаления локальных минимумов: " + queue);

        removeLocalMinima(queue);

        System.out.println("Очередь после удаления локальных минимумов: " + queue);
    }

    // Вспомогательная функция для получения n-го элемента из стека
    private static Integer getNthElement(ConcurrentLinkedDeque<Integer> deque, int index) {
        return (Integer) deque.toArray()[index];
    }

    // Метод для удаления локальных минимумов из очереди
    public static void removeLocalMinima(ConcurrentLinkedQueue<Integer> queue) {
        if (queue.size() < 3) return;

        Object[] elements = queue.toArray();
        queue.clear();

        for (int i = 0; i < elements.length; i++) {
            if (i == 0 || i == elements.length - 1) {
                queue.offer((Integer) elements[i]); // сохраняем первый и последний
            } else {
                int prev = (Integer) elements[i - 1];
                int curr = (Integer) elements[i];
                int next = (Integer) elements[i + 1];

                if (!(curr < prev && curr < next)) {
                    queue.offer(curr); // если не локальный минимум — оставляем
                }
            }
        }
    }
}