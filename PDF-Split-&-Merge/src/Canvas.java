import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import shapes.Shape;

public class Canvas extends JPanel{
	
	ArrayList<Shape> shapes;
	
	public Canvas() {
		shapes = new ArrayList();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape s : shapes) {
			s.draw(g);
		}
	}
	
	public void addShape(Shape shape) {
		shapes.add(shape);
	}
}
