package snake.view;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import snake.persistence.HighScore;

/**
 * Table model for displaying high scores in a tabular format.
 */
public class HighScoreTableModel extends AbstractTableModel {
    private final ArrayList<HighScore> highScores;
    private final String[] colName = new String[]{"Difficulty", "Level", "Score"};

    /**
     * Constructs the table model with a list of high scores.
     *
     * @param highScores the list of HighScore objects
     */
    public HighScoreTableModel(ArrayList<HighScore> highScores) {
        this.highScores = highScores;
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return the row count
     */
    @Override
    public int getRowCount() {
        return highScores.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return 3;
    }

    /**
     * Retrieves the value at a specific row and column.
     *
     * @param r the row index
     * @param c the column index
     * @return the value at the specified cell
     */
    @Override
    public Object getValueAt(int r, int c) {
        HighScore h = highScores.get(r);
        if (c == 0) return h.difficulty;
        if (c == 1) return h.level;
        return h.score;
    }

    /**
     * Returns the name of the specified column.
     *
     * @param i the column index
     * @return the column name
     */
    @Override
    public String getColumnName(int i) {
        return colName[i];
    }
}
