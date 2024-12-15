package snake.model;

public enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

    public final int x, y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Determines if this direction is the opposite of another direction.
     *
     * @param other the direction to compare against
     * @return true if this direction is opposite to the other, false otherwise
     */
    public boolean isOpposite(Direction other) {
        return this.x == -other.x && this.y == -other.y;
    }

    /**
     * Returns the opposite direction of this one.
     *
     * @return the opposite direction
     */
    public Direction opposite() {
        if (this == UP) return DOWN;
        if (this == DOWN) return UP;
        if (this == LEFT) return RIGHT;
        return LEFT; 
    }
}
