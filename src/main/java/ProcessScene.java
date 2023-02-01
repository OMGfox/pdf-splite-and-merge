import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ProcessScene extends JPanel{
	private static final long serialVersionUID = 1L;
	private Canvas canvas;
	private drawing.Image image;
	private boolean isPlaying;
	private boolean isWelcom;
	private double angle;
	
	
	public ProcessScene(int width, int height, Status status, JFrame root) {
		setBounds(0, 0, width, height);
		angle = 0;
		isPlaying = false;
		isWelcom = false;
		setBackground(Color.WHITE);
		setLayout(null);
		canvas = null;
		if (status.equals(Status.OPENING) || status.equals(Status.SAVING)) {
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
	
	public ProcessScene(int width, int height, Status status, JComponent root) {
		setBounds(0, 0, width, height);
		angle = 0;
		isPlaying = false;
		isWelcom = false;
		setBackground(Color.WHITE);
		this.setLayout(null);
		canvas = null;
		if (status.equals(Status.EMPTY)) {
			canvas = new Canvas();
			canvas.setBounds((width - 276) / 2, (height - 256) / 2, 276, 256);
			image = new drawing.Image(0, 0, "/welcom_text.png");
			canvas.addDrawObject(image);
			canvas.setBackground(Color.WHITE);
			add(canvas);
			root.add(this);
			new PlayWelcom();
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
		if (!this.equals(null)) {
			this.setBounds(0, 0, getParent().getWidth(), getParent().getHeight());
		}
		if (isWelcom) {
			canvas.setBounds((getWidth() - 276) / 2, (getHeight() - 256) / 2, 276, 256);
		}
		canvas.addDrawObject(image);
		canvas.repaint();
	}
	
	public void stopAnimation() {
		isPlaying = false;
		
	}
	
	public void stopWelcom() {
		isWelcom = false;
	}
	
	public void clear() {
		this.setVisible(false);
	}
	
	private class PlayWelcom implements Runnable {
		private Thread thread;
		
		public PlayWelcom() {
			isWelcom = true;
			thread = new Thread(this, "PlayWelcomThread");
			thread.start();
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (isWelcom) {
				try {
					repaintCanvas();
					Thread.sleep(1000 / 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			ProcessScene.this.clear();
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
