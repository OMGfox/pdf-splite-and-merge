import java.awt.Color;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import drawing.Image;

public class PageFrame extends JPanel{
	private static final long serialVersionUID = 1L;
	private Canvas imagePreview;
	private int positionNumber;
	private int width;
	private int height;
	
	private JToggleButton upPositionButton;
	
	
	public PageFrame(int positionNumber, String url) {
		width = 597;
		height = 200;
		this.positionNumber = positionNumber;
		
		imagePreview = new Canvas();
		imagePreview.setBackground(new Color(0xf2f2f2));
		imagePreview.addDrawObject(new Image(0, 0, 150, 180, url));
		imagePreview.setBounds(10, 10, 150, 180);
		
		JPanel body = new JPanel();
		body.setLayout(null);
		body.setBackground(new Color(0xf2f2f2));
		body.add(imagePreview);
		body.setBounds(0, 0, width, height);
		
//		ѕоле изменени€ пор€дкового номера
		JLabel lablePositionNumber = new JLabel("позици€: ");
		lablePositionNumber.setBounds(215, 25, 70, 20);
		JTextField fieldPositionNumber = new JTextField();
		fieldPositionNumber.setBounds(275, 25, 30, 20);
		fieldPositionNumber.setText(Integer.toString(positionNumber));
		fieldPositionNumber.setHorizontalAlignment(JTextField.CENTER);
		
		upPositionButton = new JToggleButton();
		upPositionButton.setMargin(new Insets(0, 0, 0, 0));
		upPositionButton.setBackground(Color.DARK_GRAY);
		upPositionButton.setBorder(null);
		upPositionButton.setBounds(305, 20, 20, 20);
		upPositionButton.setContentAreaFilled(false);
		
		try {
			Image upPositionButtonIcon = ImageIO.read(getClass().getResource("/folder_blue.png"));
			Image upPositionButtonRolloverIcon = ImageIO.read(getClass().getResource("/folder_blue_rollover.png"));
			upPositionButton.setIcon(new ImageIcon(upPositionButtonIcon));
			upPositionButton.setRolloverIcon(new ImageIcon(upPositionButtonRolloverIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		body.add(lablePositionNumber);
		body.add(fieldPositionNumber);
		body.add(upPositionButton);
		
//		ѕоле изменени€ угла поворота
		JLabel lableDegreeOfRotation = new JLabel("угол поворота: ");
		lableDegreeOfRotation.setBounds(180, 55, 100, 20);
		JTextField fieldDegreeOfRotation = new JTextField();
		fieldDegreeOfRotation.setBounds(275, 55, 30, 20);
		fieldDegreeOfRotation.setText("0");
		fieldDegreeOfRotation.setHorizontalAlignment(JTextField.CENTER);
		fieldDegreeOfRotation.setEditable(false);
		body.add(lableDegreeOfRotation);
		body.add(fieldDegreeOfRotation);
		
		this.setLayout(null);
		this.add(body);
		
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
}
