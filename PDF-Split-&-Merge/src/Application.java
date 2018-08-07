import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private JScrollPane sPane;
	private ArrayList<PageFrame> pageFrames;
	private ArrayList <Image> icons;
	private int width;
	private int height;
	
	private JToggleButton openButton;
	private JToggleButton deleteAllButton;
	private JToggleButton saveButton;

	
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
		mainFrame.setLocationRelativeTo(null); // to center a main window 
		
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
		
		openButton = new JToggleButton();
		// to remote the spacing between the image and button's borders
		openButton.setMargin(new Insets(0, 0, 0, 0));
		// to add a different background
		openButton.setBackground(Color.DARK_GRAY);
		// to remove the border
		openButton.setBorder(null);
		openButton.setBounds(10, 3, 30, 30);
		openButton.setContentAreaFilled(false);
		openButton.addActionListener(new OpenListener());
		
		try {
			Image openButtonIcon = ImageIO.read(getClass().getResource("/folder_blue.png"));
			Image openButtonRolloverIcon = ImageIO.read(getClass().getResource("/folder_blue_rollover.png"));
			openButton.setIcon(new ImageIcon(openButtonIcon));
			openButton.setRolloverIcon(new ImageIcon(openButtonRolloverIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		saveButton = new JToggleButton();
		saveButton.setMargin(new Insets(0, 0, 0, 0));
		saveButton.setBackground(Color.DARK_GRAY);
		saveButton.setBorder(null);
		saveButton.setBounds(57, 4, 30, 30);
		saveButton.setContentAreaFilled(false);
		saveButton.addActionListener(new SaveListener());
		
		try {
			Image saveButtonIcon = ImageIO.read(getClass().getResource("/save_button.png"));
			Image saveButtonRolloverIcon = ImageIO.read(getClass().getResource("/save_button_rollover.png"));
			saveButton.setIcon(new ImageIcon(saveButtonIcon));
			saveButton.setRolloverIcon(new ImageIcon(saveButtonRolloverIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		deleteAllButton = new JToggleButton();
		deleteAllButton.setMargin(new Insets(0, 0, 0, 0));
		deleteAllButton.setBackground(Color.DARK_GRAY);
		deleteAllButton.setBorder(null);
		deleteAllButton.setBounds(590, 3, 30, 30);
		deleteAllButton.setContentAreaFilled(false);
		deleteAllButton.addActionListener(new DeleteAllListener());
		
		try {
			Image deleteAllButtonIcon = ImageIO.read(getClass().getResource("/button_delete_red.png"));
			Image deleteAllButtonRolloverIcon = ImageIO.read(getClass().getResource("/button_delete_rollover.png"));
			deleteAllButton.setIcon(new ImageIcon(deleteAllButtonIcon));
			deleteAllButton.setRolloverIcon(new ImageIcon(deleteAllButtonRolloverIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		
		topPanel.add(openButton);
		topPanel.add(saveButton);
		topPanel.add(deleteAllButton);
		
	}
	
	private void drawContentFrame() {
		contentFrame = new JPanel();
		contentFrame.setBackground(Color.GRAY);
		contentFrame.setLayout(null);
		
		sPane = new JScrollPane(contentFrame, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sPane.setBounds(0, 38, width - 15, 445);
		sPane.getVerticalScrollBar().setUnitIncrement(16);
	}
	
	public void drawPageFrames() {
		for(PageFrame pf : pageFrames) {
			int x = 5;
			int y = (pf.getPositionNumber() - 1) * 206 + 5;
			pf.setBounds(x, y, pf.getHeight(), pf.getHeight());
			
			contentFrame.add(pf);
		}
		
		repaint();
	}
	
	public void addPage(PageFrame pageFrame) {
		pageFrames.add(pageFrame);
	}
	
	public void start() {
		
		mainFrame.add(topPanel);
		mainFrame.add(sPane);
		mainFrame.setVisible(true);
		
	}
	
	public void repaint() {
		contentFrame.setPreferredSize(new Dimension(width, pageFrames.size() * 206));
		sPane.updateUI();
		mainFrame.repaint();
	}
	
	private class DeleteAllListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			pageFrames.clear();
			deleteAllButton.setSelected(false);
			contentFrame.removeAll();
			repaint();
		}
	}
	
	private class OpenListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String url = "/pdf-document.png";
			int currentIndex = pageFrames.size();
			addPage(new PageFrame(currentIndex + 1, url));
			drawPageFrames();
			openButton.setSelected(false);
			repaint();	
		}
		
	}
	
	private class SaveListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Вызываем диалог сохранения");
			saveButton.setSelected(false);
		}
		
	}

}
