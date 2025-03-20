package traffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;

class Habitat {
    private final List<Vehicle> vehicles = new ArrayList<>();
    private int width, height;
    private boolean isSimulationRunning = false;
    private long simulationTime = 0;
    private int carCount = 0, motorcycleCount = 0;
    private int N1, N2, V;
    private double P1, P2;
    private JLabel statsLabel;
    private int maxCars = 20;
    private int maxMotorcycles = 10;
    private int[] motorcycleLanes = {400, 500};
    private Random random = new Random();
    public boolean showStats = false;

    public Habitat(int width, int height, int N1, int N2, int V, double P1, double P2, JLabel statsLabel) {
        this.width = width;
        this.height = height;
        this.N1 = N1;
        this.N2 = N2;
        this.V = V;
        this.P1 = P1;
        this.P2 = P2;
        this.statsLabel = statsLabel;
    }

    public synchronized void startSimulation() {
        System.out.println("Simulation started");
        isSimulationRunning = true;
        showStats = false;
        statsLabel.setText("");
    }

    public void stopSimulation() {
        System.out.println("Simulation stopped");
        isSimulationRunning = false;
        vehicles.clear();
        String statsText = "<html><font color='blue'>Время симуляции:</font> " + (simulationTime / 10) + " сек<br>"
                + "<font color='green'>Автомобили:</font> " + this.carCount + "<br>"
                + "<font color='red'>Мотоциклы:</font> " + this.motorcycleCount + "</html>";
        statsLabel.setText(statsText);
        showStats = true;
    }

    public synchronized void update(long deltaTime) {
        if (!isSimulationRunning) {
            return;
        }
        simulationTime += deltaTime;
        generateVehicles();
        for (Vehicle vehicle : vehicles) {
            boolean shouldStop = false;
            for (Vehicle other : vehicles) {
                if (vehicle != other && vehicle.isInIntersection(other)) {
                    shouldStop = true;
                    break;
                }
            }
            if (shouldStop) {
                vehicle.stop();
            } else {
                vehicle.resume();
            }
            vehicle.move();
        }
        vehicles.removeIf(vehicle -> {
            if (vehicle instanceof Car) {
                return vehicle.getX() > width || vehicle.getX() < 0;
            } else if (vehicle instanceof Motorcycle) {
                return vehicle.getY() > height || vehicle.getY() < 0;
            }
            return false;
        });
    }

    private void generateVehicles() {
        int currentCars = 0, currentMotorcycles = 0;
        for (Vehicle vehicle : vehicles) {
            if (vehicle instanceof Car) {
                currentCars++;
            } else if (vehicle instanceof Motorcycle) {
                currentMotorcycles++;
            }
        }
        if (simulationTime % N1 == 0 && Math.random() < P1 && currentCars < maxCars) {
            int x = 0;
            int y = 300 + 20 + random.nextInt(200);
            boolean canSpawn = true;
            for (Vehicle vehicle : vehicles) {
                if (vehicle.isInIntersection(new Car(x, y, V, width, height, 0))) {
                    canSpawn = false;
                    break;
                }
            }
            if (canSpawn) {
                vehicles.add(new Car(x, y, V, width, height, 0));
                carCount++;
            }
        }
        if (simulationTime % N2 == 0 && Math.random() < P2 && currentMotorcycles < maxMotorcycles) {
            int lane = motorcycleLanes[random.nextInt(motorcycleLanes.length)];
            int x = 600 + random.nextInt(300);
            int y = 0;
            boolean canSpawn = true;
            for (Vehicle vehicle : vehicles) {
                if (vehicle.isInIntersection(new Motorcycle(x, y, V, width, height, 1))) {
                    canSpawn = false;
                    break;
                }
            }
            if (canSpawn) {
                vehicles.add(new Motorcycle(x, y, V, width, height, 1));
                motorcycleCount++;
            }
        }
    }

    public synchronized List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public synchronized boolean isSimulationRunning() {
        return isSimulationRunning;
    }

    public synchronized long getSimulationTime() {
        return simulationTime;
    }

    public synchronized int getCarCount() {
        return carCount;
    }

    public synchronized int getMotorcycleCount() {
        return motorcycleCount;
    }
}
