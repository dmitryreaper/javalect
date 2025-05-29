package main;

import java.util.concurrent.atomic.AtomicLong;

public class NonBlockingSequenceGenerator {
    private final AtomicLong sequence;

    public NonBlockingSequenceGenerator() {
        this(0);
    }

    public NonBlockingSequenceGenerator(long initialValue) {
        sequence = new AtomicLong(initialValue);
    }

    public long nextValue() {
        return sequence.getAndIncrement();
    }

    public static void main(String[] args) {
        int threadCount = 7;
        NonBlockingSequenceGenerator generator = new NonBlockingSequenceGenerator();

        for (int i = 0; i < threadCount; i++) {
            int threadNumber = i;
            new Thread(() -> {
                System.out.println("Создан поток " + threadNumber);

                // Пример использования генератора в этом потоке
                for (int j = 0; j < 3; j++) {
                    long value = generator.nextValue();
                    System.out.println("Поток " + threadNumber + ": Сгенерировано значение " + value);
                }
            }, "Worker-" + i).start();
        }
    }
}