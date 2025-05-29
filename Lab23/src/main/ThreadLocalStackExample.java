package main;

import java.util.Stack;

public class ThreadLocalStackExample {

    // Класс для хранения данных в стеке в контексте текущего потока
    static class DataContext {
        // ThreadLocal, содержащий стек данных для каждого потока
        private static final ThreadLocal<Stack<String>> threadLocalStack = ThreadLocal.withInitial(Stack::new);

        // Добавление данных в стек
        public static void pushData(String data) {
            threadLocalStack.get().push(data);
        }

        // Удаление последнего элемента из стека
        public static String popData() {
            Stack<String> stack = threadLocalStack.get();
            if (stack.isEmpty()) {
                return null;
            }
            return stack.pop();
        }

        // Получение текущего элемента
        public static String getCurrentData() {
            Stack<String> stack = threadLocalStack.get();
            if (stack.isEmpty()) {
                return null;
            }
            return stack.peek();
        }

        // Очистка стека после использования (для избежания утечек памяти)
        public static void clear() {
            threadLocalStack.get().clear();
        }
    }

    // Метод, моделирующий работу потока
    static class WorkerThread extends Thread {
        private final String name;

        public WorkerThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(name + ": Начинаю работу.");

            DataContext.pushData("Данные 1 - " + name);
            System.out.println(name + ": Добавил в стек: Данные 1");

            DataContext.pushData("Данные 2 - " + name);
            System.out.println(name + ": Добавил в стек: Данные 2");

            System.out.println(name + ": Текущие данные: " + DataContext.getCurrentData());

            System.out.println(name + ": Удаляю данные: " + DataContext.popData());
            System.out.println(name + ": Текущие данные: " + DataContext.getCurrentData());

            DataContext.clear(); // Очищаем данные после завершения
            System.out.println(name + ": Работа завершена.");
        }
    }

    public static void main(String[] args) {
        // Создание и запуск потоков
        WorkerThread thread1 = new WorkerThread("Поток A");
        WorkerThread thread2 = new WorkerThread("Поток B");

        thread1.start();
        thread2.start();

        // Ждём завершения потоков
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Все потоки завершили работу.");
    }
}