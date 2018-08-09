import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class PageFrame extends JPanel{
	private static final long serialVersionUID = 1L;
	private Canvas imagePreview;
	private int positionNumber;
	private int width;
	private int height;
	private BeautyButton leftRotationButton;
	private BeautyButton rightRotationButton;
	private BeautyButton upPositionButton;
	private BeautyButton downPositionButton;
	private BeautyButton previewButton;
	private BeautyButton saveButton;
	private BeautyButton deleteButton;
	
	public PageFrame(int positionNumber, String url) {
		width = 597;
		height = 200;
		this.positionNumber = positionNumber;
		setLayout(null);
		setBackground(new Color(0xf2f2f2));
		
		imagePreview = new Canvas();
		imagePreview.setBackground(new Color(0xf2f2f2));
		imagePreview.addDrawObject(new drawing.Image(0, 0, 150, 180, url));
		imagePreview.setBounds(10, 10, 150, 180);
		
//		Поле изменения порядкового номера
		JLabel lablePositionNumber = new JLabel("позиция: ");
		lablePositionNumber.setBounds(215, 25, 70, 20);
		
		JTextField fieldPositionNumber = new JTextField();
		fieldPositionNumber.setBounds(275, 25, 30, 20);
		fieldPositionNumber.setText(Integer.toString(positionNumber));
		fieldPositionNumber.setHorizontalAlignment(JTextField.CENTER);
		
		upPositionButton = new BeautyButton("/arrow-up.png", "/arrow-up_rollover.png", "Переместить выше");	
		upPositionButton.setBounds(310, 17, 40, 15);
		
		downPositionButton = new BeautyButton("/arrow-down.png", "/arrow-down_rollover.png", "Переместить ниже");
		downPositionButton.setBounds(310, 36, 40, 15);
		
//		Поле изменения угла поворота
		JLabel lableDegreeOfRotation = new JLabel("угол поворота: ");
		lableDegreeOfRotation.setBounds(180, 55, 100, 20);
		
		JTextField fieldDegreeOfRotation = new JTextField();
		fieldDegreeOfRotation.setBounds(275, 55, 30, 20);
		fieldDegreeOfRotation.setText("0");
		fieldDegreeOfRotation.setHorizontalAlignment(JTextField.CENTER);
		fieldDegreeOfRotation.setEditable(false);
		
		leftRotationButton = new BeautyButton("/spinner_left.png", "/spinner_left_rollover.png", "Повернуть против часовой");
		leftRotationButton.setBounds(310, 55, 20, 20);

		rightRotationButton = new BeautyButton("/spinner_right.png", "/spinner_right_rollover.png", "Повернуть по часовой");
		rightRotationButton.setBounds(330, 55, 20, 20);
		
//		Дополнительные кнопки 
		previewButton = new BeautyButton("/search_24x24.png", "/search_rollover_24x24.png", "Посмотреть");
		previewButton.setBounds(485, 5, 24, 24);
		
		saveButton = new BeautyButton("/save_button_24x24.png", "/save_button_rollover_24x24.png", "Сохранить страницу");
		saveButton.setBounds(520, 5, 24, 24);
		
		deleteButton = new BeautyButton("/button_delete_red_24x24.png", "/button_delete_red_rollover_24x24.png", "Удалить страницу");
		deleteButton.setBounds(555, 5, 24, 24);
		
//		Добавляем все элементы на PageFrame
		add(lablePositionNumber);
		add(fieldPositionNumber);
		add(upPositionButton);
		add(leftRotationButton);
		add(lableDegreeOfRotation);
		add(fieldDegreeOfRotation);
		add(downPositionButton);
		add(rightRotationButton);
		add(previewButton);
		add(saveButton);
		add(deleteButton);
		add(imagePreview);
		
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
	
	private class UpPositionButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			upPositionButton.setSelected(false);
			
		}
		
	}
	
	private class DownPositionButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			downPositionButton.setSelected(false);
			
		}
		
	}
	
	private class LeftRotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			leftRotationButton.setSelected(false);
			
		}
		
	}
	
	private class RightRotationButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			rightRotationButton.setSelected(false);
			
		}
		
	}
	
	private class PreviewButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			previewButton.setSelected(false);
			
		}
		
	}
	
	private class SaveButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			saveButton.setSelected(false);
			
		}
		
	}
	
	private class DeleteButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			deleteButton.setSelected(false);
			
		}
		
	}
	
}
