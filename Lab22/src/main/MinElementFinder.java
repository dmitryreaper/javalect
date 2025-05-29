package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MinElementFinder {

    // Внутренняя задача, которая ищет минимум в подмассиве
    static class MinTask implements Callable<Integer> {
        private final int[] array;
        private final int start;
        private final int end;

        public MinTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        public Integer call() {
            int min = array[start];
            for (int i = start + 1; i < end; i++) {
                if (array[i] < min) {
                    min = array[i];
                }
            }
            return min;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Создаем большой массив
        int[] array = new int[10_000_000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * 100_000);
        }

        // Число потоков
        int numThreads = 4;

        // Создаем ExecutorService с фиксированным количеством потоков
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Список Future для хранения результатов задач
        List<Future<Integer>> futures = new ArrayList<>();

        // Длина блока
        int chunkSize = array.length / numThreads;

        // Разбиваем массив на блоки и отправляем задачи на исполнение
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? array.length : start + chunkSize;

            Callable<Integer> task = new MinTask(array, start, end);
            Future<Integer> future = executor.submit(task);
            futures.add(future);
        }

        // Собираем результаты
        int globalMin = Integer.MAX_VALUE;
        for (Future<Integer> future : futures) {
            int min = future.get(); // Блокирующий вызов до завершения задачи
            if (min < globalMin) {
                globalMin = min;
            }
        }

        // Завершаем работу пула
        executor.shutdown();

        // Выводим результат
        System.out.println("Минимальный элемент в массиве: " + globalMin);
    }
}