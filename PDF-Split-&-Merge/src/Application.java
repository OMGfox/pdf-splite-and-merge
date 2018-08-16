import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;

public class Application {
	
	private JFrame mainFrame;
	private JPanel contentFrame;
	private JPanel topPanel;
	private JScrollPane sPane;
	private JTextField numPages;
	private LinkedList<PageFrame> pageFrames;
	private LinkedList<PDDocument> documents;
	private ArrayList<Image> icons;
	private int width;
	private int height;
	
	private JToggleButton openButton;
	private JToggleButton deleteAllButton;
	private JToggleButton saveButton;

	public Application() {
		this.width = 640;
		this.height = 520;
		
		documents = new LinkedList<>();
		
		icons = new ArrayList<Image>();
		try {
			Image icon16x16 = ImageIO.read(getClass().getResourceAsStream("/icon_16x16.png"));
			Image icon32x32 = ImageIO.read(getClass().getResourceAsStream("/icon_32x32.png"));
			Image icon64x64 = ImageIO.read(getClass().getResourceAsStream("/icon_64x64.png"));
			Image icon128x128 = ImageIO.read(getClass().getResourceAsStream("/icon_128x128.png"));
			
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
		
		pageFrames = new LinkedList<PageFrame>();
		
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
		
		JLabel numPagesLable = new JLabel("Страниц:");
		numPagesLable.setForeground(new Color(0xf2f2f2));
		numPagesLable.setBounds(450, 10, 80, 20);
		numPages = new JTextField();
		numPages.setEditable(false);
		numPages.setText("0");
		numPages.setHorizontalAlignment(JTextField.RIGHT);
		numPages.setBounds(510, 10, 40, 20);
		
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
		openButton.setToolTipText("Добавить файл");
		
		try {
			Image openButtonIcon = ImageIO.read(getClass().getResource("/folder_blue_48x48.png"));
			Image openButtonRolloverIcon = ImageIO.read(getClass().getResource("/folder_blue_rollover_48x48.png"));
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
		saveButton.setToolTipText("Сохранить в файл");
		
		try {
			Image saveButtonIcon = ImageIO.read(getClass().getResource("/save_button_32x32.png"));
			Image saveButtonRolloverIcon = ImageIO.read(getClass().getResource("/save_button_rollover_32x32.png"));
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
		deleteAllButton.setToolTipText("Очистить все");
		
		try {
			Image deleteAllButtonIcon = ImageIO.read(getClass().getResource("/button_delete_red_48x48.png"));
			Image deleteAllButtonRolloverIcon = ImageIO.read(getClass().getResource("/button_delete_rollover_48x48.png"));
			deleteAllButton.setIcon(new ImageIcon(deleteAllButtonIcon));
			deleteAllButton.setRolloverIcon(new ImageIcon(deleteAllButtonRolloverIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		
		topPanel.add(numPagesLable);
		topPanel.add(numPages);
		topPanel.add(openButton);
		topPanel.add(saveButton);
		topPanel.add(deleteAllButton);
		
	}
	
	public void moveViewportSPane(int interval) {
		JViewport viewport = sPane.getViewport();
		Point currentPoint = viewport.getViewPosition();
		int x = currentPoint.x;
		int y = currentPoint.y + interval;
		if (y < 0) {
			viewport.setViewPosition(new Point(x, 0));
		} else if(y > contentFrame.getHeight()) {
			viewport.setViewPosition(new Point(x, contentFrame.getHeight() - 450));
		} else {
			viewport.setViewPosition(new Point(x, y));
		}
		
	}
	
	public int getNumberPageFrames() {
		return pageFrames.size();
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
			pf.setBounds(x, y, pf.getWidth(), pf.getHeight());
			
			contentFrame.add(pf);
		}
		
		repaint();
	}
	
	public void addPage(PageFrame pageFrame) {
		pageFrames.add(pageFrame);
	}
	
	public void deletePage(int numPage) {
		if (numPage == 0) {
			pageFrames.removeFirst();
		} else {
			pageFrames.remove(numPage);
		}
		
		int i = 1;
		for (PageFrame pf : pageFrames) {
			pf.setPositionNumber(i++);
		}
		
		contentFrame.removeAll();
		drawPageFrames();
		repaint();

	}
	
	public void start() {	
		mainFrame.add(topPanel);
		mainFrame.add(sPane);
		mainFrame.setVisible(true);
		
	}
	
	public void repaint() {
		contentFrame.setPreferredSize(new Dimension(width, pageFrames.size() * 206));
		sPane.updateUI();
		numPages.setText(Integer.toString(pageFrames.size()));
		mainFrame.repaint();
	}
	
	private class DeleteAllListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			pageFrames.clear();
			documents.clear();
			deleteAllButton.setSelected(false);
			contentFrame.removeAll();
			repaint();
		}
	}
	
	private class OpenListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(openButton);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            try {
					documents.add(PDDocument.load(file));
					PDDocumentCatalog docCatalog = documents.get(documents.size() - 1).getDocumentCatalog();
					List<PDPage> pages = docCatalog.getAllPages();
					int i = pageFrames.size() + 1;
					if (i <= 0) {
						i = 1;
					}
					for (PDPage page : pages) {
						addPage(new PageFrame(i++, (PDPage) page, Application.this));
						
					}
					drawPageFrames();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
	        } 
			repaint();
			openButton.setSelected(false);	
		}
		
	}
	
	private class SaveListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveButton.setSelected(false);
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(saveButton);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				PDDocument document;
				try {
					document = new PDDocument();
					for (PageFrame pf : pageFrames) {
						
						document.importPage(pf.getPage());
					}
					document.save(file);
					document.close();

				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (COSVisitorException e) {
					e.printStackTrace();
				} 
			}
		}
		
	}

	public void swapPages(int first, int second) {
		PageFrame temp = pageFrames.get(first - 1);
		pageFrames.set(first - 1, pageFrames.get(second - 1));
		pageFrames.set(second - 1, temp);
		int i = 1;
		for (PageFrame pf : pageFrames) {
			pf.setPositionNumber(i++);
		}
		contentFrame.removeAll();
		drawPageFrames();
		repaint();
	}

}
