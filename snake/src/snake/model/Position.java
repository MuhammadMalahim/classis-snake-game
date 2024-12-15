package snake.model;

public class Position {
    public int x;
    public int y;

    /**
     * Constructs a new Position.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a new Position translated by the given direction.
     * 
     * @param direction the direction to translate by
     * @return a new translated Position
     */
    public Position translate(Direction direction) {
        return new Position(x + direction.x, y + direction.y);
    }

    /**
     * Checks if this position is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the object is a Position with the same coordinates, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    /**
     * Returns a hash code value for this position, based on its coordinates.
     *
     * @return a hash code value for this position
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
