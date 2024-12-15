package snake.model;

import java.util.LinkedList;

/**
 * Represents the snake, including its body positions and movement logic.
 */
public class Snake {
    private final LinkedList<Position> body;
    private Direction currentDirection;

    /**
     * Constructs a new snake with a given initial position and direction.
     * The snake body starts with two segments.
     *
     * @param initialPosition the starting position of the snake's head
     * @param initialDirection the initial movement direction of the snake
     */
    public Snake(Position initialPosition, Direction initialDirection) {
        body = new LinkedList<>();
        body.add(initialPosition);
        body.add(initialPosition.translate(initialDirection.opposite()));
        currentDirection = initialDirection;
    }

    // getHead is a getter
    public Position getHead() {
        return body.getFirst();
    }

    // getBody is a getter
    public LinkedList<Position> getBody() {
        return body;
    }

    /**
     * Moves the snake in the specified direction. If the snake should grow,
     * it adds a new head segment without removing the tail; otherwise,
     * it moves forward by adding a head and removing the last segment.
     *
     * @param direction the direction to move the snake
     * @param grow true if the snake should grow this turn, false otherwise
     */
    public void move(Direction direction, boolean grow) {
        if (direction != null && !direction.isOpposite(currentDirection)) {
            currentDirection = direction;
        }
        Position newHead = getHead().translate(currentDirection);
        body.addFirst(newHead);

        if (!grow) {
            body.removeLast();
        }
    }

    /**
     * Checks if the snake's body (excluding the head) occupies the given position.
     *
     * @param position the position to check for collision
     * @return true if the snake's body occupies that position, false otherwise
     */
    public boolean checkCollision(Position position) {
        return body.stream().skip(1).anyMatch(segment -> segment.equals(position));
    }

    // getCurrentDirection is a getter
    public Direction getCurrentDirection() {
        return currentDirection;
    }
}
