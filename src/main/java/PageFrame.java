import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PageFrame extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private ShadowPanel shadowPanel;
	private Canvas imagePreview;
	private int positionNumber;

	private BeautyButton previewButton;
	private BeautyButton saveButton;
	private BeautyButton deleteButton;
	private JPanel buttonPanel;
	private PDPage page;
	private PDDocument document;
	private BufferedImage image;
	private PageFrameManager pageManager;
	private JTextField fieldPageNumber;
	private int rotation;
	private String lastPath; // для запоминания последнего пути при открытии и сохранении
	private boolean isSelected; // для выделения фрейма при перетаскивании мышкой
	private int initialX;
	private int initialY;
	private long startTime;
	private long deltaTime;
	private ProcessScene processScene;
	private boolean isMultySelect; // выбор фрейма с зажатой кнопкой Ctrl - для множественного выделения фреймов
	// размеры основных фреймов и множетель для увеличения\уменьшения
	private int multiplier;
	private int frameSize;
	private int canvasSize;
	private int buttonPanelSizeWidth;
	private String docName;
	private Color frameColor;
	
	
	public PageFrame(int positionNumber, PDPage page, PageFrameManager pageManager, 
			BufferedImage image, PDDocument document, String docName, Color frameColor) {
		this.document = document;
		this.pageManager = pageManager;
		this.positionNumber = positionNumber;
		this.page = page;
		this.image = image;
		this.docName = docName;
		this.frameColor = frameColor;
		isMultySelect = false;
		isSelected = false;
		rotation = page.getRotation();
		
		frameSize = 200;
		
		if (image.getWidth() < image.getHeight()) {
			setSize((int)(frameSize / 1.4f), frameSize);
		} else {
			setSize(frameSize, (int)(frameSize / 1.4f));
		}
		
		setBackground(new Color(255, 255, 255, 0));
		setLayout(null);
		addMouseListener(new PageFrameMouseListener());
		init();
	}

	private void init() {
		
		shadowPanel = new ShadowPanel();
		multiplier = 1;
		canvasSize = (int) (frameSize / 1.4f);
		if (image.getWidth() < image.getHeight()) {
			shadowPanel.setBounds((this.getWidth() - canvasSize) / 2, 0, canvasSize * multiplier, (int) (canvasSize * 1.4f * multiplier));
		} else {
			shadowPanel.setBounds(0, (this.getHeight() - canvasSize) / 2, (int) (canvasSize * 1.4f * multiplier), canvasSize * multiplier);
		}
		
		shadowPanel.setLayout(null);
		shadowPanel.setBackground(new Color(255, 255, 255, 0));
		
		imagePreview = new Canvas();
		imagePreview.setBounds(0, 0, shadowPanel.getWidth(), shadowPanel.getHeight() - 7);
		imagePreview.setBackground(Color.BLACK);
		imagePreview.addDrawObject(new drawing.Image(2, 2, imagePreview.getWidth() - 4, imagePreview.getHeight() - 4, image));
		imagePreview.addMouseListener(new DragPageFrameMouseListener());
		imagePreview.addMouseMotionListener(new DragPageFrameMouseListener());
		imagePreview.addMouseListener(new PageFrameMouseListener());
		
		shadowPanel.add(imagePreview);
		
		fieldPageNumber = new JTextField();
		fieldPageNumber.setBounds(3, 3, 30, 20);
//		fieldPageNumber.setBackground(new Color(209,231,81));
		fieldPageNumber.setBackground(frameColor);
		fieldPageNumber.setText(Integer.toString(positionNumber));
		fieldPageNumber.setHorizontalAlignment(JTextField.CENTER);
		fieldPageNumber.setEditable(false);
		fieldPageNumber.addMouseListener(new PageFrameMouseListener());
		fieldPageNumber.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		fieldPageNumber.setFont(new Font("Courier", Font.BOLD, 14));
		
		buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(209, 231, 81, 200));
		buttonPanel.setVisible(false);
		buttonPanelSizeWidth = 87;
		buttonPanel.setBounds((imagePreview.getWidth() - buttonPanelSizeWidth) / 2, imagePreview.getHeight() - 32, buttonPanelSizeWidth, 30);
		buttonPanel.addMouseListener(new PageFrameMouseListener());
		add(buttonPanel);
		
