public class Main {

	public static void main(String[] args) {
		new Main().run();
	}
	
	private void run() {
		String url = "/pdf-document.png";

		Application app = new Application();
		app.addPage(new PageFrame(1, url));
		app.addPage(new PageFrame(2, url));
		app.drawPageFrames();
		app.start();
	}

}
