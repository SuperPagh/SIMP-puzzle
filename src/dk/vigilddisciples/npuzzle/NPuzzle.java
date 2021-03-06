package dk.vigilddisciples.npuzzle;
import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import dk.vigilddisciples.npuzzle.controller.SimpController;
import dk.vigilddisciples.npuzzle.model.Board;
import dk.vigilddisciples.npuzzle.model.GameState;
import dk.vigilddisciples.npuzzle.model.Highscore;
import dk.vigilddisciples.npuzzle.model.Score;
import dk.vigilddisciples.npuzzle.model.Settings;
import dk.vigilddisciples.npuzzle.view.GamePanel;
import dk.vigilddisciples.npuzzle.view.HighscorePanel;
import dk.vigilddisciples.npuzzle.view.ImageCropPanel;
import dk.vigilddisciples.npuzzle.view.InGameMenuPanel;
import dk.vigilddisciples.npuzzle.view.MainMenuPanel;
import dk.vigilddisciples.npuzzle.view.SettingsPanel;

public class NPuzzle extends JFrame {

    private static final long serialVersionUID = 1L;
    private static Settings settings;
    private static CardLayout cardLayout;
    private static JPanel cardPanel;
    private static MainMenuPanel mainMenuPanel;
    private static SettingsPanel settingsPanel;
    private static ImageCropPanel imageCropPanel;
    private static GamePanel gamePanel;
    private static InGameMenuPanel inGameMenuPanel;
    private static HighscorePanel highscorePanel;
    private JLayeredPane puzzlePane;


    public static boolean menuToggle;

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        NPuzzle game = new NPuzzle();
    }


    public NPuzzle() {
        super("N-Puzzle Game");

        //Set look to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Tried setting look and feel to current OS default, but failed");
        }

        //Initialize the model.
        settings = new Settings();
        settings.loadSettings();

        Highscore easyHighscore = new Highscore("easy");
        Highscore mediumHighscore = new Highscore("medium");
        Highscore hardHighscore = new Highscore("hard");

        Board board = new Board();
        Score score = new Score();
        GameState gs = new GameState(board, score);

        //Create CardLayout
        cardPanel = new JPanel();
        this.getContentPane().add(cardPanel);
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.setPreferredSize(settings.getCurrWindowSize().getDimension());

        //Create Controller
        gamePanel = new GamePanel(gs, new Highscore[]{easyHighscore, mediumHighscore, hardHighscore});
        SimpController controller = new SimpController(gamePanel);

        //Create mainManuPanel
        mainMenuPanel = new MainMenuPanel(controller);
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));

        //Create settings panel and give it an instance of settings
        settingsPanel = new SettingsPanel(settings, this);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        //Create ImageCropPanel
        imageCropPanel = new ImageCropPanel(settings);

        //Create puzzlePane.
        puzzlePane = new JLayeredPane();

        //Create inGameMenuPanel initially not visible
        inGameMenuPanel = new InGameMenuPanel(controller);
        inGameMenuPanel.setLayout(new BoxLayout(inGameMenuPanel, BoxLayout.Y_AXIS));
        inGameMenuPanel.setVisible(false);

        //Add gamePanel and inGameMenuPanel to puzzlePane
        puzzlePane.add(gamePanel, new Integer(0), 0);
        puzzlePane.add(inGameMenuPanel, new Integer(1), 0);

        //Create highscorePanel
        highscorePanel = new HighscorePanel(easyHighscore, mediumHighscore, hardHighscore);

        //Add controller to panels
        gamePanel.addKeyListener(controller);
        gamePanel.addMouseListener(controller);
        gamePanel.addMouseMotionListener(controller);


        //Add the different panels to the CardLayout
        cardPanel.add(mainMenuPanel, "mainMenu");
        cardPanel.add(settingsPanel, "settings");
        cardPanel.add(highscorePanel, "highscore");
        cardPanel.add(imageCropPanel, "imageCrop");
        cardPanel.add(puzzlePane, "puzzle");


        //Set settings for main window.
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    public void setNewSize(Dimension newDimension) {

        cardPanel.setPreferredSize(newDimension);
        inGameMenuPanel.setSize(newDimension);
        inGameMenuPanel.resetBounds();
        puzzlePane.setSize(newDimension);
        gamePanel.setSize(newDimension);
        highscorePanel.resetSize();

        this.pack();
        //Make sure the window stays centered.
        this.setLocationRelativeTo(null);
    }

    public static Settings getSettings(){
        return settings;
    }

    public static void swapView(String key) {
        swapView(key, "mainMenu");
    }

    public static void swapView(String key, String origin) {
        cardLayout.show(cardPanel, key);
        if(key.equals("puzzle")) {
            settings.setScrambleAnimationSpeed();

            gamePanel.requestFocus();
        } else if(key.equals("imageCrop")) {
            imageCropPanel.init();
        } else if (key.equals("highscore")) {
            highscorePanel.reset();
            highscorePanel.setOrigin(origin);
        } else if (key.equals("settings")) {
            settingsPanel.loadSettings();
        }
    }

    public static void toggleMenu() {
        menuToggle = !menuToggle;
        inGameMenuPanel.setVisible(menuToggle);
        //Fixes issue of gamePanel being painted wrong when new game is started.
        gamePanel.repaint();
    }
}
