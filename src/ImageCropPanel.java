import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ImageCropPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	
	private Settings settings;
	private WindowSize currWindowSize;
	private BufferedImage image;
	private int sx1, sy1, sx2, sy2;
	private int mouseStartX, mouseStartY;
	
	public ImageCropPanel(Settings settings) {
		this.settings = settings;
		this.init();
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		JButton btnCrop = new JButton("Crop Image");
		btnCrop.addActionListener(this);
		this.add(btnCrop);
	}
	
	public void init() {
		this.currWindowSize = settings.getCurrWindowSize();
		try {
			image = ImageIO.read(new File(settings.getGamePicture()));
		} catch (IOException e) { }
		
		sx1 = 0;
		sy1 = 0;
		sx2 = this.currWindowSize.getBOARD_SIZE();
		sy2 = this.currWindowSize.getBOARD_SIZE();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.drawImage(this.image, this.currWindowSize.getGAME_BORDER(), this.currWindowSize.getTOP_CONTROLS_SIZE(), this.currWindowSize.getBOARD_SIZE() + this.currWindowSize.getGAME_BORDER(), this.currWindowSize.getBOARD_SIZE() + this.currWindowSize.getTOP_CONTROLS_SIZE(), sx1, sy1, sx2, sy2, null);
		g2d.drawRect(this.currWindowSize.getGAME_BORDER(), this.currWindowSize.getTOP_CONTROLS_SIZE(), this.currWindowSize.getBOARD_SIZE(), this.currWindowSize.getBOARD_SIZE());
	}

	@Override
	public void mouseClicked(MouseEvent arg0) { }

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getX() > this.currWindowSize.getGAME_BORDER() && e.getX() < this.currWindowSize.getGAME_BORDER() + this.currWindowSize.getBOARD_SIZE()) {
			if(e.getY() > this.currWindowSize.getTOP_CONTROLS_SIZE() && e.getY() < this.currWindowSize.getTOP_CONTROLS_SIZE() + this.currWindowSize.getBOARD_SIZE()) {
				mouseStartX = e.getX();
				mouseStartY = e.getY();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseDragged(MouseEvent e) { 
		if(e.getX() > this.currWindowSize.getGAME_BORDER() && e.getX() < this.currWindowSize.getGAME_BORDER() + this.currWindowSize.getBOARD_SIZE()) {
			if(e.getY() > this.currWindowSize.getTOP_CONTROLS_SIZE() && e.getY() < this.currWindowSize.getTOP_CONTROLS_SIZE() + this.currWindowSize.getBOARD_SIZE()) {
				this.sx1 += this.mouseStartX - e.getX();
				this.sx1 = Math.max(this.sx1, 0);
				if(this.sx2 + this.currWindowSize.getBOARD_SIZE() + this.sx1 <= this.image.getWidth()) {
					this.sx2 = this.sx1 + this.currWindowSize.getBOARD_SIZE();
				} else {
					this.sx1 = this.sx2 - this.currWindowSize.getBOARD_SIZE();
				}
				
				this.sy1 += this.mouseStartY - e.getY();
				this.sy1 = Math.max(this.sy1, 0);
				if(this.sy2 + this.currWindowSize.getBOARD_SIZE() + this.sy1 <= this.image.getHeight()) {
					this.sy2 = this.sy1 + this.currWindowSize.getBOARD_SIZE();
				} else {
					this.sy1 = this.sy2 - this.currWindowSize.getBOARD_SIZE();
				}
				
				this.repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) { }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		settings.setGamePicture("resources/pics/" + ImageHandler.cropAndSave(this.image, this.sx1, this.sx2));
		Window.swapView("settings");
	}
}