package snake.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import snake.persistence.Database;
import snake.persistence.HighScore;
import snake.res.ResourceLoader;

/**
 * Represents the overall game, including levels, high scores, and gameplay logic.
 */
public class Game {
    private final HashMap<String, HashMap<Integer, GameLevel>> gameLevels;
    private GameLevel gameLevel = null;
    private final Database database;
    private boolean isBetterHighScore = false;
    public int initialSpeed = 10;  
    

    /**
     * Initializes the game by loading levels and high scores from the database.
     */
    public Game() {
        gameLevels = new HashMap<>();
        database = new Database();
        readLevels();
    }

     /**
     * Loads a specified game level.
     *
     * @param gameID the identifier of the level to load
     */
    public void loadGame(GameID gameID){
        gameLevel = new GameLevel(gameLevels.get(gameID.difficulty).get(gameID.level));
        isBetterHighScore = false;
    }
    
    /**
     * Returns the identifier of the next level, if it exists.
     *
     * @param currentID the current level's identifier
     * @return the identifier of the next level, or null if no further levels exist
     */
    public GameID getNextLevelID(GameID currentID) {
        HashMap<Integer, GameLevel> levelsOfDifficulty = gameLevels.get(currentID.difficulty);
        if (levelsOfDifficulty == null) return null;

        int nextLevelNumber = currentID.level + 1;
        return levelsOfDifficulty.containsKey(nextLevelNumber)
            ? new GameID(currentID.difficulty, nextLevelNumber)
            : null;
    }
    
    /**
     * Prints the current game level's layout to the console.
     */
    public void printGameLevel(){ gameLevel.printLevel(); }
    
    /**
     * Processes a single step in the game by moving the snake.
     *
     * @param direction the direction to move the snake
     * @return true if the move was successful, false if the game has ended
     */
    public boolean step(Direction direction) {
        if (gameLevel == null) return false;

        boolean stepped = gameLevel.moveSnake(direction);

        if (gameLevel.isGameEnded()) {
            GameID id = gameLevel.gameID;
            int score = gameLevel.getScore();
            isBetterHighScore = database.storeHighScore(id, score);
        }

        return stepped;
    }

    public Collection<String> getDifficulties(){ return gameLevels.keySet(); }
    
    public Collection<Integer> getLevelsOfDifficulty(String difficulty){
        if (!gameLevels.containsKey(difficulty)) return null;
        return gameLevels.get(difficulty).keySet();
    }
    
    public boolean isLevelLoaded(){ return gameLevel != null; }
    public int getLevelRows(){ return gameLevel.rows; }
    public int getLevelCols(){ return gameLevel.cols; }
    public int getScore(){ return (gameLevel != null) ? gameLevel.getScore(): 0; }
    public LevelItem getItem(int row, int col){ return gameLevel.level[row][col]; }
    public GameID getGameID(){ return (gameLevel != null) ? gameLevel.gameID : null; }
    public boolean isGameEnded(){ return (gameLevel != null && gameLevel.isGameEnded()); }
    public boolean isBetterHighScore(){ return isBetterHighScore; }
    public int getSpeed(){ return gameLevel.speed; }
    public Position getPlayerPos(){ // MAKE IT ~IMMUTABLE
        return new Position(gameLevel.snake.getHead().x, gameLevel.snake.getHead().y); 
    }
    public ArrayList<HighScore> getHighScores() { return database.getHighScores(); }

    /**
     * Reads and initializes game levels from a resource file.
     */
    private void readLevels(){
        InputStream is;
        is = ResourceLoader.loadResource("snake/res/levels.txt");
        
        try (Scanner sc = new Scanner(is)){
            String line = readNextLine(sc);
            ArrayList<String> gameLevelRows = new ArrayList<>();
            
            while (!line.isEmpty()){
                GameID id = readGameID(line);
                if (id == null) return;

                gameLevelRows.clear();
                line = readNextLine(sc);
                while (!line.isEmpty() && line.trim().charAt(0) != ';'){
                    gameLevelRows.add(line);                    
                    line = readNextLine(sc);
                }
                addNewGameLevel(new GameLevel(gameLevelRows, id, initialSpeed));
            }
        } catch (Exception e){
            System.out.println("Exception while reading levels.");
        }
    }
    
    /**
     * Adds a new level to the game.
     *
     * @param gameLevel the level to add
     */
    private void addNewGameLevel(GameLevel gameLevel){
        HashMap<Integer, GameLevel> levelsOfDifficulty;
        if (gameLevels.containsKey(gameLevel.gameID.difficulty)){
            levelsOfDifficulty = gameLevels.get(gameLevel.gameID.difficulty);
            levelsOfDifficulty.put(gameLevel.gameID.level, gameLevel);
        } else {
            levelsOfDifficulty = new HashMap<>();
            levelsOfDifficulty.put(gameLevel.gameID.level, gameLevel);
            gameLevels.put(gameLevel.gameID.difficulty, levelsOfDifficulty);
                    

        }
        database.storeHighScore(gameLevel.gameID, 0);
    }
  
    /**
     * Reads the next non-empty line from the scanner.
     *
     * @param sc the scanner
     * @return the next non-empty line as a string
     */
    private String readNextLine(Scanner sc){
        String line = "";
        while (sc.hasNextLine() && line.trim().isEmpty()){
            line = sc.nextLine();
        }
        return line;
    }
    
    /**
     * Parses a GameID from a line of text.
     *
     * @param line the line containing a GameID definition
     * @return the parsed GameID, or null if parsing fails
     */
    private GameID readGameID(String line){
        line = line.trim();
        if (line.isEmpty() || line.charAt(0) != ';') return null;
        Scanner s = new Scanner(line);
        s.next(); 
        if (!s.hasNext()) return null;
        String difficulty = s.next().toUpperCase();
        if (!s.hasNextInt()) return null;
        int id = s.nextInt();
        return new GameID(difficulty, id);
    }    
}
