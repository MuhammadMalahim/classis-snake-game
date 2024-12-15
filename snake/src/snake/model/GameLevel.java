package snake.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a single game level, including its layout, snake,
 * current score, and logic for movement and game end conditions.
 */
public class GameLevel {

    /** The unique identifier for this game level. */
    public final GameID gameID;
    /** The number of rows in the level. */
    public final int rows;
    /** The number of columns in the level. */
    public final int cols;
    /** The grid representing the level layout. */
    public final LevelItem[][] level;
    /** The snake currently in this level. */
    public Snake snake;
    /** The current score. */
    public int score;
    /** The current speed of the snake. */
    public int speed = 1;

    /**
     * Returns the current score.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Constructs a new GameLevel from given rows of text, an ID, and an initial speed.
     *
     * @param gameLevelRows the textual rows defining the level
     * @param gameID the unique ID of this level
     * @param speed the initial speed of the snake
     */
    public GameLevel(ArrayList<String> gameLevelRows, GameID gameID, int speed) {
        this.gameID = gameID;
        this.speed = speed;

        int c = 0;
        for (String s : gameLevelRows) {
            if (s.length() > c) c = s.length();
        }
        rows = gameLevelRows.size();
        cols = c;
        level = new LevelItem[rows][cols];
        score = 0;

        // Initialize the level and snake from the provided layout
        for (int i = 0; i < rows; i++) {
            String s = gameLevelRows.get(i);
            for (int j = 0; j < s.length(); j++) {
                switch (s.charAt(j)) {
                    case '#': level[i][j] = LevelItem.WALL; break;
                    case '@': 
                        snake = new Snake(new Position(j, i), Direction.RIGHT);
                        level[i][j] = LevelItem.EMPTY; 
                        break;
                    case 'O': level[i][j] = LevelItem.SNAKE_BODY; break;
                    case 'F': level[i][j] = LevelItem.FOOD; break;
                    case 'R': level[i][j] = LevelItem.ROCK; break;
                    default:  level[i][j] = LevelItem.EMPTY; break;
                }
            }
            for (int j = s.length(); j < cols; j++) {
                level[i][j] = LevelItem.EMPTY;
            }
        }

        updateSnakeInLevel();
    }

    /**
     * Constructs a copy of another GameLevel.
     *
     * @param gl the GameLevel to copy
     */
    public GameLevel(GameLevel gl) {
        gameID = gl.gameID;
        speed = gl.speed;
        rows = gl.rows;
        cols = gl.cols;
        snake = new Snake(gl.snake.getHead(), gl.snake.getCurrentDirection());
        level = new LevelItem[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(gl.level[i], 0, level[i], 0, cols);
        }

        updateSnakeInLevel();
    }

    /**
     * Updates the level array to match the snake's current position.
     */
    private void updateSnakeInLevel() {
        // Clear old snake positions
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (level[i][j] == LevelItem.SNAKE_BODY || level[i][j] == LevelItem.SNAKE_HEAD) {
                    level[i][j] = LevelItem.EMPTY;
                }
            }
        }

        // Set new snake positions
        for (Position p : snake.getBody()) {
            if (p.y >= 0 && p.y < rows && p.x >= 0 && p.x < cols) {
                level[p.y][p.x] = p.equals(snake.getHead()) ? LevelItem.SNAKE_HEAD : LevelItem.SNAKE_BODY;
            }
        }
    }

    /**
     * Checks if the game has ended (snake hit a wall, rock, or itself).
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameEnded() {
        Position head = snake.getHead();
        return (level[head.y][head.x] == LevelItem.WALL 
             || level[head.y][head.x] == LevelItem.ROCK
             || snake.checkCollision(head));
    }

    /**
     * Checks if the given position is free (empty or contains food).
     *
     * @param p the position to check
     * @return true if free, false otherwise
     */
    public boolean isFree(Position p) {
        LevelItem li = level[p.y][p.x];
        return (li == LevelItem.EMPTY || li == LevelItem.FOOD);
    }

    /**
     * Moves the snake in the given direction. If the snake eats food,
     * it grows and the score and speed increase. If the game ends
     * as a result of the move, returns false.
     *
     * @param d the direction to move
     * @return true if the move succeeded, false if the game ended
     */
    public boolean moveSnake(Direction d) {
        if (isGameEnded()) return false;

        boolean grow = (level[snake.getHead().translate(d).y][snake.getHead().translate(d).x] == LevelItem.FOOD);
        snake.move(d, grow);

        if (isGameEnded()) return false;

        if (grow) {
            score++;
            speed++;
            placeFood();
        }

        updateSnakeInLevel();
        return true;
    }

    /**
     * Places food at a random empty location in the level.
     */
    private void placeFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(cols);
            y = rand.nextInt(rows);
        } while (level[y][x] != LevelItem.EMPTY);

        level[y][x] = LevelItem.FOOD;
    }

    /**
     * Prints the current level layout to the console.
     */
    public void printLevel() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(level[i][j].representation);
            }
            System.out.println();
        }
    }
}
