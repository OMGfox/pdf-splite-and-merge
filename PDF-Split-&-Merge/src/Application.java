import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
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
	
	public Application(int width, int height) {
		mainFrame = new JFrame();
		pageFrames = new ArrayList<PageFrame>();
		this.width = width;
		this.height = height;
		init();
	}
	
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
		
		if (!icons.isEmpty()) {
			mainFrame.setIconImages(icons);
		}
		
		pageFrames = new ArrayList<PageFrame>();
		
		init();
	}
	
	private void init() {
//		drawTopPanel();
//		drawContentFrame();
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
