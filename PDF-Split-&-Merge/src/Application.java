import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;

import Polygon.FileDrop;

public class Application {
	
	private String VERSION;
	
	private JFrame mainFrame;
	private JPanel contentFrame;
	private JPanel topPanel;
	private JScrollPane sPane;
	private JTextField numPages;
	private JLabel numPagesLable;
	private String lastPath;
	private LinkedList<PageFrame> pageFrames;
	private LinkedList<PDDocument> documents;
	private ArrayList<Image> icons;
	private int width;
	private int height;
	private Status status;
	private ProcessScene processScene;
	private JMenuBar menuBar;
	private boolean isCtrlPressed;
	
	private BeautyButton openButton;
	private BeautyButton deleteAllButton;
	private BeautyButton saveButton;

	public Application() {
		VERSION = "v0.5.0-alpha";
		
		new CheckKeyPressing();
		
		this.width = 640;
		this.height = 520;
		status = Status.EMPTY;
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
		mainFrame.setTitle("PDF++ " + VERSION);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(null);	
		mainFrame.setLocationRelativeTo(null); // to center a main window 
		mainFrame.addWindowStateListener(new WindowStateChangeListener());
		mainFrame.addComponentListener(new WindowStateChangeListener());
		mainFrame.addWindowListener(new WindowEventListener());
		mainFrame.setMinimumSize(new Dimension(655, 520));
		
		if (!icons.isEmpty()) {
			mainFrame.setIconImages(icons);
		}
		
		pageFrames = new LinkedList<PageFrame>();
		
		init();
	}
	
	private void init() 
	{
		drawTopPanel();
		drawContentFrame();
		drawMenu();
		drawMenu();
	}
	
	private void drawMenu() {
		menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("Файл");
		menuBar.add(menuFile);
		JMenuItem menuItemOpen = new JMenuItem("Открыть");
		menuItemOpen.addActionListener(new OpenListener());
		menuFile.add(menuItemOpen);
		
		JMenuItem menuItemSaveAs = new JMenuItem("Сохранить как...");
		menuItemSaveAs.addActionListener(new SaveListener());
		menuFile.add(menuItemSaveAs);
		
		JMenuItem menuItemSaveSelectedAs = new JMenuItem("Сохранить выделенные как...");
		menuItemSaveSelectedAs.addActionListener(new SaveSelectedListener());
		menuFile.add(menuItemSaveSelectedAs);
		
		menuFile.addSeparator();
		
		JMenuItem menuItemExit = new JMenuItem("Выход");
		menuItemExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuFile.add(menuItemExit);
		
		JMenu menuAdditional = new JMenu("Дополнительно");
		menuBar.add(menuAdditional);
		
		JMenuItem menuDeleteAll = new JMenuItem("Очистить список");
		menuDeleteAll.addActionListener(new DeleteAllListener());
		menuAdditional.add(menuDeleteAll);
		
		JMenuItem menuSaveAllToSingleFile = new JMenuItem("Сохранить каждую страницу в отдельный файл");
		menuSaveAllToSingleFile.addActionListener(new SaveAllToSingleFileListener());
		menuAdditional.add(menuSaveAllToSingleFile);
		
		// Sorting
		menuAdditional.addSeparator();
		
		JMenuItem menuSortByPageNumber = new JMenuItem("Сортировать по номеру");
		menuSortByPageNumber.addActionListener(new SortByOrder(Sorting.PAGE_NUMBER));
		menuAdditional.add(menuSortByPageNumber);
		
		JMenuItem menuSortByPageNuberInverse = new JMenuItem("Сортировать по номеру в обратном порядке");
		menuSortByPageNuberInverse.addActionListener(new SortByOrder(Sorting.PAGE_NUMBER_INVERSE));
		menuAdditional.add(menuSortByPageNuberInverse);
		
		JMenu menuHelp = new JMenu("Помощь");
		menuBar.add(menuHelp);
		
		JMenuItem menuItemAbout = new JMenuItem("О PDF++");
		menuHelp.add(menuItemAbout);
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}
	
	public String getVersion() {
		return VERSION;
	}
	
	public Dimension getContentPanelPreferSize() {
		return contentFrame.getPreferredSize();
	}
	
