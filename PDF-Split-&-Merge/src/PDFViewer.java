import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class PDFViewer {
	
	private PageFrame pf;
	private JFrame viewFrame;
	private Canvas canvas;
	private JScrollPane sPane;
	private BufferedImage image;
	private String VERSION;
	private StateShowing state;
	private BeautyButton intoWindowButton;
	private BeautyButton originalSizeButton;
	private BeautyButton wideSizeButton;
	
	public PDFViewer(String VERSION, PageFrame pf) {
		this.pf = pf;
		this.VERSION = VERSION;
		state = StateShowing.INTO_WINDOW;
		init();
	}

	private void init() {
		viewFrame = new JFrame();
		viewFrame.setLayout(null);
		viewFrame.setTitle("PDF++ Viewer " + VERSION);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension deffaultDimension = tk.getScreenSize();
		viewFrame.setSize(deffaultDimension);
		viewFrame.setMinimumSize(new Dimension(600, 480));
		viewFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		viewFrame.setBackground(Color.DARK_GRAY);
//		viewFrame.setAlwaysOnTop(true);
		viewFrame.setExtendedState(viewFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		viewFrame.addWindowStateListener(new WindowResizeListener());
		viewFrame.addComponentListener(new WindowResizeListener());
		
		ArrayList<Image> icons = new ArrayList<Image>();
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
		viewFrame.setIconImages(icons);
		
		canvas = new Canvas();
		canvas.setLayout(null);
		canvas.setBackground(new Color(50, 50, 50));
		
		sPane = new JScrollPane(canvas, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sPane.setBackground(new Color(50, 50, 50));
		sPane.setBounds(0, 0, viewFrame.getWidth(), viewFrame.getHeight());
		sPane.getVerticalScrollBar().setUnitIncrement(16);
		sPane.getHorizontalScrollBar().setUnitIncrement(16);
		sPane.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				sPane.repaint();
			}
		});
		
		sPane.getHorizontalScrollBar().addMouseListener(new ScrollPaneListener());
		sPane.getVerticalScrollBar().addMouseListener(new ScrollPaneListener());
		
		// buttons
		intoWindowButton = new BeautyButton("/into_window_64x64.png", "/into_window_rollover_64x64.png", "Вписать в размеры окна");
		intoWindowButton.addActionListener(new IntoWindowButtonListener());
		
		originalSizeButton = new BeautyButton("/original_size_64x64.png", "/original_size_rollover_64x64.png", "В оригинальном размере");
		originalSizeButton.addActionListener(new OriginalSizeButtonListener());
		
		wideSizeButton = new BeautyButton("/width_size_64x64.png", "/width_size_rollover_64x64.png", "По ширине окна");
		wideSizeButton.addActionListener(new WideSizeButtonListener());
		
		reboundsButtons();
		
		sPane.add(intoWindowButton);
		sPane.add(originalSizeButton);
		sPane.add(wideSizeButton);
		defaultOrderComponents();
		viewFrame.add(sPane);

		viewFrame.setVisible(true);
		new LoadingImageThread();
		
	}
	
	private double getRelation(int width, int height) {
		
		return (double)width / (double)height;
	}
	
	private void reboundsButtons() {
		int x = sPane.getWidth() - 90;
		int s = 5;
		intoWindowButton.setBounds(x, 5 + s, 64, 64);
		wideSizeButton.setBounds(x, 83 + s, 64, 64);
		originalSizeButton.setBounds(x, 166 + s, 64, 64);
	}
	
	private void resizeInterface() {
		canvas.setBounds(0, 0, viewFrame.getWidth(), viewFrame.getHeight());
		canvas.clearDrawObjects();
		
		if (state.equals(StateShowing.INTO_WINDOW)) {
			if (image != null && canvas != null) {
				if (image.getHeight() < canvas.getHeight()) {
					canvas.addDrawObject(new drawing.Image((canvas.getWidth() - image.getWidth()) / 2, 
							(canvas.getHeight() - image.getHeight()) / 2, image));
					reboundsButtons();
				} else {
					int y = (int) ((canvas.getWidth() - canvas.getHeight() * getRelation(image.getWidth(), image.getHeight())) / 2);
					int height = canvas.getHeight();
					int width = (int)(canvas.getHeight() * getRelation(image.getWidth(), image.getHeight()));
					canvas.addDrawObject(new drawing.Image(y, 0, width, height, image));
					reboundsButtons();
					canvas.setPreferredSize(new Dimension(0, 0));
					sPane.setBounds(0, 0, viewFrame.getWidth(), viewFrame.getHeight());
					sPane.updateUI();
				}
			}

		} else if(state.equals(StateShowing.ORIGINAL_SIZE)) {
			int x = 0;
			int y = 0;
			
			if (image.getWidth() < canvas.getWidth()) {
				x = (canvas.getWidth() - image.getWidth()) / 2;
			} else {
				x = 0;
			}
			
			if (image.getHeight() < canvas.getHeight()) {
				y = (canvas.getHeight() - image.getHeight() / 2); 
			} else {
				y = 0;
			}
			
			canvas.addDrawObject(new drawing.Image(x, y, image));
			canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			
			if (viewFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
				sPane.setBounds(0, 0, viewFrame.getWidth() - 15, viewFrame.getHeight() - 37);
			} else {
				sPane.setBounds(0, 0, viewFrame.getWidth() - 15, viewFrame.getHeight() - 37);
			}
			reboundsButtons();
			sPane.updateUI();
		} else if (state.equals(StateShowing.WIDE_WINDOW)) {
			
			if (viewFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
				sPane.setBounds(0, 0, viewFrame.getWidth() - 15, viewFrame.getHeight() - 37);
			} else {
				sPane.setBounds(0, 0, viewFrame.getWidth() - 15, viewFrame.getHeight() - 37);
			}
			
			int width = 0;
			int height = 0;

			width = viewFrame.getWidth();
			height = (int)(width * getRelation(image.getHeight(), image.getWidth()));
			
			int x = 0;
			int y = 0;
			
			if (width < sPane.getWidth()) {
				x = (sPane.getWidth() - width) / 2;
			} else {
				x = 0;
			}
			
			if (height < sPane.getHeight()) {
				y = (sPane.getHeight() - height) / 2; 
			} else {
				y = 0;
			}
			
			canvas.addDrawObject(new drawing.Image(x, y, width, height, image));
			canvas.setPreferredSize(new Dimension(0, height));
			
			reboundsButtons();
			sPane.updateUI();
		}
		defaultOrderComponents();
		viewFrame.setResizable(true);
		viewFrame.repaint();
		
	}
	
	private class LoadingImageThread implements Runnable {
		private Thread thread;
		
		public LoadingImageThread() {
			thread = new Thread(this, "LoadingImageThread");
			thread.start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			viewFrame.setResizable(false);
			if (image == null) {
				image = pf.getHighResolutionImage();
			}
			canvas.clearDrawObjects();
			if (state.equals(StateShowing.INTO_WINDOW)) {
				if (image.getHeight() < canvas.getHeight()) {
					canvas.addDrawObject(new drawing.Image((canvas.getWidth() - image.getWidth()) / 2, 
							(canvas.getHeight() - image.getHeight()) / 2, image));
				} else {
					int y = (int) ((canvas.getWidth() - canvas.getHeight() * getRelation(image.getWidth(), image.getHeight())) / 2);
					int height = canvas.getHeight();
					int width = (int)(canvas.getHeight() * getRelation(image.getWidth(), image.getHeight()));
					canvas.addDrawObject(new drawing.Image(y, 0, width, height, image));
				}
			}
			
			canvas.repaint();
			defaultOrderComponents();
			viewFrame.setResizable(true);
		}
		
	}
	
	private class WindowResizeListener extends ComponentAdapter implements WindowStateListener {

		@Override
		public void windowStateChanged(WindowEvent e) {
			resizeInterface();
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			resizeInterface();
		}
	
	}
	
	private void defaultOrderComponents() {
		sPane.setComponentZOrder(intoWindowButton, 0);
		sPane.setComponentZOrder(wideSizeButton, 0);
		sPane.setComponentZOrder(originalSizeButton, 0);
	}
	
	private enum StateShowing {
		INTO_WINDOW,
		ORIGINAL_SIZE,
		WIDE_WINDOW;
	}
	
	private class IntoWindowButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			intoWindowButton.setSelected(false);
			if (!state.equals(StateShowing.INTO_WINDOW)) {
				state = StateShowing.INTO_WINDOW;
				resizeInterface();
			}
			
		}
		
	}
	
	private class OriginalSizeButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			originalSizeButton.setSelected(false);
			if (!state.equals(StateShowing.ORIGINAL_SIZE)) {
				state = StateShowing.ORIGINAL_SIZE;
				resizeInterface();
			}	
		}
	}
	
	private class WideSizeButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			wideSizeButton.setSelected(false);
			if (!state.equals(StateShowing.WIDE_WINDOW)) {
				state = StateShowing.WIDE_WINDOW;
				resizeInterface();
			}		
		}
		
	}
	
	private class ScrollPaneListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			intoWindowButton.setVisible(false);
			wideSizeButton.setVisible(false);
			originalSizeButton.setVisible(false);
			sPane.repaint();
			
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			intoWindowButton.setVisible(true);
			wideSizeButton.setVisible(true);
			originalSizeButton.setVisible(true);	
			sPane.repaint();
			
		}

	}
	
}
