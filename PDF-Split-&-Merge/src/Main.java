import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import shapes.Circle;
import shapes.Rectangle;

public class Main {

	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		JFrame frame = new JFrame();
		Canvas canvas = new Canvas();
		canvas.setSize(new Dimension(600, 480));
		canvas.addShape(new Circle(10, 10, 100, Color.BLACK));
		canvas.addShape(new Rectangle(20, 20, 100, 120, Color.PINK));
		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
