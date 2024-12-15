package snake.model;

import java.util.Objects;

public class GameID {
    public final String difficulty;
    public final int level;

    public GameID(String difficulty, int level) {
        this.difficulty = difficulty;
        this.level = level;
    }
    
    /**
     * Returns a hash code for this GameID based on its difficulty and level.
     *
     * @return a hash code value for this GameID
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.difficulty);
        hash = 29 * hash + this.level;
        return hash;
    }

    /**
     * Determines whether another object is equal to this GameID.
     * Two GameIDs are equal if they have the same difficulty and level.
     *
     * @param obj the object to compare
     * @return true if the given object is equal to this GameID, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GameID other = (GameID) obj;
        return this.level == other.level && Objects.equals(this.difficulty, other.difficulty);
    }
}
