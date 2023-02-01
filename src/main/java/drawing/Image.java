package drawing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image extends DrawObject{
	
	private int width;
	private int height;
	private BufferedImage image;
	private double angle;

	
	public Image(int x, int y, BufferedImage image) {
		this.x = x;
		this.y = y;	
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	public Image(int x, int y, int width, int height, BufferedImage image) {	
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = image;
	}
	
	public Image(int x, int y, String url) {
		this.x = x;
		this.y = y;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(url));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	public Image(int x, int y, int width, int height, String url) {
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		try {
			image = ImageIO.read(getClass().getResource(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
    public double getAngle() {

        return Math.toRadians(angle);

    }
    
    public void setAngle(double angle) {
    	this.angle = angle;
    }
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (angle > 0) {
			
			g2d.rotate(Math.toRadians(getAngle()), image.getWidth() / 2, image.getWidth() / 2);
		}
		g2d.drawImage(image, x, y, width, height, null);
		g2d.dispose();
	}

}
