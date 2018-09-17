import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import drawing.DrawObject;


public class Canvas extends JPanel{
	
	private static final long serialVersionUID = 1L;
	ArrayList<DrawObject> dObjects;
	
	public Canvas() {
		dObjects = new ArrayList<DrawObject>();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (DrawObject d : dObjects) {
			d.draw(g);
		}
	}
	
	public void addDrawObject(DrawObject drawObject) {
		dObjects.add(drawObject);
	}
	
	public void clearDrawObjects() {
		dObjects.clear();
	}
	
}
