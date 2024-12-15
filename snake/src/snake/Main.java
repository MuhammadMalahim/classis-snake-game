package snake;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import snake.model.Direction;
import snake.model.Game;
import snake.model.GameID;
import snake.view.Board;
import snake.view.HighScoreWindow;

/**
 * Main application class for the Snake game.
 */
public class Main extends JFrame {

    private final Game game;
    private Board board;
    private final JLabel gameStatLabel;
    private Direction currentDirection;
    private boolean isRunning = false;
    private boolean isDirectionSet = false;
    private Thread gameThread = null;
    private Timer timer;
    private int elapsedTime = 0;

    /**
     * Initializes the main window and game setup.
     *
     * @throws IOException if resources fail to load
     */
    public Main() throws IOException {
        game = new Game();

        setTitle("Snake");
        setSize(600, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        URL url = Main.class.getClassLoader().getResource("snake/res/food.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(url));

        timer = new Timer(1000, e -> {
            elapsedTime++;
            refreshGameStatLabel();
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu menuGame = new JMenu("Play");
        JMenu menuGameLevel = new JMenu("Level");
        JMenu menuGameScale = new JMenu("Scale");
        createGameLevelMenuItems(menuGameLevel);
        createScaleMenuItems(menuGameScale, 1.0, 2.0, 0.5);

        JMenuItem menuHighScores = new JMenuItem(new AbstractAction("Highscore Table") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HighScoreWindow(game.getHighScores(), Main.this);
            }
        });

        JMenuItem menuGameExit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuGame.add(menuGameLevel);
        menuGame.add(menuGameScale);
        menuGame.add(menuHighScores);
        menuGame.addSeparator();
        menuGame.add(menuGameExit);
        menuBar.add(menuGame);
        setJMenuBar(menuBar);

        // Setup layout
        setLayout(new BorderLayout(0, 10));
        gameStatLabel = new JLabel("label");
        add(gameStatLabel, BorderLayout.NORTH);

        // Add game board
        try {
            add(board = new Board(game), BorderLayout.CENTER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Add key listener for controlling the game
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent key) {
                super.keyPressed(key);
                if (!game.isLevelLoaded()) {
                    return;
                }

                int kk = key.getKeyCode();
                Direction newDirection = null;

                switch (kk) {
                    case KeyEvent.VK_A ->
                        newDirection = Direction.LEFT;
                    case KeyEvent.VK_D ->
                        newDirection = Direction.RIGHT;
                    case KeyEvent.VK_W ->
                        newDirection = Direction.UP;
                    case KeyEvent.VK_S ->
                        newDirection = Direction.DOWN;
                    case KeyEvent.VK_ESCAPE ->
                        restartLevel();
                }

                if (newDirection != null) {
                    if (!isDirectionSet) {
                        isDirectionSet = true;
                        currentDirection = newDirection;
                        startGameLoop();
                    } else if (currentDirection != newDirection && !newDirection.isOpposite(currentDirection)) {
                        currentDirection = newDirection;
                    }
                }
            }
        });

        // Finalize setup
        setResizable(false);
        setLocationRelativeTo(null);
        game.loadGame(new GameID("EASY", 1));
        board.setScale(2.0);
        pack();
        refreshGameStatLabel();
        setVisible(true);
    }

    /**
     * Stops the current game thread.
     */
    public void stopGameThread() {
        if (timer != null) {
            timer.stop();
        }
        isRunning = false;
        if (gameThread != null && gameThread.isAlive()) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Starts the game loop in a separate thread.
     */
    private void startGameLoop() {
        stopGameThread();
        isRunning = true;
        elapsedTime = 0;
        timer.start();
        gameThread = new Thread(() -> {
            while (isRunning) {
                if (!isDirectionSet) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                if (game.isGameEnded()) {
                    timer.stop();
                    elapsedTime = 0;
                    SwingUtilities.invokeLater(() -> {
                        String msg = "Game Over!";
                        if (game.isBetterHighScore()) {
                            msg += " Congratulations! You have made the high score!";
                        }
                        JOptionPane.showMessageDialog(
                                Main.this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE
                        );
                    });
                    isRunning = false;
                    resetToLevelOne(game.getGameID().difficulty);
                    break;
                }

                game.step(currentDirection);
                board.repaint();
                refreshGameStatLabel();

                try {
                    Thread.sleep(Math.max(100, 500 - (game.getSpeed() * 20)));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        gameThread.start();
    }

    /**
     * Restarts the current level.
     */
    private void restartLevel() {
        game.loadGame(game.getGameID());
        board.refresh();
        refreshGameStatLabel();
        isDirectionSet = false;
        elapsedTime = 0;
    }

    /**
     * Updates the game status label.
     */
    private void refreshGameStatLabel() {
        String s = "Score: " + game.getScore() + " | Time: " + elapsedTime + "s";
        gameStatLabel.setText(s);
    }

    /**
     * Creates menu items for game levels.
     *
     * @param menu the menu to populate
     */
    private void createGameLevelMenuItems(JMenu menu) {
        for (String s : game.getDifficulties()) {
            JMenu difficultyMenu = new JMenu(s);
            menu.add(difficultyMenu);
            for (Integer i : game.getLevelsOfDifficulty(s)) {
                JMenuItem item = new JMenuItem(new AbstractAction("Level-" + i) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        game.loadGame(new GameID(s, i));
                        board.refresh();
                        pack();
                        refreshGameStatLabel();
                        isDirectionSet = false;
                    }
                });
                difficultyMenu.add(item);
            }
        }
    }

    /**
     * Creates menu items for setting game scale.
     *
     * @param menu the menu to populate
     * @param from starting scale
     * @param to ending scale
     * @param by increment step
     */
    private void createScaleMenuItems(JMenu menu, double from, double to, double by) {
        while (from <= to) {
            final double scale = from;
            JMenuItem item = new JMenuItem(new AbstractAction(from + "x") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (board.setScale(scale)) {
                        pack();
                    }
                }
            });
            menu.add(item);

            if (from == to) {
                break;
            }
            from += by;
            if (from > to) {
                from = to;
            }
        }
    }

    /**
     * Resets the game to level 1 of the given difficulty.
     *
     * @param difficulty the difficulty to reset to
     */
    private void resetToLevelOne(String difficulty) {
        game.loadGame(new GameID(difficulty, 1));
        board.refresh();
        refreshGameStatLabel();
        isDirectionSet = false;
        elapsedTime = 0;
    }

    /**
     * Entry point of the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        try {
            new Main();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
