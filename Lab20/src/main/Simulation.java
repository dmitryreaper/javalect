package main;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

public class Simulation {

    // Интерфейс поведения объекта
    public interface IBehaviour {
        void update(long time);
        String getType();
        int getX();
        int getY();
    }

    // Класс для хранения данных о позиции
    public static class PositionData {
        public final String sender;
        public final int x;
        public final int y;

        public PositionData(String sender, int x, int y) {
            this.sender = sender;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("(x=%d, y=%d)", x, y);
        }
    }

    // Буфер обмена данными между потоками
    public static class DataExchangeBuffer {
        private final List<PositionData> buffer = new ArrayList<>();
        private final ReentrantLock lock = new ReentrantLock();

        public void sendData(PositionData data) {
            lock.lock();
            try {
                buffer.add(data);
            } finally {
                lock.unlock();
            }
        }

        public PositionData receiveData(String selfName) {
            lock.lock();
            try {
                if (buffer.isEmpty()) return null;

                List<PositionData> others = new ArrayList<>();
                for (PositionData data : buffer) {
                    if (!data.sender.equals(selfName)) {
                        others.add(data);
                    }
                }

                if (others.isEmpty()) {
                    // Если других нет, вернуть свою запись как резервную
                    Random rand = new Random();
                    return new PositionData("SelfFallback", rand.nextInt(800), rand.nextInt(600));
                }

                Collections.shuffle(others);
                return others.get(0);

            } finally {
                lock.unlock();
            }
        }

        public void clear() {
            lock.lock();
            try {
                buffer.clear();
            } finally {
                lock.unlock();
            }
        }
    }

    // Абстрактный класс интеллекта - работает в отдельном потоке
    public abstract static class BaseAI extends Thread {
        protected final IBehaviour entity;
        protected final AtomicBoolean running = new AtomicBoolean(true);
        protected final String name;
        protected final DataExchangeBuffer buffer;

        public BaseAI(IBehaviour entity, String name, DataExchangeBuffer buffer) {
            this.entity = entity;
            this.name = name;
            this.buffer = buffer;
            setDaemon(true);
        }

        public void stopAI() {
            running.set(false);
        }

        public void exchangeData() {
            int x = entity.getX();
            int y = entity.getY();
            buffer.sendData(new PositionData(name, x, y));

            // Небольшая пауза, чтобы все успели отправить данные
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}

