package minesweeper.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.*;

public class App extends JFrame {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final int SIZE_FACTOR = 50;
    private static final int EMOJI_FONT_SIZE = 10;
    private static final int FONT_SIZE = 15;

    private JPanel gamePanel;
    private JPanel gameInfoPanel;
    private JButton[][] gameButtons;
    private JButton newGameButton;
    private JLabel timerLabel;
    private JLabel minesLeftLabel;
    private Timer gameTimer;

    private int elapsedSeconds;
    private Font buttonFont;
    private SoundMan soundManager;

    private GameMan game;
    private Difficulty difficulty;

    static {
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) logDir.mkdirs();
            FileHandler handler = new FileHandler("logs/applog.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public App() {
        try (InputStream is = getClass().getResourceAsStream("/fonts/Bytesized-Regular.ttf")) {
            buttonFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, FONT_SIZE);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(buttonFont);
        } catch (IOException | FontFormatException e) {
            logger.log(Level.WARNING, "Utilizing Fallback Font", e);
            buttonFont = new Font("SansSerif", Font.PLAIN, FONT_SIZE);
        }

        UIManager.put("Label.font", buttonFont);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.disabledText", Color.BLACK);

        soundManager = new SoundMan();
        for (Audios audio : Audios.values()) {
            soundManager.load(audio, audio.path);
        }

        setTitle("Mine Sweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initGame();
    }

    private void initGame() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        chooseDifficulty();

        game = new GameMan(difficulty);
        int size = game.getSize();
        setSize(size * SIZE_FACTOR, size * SIZE_FACTOR);

        gameInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        gamePanel = new JPanel(new GridLayout(size, size));

        elapsedSeconds = 0;
        gameTimer = new Timer(1000, e -> timerLabel.setText(String.valueOf(++elapsedSeconds)));

        initInfoPanel();
        initButtons();

        add(gameInfoPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        setVisible(true);
        gameTimer.start();
    }

    private void chooseDifficulty() {
        Object choice = JOptionPane.showInputDialog(
            this,
            "Choose a difficulty",
            "Difficulty",
            JOptionPane.QUESTION_MESSAGE,
            null,
            Difficulty.values(),
            Difficulty.EASY
        );
        if (choice == null) System.exit(0);
        difficulty = (Difficulty) choice;
    }

    private void initInfoPanel() {
        timerLabel = new JLabel("0");
        minesLeftLabel = new JLabel(String.valueOf(difficulty.mines));
        newGameButton = new JButton("😄");
        newGameButton.addActionListener(e -> restartGame());

        gameInfoPanel.add(timerLabel);
        gameInfoPanel.add(newGameButton);
        gameInfoPanel.add(minesLeftLabel);
    }

    private void initButtons() {
        int size = game.getSize();
        gameButtons = new JButton[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                JButton btn = new JButton();
                btn.setFont(buttonFont);
                Cords fcords = new Cords(x, y);
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) handleFlag(fcords);
                        else if (SwingUtilities.isLeftMouseButton(e)) handleReveal(fcords);
                    }
                });
                gameButtons[x][y] = btn;
                gamePanel.add(btn);
            }
        }
    }

    private void handleReveal(Cords cords) {
        if (!game.reveal(cords)) return;
        soundManager.play(Audios.CLICK);

        if (game.isMine(cords)) {
            showMine(cords);
            gameOver();
            return;
        }

        updateButton(cords);

        if (game.hasWon()) winGame();
        else if (game.getCell(cords) == 0) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = cords.x() + dx, ny = cords.y() + dy;
                    if (nx >= 0 && ny >= 0 && nx < game.getSize() && ny < game.getSize()) {
                        handleReveal(new Cords(nx, ny));
                    }
                }
            }
        }
    }

    private void handleFlag(Cords cords) {
        if (game.isRevealed(cords)) return;
        soundManager.play(Audios.FLAG);
        game.toggleFlag(cords);

        JButton btn = gameButtons[cords.x()][cords.y()];
        if (game.isFlagged(cords)) {
            btn.setText("F");
            btn.setForeground(Color.RED);
        } else {
            btn.setText("");
        }

        minesLeftLabel.setText(String.valueOf(difficulty.mines - game.getFlagsPlaced()));
    }

    private void updateButton(Cords cords) {
        JButton btn = gameButtons[cords.x()][cords.y()];
        btn.setEnabled(false);
        int value = game.getCell(cords);
        if (value > 0) {
            btn.setText(String.valueOf(value));
        } else {
            btn.setText(" ");
        }

        switch (value) {
            case 0:
                btn.setBackground(Color.getHSBColor(293f / 360f, 0.10f, 1f));
                break;
            case 1:
                btn.setBackground(Color.getHSBColor(195f / 350f, 0.5f, 1f));
                break;
            case 2:
                btn.setBackground(Color.getHSBColor(29f / 360f, 0.5f, 1f));
                break;
            case 3:
                btn.setBackground(Color.getHSBColor(0f / 360f, 0.5f, 1f));
                break;
            case 4:
                btn.setBackground(Color.getHSBColor(125f / 360f, 0.5f, 1f));
                break;
            case 5:
                 btn.setBackground(Color.getHSBColor(166f / 360f, 0.5f, 1f));
                 break;
            case 6:
                   btn.setBackground(Color.getHSBColor(202f / 360f, 0.5f, 1f));
                break;
            case 7:
                btn.setBackground(Color.getHSBColor(268f / 360f, 0.5f, 1f));
                 break;
            case 8:
                btn.setBackground(Color.getHSBColor(310f / 360f, 0.5f, 1f));
                break;
            default:
                btn.setBackground(Color.getHSBColor(240f / 360f, 0.5f, 0f));
                break;
        }
    }

    private void showMine(Cords cords) {
        for (int i = 0; i < game.getSize(); i++) {
            for (int j = 0; j < game.getSize(); j++) {
                JButton btn = gameButtons[i][j];
                if (game.isMine(new Cords(i, j))) {
                    btn.setText("💣");
                    btn.setFont(new Font("SansSerif", Font.PLAIN, EMOJI_FONT_SIZE));
                }
                btn.setEnabled(false);
            }
        }
    }

    private void gameOver() {
        soundManager.play(Audios.LOSE);
        gameTimer.stop();
        newGameButton.setText("😡");
        JOptionPane.showMessageDialog(this, "You Lose!", "Failure", JOptionPane.INFORMATION_MESSAGE);
        restartGame();
    }

    private void winGame() {
        soundManager.play(Audios.WIN);
        gameTimer.stop();
        newGameButton.setText("😎");
        JOptionPane.showMessageDialog(this, "You Win!", "Victory", JOptionPane.INFORMATION_MESSAGE);
        restartGame();
    }

    private void restartGame() {
        soundManager.play(Audios.NEW_GAME);
        gameTimer.stop();
        initGame();
        revalidate();
        repaint();
    }

    public enum Difficulty {
        EASY(10, 10),
        MEDIUM(20, 15),
        HARD(40, 20);
        final int mines, size;
        Difficulty(int mines, int size) {
            this.mines = mines;
            this.size = size;
        }
    }

    public enum Audios {
        WIN("/audio/winnoise.wav"),
        LOSE("/audio/bombclick.wav"),
        CLICK("/audio/click.wav"),
        FLAG("/audio/flagclick.wav"),
        NEW_GAME("/audio/newgameclick.wav");

        final String path;
        Audios(String path) {
            this.path = path;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
