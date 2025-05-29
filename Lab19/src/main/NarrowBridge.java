package main;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class NarrowBridge {

    private enum Direction { NORTH, SOUTH }

    private final ReentrantLock lock = new ReentrantLock(true); // true - fair lock
    private final Condition waitToCross = lock.newCondition();

    private int carsOnBridge = 0;
    private Direction currentDirection = Direction.NORTH; // начальное направление

    // Функция, которую вызывает машина перед въездом на мост
    public void enterBridge(Direction direction) throws InterruptedException {
        lock.lock();
        try {
            while (!(carsOnBridge == 0 || currentDirection == direction)) {
                waitToCross.await(); // ждём, пока можно будет въехать
            }
            carsOnBridge++;
            currentDirection = direction;
            System.out.println(Thread.currentThread().getName() + " въехал на мост (" + direction + ")");
        } finally {
            lock.unlock();
        }
    }

    // Функция, которую вызывает машина после выезда с моста
    public void leaveBridge() {
        lock.lock();
        try {
            carsOnBridge--;
            System.out.println(Thread.currentThread().getName() + " покинул мост");
            if (carsOnBridge == 0) {
                // Оповещаем всех, что направление свободно
                waitToCross.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    // Класс автомобиля
    static class Car implements Runnable {
        private final NarrowBridge bridge;
        private final Direction direction;
        private final Random random = new Random();

        public Car(NarrowBridge bridge, Direction direction) {
            this.bridge = bridge;
            this.direction = direction;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(random.nextInt(1000)); // Имитация времени прибытия
                bridge.enterBridge(direction);
                Thread.sleep(random.nextInt(1000)); // Имитация времени пересечения моста
                bridge.leaveBridge();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Точка входа
    public static void main(String[] args) {
        NarrowBridge bridge = new NarrowBridge();

        // Создаем несколько машин
        for (int i = 0; i < 10; i++) {
            Direction dir = i % 2 == 0 ? Direction.NORTH : Direction.SOUTH;
            Thread car = new Thread(new Car(bridge, dir), "Car-" + i);
            car.start();
        }
    }
}