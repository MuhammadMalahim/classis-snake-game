package snake.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import snake.model.GameID;

/**
 * Manages the persistence of high scores in a database.
 */
public class Database {
    private final String tableName = "highscore";
    private final Connection conn;
    private final HashMap<GameID, Integer> highScores;

    /**
     * Initializes a new Database connection and loads high scores.
     */
    public Database() {
        Connection c = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost/snake_db?"
                    + "serverTimezone=UTC&user=root&password=malahim123");
        } catch (Exception ex) {
            System.out.println("No connection");
        }
        this.conn = c;
        highScores = new HashMap<>();
        loadHighScores();
    }

    /**
     * Stores a new high score in memory and optionally in the database if applicable.
     * 
     * @param id the game identifier for which to store the score
     * @param newScore the new score to store
     * @return true if the score was successfully updated in the database, false otherwise
     */
    public boolean storeHighScore(GameID id, int newScore) {
        return mergeHighScores(id, newScore, newScore > 0);
    }

    /**
     * Retrieves all high scores from memory as a list.
     * 
     * @return a list of high scores
     */
    public ArrayList<HighScore> getHighScores() {
        ArrayList<HighScore> scores = new ArrayList<>();
        for (GameID id : highScores.keySet()) {
            HighScore h = new HighScore(id, highScores.get(id));
            scores.add(h);
            System.out.println(h);
        }
        return scores;
    }

    /**
     * Loads high scores from the database into memory.
     */
    private void loadHighScores() {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            while (rs.next()) {
                String diff = rs.getString("Difficulty");
                int level = rs.getInt("GameLevel");
                int score = rs.getInt("Score");
                GameID id = new GameID(diff, level);
                mergeHighScores(id, score, false);
            }
        } catch (Exception e) {
            System.out.println("loadHighScores error: " + e.getMessage());
        }
    }

    /**
     * Merges a new score into memory, optionally updating the database.
     * 
     * @param id the game identifier for the score
     * @param score the new score
     * @param store true if the score should also be stored in the database
     * @return true if the merge was successful, false otherwise
     */
    private boolean mergeHighScores(GameID id, int score, boolean store) {
        boolean doUpdate = true;
        if (highScores.containsKey(id)) {
            int oldScore = highScores.get(id);
            doUpdate = (score > oldScore || oldScore == 0);
        }
        if (doUpdate) {
            highScores.put(id, score);
            if (store) {
                return storeToDatabase(id, score) > 0;
            }
            return true;
        }
        return false;
    }

    /**
     * Stores a high score in the database or updates it if it already exists.
     * 
     * @param id the game identifier for the score
     * @param score the new score to store
     * @return the number of rows affected in the database
     */
    private int storeToDatabase(GameID id, int score) {
        try (Statement stmt = conn.createStatement()) {
            String s = "INSERT INTO " + tableName +
                       " (Difficulty, GameLevel, Score) " +
                       "VALUES('" + id.difficulty + "'," + id.level + "," + score +
                       ") ON DUPLICATE KEY UPDATE Score=" + score;
            return stmt.executeUpdate(s);
        } catch (Exception e) {
            System.out.println("storeToDatabase error");
        }
        return 0;
    }
}
