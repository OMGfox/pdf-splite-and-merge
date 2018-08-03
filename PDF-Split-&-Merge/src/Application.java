import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Application {
	
	private JFrame mainFrame;
	private JPanel contentFrame;
	private JPanel topPanel;
	private ArrayList<PageFrame> pageFrames;
	private int width;
	private int height;
	
	public Application(int width, int height) {
		mainFrame = new JFrame();
		pageFrames = new ArrayList<PageFrame>();
		this.width = width;
		this.height = height;
		init();
	}
	
	public Application() {
		mainFrame = new JFrame();
		pageFrames = new ArrayList<PageFrame>();
		this.width = 640;
		this.height = 480;
		init();
	}
	
	private void init() {
//		drawTopPanel();
		drawContentFrame();
	}
	
	private void drawTopPanel() {
		topPanel = new JPanel();
		topPanel.setSize(new Dimension(width, 20));
		topPanel.setBackground(Color.BLACK);
		mainFrame.add(topPanel);
	}
	
	private void drawContentFrame() {
		contentFrame = new JPanel();
		contentFrame.setSize(new Dimension(width, height));
		contentFrame.setBackground(Color.BLUE);
		mainFrame.add(contentFrame);
	}
	
	public void drawPageFrames() {
		for(PageFrame pf : pageFrames) {
			contentFrame.add(pf);
		}
	}
	
	public void addPage(PageFrame pageFrame) {
		pageFrames.add(pageFrame);
	}
	
	public void start() {
		mainFrame.setVisible(true);
	}
	
}
