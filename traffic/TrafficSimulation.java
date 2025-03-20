package traffic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TrafficSimulation extends JFrame {
    private Habitat habitat;
    private boolean showTime = true;
    private JLabel statsLabel;
    private Image backgroundImage;
    private Image carImage;
    private Image motorcycleImage;
    private DrawingPanel drawingPanel;
    private Thread simulationThread;

    public TrafficSimulation() {
        setTitle("Traffic Simulation");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        // Загрузка фонового изображения
        try {
            backgroundImage = new ImageIcon("background.jpg").getImage();
        } catch (Exception e) {
            System.err.println("Фоновое изображение не найдено: " + e.getMessage());
        }

        // Загрузка изображений транспортных средств
        try {
            Image originalCarImage = new ImageIcon("Cars.png").getImage();
            carImage = originalCarImage.getScaledInstance(100, 60, Image.SCALE_SMOOTH);
            Image oriImageMoto = new ImageIcon("motorcycle.png").getImage();
            motorcycleImage = oriImageMoto.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Ошибка изображений транспортных средств: " + e.getMessage());
        }

        // Создаем JLabel для отображения статистики
        statsLabel = new JLabel("");
        statsLabel.setBounds(100, 500, 780, 100);
        statsLabel.setForeground(Color.BLACK);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(statsLabel);

        habitat = new Habitat(1600, 900, 5, 5, 10, 0.07, 0.05, statsLabel);

        // Создаем панель для отрисовки
        drawingPanel = new DrawingPanel();
        drawingPanel.setBounds(0, 0, 1600, 900);
        add(drawingPanel);

        // Создаем и добавляем панель управления
        JPanel controlPanel = createControlPanel();
        controlPanel.setBounds(1300, 10, 200, 150);
        controlPanel.setOpaque(true);
        controlPanel.setBackground(new Color(200, 200, 200, 10));
        add(controlPanel);

        // Таймер для обновления GUI
        Timer timer = new Timer(30, e -> {
            drawingPanel.repaint();
            controlPanel.repaint();
            statsLabel.repaint();
        });
        timer.start();

        addKeyBindings();
    }

    synchronized JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1, 5, 5));

        // Кнопка "Start Simulation"
        JButton startButton = new JButton("Start (S)");
        startButton.setFocusable(false);

        // Кнопка "Stop Simulation"
        JButton stopButton = new JButton("Stop (E)");
        stopButton.setFocusable(false);

        // Кнопка "Toggle Time Display"
        JButton toggleTimeButton = new JButton("Toggle Time (T)");
        toggleTimeButton.setFocusable(false);

        // Добавляем кнопки на панель
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(toggleTimeButton);

        // Добавляем обработчики событий
        startButton.addActionListener(e -> {
            if (simulationThread == null || !simulationThread.isAlive()) {
                simulationThread = new Thread(() -> {
                    habitat.startSimulation();
                    while (habitat.isSimulationRunning()) {
                        synchronized (habitat) {
                            habitat.update(1);
                        }
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                simulationThread.start();
            }
        });

        stopButton.addActionListener(e -> habitat.stopSimulation());

        toggleTimeButton.addActionListener(e -> {
            showTime = !showTime;
            drawingPanel.repaint();
        });

        return controlPanel;
    }

    private void addKeyBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // Привязка клавиши S
        inputMap.put(KeyStroke.getKeyStroke("S"), "startSimulation");
        actionMap.put("startSimulation", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (simulationThread == null || !simulationThread.isAlive()) {
                    simulationThread = new Thread(() -> {
                        habitat.startSimulation();
                        while (habitat.isSimulationRunning()) {
                            synchronized (habitat) {
                                habitat.update(1);
                            }
                            try {
                                Thread.sleep(60);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    simulationThread.start();
                }
            }
        });

        // Привязка клавиши E
        inputMap.put(KeyStroke.getKeyStroke("E"), "stopSimulation");
        actionMap.put("stopSimulation", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                habitat.stopSimulation();
                drawingPanel.repaint();
            }
        });

        // Привязка клавиши T
        inputMap.put(KeyStroke.getKeyStroke("T"), "toggleTime");
        actionMap.put("toggleTime", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTime = !showTime;
                drawingPanel.repaint();
            }
        });
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Отрисовка фона
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // Синхронизированный доступ к списку транспортных средств
            synchronized (habitat) {
                for (Vehicle vehicle : habitat.getVehicles()) {
                    if (vehicle instanceof Car) {
                        if (carImage != null) {
                            g.drawImage(carImage, vehicle.getX(), vehicle.getY(), this);
                        }
                    } else if (vehicle instanceof Motorcycle) {
                        if (motorcycleImage != null) {
                            g.drawImage(motorcycleImage, vehicle.getX(), vehicle.getY(), this);
                        }
                    }
                }
            }

            // Отображение времени симуляции
            if (showTime && habitat.isSimulationRunning()) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("Time: " + habitat.getSimulationTime() + " msec", 10, 50);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrafficSimulation simulation = new TrafficSimulation();
            simulation.setVisible(true);
        });
    }
}
