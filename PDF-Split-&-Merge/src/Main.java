import java.awt.Dimension;

import javax.swing.JFrame;

import drawing.Image;


public class Main {

	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		String url = "/pdf-document.png";

		JFrame frame = new JFrame();
		frame.setSize(new Dimension(640, 480));
		Canvas canvas = new Canvas();
		canvas.setSize(new Dimension(640, 480));
		canvas.addDrawObject(new Image(10, 10, 170, 200, url));
		frame.add(canvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
