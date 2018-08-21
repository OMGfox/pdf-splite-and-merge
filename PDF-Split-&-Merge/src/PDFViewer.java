import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.pdfbox.pdmodel.PDPage;

public class PDFViewer {
	
	private PDPage page;
	private JFrame viewFrame;
	private Canvas canvas;
	private BufferedImage image;
	private JButton buttonBiger;
	private JButton buttonSmaller;
	
	public PDFViewer(PDPage page) {
		this.page = page;
		init();
	}

	private void init() {
		viewFrame = new JFrame();
		viewFrame.setLayout(null);
		viewFrame.setTitle("PDF++ Previewer v0.1-alpha");
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension deffaultDimension = tk.getScreenSize();
		viewFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		viewFrame.setBackground(Color.DARK_GRAY);
		viewFrame.setResizable(false);
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
		
		try {
			image = page.convertToImage(BufferedImage.TYPE_INT_RGB, 150);
			canvas = new Canvas();
			canvas.setLayout(null);
			
			if(image.getWidth() > image.getHeight()) {
				viewFrame.setSize(new Dimension(800, 640));
				viewFrame.setMinimumSize(new Dimension(800, 640));
			} else {
				viewFrame.setSize(new Dimension(640, 800));
				viewFrame.setMinimumSize(new Dimension(640, 800));

			}
			canvas.setBounds(0, 0, viewFrame.getWidth(), viewFrame.getHeight());
			canvas.addDrawObject(new drawing.Image(0, 0, canvas.getWidth(), canvas.getHeight(), image));
			viewFrame.setLocation((int)(deffaultDimension.getWidth() - viewFrame.getWidth()) / 2, 
					(int)(deffaultDimension.getHeight() - viewFrame.getHeight()) / 2);
			viewFrame.add(canvas);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		buttonBiger = new JButton("+");
		buttonBiger.setBounds(canvas.getWidth() - 80, 0, 60, 20);
		buttonBiger.addActionListener(new ButtenResizeListener(1.2));
		buttonBiger.setFocusable(false);
		buttonBiger.setBackground(Color.DARK_GRAY);
		buttonBiger.setForeground(Color.LIGHT_GRAY);
		
		buttonSmaller = new JButton("-");
		buttonSmaller.setBounds(canvas.getWidth() - 140, 0, 60, 20);
		buttonSmaller.addActionListener(new ButtenResizeListener(0.8));
		buttonSmaller.setFocusable(false);
		buttonSmaller.setBackground(Color.DARK_GRAY);
		buttonSmaller.setForeground(Color.LIGHT_GRAY);
		
		canvas.add(buttonBiger);
		canvas.add(buttonSmaller);
		
		viewFrame.setVisible(true);
		
	}
	
	private void resizeInterface(double x) {
		viewFrame.setSize(new Dimension((int)(viewFrame.getWidth() * x), (int)(viewFrame.getHeight() * x)));
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension deffaultDimension = tk.getScreenSize();
		viewFrame.setLocation((int)(deffaultDimension.getWidth() - viewFrame.getWidth()) / 2, 
					(int)(deffaultDimension.getHeight() - viewFrame.getHeight()) / 2);
		viewFrame.remove(canvas);
		
		canvas = new Canvas();
		canvas.setLayout(null);
		canvas.setBounds(0, 0, viewFrame.getWidth(), viewFrame.getHeight());
		canvas.addDrawObject(new drawing.Image(0, 0, canvas.getWidth(), canvas.getHeight(), image));
		buttonBiger.setBounds(canvas.getWidth() - 80, 0, 60, 20);
		buttonSmaller.setBounds(canvas.getWidth() - 140, 0, 60, 20);
		
		canvas.add(buttonSmaller);
		canvas.add(buttonBiger);
		viewFrame.add(canvas);
		viewFrame.repaint();
	}
	
	private class ButtenResizeListener implements ActionListener {

		private double x;
		
		public ButtenResizeListener(double x) {
			this.x = x;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			resizeInterface(x);
		}
		
	}
	
}
