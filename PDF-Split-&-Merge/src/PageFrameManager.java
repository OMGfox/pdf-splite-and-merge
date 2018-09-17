import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PageFrameManager {
	
	private Application application;
	private JPanel contentFrame;
	private JScrollPane sPane;
	private JFrame mainFrame;
	private JTextField numPages;
	private LinkedList<PageFrame> pageFrames;
	private LinkedList<PDDocument> documents;
	
	public PageFrameManager(Application application) {
		this.application = application;
		this.contentFrame = application.getContentFrame();
		this.sPane = application.getSPane();
		this.mainFrame = application.getMainFrame();
		this.numPages = application.getNumPages();
		
		documents = new LinkedList<>();
		pageFrames = new LinkedList<PageFrame>();
	}
	
	// getters
	
	public LinkedList<PageFrame> getPageFrames(){
		return pageFrames;
	}
	
	public int getNumberPageFrames() {
		return pageFrames.size();
	}
	
	public JViewport getViewport() {
		return sPane.getViewport();
	}
	
	public Application getApplication() {
		return application;
	}
	
	public JFrame getMainFrame() {
		return mainFrame;
	}
	
	public JPanel getContentFrame() {
		return contentFrame;
	}
	
	public JPanel getDelimiterDecorator() {
		return application.getDelimiterDecorator();
	}
	
	public Status getStatus() {
		return application.getStatus();
	}

	public JMenuBar getMenuBar() {
		return application.getMenuBar();
		
	}
	
	public boolean isCtrlPressed() {
		return application.isCtrlPressed();
	}
	
	public String getVersion() {
		return application.getVersion();
	}

	public Dimension getContentPanelPreferSize() {
		return application.getContentPanelPreferSize();
	}
	
	// setters
	
	public void setDelimiterDecorator(JPanel delimiterDecorator) {
		application.setDelimiterDecorator(delimiterDecorator);
	}
	
	public void setStatus(Status status) {
		application.setStatus(status);
	}
	
	public void unsetMultiselectForAllFrames(int positionForExeption) {
		for(PageFrame pf : pageFrames) {
			if(pf.getPositionNumber() == positionForExeption) {
				continue;
			} else {
				pf.unsetMultySelect();
			}
		}
	}
	
	//
	
	public void selectAllPageFrames() {
		for(PageFrame pf : pageFrames) {
			pf.setMultySelect();
		}
	}
	
	public void showRotationButtons() {
		application.showRotationButtons();
	}
	
	public void hideRotationButtons() {
		application.hideRotationButtons();
	}
	
	public boolean hasMultySelect() {
		boolean isHas = false;
		for(PageFrame pf : pageFrames) {
			if(pf.isMultySelect()) isHas = true;
		}
		return isHas;
	}
	
	public void resizeInterface() { 
		application.resizeInterface();
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
	
	public boolean checkFileType(File file){
		boolean isPDF = false;
		String[] splitedList = file.getAbsolutePath().split("\\.");
		String fileType = splitedList[splitedList.length - 1].toLowerCase(); 
		if (fileType.equals("pdf")) {
				isPDF = true;
		}
		return isPDF;
	}
	
	public void drawPageFrames() {
		int x = 0;
		int y = 0;
		int yOffset = 0;
		int xOffset = 0;

		for(PageFrame pf : pageFrames) {
			contentFrame.remove(pf);
			int frameSquareSize = pf.getFrameSize();
			int numCol = (mainFrame.getWidth()) / frameSquareSize;
			
			xOffset = (frameSquareSize - pf.getWidth()) / 2 + 10;
			x = ((pf.getPositionNumber() - 1) % numCol) * frameSquareSize + xOffset;
			
			if ((pf.getPositionNumber() - 1) % numCol == 0 && (pf.getPositionNumber() - 1) != 0) {
				yOffset += frameSquareSize;
			}
			y = ((frameSquareSize - pf.getHeight()) / 2) + yOffset;
			
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
			contentFrame.remove(pageFrames.getFirst());
			pageFrames.removeFirst();
		} else {
			contentFrame.remove(pageFrames.get(numPage));
			pageFrames.remove(numPage);
		}
		
		int i = 1;
		for (PageFrame pf : pageFrames) {
			pf.setPositionNumber(i++);
			
		}
		
		drawPageFrames();
		contentFrame.repaint();

	}
		
	public void repaint() {
		if (contentFrame != null) {
			if (pageFrames.size() > 0) {
				PageFrame lastPageFrame = pageFrames.get(pageFrames.size() - 1);
				contentFrame.setPreferredSize(new Dimension(application.getWidth(), lastPageFrame.getY() + lastPageFrame.getHeight() + 5));
			} else {
				contentFrame.setPreferredSize(new Dimension(0, 0));
			}
		}
		if (sPane != null) {
			sPane.updateUI();
		}
		if (numPages != null) {
			numPages.setText(Integer.toString(pageFrames.size()));
		}

		mainFrame.repaint();
	}
	
	public void removeAllPagesFromContentFrame() {
		for(PageFrame pf : pageFrames) {
			contentFrame.remove(pf);
		}
	}

	public void pageFramesClear() {
		pageFrames.clear();
	}
	
	public void documentsClear() {
		documents.clear();
	}

	public void addDocument(PDDocument document) {
		documents.add(document);
	}
	
	public void loadFile(File file) {
        try {
        	PDDocument document = PDDocument.load(file);

			documents.add(document);
			PDPageTree pages = document.getPages();
			int i = pageFrames.size() + 1;
			if (i <= 0) {
				i = 1;
			}
			int index = 0;
			for (PDPage page : pages) {
				BufferedImage image = new PDFRenderer(document).renderImageWithDPI(index++, 75, ImageType.RGB);
				addPage(new PageFrame(i++, (PDPage) page, PageFrameManager.this, image, document));
			}
			
			drawPageFrames();
			
		} catch (IOException ex) {
			ex.printStackTrace();
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
		drawPageFrames();

	}

	public void hidePageFrames(){
		for (PageFrame pf : pageFrames) {
			pf.setVisible(false);
		}
	}
	
    public void showPageFrames() {
		for (PageFrame pf : pageFrames) {
			pf.setVisible(true);
		}
	}

	public void pageLeftRotation() {
		for(PageFrame pf : pageFrames) {
			if(pf.isMultySelect()) {
				pf.rotation("left");
			}
		}	
	}

	public void pageRightRotation() {
		for(PageFrame pf : pageFrames) {
			if(pf.isMultySelect()) {
				pf.rotation("right");
			}
		}	
		
	}
	
}
