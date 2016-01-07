import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel {

	public static final int COGWHEEL_SIZE = Window.TOP_CONTROLS_SIZE - Window.TOP_CONTROLS_SIZE/4;
	public static final String RESOURCE_PATH = "resources/";
	public static final String THEME_PATH = RESOURCE_PATH + "themes/default/";
	private static final long serialVersionUID = 1L;
	private final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
	private final Color TILE_TEXT_COLOR = Color.WHITE;
	private GameState gameState;
	private Image boardImg;
	private Image tileImg;
	private Image cogwheelImg;
	private int movesLabelxPos;
	private int movesLabelyPos;
	private int timeLabelxPos;
	private int timeLabelyPos;
	private int[] stringWidths;
	private boolean firstPaint;
	
	
	//1000 is a 1000milliseconds so the timer will fire each second. 
	private Timer timer = new Timer(1000, new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// call the updateSeconds functions which adds a second to the scoreModel and updates the labeltext. 
			getScore().addSeconds(1);
			repaint();
		}
		
	});
	
	public GameState getGameState() {
		return gameState;
	}
	

	public void updateGameState(GameState gs) {
		this.gameState = gs;
	}
	
	public GamePanel(GameState gs) {

		this.setBounds(0, 0, Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
		this.setOpaque(true);
		
		//TODO: Shouldn't this just get board and score from gamestate?
		this.setBackground(BACKGROUND_COLOR);

		this.gameState = gs;

		this.firstPaint = true;
		
		this.loadImages();
		
	}
	
	
	private void loadImages() {
		//Load boardImage
		ImageIcon boardIc = new ImageIcon(THEME_PATH + "board.jpeg");
		this.boardImg = boardIc.getImage();
		
		//Load tileImage
		ImageIcon tileIc = new ImageIcon(THEME_PATH + "tile.jpeg");
		this.tileImg = tileIc.getImage();
		
		//Load cogwheelImage
		ImageIcon cogwheelIc = new ImageIcon(RESOURCE_PATH + "cogwheel.png");
		this.cogwheelImg = cogwheelIc.getImage();
	}
	
	//TODO: Can we move a lot of code out of this that does not need to be calculated at each repaint? 
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//Set font. Has to be done on each repaint, because it defaults it otherwise. 
		g.setFont(new Font("Sans Serif", Font.PLAIN, Window.LABEL_TEXT_SIZE));
		
		//Only calculate labelpositions first time round. 
		//We do this so it doesn't have to calculate the positions every single time the view is repainted. 
		//It only saves a bit cpu, but we think it's worth it. 
		if (this.firstPaint) {
			calcLabelPositions(g);
		}
		
		//Draw Time and Move labels.  
		g.drawString("Time: " + this.getScore().timeToString(), this.timeLabelxPos, this.timeLabelyPos);
		g.drawString("Moves: " + this.getScore().getMoves(), this.movesLabelxPos, this.movesLabelyPos);
	
		//Draw cogwheel (settings) button
		int cogWheelXPos = Window.WINDOW_WIDTH - Window.GAME_BORDER - COGWHEEL_SIZE;
		int cogWheelYPos = (Window.TOP_CONTROLS_SIZE - COGWHEEL_SIZE) / 2;
		g.drawImage(cogwheelImg, cogWheelXPos, cogWheelYPos, COGWHEEL_SIZE, COGWHEEL_SIZE, null);
		
		//Draw board background. 
		g.drawImage(boardImg, Window.GAME_BORDER, Window.WINDOW_HEIGHT - Window.GAME_BORDER - Window.BOARD_SIZE, Window.BOARD_SIZE, Window.BOARD_SIZE, null);
		
		//TODO: Somehow the tiles are positioned a bit off the y position at other boardsizes than 4. 
		g.setFont(new Font("Sans Serif", Font.ITALIC, Window.LABEL_TEXT_SIZE));
		
		
		//Calculate width of strings with 1 digit to 4 digits. 
		if (this.firstPaint) {
			this.stringWidths = calcStringWidths(g);
			this.firstPaint = false;
		}
		
		//Draw Board
		int[][] tiles = this.getBoard().getTiles();
		for(int y = 0; y < tiles.length; y++) {
			for(int x = 0; x < tiles.length; x++) {
				if(tiles[x][y] != Math.pow(this.getBoard().getBoardSize(),2)) {
					
					int xPos = Window.GAME_BORDER + Window.BOARD_BORDER_SIZE + (x * this.getBoard().getTileSize());
					//Y position is gotten from the bottom and then up. This way it will always have exactly distance to bottom if the top is changed. 
					int yPos = Window.WINDOW_HEIGHT - Window.GAME_BORDER - ((this.getBoard().getBoardSize() - y) * (this.getBoard().getTileSize())) - Window.BOARD_BORDER_SIZE;
					
					//Draws tile at x and y pos with image gotten from ressources. 
					g.drawImage(tileImg, xPos, yPos, this.getBoard().getTileSize(), this.getBoard().getTileSize(), null);
					
					//Draws text on image
					g.setColor(TILE_TEXT_COLOR);
					
					//Position labels on tiles. 
					String TileNum = Integer.toString(tiles[x][y]);
					int strXPos = xPos + (this.getBoard().getTileSize() / 2) - this.stringWidths[TileNum.length()] / 2;
					int strYPos = yPos + (this.getBoard().getTileSize() / 2) + g.getFontMetrics().getHeight()/4;
					
					//Draw text for each tile
					g.drawString(TileNum, strXPos, strYPos);
				}
			}
		}
		
	}
	
	//Helper method to calculate label positions. 
	private void calcLabelPositions(Graphics g) {
		
		//Calculate position so it will be in the middle. To do this we need to know the width of the label with current font. 
		int movesLabelWidth = calcWidthOfString(g, "Move: " + this.getScore().getMoves());
		
		this.movesLabelxPos = (Window.WINDOW_WIDTH-movesLabelWidth)/2;
		this.movesLabelyPos = g.getFontMetrics().getHeight() + Window.TOP_CONTROLS_SIZE / 4;
		
		this.timeLabelxPos = Window.GAME_BORDER;
		this.timeLabelyPos = g.getFontMetrics().getHeight() + Window.TOP_CONTROLS_SIZE / 4;
	}
	
	//Returns an array with the width of the labels according to how many digits there are. Goes from 0 to 4 digits. 
	private int[] calcStringWidths(Graphics g) {
		int[] stringWidths = new int[5];
		int counter = 1;
		stringWidths[0] = 0;
		for (int i = 1; i < stringWidths.length; i++) {
			stringWidths[i] = calcWidthOfString(g, Integer.toString(counter));
			counter *= 10;
		}
		
		return stringWidths;
	}
	
	//Returns the width of a given string with it's current font
	private int calcWidthOfString(Graphics g, String str) {
		FontMetrics fontMetrics = g.getFontMetrics(g.getFont());
		int width = fontMetrics.stringWidth(str);
		return width;
	}
	
	public void startTiming () {
		timer.start();
	}
	
	public void stopTiming() {
		timer.stop();
	}

	//Helper method to retrieve board from gameState. 
	public Board getBoard() {
		return this.gameState.getBoard();
	}
	
	//Helper method to retrieve score from gameState
	public Score getScore() {
		return this.gameState.getScore();
	}
}
