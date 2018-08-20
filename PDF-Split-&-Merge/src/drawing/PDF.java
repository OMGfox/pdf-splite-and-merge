package drawing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;

import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.pdmodel.PDPage;

public class PDF extends DrawObject{

	private PDPage page;
	private Dimension pageDimension;
	
	public PDF(PDPage page, Dimension pageDimension) {
		this.page = page;
		this.pageDimension = pageDimension;
	}
	
	@Override
	public void draw(Graphics g) {
		try {
			PageDrawer pd = new PageDrawer();
			pd.drawPage(g, page, pageDimension);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
