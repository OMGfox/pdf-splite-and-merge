import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Application {
	
	private JFrame mainFrame;
	private JPanel contentFrame;
	private JPanel topPanel;
	private ArrayList<PageFrame> pageFrames;
	private ArrayList <Image> icons;
	private int width;
	private int height;

	
	public Application() {
		this.width = 640;
		this.height = 480;
		
		icons = new ArrayList<Image>();
		try {
			Image icon16x16 = ImageIO.read(getClass().getResourceAsStream("/icon16x16.png"));
			Image icon32x32 = ImageIO.read(getClass().getResourceAsStream("/icon32x32.png"));
			Image icon64x64 = ImageIO.read(getClass().getResourceAsStream("/icon64x64.png"));
			Image icon128x128 = ImageIO.read(getClass().getResourceAsStream("/icon128x128.png"));
			
			icons.add(icon16x16);
			icons.add(icon32x32);
			icons.add(icon64x64);
			icons.add(icon128x128);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		mainFrame = new JFrame();
		mainFrame.setSize(new Dimension(this.width, this.height));
		mainFrame.setTitle("PDF - Split & Merge");
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (!icons.isEmpty()) {
			mainFrame.setIconImages(icons);
		}
		
		pageFrames = new ArrayList<PageFrame>();
		
		init();
	}
	
	private void init() {
		drawTopPanel();
		drawContentFrame();
	}
	
	private void drawTopPanel() {
		topPanel = new JPanel();
		topPanel.setSize(new Dimension(width, 38));
		topPanel.setBackground(Color.LIGHT_GRAY);
	}
	
	private void drawContentFrame() {
		
		contentFrame = new JPanel();
//		contentFrame.setSize(new Dimension(width, height));
		contentFrame.setBackground(Color.DARK_GRAY);	
		
	}
	
	public void drawPageFrames() {
		contentFrame.setLayout(new GridLayout(pageFrames.size(), 1, 5, 10));
		for(PageFrame pf : pageFrames) {
			contentFrame.add(pf);
		}
	}
	
	public void addPage(PageFrame pageFrame) {
		pageFrames.add(pageFrame);
		
	}
	
	public void start() {
		mainFrame.add(topPanel);
		mainFrame.add(contentFrame);
		mainFrame.setVisible(true);
		
	}
	
	public void repaint() {
		mainFrame.repaint();
		topPanel.repaint();
		contentFrame.repaint();
	}
	
}
