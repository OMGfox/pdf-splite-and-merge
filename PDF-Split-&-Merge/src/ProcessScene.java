import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ProcessScene extends JPanel{
	private static final long serialVersionUID = 1L;
	private Canvas canvas;
	private drawing.Image image;
	private boolean isPlaying;
	private double angle;
	
	public ProcessScene(int width, int height, Status status, JFrame root) {
		setBounds(0, 0, width, height);
		angle = 0;
		isPlaying = false;
		setBackground(Color.WHITE);
		this.setLayout(null);
		canvas = null;
		if (status.equals(Status.EMPTY)) {
			
		} else if (status.equals(Status.OPENING) || status.equals(Status.SAVING)) {
			canvas = new Canvas();
			canvas.setBounds((width - 128) / 2, (height - 128) / 2, 128, 128);
			image = new drawing.Image(0, 0, "/spiner.png");
			image.setAngle(0);
			canvas.addDrawObject(image);
			canvas.setBackground(new Color(255, 255, 255));
			add(canvas);
			root.add(this);
			new PlayAnimation();
		} 

	}
	
	public void spinAnimation() {
		repaintCanvas();
		repaint();
		angle += 300;
	}
	
	private void repaintCanvas() {
		image.setAngle(angle);
		canvas.clearDrawObjects();
		canvas.addDrawObject(image);
		canvas.repaint();
	}
	
	public void stopAnimation() {
		isPlaying = false;
		
	}
	
	private class PlayAnimation implements Runnable {
		private Thread thread;
		
		public PlayAnimation() {
			isPlaying = true;
			thread = new Thread(this, "PlayAnimationThread");
			thread.start();	
		}
		
		@Override
		public void run() {
			isPlaying = true;
			while (isPlaying) {
					spinAnimation();
					try {
						Thread.sleep(1000 / 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
