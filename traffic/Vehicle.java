package traffic;

import java.awt.Rectangle;

abstract class Vehicle {
    protected int x, y;
    protected int speed;
    protected int width;
    protected int height;
    protected int direction;
    protected boolean isStopped = false;
    protected int safeDistance = 5;

    public Vehicle(int x, int y, int speed, int width, int height, int direction) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.direction = direction;
    }

    public abstract void move();

    public void stop() {
        isStopped = true;
    }

    public void resume() {
        isStopped = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isInIntersection(Vehicle other) {
        Rectangle rect1 = new Rectangle(this.x - safeDistance, this.y - safeDistance, 95 + 2 * safeDistance, 50 + 2 * safeDistance);
        Rectangle rect2 = new Rectangle(other.getX() - safeDistance, other.getY() - safeDistance, 95 + 2 * safeDistance, 50 + 2 * safeDistance);
        return rect1.intersects(rect2);
    }
}
