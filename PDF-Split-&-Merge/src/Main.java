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
		frame.setSize(new Dimension(520, 540));
		Canvas canvas = new Canvas();
		canvas.setSize(new Dimension(520, 540));
		for (int i = 0; i < 100; i+=1) {		
			if(i % 10 == 0 || i / 10 == 0 || i % 10 == 9 || i / 10 == 9) {
				canvas.addShape(new Rectangle(i % 10 * 50 , i / 10 * 50  , 50, 50, Color.WHITE));
				canvas.addShape(new Circle(i % 10 * 50 , i / 10 * 50  , 50, Color.BLACK));	
			} else {
				canvas.addShape(new Rectangle(i % 10 * 50 , i / 10 * 50  , 50, 50, Color.BLACK));
				canvas.addShape(new Circle(i % 10 * 50 , i / 10 * 50  , 50, Color.WHITE));
//				
			}
			
		}
		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