	private void drawTopPanel() {
		topPanel = new JPanel();
		topPanel.setSize(new Dimension(mainFrame.getWidth() - 15, 38));
		topPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(209,231,81)));
		topPanel.setBackground(new Color(0, 0, 0));
		topPanel.setLayout(null);
		
		numPagesLable = new JLabel("Страниц:");
		numPagesLable.setForeground(new Color(0xf2f2f2));
		numPagesLable.setBounds(mainFrame.getWidth() - 195, 10, 80, 20);
		numPages = new JTextField();
		numPages.setEditable(false);
		numPages.setText("0");
		numPages.setHorizontalAlignment(JTextField.RIGHT);
		numPages.setBounds(mainFrame.getWidth() - 135, 10, 40, 20);

		openButton = new BeautyButton("/folder_blue_48x48.png", "/folder_blue_rollover_48x48.png", "Добавить файл");
		openButton.setBounds(10, 3, 30, 30);
		openButton.addActionListener(new OpenListener());
		
		saveButton = new BeautyButton("/save_button_32x32.png", "/save_button_rollover_32x32.png", "Сохранить в файл");
		saveButton.setBounds(57, 4, 30, 30);
		saveButton.addActionListener(new SaveListener());
		
		deleteAllButton = new BeautyButton("/button_delete_32x32.png", "/button_delete_rollover_32x32.png", "Очистить все");
		deleteAllButton.setBounds(topPanel.getWidth() - 35, 3, 30, 30);
		deleteAllButton.addActionListener(new DeleteAllListener());
		
		topPanel.add(numPagesLable);
		topPanel.add(numPages);
		topPanel.add(openButton);
		topPanel.add(saveButton);
		topPanel.add(deleteAllButton);
		
	}
	
	public LinkedList<PageFrame> getPageFrames(){
		return pageFrames;
	}
	
	public JViewport getViewport() {
		return sPane.getViewport();
		
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
		contentFrame.setBackground(Color.WHITE);
		contentFrame.setLayout(null);
		new  FileDrop( contentFrame, new FileDrop.Listener()
		  {   public void  filesDropped( java.io.File[] files )
		      {   
		         // handle file drop
		        boolean isPDF = true;
		        for (File file : files) {
		        	if (!checkFileType(file)){
		        		isPDF = false;
		        	}
		        }
		        
		        if (isPDF){
		        	processScene.stopWelcom();
					status = Status.OPENING;
					new DragOpeningThread(files);
					processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
		        }

		      }   // end filesDropped
		  }); // end FileDrop.Listener
		
		sPane = new JScrollPane(contentFrame, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sPane.setBounds(0, 38, width, 445);
		sPane.getVerticalScrollBar().setUnitIncrement(16);
	}
	
	private boolean checkFileType(File file){
		boolean isPDF = false;
		String[] splitedList = file.getAbsolutePath().split("\\.");
		String fileType = splitedList[splitedList.length - 1].toLowerCase(); 
		if (fileType.equals("pdf")) {
				isPDF = true;
		}
		return isPDF;
	}
	
	public void drawPageFrames() {
		for(PageFrame pf : pageFrames) {
			if (mainFrame.getWidth() > 1024) {
				pf.setWidth(1024);
			} else {
				pf.setWidth(mainFrame.getWidth() - 30);
			}
			int x = (mainFrame.getWidth() - pf.getWidth() - 15) / 2;
			int y = (pf.getPositionNumber() - 1) * 206 + 5;
			pf.setBounds(x, y, pf.getWidth(), pf.getHeight());
			contentFrame.add(pf);
		}
		repaint();
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public ProcessScene getProcessScene() {
		return processScene;
	}
	
	public JFrame getMainFrame() {
		return mainFrame;
	}
	
	public void repaintPageFrames() {
		for(PageFrame pf : pageFrames) {
			if (mainFrame.getWidth() > 1024) {
				pf.setWidth(1024);
			} else {
				pf.setWidth(mainFrame.getWidth() - 30);
			}
			int x = (mainFrame.getWidth() - pf.getWidth() - 15) / 2;
			int y = (pf.getPositionNumber() - 1) * 206 + 5;
			pf.setBounds(x, y, pf.getWidth(), pf.getHeight());
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
		processScene = new ProcessScene(contentFrame.getWidth(), contentFrame.getHeight(), status, contentFrame);
		
	}
	
	public void repaint() {
		if (contentFrame != null) {
			contentFrame.setPreferredSize(new Dimension(width, pageFrames.size() * 206));
		}
		if (sPane != null) {
			sPane.updateUI();
		}
		if (numPages != null) {
			numPages.setText(Integer.toString(pageFrames.size()));
		}

		mainFrame.repaint();
	}

	public void swapPages(int first, int second) {
		PageFrame temp = pageFrames.get(first - 1);
		pageFrames.set(first - 1, pageFrames.get(second - 1));
		pageFrames.set(second - 1, temp);
		int i = 1;
		for (PageFrame pf : pageFrames) {
			pf.setPositionNumber(i++);
		}
		repaintPageFrames();

	}
	
	public void swapPagesWithNoRepaint(int first, int second) {
		PageFrame temp = pageFrames.get(first - 1);
		pageFrames.set(first - 1, pageFrames.get(second - 1));
		pageFrames.set(second - 1, temp);
		int i = 1;
		for (PageFrame pf : pageFrames) {
			pf.setPositionNumber(i++);
		}
	}
	
	public void resizeInterface() {
		if (deleteAllButton != null) {
			topPanel.setSize(new Dimension(mainFrame.getWidth() - 15, 38));
			deleteAllButton.setBounds(topPanel.getWidth() - 35, 3, 30, 30);
			sPane.setBounds(0, 38, mainFrame.getWidth() - 15, mainFrame.getHeight() - 98);

			if (contentFrame.getPreferredSize().getHeight() > contentFrame.getSize().getHeight()) {
				sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			} else {
				sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			}
			numPages.setBounds(mainFrame.getWidth() - 135, 10, 40, 20);
			numPagesLable.setBounds(mainFrame.getWidth() - 195, 10, 80, 20);
			
			if (pageFrames.size() > 0) {
				for (PageFrame pf : pageFrames) {
					pf.resizeInterface();
				}
				repaintPageFrames();
			}
			repaint();	
		}

	}

	public void hidePageFrames(){
		for (PageFrame pf : pageFrames) {
			pf.setVisible(false);
		}
	}
	
    void showPageFrames() {
		for (PageFrame pf : pageFrames) {
			pf.setVisible(true);
		}
	}
	
	public boolean isCtrlPressed() {
		return isCtrlPressed;
	}

	public void setCtrlPressed(boolean isCtrlPressed) {
		this.isCtrlPressed = isCtrlPressed;
	}

	private class DeleteAllListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			pageFrames.clear();
			documents.clear();
			deleteAllButton.setSelected(false);
			contentFrame.removeAll();
			repaint();
			status = Status.EMPTY;
		}
	}
	
	private class OpenListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc;
			if (lastPath == null) {
				fc = new JFileChooser();
			} else {
				fc = new JFileChooser(lastPath);
			}
			
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
			fc.setFileFilter(filter);
			int returnVal = fc.showOpenDialog(openButton);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				processScene.stopWelcom();
				status = Status.OPENING;
				new OpeningThread(fc);
				processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
	        } 
			repaint();
			
			openButton.setSelected(false);	
			
		}	
		
		private class OpeningThread implements Runnable {
			Thread thread;
			JFileChooser fc;
			
			public OpeningThread(JFileChooser fc) {
				this.fc = fc;
				thread = new Thread(this, "OpeningThread");
				thread.start();
			}
			
			@Override
			public void run() {
				hidePageFrames();
				mainFrame.setResizable(false);
				menuBar.setVisible(false);
	            File file = fc.getSelectedFile();
	            try {
					documents.add(PDDocument.load(file));
					PDDocumentCatalog docCatalog = documents.get(documents.size() - 1).getDocumentCatalog();
					@SuppressWarnings("unchecked")
					List<PDPage> pages = docCatalog.getAllPages();
					int i = pageFrames.size() + 1;
					if (i <= 0) {
						i = 1;
					}
					for (PDPage page : pages) {
						addPage(new PageFrame(i++, (PDPage) page, Application.this));
					}
					
					drawPageFrames();
					lastPath = file.getParent();
					
				} catch (IOException ex) {
					ex.printStackTrace();
				} 
	            
	            status = Status.WORKING;
	            processScene.stopAnimation();
	            showPageFrames();
	            resizeInterface();
	            System.out.println("document was opened!");
	            mainFrame.setResizable(true);
	            menuBar.setVisible(true);
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class SaveListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveButton.setSelected(false);
			if (pageFrames.size() > 0) {
				JFileChooser fc;
				if (lastPath == null) {
					fc = new JFileChooser();
				} else {
					fc = new JFileChooser(lastPath);
				}
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
				fc.setFileFilter(filter);
				int returnVal = fc.showSaveDialog(saveButton);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					status = Status.SAVING;
					new SavingThread(fc);
					processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
				}
			} else {
				JOptionPane.showMessageDialog(mainFrame,
					    "Документ должен содержать минимум одну страницу.",
					    "Сохранение файла",
					    JOptionPane.WARNING_MESSAGE);
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
				hidePageFrames();
				mainFrame.setResizable(false);
				menuBar.setVisible(false);
				File file = fc.getSelectedFile();
				PDDocument document;
				try {
					document = new PDDocument();
					for (PageFrame pf : pageFrames) {
						
						document.importPage(pf.getPage());
					}
					
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
				} catch (COSVisitorException e) {
					e.printStackTrace();
				} 
				System.out.println("document was saved!");
				status = Status.WORKING;
				processScene.stopAnimation();
	            showPageFrames();
	            mainFrame.setResizable(true);
	            menuBar.setVisible(true);
	            repaint();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class SaveSelectedListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveButton.setSelected(false);
			if (pageFrames.size() > 0 && isHasMultySelectPages()) {
				JFileChooser fc;
				if (lastPath == null) {
					fc = new JFileChooser();
				} else {
					fc = new JFileChooser(lastPath);
				}
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
				fc.setFileFilter(filter);
				int returnVal = fc.showSaveDialog(saveButton);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					status = Status.SAVING;
					new SavingSelectedThread(fc);
					processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
				}
			} else {
				JOptionPane.showMessageDialog(mainFrame,
					    "Документ должен содержать минимум одну выделенную страницу.",
					    "Сохранение файла",
					    JOptionPane.WARNING_MESSAGE);
			}

		}
		
		private boolean isHasMultySelectPages() {
			
			boolean hasIt = false;
			
			for (PageFrame pf : pageFrames) {
				if (pf.isMultySelect()) {
					hasIt = true;
					break;
				}
			}
			
			return hasIt;
		}
		
		private class SavingSelectedThread implements Runnable {
			Thread thread;
			JFileChooser fc;
			
			public SavingSelectedThread(JFileChooser fc) {
				this.fc = fc;
				thread = new Thread(this, "SavingThread");
				thread.start();
			}
			
			@Override
			public void run() {
				hidePageFrames();
				mainFrame.setResizable(false);
				File file = fc.getSelectedFile();
				PDDocument document;
				try {
					document = new PDDocument();
					for (PageFrame pf : pageFrames) {
						if (pf.isMultySelect()) {
							document.importPage(pf.getPage());
						}
					}
					
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
				} catch (COSVisitorException e) {
					e.printStackTrace();
				} 
				System.out.println("document was saved!");
				status = Status.WORKING;
				processScene.stopAnimation();
	            showPageFrames();
	            mainFrame.setResizable(true);
	            repaint();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class SaveAllToSingleFileListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			saveButton.setSelected(false);
			if (pageFrames.size() > 0) {
				JFileChooser fc;
				if (lastPath == null) {
					fc = new JFileChooser();
				} else {
					fc = new JFileChooser(lastPath);
				}
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
				fc.setFileFilter(filter);
				int returnVal = fc.showSaveDialog(saveButton);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					status = Status.SAVING;
					new SaveAllToSingleFileThread(fc);
					processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
				}
			} else {
				JOptionPane.showMessageDialog(mainFrame,
					    "Документ должен содержать минимум одну страницу.",
					    "Сохранение файла",
					    JOptionPane.WARNING_MESSAGE);
			}

		}
		
		private class SaveAllToSingleFileThread implements Runnable {
			Thread thread;
			JFileChooser fc;
			
			public SaveAllToSingleFileThread(JFileChooser fc) {
				this.fc = fc;
				thread = new Thread(this, "SavingThread");
				thread.start();
			}
			
			@Override
			public void run() {
				hidePageFrames();
				mainFrame.setResizable(false);
				File file = fc.getSelectedFile();
				PDDocument document;
				try {
					int i = 1;
					for (PageFrame pf : pageFrames) {
						document = new PDDocument();
						document.importPage(pf.getPage());
						
						String[] splitedPath = file.getPath().split("\\.");
						if (splitedPath[splitedPath.length - 1].equals("pdf")) {
							document.save(file.getPath().replaceAll(".pdf", "") + " page(" + i + ")" + ".pdf" );
						} else {
							document.save(file.getPath() + " page(" + i + ")" + ".pdf");	
						}

						document.close();
						i++;
					}
					
					lastPath = file.getParent();

				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (COSVisitorException e) {
					e.printStackTrace();
				} 
				System.out.println("document was saved!");
				status = Status.WORKING;
				processScene.stopAnimation();
	            showPageFrames();
	            mainFrame.setResizable(true);
	            repaint();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private class WindowStateChangeListener extends ComponentAdapter implements WindowStateListener{

		@Override
		public void windowStateChanged(WindowEvent e) {
			resizeInterface();
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
            resizeInterface();
        }

	}

	private class WindowEventListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	private class SortByOrder implements ActionListener{

		private Sorting sortBy;
		
		public SortByOrder(Sorting sortBy) {
			this.sortBy = sortBy;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (sortBy.equals(Sorting.PAGE_NUMBER)) {
				sort(new Comparator<PageFrame>() {

				@Override
				public int compare(PageFrame pf1, PageFrame pf2) {
					int result = 0;
					if (pf1.getPageNumber() < pf2.getPageNumber()) {
						result = -1;
					}
					if (pf1.getPageNumber() > pf2.getPageNumber()) {
						result = 1;
					}
					return result;
				}
				
			});
			} else if(sortBy.equals(Sorting.PAGE_NUMBER_INVERSE)) {
				sort(new Comparator<PageFrame>() {

					@Override
					public int compare(PageFrame pf1, PageFrame pf2) {
						int result = 0;
						if (pf1.getPageNumber() > pf2.getPageNumber()) {
							result = -1;
						}
						if (pf1.getPageNumber() < pf2.getPageNumber()) {
							result = 1;
						}
						return result;
					}
					
				});
			}
			
		}
		
		private void sort(Comparator<PageFrame> comparator) {
			contentFrame.removeAll();
			pageFrames.sort(comparator);
			
			int i = 1;
			for (PageFrame pf : pageFrames) {
				pf.setPositionNumber(i++);
			}
			drawPageFrames();
		}
		
	}
	
	private class DragOpeningThread implements Runnable {
		private Thread thread;
		private File[] files;
		
		
		public DragOpeningThread(File[] files) {
			this.files = files;
			thread = new Thread(this, "DragOpeningThread");
			thread.start();
		}
		
		@Override
		public void run() {
			hidePageFrames();
			menuBar.setVisible(false);
			mainFrame.setResizable(false);
            for (File file : files) {       	
            	try {
					documents.add(PDDocument.load(file));
					PDDocumentCatalog docCatalog = documents.get(documents.size() - 1).getDocumentCatalog();
					@SuppressWarnings("unchecked")
					List<PDPage> pages = docCatalog.getAllPages();
					int i = pageFrames.size() + 1;
					if (i <= 0) {
						i = 1;
					}
					for (PDPage page : pages) {
						addPage(new PageFrame(i++, (PDPage) page, Application.this));
					}
					
					drawPageFrames();
					lastPath = file.getParent();
					
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
            status = Status.WORKING;
            processScene.stopAnimation();
            showPageFrames();
            resizeInterface();
            System.out.println("document was opened!");
            mainFrame.setResizable(true);
            menuBar.setVisible(true);
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	private class CheckKeyPressing implements Runnable {
		private Thread thread;
		private KeyboardFocusManager keyboardFocusManager;
		
		public CheckKeyPressing() {
			thread = new Thread(this, "CheckKeyPressing");
			keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			keyboardFocusManager.addKeyEventDispatcher(new KeyEventDispatcher() {
				
				@Override
				public boolean dispatchKeyEvent(KeyEvent ke) {
					switch (ke.getID()) {
	                case KeyEvent.KEY_PRESSED:
	                    if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
	                        setCtrlPressed(true);
	                    }
	                    break;

	                case KeyEvent.KEY_RELEASED:
	                    if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
	                    	setCtrlPressed(false);
	                    }
	                    break;
					}
					return false;
				}
				
			});
			thread.start();
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (true) {

			}
			
		}
		
	}
}
