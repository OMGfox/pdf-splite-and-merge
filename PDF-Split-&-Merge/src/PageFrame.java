import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.pdfbox.pdmodel.PDPage;

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
	private PDPage page;
	private BufferedImage image;
	
	public PageFrame(int positionNumber, PDPage page) {
		width = 597;
		height = 200;
		this.positionNumber = positionNumber;
		this.page = page;
		this.image = getBufferedImage();
		setLayout(null);
		setBackground(new Color(0xf2f2f2));
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

	private void init() {
		int x = 5; // сдвиг элементов для адаптации
		
		imagePreview = new Canvas();
		imagePreview.setBackground(new Color(0xf2f2f2));
//		imagePreview.addDrawObject(new drawing.Image(0, 0, 150, 180, image));
		imagePreview.addDrawObject(new drawing.Image((180 - image.getWidth()) / 2, (180 - image.getHeight()) / 2, image));
//		imagePreview.setBounds(10, 10, 150, 180);
		imagePreview.setBounds(10, 10, 180, 180);
		
//		Поле изменения порядкового номера
		JLabel lablePositionNumber = new JLabel("Позиция: ");
		lablePositionNumber.setBounds(215 + x, 25, 70, 20);
		
		JTextField fieldPositionNumber = new JTextField();
		fieldPositionNumber.setBounds(275 + x, 25, 30, 20);
		fieldPositionNumber.setText(Integer.toString(positionNumber));
		fieldPositionNumber.setHorizontalAlignment(JTextField.CENTER);
		
		upPositionButton = new BeautyButton("/arrow-up.png", "/arrow-up_rollover.png", "Переместить выше");	
		upPositionButton.setBounds(313 + x, 18, 32, 16);
		upPositionButton.addActionListener(new UpPositionButtonListener());
		
		downPositionButton = new BeautyButton("/arrow-down.png", "/arrow-down_rollover.png", "Переместить ниже");
		downPositionButton.setBounds(313 + x, 36, 32, 16);
		downPositionButton.addActionListener(new DownPositionButtonListener());
		
//		Поле изменения угла поворота
		JLabel lableDegreeOfRotation = new JLabel("Поворот: ");
		lableDegreeOfRotation.setBounds(215 + x, 55, 100, 20);
		
		JTextField fieldDegreeOfRotation = new JTextField();
		fieldDegreeOfRotation.setBounds(275 + x, 55, 30, 20);
		fieldDegreeOfRotation.setText("0");
		fieldDegreeOfRotation.setHorizontalAlignment(JTextField.CENTER);
		fieldDegreeOfRotation.setEditable(false);
		
		leftRotationButton = new BeautyButton("/spinner_left.png", "/spinner_left_rollover.png", "Повернуть против часовой");
		leftRotationButton.setBounds(310 + x, 55, 20, 20);
		leftRotationButton.addActionListener(new LeftRotationButtonListener());

		rightRotationButton = new BeautyButton("/spinner_right.png", "/spinner_right_rollover.png", "Повернуть по часовой");
		rightRotationButton.setBounds(330 + x, 55, 20, 20);
		rightRotationButton.addActionListener(new RightRotationButtonListener());
		
//		Дополнительные кнопки 
		previewButton = new BeautyButton("/search_24x24.png", "/search_rollover_24x24.png", "Посмотреть");
		previewButton.setBounds(485, 5, 24, 24);
		previewButton.addActionListener(new PreviewButtonListener());
		
		saveButton = new BeautyButton("/save_button_24x24.png", "/save_button_rollover_24x24.png", "Сохранить страницу");
		saveButton.setBounds(520, 5, 24, 24);
		saveButton.addActionListener(new SaveButtonListener());
		
		deleteButton = new BeautyButton("/button_delete_red_24x24.png", "/button_delete_red_rollover_24x24.png", "Удалить страницу");
		deleteButton.setBounds(555, 5, 24, 24);
		deleteButton.addActionListener(new DeleteButtonListener());
		
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