            PositionData received = buffer.receiveData(name);
            if (received != null) {
                System.out.printf("Поток %s передал информацию %s и получил информацию %s%n",
                        name, new PositionData(name, x, y), received);
            } else {
                System.out.printf("Поток %s передал информацию %s, но не получил ответа.%n",
                        name, new PositionData(name, x, y));
            }
        }
    }

    // Конкретные типы объектов
    public static class Car implements IBehaviour {
        private int x, y;
        private final int speed;
        private final int maxX;

        public Car(int maxX, int maxY, int speed) {
            this.x = 0;
            this.y = (int) (Math.random() * maxY);
            this.speed = speed;
            this.maxX = maxX;
        }

        @Override
        public void update(long time) {
            x += speed;
            if (x > maxX) x = 0;
        }

        @Override
        public String getType() {
            return "Car";
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    public static class Motorcycle implements IBehaviour {
        private int x, y;
        private final int speed;
        private final int maxY;

        public Motorcycle(int maxX, int maxY, int speed) {
            this.x = (int) (Math.random() * maxX);
            this.y = 0;
            this.speed = speed;
            this.maxY = maxY;
        }

        @Override
        public void update(long time) {
            y += speed;
            if (y > maxY) y = 0;
        }

        @Override
        public String getType() {
            return "Motorcycle";
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    // Класс AI для объектов
    public static class CarAI extends BaseAI {
        public CarAI(Car car, String name, DataExchangeBuffer buffer) {
            super(car, name, buffer);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            while (running.get()) {
                try {
                    Thread.sleep(1000); // Обновляем каждую секунду
                    ((Car) entity).update(System.currentTimeMillis() - startTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public static class MotorcycleAI extends BaseAI {
        public MotorcycleAI(Motorcycle motorcycle, String name, DataExchangeBuffer buffer) {
            super(motorcycle, name, buffer);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            while (running.get()) {
                try {
                    Thread.sleep(1000);
                    ((Motorcycle) entity).update(System.currentTimeMillis() - startTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    // Среда симуляции
    public static class Habitat {
        private final int width = 800;
        private final int height = 600;
        private final int carGenInterval = 3; // секунды
        private final double carProbability = 1.0;
        private final int bikeGenInterval = 3; // секунды
        private final double bikeProbability = 1.0;
        private final int speed = 10;

        private final List<IBehaviour> entities = new ArrayList<>();
        private final List<BaseAI> ais = new ArrayList<>();
        private final DataExchangeBuffer buffer = new DataExchangeBuffer();

        private boolean running = false;
        private long simulationStartTime;
        private int carCount = 0;
        private int bikeCount = 0;

        public void startSimulation() {
            if (running) return;

            running = true;
            simulationStartTime = System.currentTimeMillis();

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

            // Генератор автомобилей
            scheduler.scheduleAtFixedRate(() -> {
                if (Math.random() < carProbability) {
                    Car car = new Car(width, height, speed);
                    String name = "Car-" + (++carCount);
                    entities.add(car);
                    BaseAI ai = new CarAI(car, name, buffer);
                    ais.add(ai);
                    ai.start();
                }
            }, 0, carGenInterval, TimeUnit.SECONDS);

            // Генератор мотоциклов
            scheduler.scheduleAtFixedRate(() -> {
                if (Math.random() < bikeProbability) {
                    Motorcycle bike = new Motorcycle(width, height, speed);
                    String name = "Motorcycle-" + (++bikeCount);
                    entities.add(bike);
                    BaseAI ai = new MotorcycleAI(bike, name, buffer);
                    ais.add(ai);
                    ai.start();
                }
            }, 0, bikeGenInterval, TimeUnit.SECONDS);

            // Обновление экрана
            scheduler.scheduleAtFixedRate(this::update, 0, 1, TimeUnit.SECONDS);

            // Обмен данными между потоками
            scheduler.scheduleAtFixedRate(() -> {
                for (BaseAI ai : ais) {
                    ai.exchangeData();
                }
                buffer.clear(); // Очищаем после обмена
            }, 0, 5, TimeUnit.SECONDS);
        }

        public void stopSimulation() {
            running = false;
            entities.clear();
            for (BaseAI ai : ais) {
                ai.stopAI();
            }
            ais.clear();
            buffer.clear();
        }

        public void update() {
            System.out.println("\n--- Текущее состояние ---");
            for (IBehaviour e : entities) {
                System.out.printf("%s: (%d, %d)%n", e.getType(), e.getX(), e.getY());
            }
            System.out.printf("Время симуляции: %.1f сек%n", (System.currentTimeMillis() - simulationStartTime) / 1000.0);
        }

        public void printFinalStats() {
            int cars = 0, bikes = 0;
            for (IBehaviour e : entities) {
                if (e instanceof Car) cars++;
                else if (e instanceof Motorcycle) bikes++;
            }
            System.out.println("\n--- Статистика завершения ---");
            System.out.println("Количество автомобилей: " + cars);
            System.out.println("Количество мотоциклов: " + bikes);
            System.out.printf("Общее время симуляции: %.1f сек%n", (System.currentTimeMillis() - simulationStartTime) / 1000.0);
        }

        public double getElapsedTime() {
            return (System.currentTimeMillis() - simulationStartTime) / 1000.0;
        }
    }

    // Основная программа
    public static void main(String[] args) {
        Habitat habitat = new Habitat();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Для управления:");
        System.out.println("S - Запустить симуляцию");
        System.out.println("E - Остановить симуляцию");
        System.out.println("T - Показать текущее время");

        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "S":
                    System.out.println("Симуляция начата.");
                    habitat.startSimulation();
                    break;
                case "E":
                    habitat.stopSimulation();
                    habitat.printFinalStats();
                    System.out.println("Введите S для повторного запуска или X для выхода.");
                    break;
                case "T":
                    System.out.printf("Текущее время симуляции: %.1f сек%n", habitat.getElapsedTime());
                    break;
                case "X":
                    habitat.stopSimulation();
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неизвестная команда. Доступно: S, E, T, X");
            }
        }
    }
}