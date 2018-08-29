import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PageFrame extends JPanel{
	private static final long serialVersionUID = 1L;
	private Canvas imagePreview;
	private int positionNumber;
	private int width;
	private int height;
	private BeautyButton leftRotationButton;
	private BeautyButton rightRotationButton;
	private BeautyButton previewButton;
	private BeautyButton saveButton;
	private BeautyButton deleteButton;
	private PDPage page;
	private BufferedImage image;
	private Application app;
	private JTextField fieldPageNumber;
	private JTextField fieldDegreeOfRotation;
	private int rotation;
	private String lastPath;
	private boolean isSelected;
	private int initialX;
	private int initialY;
	private long startTime;
	private long deltaTime;

	public PageFrame(int positionNumber, PDPage page, Application app) {

		this.app = app;
		this.positionNumber = positionNumber;
		this.page = page;
		isSelected = false;
		rotation = 0;
		width = 597;
		height = 200;
		image = getBufferedImage();
		setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0, 0, 0)));
		setLayout(null);
		setBackground(new Color(209,231,81));
		addMouseListener(new PageFrameMouseListener());
		addMouseListener(new DragPageFrameMouseListener());
		addMouseMotionListener(new DragPageFrameMouseListener());
		init();
	}
	
	private BufferedImage getBufferedImage() {
		BufferedImage image = null;
		try {
			image = page.convertToImage(BufferedImage.TYPE_INT_RGB, 16);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	private void init() {
//		Поле номер страницы
		JLabel lablePageNumber = new JLabel("№ стр.: ");
		lablePageNumber.setBounds(250, 25, 70, 20);
		
		fieldPageNumber = new JTextField();
		fieldPageNumber.setBounds(300, 25, 30, 20);
		fieldPageNumber.setText(Integer.toString(positionNumber));
		fieldPageNumber.setHorizontalAlignment(JTextField.CENTER);
		fieldPageNumber.setEditable(false);
		fieldPageNumber.addMouseListener(new PageFrameMouseListener());
		
//		Поля поворота по и против часовой
		JLabel lableDegreeOfRotation = new JLabel("Поворот: ");
		lableDegreeOfRotation.setBounds(240, 55, 100, 20);
		
		fieldDegreeOfRotation = new JTextField();
		fieldDegreeOfRotation.setBounds(300, 55, 30, 20);
		fieldDegreeOfRotation.setText("0");
		fieldDegreeOfRotation.setHorizontalAlignment(JTextField.CENTER);
		fieldDegreeOfRotation.setEditable(false);
		fieldDegreeOfRotation.addMouseListener(new PageFrameMouseListener());
		
		leftRotationButton = new BeautyButton("/spinner_left.png", "/spinner_left_rollover.png", "Повернуть против часовой");
		leftRotationButton.setBounds(335, 55, 20, 20);
		leftRotationButton.addActionListener(new LeftRotationButtonListener());
		leftRotationButton.addMouseListener(new PageFrameMouseListener());

		rightRotationButton = new BeautyButton("/spinner_right.png", "/spinner_right_rollover.png", "Повернуть по часовой");
		rightRotationButton.setBounds(355, 55, 20, 20);
		rightRotationButton.addActionListener(new RightRotationButtonListener());
		rightRotationButton.addMouseListener(new PageFrameMouseListener());
		
//		Предпросмотр странички
		previewButton = new BeautyButton("/search_24x24.png", "/search_24x24.png", "Посмотреть");
		previewButton.setBounds(180, 25, 24, 24);
		previewButton.setVisible(false);
		previewButton.addMouseListener(new PageFrameMouseListener());
		previewButton.addMouseListener(new CanvasMouseListener());
		
		saveButton = new BeautyButton("/save_button_24x24.png", "/save_button_rollover_24x24.png", "Сохранить страничку в файл");
		saveButton.setBounds(this.width - 72, 7, 24, 24);
		saveButton.setVisible(false);
		saveButton.addActionListener(new SaveButtonListener());
		saveButton.addMouseListener(new PageFrameMouseListener());
		
		deleteButton = new BeautyButton("/button_delete_red_24x24.png", "/button_delete_red_rollover_24x24.png", "Удалить");
		deleteButton.setBounds(this.width - 40, 7, 24, 24);
		deleteButton.setVisible(false);
		deleteButton.addActionListener(new DeleteButtonListener());
		deleteButton.addMouseListener(new PageFrameMouseListener());
		
//		Добавление элементов на PageFrame
		add(previewButton);
		initImagePreview();
		add(lablePageNumber);
		add(fieldPageNumber);
		add(leftRotationButton);
		add(lableDegreeOfRotation);
		add(fieldDegreeOfRotation);
		add(rightRotationButton);
		
		add(saveButton);
		add(deleteButton);
	}
	
	private void initImagePreview() {
		imagePreview = new Canvas();
		imagePreview.setBackground(new Color(77,188,233, 25));
		imagePreview.addDrawObject(new drawing.Image((180 - image.getWidth()) / 2, (180 - image.getHeight()) / 2, image));
		imagePreview.setBounds(25, 10, 180, 180);
		imagePreview.addMouseListener(new CanvasMouseListener());
		imagePreview.addMouseListener(new PageFrameMouseListener());
		imagePreview.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(38,173,228)));
		PageFrame.this.add(imagePreview);
	}
	
	public void resizeInterface() {
		previewButton.setBounds(180, 25, 24, 24);
		saveButton.setBounds(this.width - 72, 7, 24, 24);
		deleteButton.setBounds(this.width - 40, 7, 24, 24);
	}
	
	public PDPage getPage() {
		PDPage page = this.page;
		return page;
	}
	
	public int getPositionNumber() {
		return this.positionNumber;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setPositionNumber(int positionNumber) {
		this.positionNumber = positionNumber;
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	private class LeftRotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			leftRotationButton.setSelected(false);
			rotation -= 90;
			if (rotation == -360) rotation = 0;
			page.setRotation(rotation);
			fieldDegreeOfRotation.setText(Integer.toString(rotation));
			image = getBufferedImage();
			PageFrame.this.remove(imagePreview);
			initImagePreview();
			app.repaint();
		}
		
	}
	
	private class RightRotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			rightRotationButton.setSelected(false);
			rotation += 90;
			if (rotation == 360) rotation = 0;
			page.setRotation(rotation);
			fieldDegreeOfRotation.setText(Integer.toString(rotation));
			image = getBufferedImage();
			PageFrame.this.remove(imagePreview);
			initImagePreview();
			app.repaint();
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
				File file = fc.getSelectedFile();
				PDDocument document;
				try {
					document = new PDDocument();
					document.addPage(getPage());
					
					String[] splitedPath = file.getPath().split("\\.");
					if (splitedPath[splitedPath.length - 1].equals("pdf")) {
						document.save(file.getPath());
					} else {
						document.save(file.getPath() + ".pdf");	
					}
				
					lastPath = file.getParent();
					document.close();

				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (COSVisitorException ex1) {
					ex1.printStackTrace();
				} 
			}
		}
		
	}
	
	private class DeleteButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			deleteButton.setSelected(false);
			app.deletePage(getPositionNumber() - 1);
			app.resizeInterface();
		}
		
	}
	
	private class DragPageFrameMouseListener implements MouseListener, MouseMotionListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0, 0, 0)));
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {

			setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0, 0, 0)));
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			PageFrame.this.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(38,173,228)));
			isSelected = true;
			initialX = e.getX();
			initialY = e.getY();
			new SwapThread();
			new MoveThread();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0, 0, 0)));
			isSelected = false;	
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			PageFrame.this.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(38,173,228)));
			PageFrame.this.getParent().setComponentZOrder(PageFrame.this, 0);
			int dx = e.getX() - initialX;
			int dy = e.getY() - initialY;
			int x = PageFrame.this.getX() + dx;
			int y = PageFrame.this.getY() + dy;
			PageFrame.this.setLocation(x, y);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class PageFrameMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			saveButton.setVisible(true);
			deleteButton.setVisible(true);
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			saveButton.setVisible(false);
			deleteButton.setVisible(false);
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			app.resizeInterface();
		}
		
	}
	
	private class CanvasMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			previewButton.setVisible(true);
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			previewButton.setVisible(false);
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			@SuppressWarnings("unused")
			PDFViewer viewer = new PDFViewer(getPage());
			
		}
		
	}
	
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
				if (deltaTime / 1000F >= 0.001) {
					if(PageFrame.this.getY() <= app.getViewport().getViewPosition().getY() - 10 && app.getViewport().getViewPosition().getY() > 0) {
						offset = -1;
						app.moveViewportSPane(offset);
						PageFrame.this.setLocation(PageFrame.this.getX(), PageFrame.this.getY() + offset);
					}
					if(PageFrame.this.getY() + PageFrame.this.height >= app.getViewport().getViewPosition().getY() + app.getViewport().getHeight() + 10 &&
							app.getViewport().getViewPosition().getY() + app.getViewport().getHeight() < app.getContentPanelPreferSize().getHeight()) {
						offset = +1;
						app.moveViewportSPane(offset);
						PageFrame.this.setLocation(PageFrame.this.getX(), PageFrame.this.getY() + offset);
					}
					
					startTime = System.currentTimeMillis();
					
				}
				deltaTime = System.currentTimeMillis() - startTime;
			}
		
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private class SwapThread implements Runnable{
		private Thread thread;
		
		public SwapThread() {
			this.thread = new Thread(this);
			thread.start();
		}
			
		@Override
		public void run() {
			while (PageFrame.this.isSelected) {
				check();
			}
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void check() {
			int y = PageFrame.this.getY();
			for (PageFrame pf : app.getPageFrames()) {
				if (pf.isSelected) {
					continue;
				}
				if ((pf.getY() < y + pf.height / 2 && pf.getY() + pf.height / 2 > y + pf.height / 2 ||
						pf.getY() + pf.height / 2 < y + pf.height / 2 && pf.getY() + pf.height > y + pf.height /2)) {
					app.swapPages(PageFrame.this.getPositionNumber(), pf.getPositionNumber());
				}
			}
		}
		
	}
}
