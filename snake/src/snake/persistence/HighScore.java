package snake.persistence;

import java.util.Objects;
import snake.model.GameID;

/**
 * Represents a high score for a specific game difficulty and level.
 */
public class HighScore {
    public final String difficulty;
    public final int level;
    public final int score;

    /**
     * Creates a HighScore using a GameID and score.
     *
     * @param gameID the GameID containing difficulty and level
     * @param score  the score value
     */
    public HighScore(GameID gameID, int score) {
        this.difficulty = gameID.difficulty;
        this.level = gameID.level;
        this.score = score;
    }

    /**
     * Creates a HighScore using difficulty, level, and score.
     *
     * @param difficulty the game difficulty
     * @param level      the game level
     * @param score      the score value
     */
    public HighScore(String difficulty, int level, int score) {
        this.difficulty = difficulty;
        this.level = level;
        this.score = score;
    }

    /**
     * Computes the hash code based on difficulty and level.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.difficulty);
        hash = 89 * hash + this.level;
        return hash;
    }

    /**
     * Checks equality based on difficulty and level.
     *
     * @param obj the object to compare
     * @return true if equal; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final HighScore other = (HighScore) obj;
        return this.level == other.level && Objects.equals(this.difficulty, other.difficulty);
    }

    /**
     * Returns a string representation in the format "difficulty-level: score".
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return difficulty + "-" + level + ": " + score;
    }
}
