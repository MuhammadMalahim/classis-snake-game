package snake.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import javax.swing.JPanel;
import snake.model.*;
import snake.res.ResourceLoader;

/**
 * The game board panel that renders the game level and elements.
 */
public class Board extends JPanel {
    private Game game;
    private final Image snakeHead, snakeBody, food, wall, rock, empty;
    private double scale;
    private int scaledSize;
    private final int tileSize = 32;

    /**
     * Creates a Board instance and initializes resources.
     *
     * @param g the Game instance
     * @throws IOException if any resource fails to load
     */
    public Board(Game g) throws IOException {
        game = g;
        scale = 1.0;
        scaledSize = (int)(scale * tileSize);

        // Load images
        snakeHead = ResourceLoader.loadImage("snake/res/snake_head.png");
        snakeBody = ResourceLoader.loadImage("snake/res/snake_body.png");
        food = ResourceLoader.loadImage("snake/res/food.png");
        wall = ResourceLoader.loadImage("snake/res/wall.png");
        rock = ResourceLoader.loadImage("snake/res/rock.png");
        empty = ResourceLoader.loadImage("snake/res/empty.png");
    }

    /**
     * Sets the scale for rendering the board.
     *
     * @param scale the new scale factor
     * @return true if refresh succeeds; false otherwise
     */
    public boolean setScale(double scale) {
        this.scale = scale;
        scaledSize = (int)(scale * tileSize);
        return refresh();
    }

    /**
     * Refreshes the board by updating its size and repainting.
     *
     * @return true if the level is loaded; false otherwise
     */
    public boolean refresh() {
        if (!game.isLevelLoaded()) return false;
        Dimension dim = new Dimension(game.getLevelCols() * scaledSize, game.getLevelRows() * scaledSize);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setSize(dim);
        repaint();
        return true;
    }

    /**
     * Paints the board and its elements.
     *
     * @param g the Graphics object used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!game.isLevelLoaded()) return;
        Graphics2D gr = (Graphics2D) g;
        int cols = game.getLevelCols();
        int rows = game.getLevelRows();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Image img = null;
                LevelItem element = game.getItem(y, x);
                switch (element) {
                    case SNAKE_HEAD: img = snakeHead; break;
                    case SNAKE_BODY: img = snakeBody; break;
                    case FOOD: img = food; break;
                    case WALL: img = wall; break;
                    case ROCK: img = rock; break;
                    case EMPTY: img = empty; break;
                }
                if (img != null) {
                    gr.drawImage(img, x * scaledSize, y * scaledSize, scaledSize, scaledSize, null);
                }
            }
        }
    }
}
