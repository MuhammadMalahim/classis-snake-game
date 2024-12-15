package snake.view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import snake.persistence.HighScore;

/**
 * A dialog window displaying the high scores in a sortable table.
 */
public class HighScoreWindow extends JDialog {
    private final JTable table;

    /**
     * Constructs a new HighScoreWindow.
     *
     * @param highScores the list of high scores to display
     * @param parent the parent frame of this dialog
     */
    public HighScoreWindow(ArrayList<HighScore> highScores, JFrame parent) {
        super(parent, true);
        table = new JTable(new HighScoreTableModel(highScores));
        table.setFillsViewportHeight(true);

        // Enable sorting for the table
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Sort by difficulty
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING)); // Sort by level
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);

        add(new JScrollPane(table)); // Add the table to a scroll pane
        setSize(400, 400);
        setTitle("HighScore Table");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
