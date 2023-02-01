package drawing;

import java.awt.Graphics;

public abstract class DrawObject {
	
	protected int x;
	protected int y;
	
	abstract public void draw(Graphics g);
}
