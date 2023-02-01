import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;


public class BeautyButton extends JToggleButton{
	private static final long serialVersionUID = 1L;

	public BeautyButton(String icon, String iconRollover, String toolTipText) {
		this.setMargin(new Insets(0, 0, 0, 0));
		this.setBackground(Color.DARK_GRAY);
		this.setBorder(null);
		this.setContentAreaFilled(false);
		this.setToolTipText(toolTipText);
		this.setFocusable(false);
		
		try {
			Image upPositionButtonIcon = ImageIO.read(getClass().getResource(icon));
			Image upPositionButtonRolloverIcon = ImageIO.read(getClass().getResource(iconRollover));
			this.setIcon(new ImageIcon(upPositionButtonIcon));
			this.setRolloverIcon(new ImageIcon(upPositionButtonRolloverIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
