package traffic;

class Motorcycle extends Vehicle {
    public Motorcycle(int x, int y, int speed, int width, int height, int direction) {
        super(x, y, speed + 10, width, height, direction);
    }

    @Override
    public void move() {
        if (isStopped) {
            return;
        }
        switch (direction) {
            case 0:
                x += speed;
                break;
            case 1:
                y += speed;
                break;
            case 2:
                x -= speed;
                break;
            case 3:
                y -= speed;
                break;
        }
    }
}