//		Предпросмотр странички
		previewButton = new BeautyButton("/search_24x24.png", "/search_rollover_24x24.png", "Посмотреть");
		previewButton.setBounds(1, 1, 24, 24);
		previewButton.addActionListener(new PreviewButtonListener());
		previewButton.addMouseListener(new PageFrameMouseListener());
		buttonPanel.add(previewButton);
		
		saveButton = new BeautyButton("/save_button_24x24.png", "/save_button_rollover_24x24.png", "Сохранить страничку в файл");
		saveButton.setBounds(28, 1, 24, 24);
		saveButton.addActionListener(new SaveButtonListener());
		saveButton.addMouseListener(new PageFrameMouseListener());
		buttonPanel.add(saveButton);
		
		deleteButton = new BeautyButton("/button_delete_red_24x24.png", "/button_delete_red_rollover_24x24.png", "Удалить");
		deleteButton.setBounds(55, 1, 24, 24);
		deleteButton.addActionListener(new DeleteButtonListener());
		deleteButton.addMouseListener(new PageFrameMouseListener());
		buttonPanel.add(deleteButton);
		
//		Добавление элементов на PageFrame

		add(shadowPanel);
		add(fieldPageNumber);
		
		this.setComponentZOrder(fieldPageNumber, 0);
		this.setComponentZOrder(buttonPanel, 1);
		this.setComponentZOrder(shadowPanel, 2);
	}
	
	public void rotation(String direction) {
		if (direction.equals("left")) {
			rotation -= 90;
			if (rotation == -360) rotation = 0;
		} else if (direction.equals("right")){
			rotation += 90;
			if (rotation == 360) rotation = 0;
		}
		page.setRotation(rotation);
		
		rotateBufferedImage(direction);
		if (image.getWidth() < image.getHeight()) {
			setSize((int)(frameSize / 1.4f), frameSize);
		} else {
			setSize(frameSize, (int)(frameSize / 1.4f));
		}
		
		if (image.getWidth() < image.getHeight()) {
			shadowPanel.setBounds((this.getWidth() - canvasSize) / 2, 0, canvasSize * multiplier, (int) (canvasSize * 1.4f * multiplier));
		} else {
			shadowPanel.setBounds(0, (this.getHeight() - canvasSize) / 2, (int) (canvasSize * 1.4f * multiplier), canvasSize * multiplier);
		}
		
		imagePreview.setBounds(0, 0, shadowPanel.getWidth(), shadowPanel.getHeight() - 7);
		imagePreview.clearDrawObjects();
		imagePreview.addDrawObject(new drawing.Image(2, 2, imagePreview.getWidth() - 4, 
				imagePreview.getHeight() - 4, image));
		resizeInterface();
		pageManager.drawPageFrames();
	}
	
	private void rotateBufferedImage(String direction) {
		int width = image.getWidth();
		int height = image.getHeight();
	    BufferedImage newImage = new BufferedImage(height, width, image.getType());
	    
		if (direction.equals("right")) {
			for (int i=0; i < width; i++) {
				for (int j=0; j < height; j++) {
					newImage.setRGB(height - 1 - j, i, image.getRGB(i, j));
				}      
			}   
			
		} else if (direction.equals("left")) {
			for (int i=0; i < width; i++) {
				for (int j=0; j < height; j++) {
		        	newImage.setRGB(j, width - 1 -i, image.getRGB(i, j));
		        }
			}        	
		}
		
		image = newImage;
	}
	
	public void resizeInterface() {
		buttonPanel.setBounds((imagePreview.getWidth() - buttonPanelSizeWidth) / 2, 
				imagePreview.getHeight() - 32, buttonPanelSizeWidth, 30);
		previewButton.setBounds(1, 1, 24, 24);
		saveButton.setBounds(28, 1, 24, 24);
		deleteButton.setBounds(55, 1, 24, 24);
	}

	// getters
	
	public String getDocName() {
		return docName;
	}
	
	public PDDocument getDocument() {
		return document;
	}
	
	public Canvas getImagePreview() {
		return imagePreview;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public int getPageNumber() {
		return Integer.parseInt(fieldPageNumber.getText());
	}
	
	public PDPage getPage() {
		PDPage page = this.page;
		return page;
	}
	
	public int getPositionNumber() {
		return this.positionNumber;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public boolean isMultySelect() {
		return isMultySelect;
	}

	public int getFrameSize() {
		return frameSize;
	}
	
	public BufferedImage getHighResolutionImage() {
		int index = document.getPages().indexOf(this.getPage());
		BufferedImage HRImage = null;
		try {
			HRImage = new PDFRenderer(document).renderImageWithDPI(index, 300, ImageType.RGB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return HRImage;
	}
	
	// setters
	
	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}
	
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	public void setPositionNumber(int positionNumber) {
		this.positionNumber = positionNumber;
	}

	public void setMultySelect() {
		imagePreview.setBackground(Color.ORANGE);
		isMultySelect = true;
		pageManager.showRotationButtons();
		this.repaint();
	}
	
	public void unsetMultySelect() {
		imagePreview.setBackground(Color.BLACK);
		isMultySelect = false;
		if(!pageManager.hasMultySelect()) {
			pageManager.hideRotationButtons();
		}
		this.repaint();
	}
	
	// buttons listeners
	
	private class PreviewButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			previewButton.setSelected(false);
			@SuppressWarnings("unused")
			PDFViewer viewer = new PDFViewer(pageManager.getVersion(), PageFrame.this);
			
		}
		
	}
	
	private class SaveButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			saveButton.setSelected(false);
			
			JFileChooser fc;
			if (lastPath == null) {
				fc = new JFileChooser();
			} else {
				fc = new JFileChooser(lastPath);
			}
			
			int returnVal = fc.showSaveDialog(saveButton);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				pageManager.setStatus(Status.SAVING);
				new SavingThread(fc);
				JFrame mainFrame = pageManager.getMainFrame();
				processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), pageManager.getStatus(), mainFrame);
			}
		}
		
		private class SavingThread implements Runnable {
			Thread thread;
			JFileChooser fc;
			
			public SavingThread(JFileChooser fc) {
				this.fc = fc;
				thread = new Thread(this, "SavingThread");
				thread.start();
			}
			
			@Override
			public void run() {
				pageManager.hidePageFrames();
				pageManager.getMainFrame().setResizable(false);
				pageManager.getMenuBar().setVisible(false);
				File file = fc.getSelectedFile();

				if(file.exists()) {
					int n = JOptionPane.showConfirmDialog(
						    pageManager.getMainFrame(),
						    "Файл с таким именем уже существует, хотите его заменить?",
						    "Файл существует",
						    JOptionPane.YES_NO_OPTION);
					if(n == JFileChooser.APPROVE_OPTION) {
						savingProcess(file);
					}
				} else {
					savingProcess(file);
				}
				
				System.out.println("document was saved!");
				pageManager.setStatus(Status.WORKING);;
				processScene.stopAnimation();
		        pageManager.showPageFrames();
		        pageManager.getMainFrame().setResizable(true);
		        pageManager.getMenuBar().setVisible(true);
		        repaint();
		        
		        lastPath = file.getParent();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			private void savingProcess(File file) {
				PDDocument document;
				try {
					document = new PDDocument();
					document.addPage(PageFrame.this.getPage());
					
					String[] splitedPath = file.getPath().split("\\.");
					if (splitedPath[splitedPath.length - 1].equals("pdf")) {
						document.save(file.getPath());
					} else {
						document.save(file.getPath() + ".pdf");	
					}

					document.close();

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class DeleteButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			deleteButton.setSelected(false);
			pageManager.deletePage(getPositionNumber() - 1);
			pageManager.resizeInterface();
		}
		
	}
	
	// mouse listeners
	
	private class DragPageFrameMouseListener implements MouseListener, MouseMotionListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!pageManager.isCtrlPressed()) {
				isSelected = true;
				initialX = e.getX();
				initialY = e.getY();
				new SwapDecorationThread();
				new MoveThread();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int eX = (int) MouseInfo.getPointerInfo().getLocation().getX();
					int eY = (int) MouseInfo.getPointerInfo().getLocation().getY();
					int pfX = (int) PageFrame.this.getLocationOnScreen().getX();
					int pfY = (int) PageFrame.this.getLocationOnScreen().getY();
					
					if(eX >= pfX && eY >= pfY && eX <= pfX + PageFrame.this.getWidth() && eY <= pfY + PageFrame.this.getHeight()) {
						buttonPanel.setVisible(true);
					}
					
				}
			}).start();
			
			if (!pageManager.isCtrlPressed()) {
				isSelected = false;	
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			buttonPanel.setVisible(false);
			if (!pageManager.isCtrlPressed()) {
				if (PageFrame.this.getParent() != null) {
					PageFrame.this.getParent().setComponentZOrder(PageFrame.this, 0);
				}
				int dx = e.getX() - initialX;
				int dy = e.getY() - initialY;
				int x = PageFrame.this.getX() + dx;
				int y = PageFrame.this.getY() + dy;
				PageFrame.this.setLocation(x, y);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
	
		}
	}
	
	private class PageFrameMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			buttonPanel.setVisible(true);
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			buttonPanel.setVisible(false);
			
		}

		@Override
		public void mousePressed(MouseEvent e) {			
			if (pageManager.isCtrlPressed()) {
				if(isMultySelect) {
					unsetMultySelect();
				} else {
					setMultySelect();
				}
			} else {
				setMultySelect();
				pageManager.unsetMultiselectForAllFrames(PageFrame.this.getPositionNumber());
			}
			pageManager.getContentFrame().repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			int x = PageFrame.this.getX() + PageFrame.this.getWidth() / 2;
			int y = PageFrame.this.getY() + PageFrame.this.getHeight() / 2;
			
			for (PageFrame pf : pageManager.getPageFrames()) {
				if (pf.isSelected) {
					continue;
				}
				
				boolean checkA1 = PageFrame.this.getPositionNumber() < pf.getPositionNumber() &&
						x > pf.getX() - pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() / 2 &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
						
				boolean checkB1 = PageFrame.this.getPositionNumber() < pf.getPositionNumber() &&
						x > pf.getX() + pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
						
				boolean checkA2 = PageFrame.this.getPositionNumber() > pf.getPositionNumber() &&
						x > pf.getX() - pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() / 2 &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
						
				boolean checkB2 = PageFrame.this.getPositionNumber() > pf.getPositionNumber() &&
						x > pf.getX() + pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
				
				if (checkA1) {
					for (int i = PageFrame.this.getPositionNumber(); i < pf.getPositionNumber() - 1; i++) {
						pageManager.swapPages(i, i + 1);
					}
				} else if (checkB1) {
					for (int i = PageFrame.this.getPositionNumber(); i < pf.getPositionNumber(); i++) {
						pageManager.swapPages(i, i + 1);
					}
				} else if (checkA2) {
					for (int i = PageFrame.this.getPositionNumber(); i > pf.getPositionNumber(); i--) {
						pageManager.swapPages(i, i - 1);
					}
				} else if (checkB2) {
					for (int i = PageFrame.this.getPositionNumber(); i > pf.getPositionNumber() + 1; i--) {
						pageManager.swapPages(i, i - 1);
					}
				}
			}
			pageManager.getDelimiterDecorator().setVisible(false);
			pageManager.resizeInterface();
			
		}
		
	}


	// Additional panels
	
	private class ShadowPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    Graphics2D g2d = (Graphics2D) g.create();
		    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
		    
		    GradientPaint gp = new GradientPaint(0, getHeight() - 7, new Color(0, 0, 0, 100), 
    		0, getHeight(), new Color(0, 0, 0, 0) );
		    g2d.setPaint(gp);
		    g2d.fillRect(3, getHeight() - 7, getWidth() - 6, getHeight());
		    g2d.dispose();
		}
	}
	
	// threads

	private class MoveThread implements Runnable {
		private Thread thread;
		private int offset;
		
		public MoveThread() {
			thread = new Thread(this);
			thread.start();
		}
		
		@Override
		public void run() {
			startTime = System.currentTimeMillis();
			deltaTime = System.currentTimeMillis() - startTime;
			while (isSelected) {
				if (deltaTime / 1000F >= 0.01) {
					if(PageFrame.this.getY() <= pageManager.getViewport().getViewPosition().getY() - getFrameSize() / 2 && pageManager.getViewport().getViewPosition().getY() > 0) {
						offset = -10;
						pageManager.moveViewportSPane(offset);
						PageFrame.this.setLocation(PageFrame.this.getX(), PageFrame.this.getY() + offset);
					} else if(PageFrame.this.getY() + PageFrame.this.getWidth() >= pageManager.getViewport().getViewPosition().getY() + pageManager.getViewport().getHeight() + getFrameSize() / 2 &&
							pageManager.getViewport().getViewPosition().getY() + pageManager.getViewport().getHeight() < pageManager.getContentPanelPreferSize().getHeight()) {
						offset = +10;
						pageManager.moveViewportSPane(offset);
						PageFrame.this.setLocation(PageFrame.this.getX(), PageFrame.this.getY() + offset);
					} else {
						pageManager.setStatus(Status.WORKING);
					}
					
					startTime = System.currentTimeMillis();
					
				}
				deltaTime = System.currentTimeMillis() - startTime;
			}
			
			pageManager.setStatus(Status.WORKING);
		
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private class SwapDecorationThread implements Runnable{
		private Thread thread;
		
		public SwapDecorationThread() {
			this.thread = new Thread(this);
			thread.start();
		}
			
		@Override
		public void run() {
			while (PageFrame.this.isSelected) {
				swap();
				try {
					Thread.sleep(50);
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
		
		private void swap() {
			int x = PageFrame.this.getX() + PageFrame.this.getWidth() / 2;
			int y = PageFrame.this.getY() + PageFrame.this.getHeight() / 2;
			
			for (PageFrame pf : pageManager.getPageFrames()) {
				if (pf.isSelected) {
					continue;
				}
				
				boolean checkA1 = PageFrame.this.getPositionNumber() < pf.getPositionNumber() &&
						x > pf.getX() - pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() / 2 &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
						
				boolean checkB1 = PageFrame.this.getPositionNumber() < pf.getPositionNumber() &&
						x > pf.getX() + pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
						
				boolean checkA2 = PageFrame.this.getPositionNumber() > pf.getPositionNumber() &&
						x > pf.getX() - pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() / 2 &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
						
				boolean checkB2 = PageFrame.this.getPositionNumber() > pf.getPositionNumber() &&
						x > pf.getX() + pf.getFrameSize() / 2 && x < pf.getX() + pf.getFrameSize() &&
						y > pf.getY() && y < pf.getY() + pf.getHeight();
				
				if (checkA1 || checkA2) {
						int pfXCenter = pf.getX() + pf.getWidth() / 2;
						int pfYCenter = pf.getY() + pf.getHeight() / 2;
						int dDX = pfXCenter - pf.getFrameSize() / 2 - 2;
						int dDY = pfYCenter - pf.getFrameSize() / 2;
						pageManager.getDelimiterDecorator().setBounds(dDX, dDY, 2, pf.getFrameSize());
						pageManager.getContentFrame().setComponentZOrder(pageManager.getDelimiterDecorator(), 1);
						pageManager.getContentFrame().setComponentZOrder(pf, 2);
						pageManager.getDelimiterDecorator().setVisible(true);
				
				} else if (checkB1 || checkB2) {
						int pfXCenter = pf.getX() + pf.getWidth() / 2;
						int pfYCenter = pf.getY() + pf.getHeight() / 2;
						int dDX = pfXCenter + pf.getFrameSize() / 2 - 2;
						int dDY = pfYCenter - pf.getFrameSize() / 2;
						pageManager.getDelimiterDecorator().setBounds(dDX, dDY, 2, pf.getFrameSize());
						pageManager.getContentFrame().setComponentZOrder(pageManager.getDelimiterDecorator(), 1);
						pageManager.getContentFrame().setComponentZOrder(pf, 2);
						pageManager.getDelimiterDecorator().setVisible(true);
						
					}
				
			}
		}
		
	}
	
	
}
