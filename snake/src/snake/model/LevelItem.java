package snake.model;

/**
 * Enum representing the different types of items that can appear on a game level.
 */
public enum LevelItem {
    /** A wall or barrier in the level. */
    WALL('#'),
    /** The head of the snake. */
    SNAKE_HEAD('@'),
    /** The body of the snake. */
    SNAKE_BODY('O'),
    /** Food that the snake can eat. */
    FOOD('F'),
    /** A rock or obstacle in the level. */
    ROCK('R'),
    /** An empty space in the level. */
    EMPTY(' ');

    /**
     * Constructs a LevelItem with its corresponding character representation.
     * 
     * @param representation the character representing this level item
     */
    LevelItem(char representation) {
        this.representation = representation;
    }

    /** The character representation of the level item. */
    public final char representation;
}
