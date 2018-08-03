import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import drawing.Image;

public class PageFrame extends JPanel{
	private static final long serialVersionUID = 1L;
	private Canvas imagePreview;
	private int positionNumber;
	private int width;
	private int height;
	
	public PageFrame(int positionNumber, String url) {
		width = 600;
		height = 250;
		
		imagePreview = new Canvas();
		imagePreview.setSize(new Dimension(170, 200));
		imagePreview.setBackground(Color.GRAY);
		imagePreview.addDrawObject(new Image(0, 0, url));
		
		JPanel body = new JPanel();
		body.setSize(new Dimension(width, height));
		body.setBackground(Color.PINK);
		body.add(imagePreview);
		
//		this.add(body);
		
	}
}
