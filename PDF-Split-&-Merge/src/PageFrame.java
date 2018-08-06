import java.awt.Color;

import javax.swing.JPanel;

import drawing.Image;

public class PageFrame extends JPanel{
	private static final long serialVersionUID = 1L;
	private Canvas imagePreview;
	private int positionNumber;
	private int width;
	private int height;
	
	public PageFrame(int positionNumber, String url) {
		width = 597;
		height = 200;
		this.positionNumber = positionNumber;
		
		imagePreview = new Canvas();
		imagePreview.setBackground(new Color(0xf2f2f2));
		imagePreview.addDrawObject(new Image(0, 0, 150, 180, url));
		imagePreview.setBounds(10, 10, 150, 180);
		
		JPanel body = new JPanel();
		body.setLayout(null);
		body.setBackground(new Color(0xf2f2f2));
		body.add(imagePreview);
		body.setBounds(0, 0, width, height);
		
		this.setLayout(null);
		this.add(body);
		
	}
	
	public int getPositionNumber() {
		return this.positionNumber;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
