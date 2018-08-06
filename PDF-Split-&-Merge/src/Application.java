import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

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
		this.height = 520;
		
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
		mainFrame.setLayout(null);	
		
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
		topPanel.setBackground(Color.DARK_GRAY);
		topPanel.setLayout(null);
		
		JToggleButton openButton = new JToggleButton();
		// to remote the spacing between the image and button's borders
		openButton.setMargin(new Insets(0, 0, 0, 0));
		// to add a different background
		openButton.setBackground(Color.DARK_GRAY);
		// to remove the border
		openButton.setBorder(null);
		openButton.setBounds(10, 0, 32, 32);
		
		try {
			Image openButtonIcon = ImageIO.read(getClass().getResource("/folder_blue.png"));
			Image openButtonSelectedIcon = ImageIO.read(getClass().getResource("/folder_blue_favorites.png"));
			openButton.setIcon(new ImageIcon(openButtonIcon));
			openButton.setRolloverIcon(new ImageIcon(openButtonSelectedIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		topPanel.add(openButton);
		
	}
	
	private void drawContentFrame() {
		contentFrame = new JPanel();
		contentFrame.setBackground(Color.GRAY);
		contentFrame.setLayout(null);	
	}
	
	public void drawPageFrames() {
		for(PageFrame pf : pageFrames) {
			int x = 5;
			int y = (pf.getPositionNumber() - 1) * 205 + 5;
			pf.setBounds(x, y, pf.getHeight(), pf.getHeight());
			
			contentFrame.add(pf);
		}
		
		contentFrame.setPreferredSize(new Dimension(width, pageFrames.size() * 206));
	}
	
	public void addPage(PageFrame pageFrame) {
		pageFrames.add(pageFrame);
	}
	
	public void start() {
		JScrollPane sPane = new JScrollPane(contentFrame, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sPane.setBounds(0, 38, width - 15, 444);
		sPane.getVerticalScrollBar().setUnitIncrement(16);
		
		mainFrame.add(topPanel);
		mainFrame.add(sPane);
		mainFrame.setVisible(true);
		
	}
	
	public void repaint() {
		mainFrame.repaint();
		topPanel.repaint();
		contentFrame.repaint();
	}
	
}
