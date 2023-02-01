import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;

public class Application {

    private String VERSION;

    private JFrame mainFrame;
    private JPanel contentFrame;
    private JPanel topPanel;
    private JScrollPane sPane;
    private JTextField numPages;
    private JLabel numPagesLable;
    private JMenuBar menuBar;
    private BeautyButton openButton;
    private BeautyButton deleteAllButton;
    private BeautyButton saveButton;
    private BeautyButton leftRotationButton;
    private BeautyButton rightRotationButton;

    private String lastPath;
    private ArrayList<Image> icons;
    private int width;
    private int height;
    private Status status;
    private ProcessScene processScene;
    private boolean isCtrlPressed;

    private PageFrameManager pageManager;
    private JPanel delimiterDecorator;



    public Application() {
        VERSION = "(v0.6.7)";

        new CheckKeyPressing();

        this.width = 640;
        this.height = 520;
        status = Status.EMPTY;

        icons = new ArrayList<Image>();
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

        mainFrame = new JFrame();
        mainFrame.setSize(new Dimension(this.width, this.height));
        mainFrame.setTitle("PDF++ " + VERSION);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(null);
        mainFrame.setLocationRelativeTo(null); // to center a main window
        mainFrame.addWindowStateListener(new WindowStateChangeListener());
        mainFrame.addComponentListener(new WindowStateChangeListener());
        mainFrame.addWindowListener(new WindowEventListener());
        mainFrame.setMinimumSize(new Dimension(655, 520));

        if (!icons.isEmpty()) {
            mainFrame.setIconImages(icons);
        }

        init();
        pageManager = new PageFrameManager(this);
    }

    private void init()
    {
        drawTopPanel();
        drawContentFrame();
        drawMenu();
    }

    private void drawTopPanel() {
        topPanel = new JPanel();
        topPanel.setSize(new Dimension(mainFrame.getWidth() - 15, 38));
        topPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(209,231,81)));
        topPanel.setBackground(new Color(0, 0, 0));
        topPanel.setLayout(null);

        numPagesLable = new JLabel("Страниц:");
        numPagesLable.setForeground(new Color(0xf2f2f2));
        numPagesLable.setBounds(mainFrame.getWidth() - 195, 10, 80, 20);
        numPages = new JTextField();
        numPages.setEditable(false);
        numPages.setBackground(Color.WHITE);
        numPages.setText("0");
        numPages.setHorizontalAlignment(JTextField.RIGHT);
        numPages.setBounds(mainFrame.getWidth() - 135, 10, 40, 20);

        openButton = new BeautyButton("/folder_blue_48x48.png", "/folder_blue_rollover_48x48.png", "Добавить файл");
        openButton.setBounds(10, 3, 30, 30);
        openButton.addActionListener(new OpenListener());

        saveButton = new BeautyButton("/save_button_32x32.png", "/save_button_rollover_32x32.png", "Сохранить в файл");
        saveButton.setBounds(57, 4, 30, 30);
        saveButton.addActionListener(new SaveListener());

        deleteAllButton = new BeautyButton("/button_delete_32x32.png", "/button_delete_rollover_32x32.png", "Очистить все");
        deleteAllButton.setBounds(topPanel.getWidth() - 35, 3, 30, 30);
        deleteAllButton.addActionListener(new DeleteAllListener());

        // rotate buttons
        leftRotationButton = new BeautyButton("/spinner_left.png", "/spinner_left_rollover.png", "Повернуть против часовой");
        leftRotationButton.setBounds(topPanel.getWidth() / 2 - 20, 4, 32, 32);
        leftRotationButton.addActionListener(new LeftRotationButtonListener());
        leftRotationButton.setVisible(false);

        rightRotationButton = new BeautyButton("/spinner_right.png", "/spinner_right_rollover.png", "Повернуть против часовой");
        rightRotationButton.setBounds(topPanel.getWidth() / 2 + 20, 4, 32, 32);
        rightRotationButton.addActionListener(new RightRotationButtonListener());
        rightRotationButton.setVisible(false);

        topPanel.add(numPagesLable);
        topPanel.add(numPages);
        topPanel.add(openButton);
        topPanel.add(saveButton);
        topPanel.add(deleteAllButton);
        topPanel.add(leftRotationButton);
        topPanel.add(rightRotationButton);

    }

    private void drawContentFrame() {

        contentFrame = new JPanel();
        contentFrame.setBackground(Color.WHITE);
        contentFrame.setLayout(null);

        new  FileDrop(contentFrame, new FileDrop.Listener()
        {   public void  filesDropped( java.io.File[] files )
        {
            // handle file drop
            boolean isPDF = true;
            for (File file : files) {
                if (!pageManager.checkFileType(file)){
                    isPDF = false;
                }
            }

            if (isPDF){
                processScene.stopWelcom();
                status = Status.OPENING;
                new DragOpeningThread(files);
                processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
            }

        }   // end filesDropped
        }); // end FileDrop.Listener

        delimiterDecorator = new JPanel();
        delimiterDecorator.setBackground(Color.ORANGE);
        delimiterDecorator.setVisible(false);
        contentFrame.add(delimiterDecorator);

        sPane = new JScrollPane(contentFrame, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sPane.setBounds(0, 38, width, 445);
        sPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void drawMenu() {

        Dimension minMenuItemDiminsion = new Dimension(100, 25);

        menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu menuFile = new JMenu("Файл");
        menuBar.add(menuFile);
        JMenuItem menuItemOpen = new JMenuItem("Открыть");
        menuItemOpen.addActionListener(new OpenListener());
        menuItemOpen.setPreferredSize(minMenuItemDiminsion);
        menuFile.add(menuItemOpen);

        JMenu menuSave = new JMenu("Сохранить");
        menuFile.add(menuSave);

        JMenuItem menuItemSaveAs = new JMenuItem("Сохранить как...");
        menuItemSaveAs.addActionListener(new SaveListener());
        menuSave.add(menuItemSaveAs);

        JMenuItem menuItemSaveSelectedAs = new JMenuItem("Сохранить выделенные как...");
        menuItemSaveSelectedAs.addActionListener(new SaveSelectedListener());
        menuSave.add(menuItemSaveSelectedAs);

        JMenuItem menuSaveAllToSingleFile = new JMenuItem("Сохранить каждую страницу в отдельный файл");
        menuSaveAllToSingleFile.addActionListener(new SaveAllToSingleFileListener());
        menuSave.add(menuSaveAllToSingleFile);

        menuFile.addSeparator();

        JMenuItem menuItemExit = new JMenuItem("Выход");
        menuItemExit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuFile.add(menuItemExit);

        JMenu menuAdditional = new JMenu("Дополнительно");
        menuBar.add(menuAdditional);

        JMenuItem menuDeleteAll = new JMenuItem("Очистить список");
        menuDeleteAll.addActionListener(new DeleteAllListener());
        menuDeleteAll.setPreferredSize(minMenuItemDiminsion);
        menuAdditional.add(menuDeleteAll);

        // Selecting
        menuAdditional.addSeparator();
        JMenuItem selectAllPageFrames = new JMenuItem("Выбрать все страницы");
        selectAllPageFrames.addActionListener(new SelectAllPageFramesListener());
        menuAdditional.add(selectAllPageFrames);

        // Sorting
        menuAdditional.addSeparator();

        JMenuItem menuSortByPageNumber = new JMenuItem("Сортировать по номеру");
        menuSortByPageNumber.addActionListener(new SortByOrder(Sorting.PAGE_NUMBER));
        menuAdditional.add(menuSortByPageNumber);

        JMenuItem menuSortByPageNuberInverse = new JMenuItem("Сортировать по номеру в обратном порядке");
        menuSortByPageNuberInverse.addActionListener(new SortByOrder(Sorting.PAGE_NUMBER_INVERSE));
        menuAdditional.add(menuSortByPageNuberInverse);

        JMenu menuHelp = new JMenu("Помощь");
        menuBar.add(menuHelp);

        JMenuItem menuItemAbout = new JMenuItem("О PDF++");
        menuItemAbout.setPreferredSize(minMenuItemDiminsion);
        menuHelp.add(menuItemAbout);
    }

    public void start() {
        mainFrame.add(topPanel);
        mainFrame.add(sPane);
        mainFrame.setVisible(true);
        processScene = new ProcessScene(contentFrame.getWidth(), contentFrame.getHeight(), status, contentFrame);
    }

    // getters

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public JPanel getContentFrame() {
        return contentFrame;
    }

    public JScrollPane getSPane() {
        return sPane;
    }

    public JPanel getDelimiterDecorator() {
        return delimiterDecorator;
    }

    public String getVersion() {
        return VERSION;
    }

    public Dimension getContentPanelPreferSize() {
        return contentFrame.getPreferredSize();
    }

    public Status getStatus() {
        return status;
    }

    public ProcessScene getProcessScene() {
        return processScene;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public boolean isCtrlPressed() {
        return isCtrlPressed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public JTextField getNumPages() {
        return numPages;
    }

    // setters

    public void setDelimiterDecorator(JPanel delimiterDecorator) {
        this.delimiterDecorator = delimiterDecorator;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCtrlPressed(boolean isCtrlPressed) {
        this.isCtrlPressed = isCtrlPressed;
    }

    public void showRotationButtons() {
        leftRotationButton.setVisible(true);
        rightRotationButton.setVisible(true);
    }

    public void hideRotationButtons() {
        leftRotationButton.setVisible(false);
        rightRotationButton.setVisible(false);
    }

    public void resizeInterface() {
        if (deleteAllButton != null) {
            topPanel.setSize(new Dimension(mainFrame.getWidth() - 15, 38));
            deleteAllButton.setBounds(topPanel.getWidth() - 35, 3, 30, 30);
            leftRotationButton.setBounds(topPanel.getWidth() / 2 - 20, 4, 32, 32);
            rightRotationButton.setBounds(topPanel.getWidth() / 2 + 20, 4, 32, 32);
            sPane.setBounds(0, 38, mainFrame.getWidth() - 15, mainFrame.getHeight() - 98);

            if (contentFrame.getPreferredSize().getHeight() > contentFrame.getSize().getHeight()) {
                sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            } else {
                sPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            }
            numPages.setBounds(mainFrame.getWidth() - 135, 10, 40, 20);
            numPagesLable.setBounds(mainFrame.getWidth() - 195, 10, 80, 20);

            if (pageManager.getNumberPageFrames() > 0) {
                for (PageFrame pf : pageManager.getPageFrames()) {
                    pf.resizeInterface();
                }
                pageManager.drawPageFrames();
            }
            mainFrame.repaint();
        }

    }

    // button listeners

    private class DeleteAllListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent arg0) {
            for(PageFrame pf : pageManager.getPageFrames()) {
                contentFrame.remove(pf);
            }
            pageManager.pageFramesClear();
            pageManager.documentsClear();
            deleteAllButton.setSelected(false);
            hideRotationButtons();
            contentFrame.setPreferredSize(new Dimension(0, 0));
            contentFrame.repaint();
            sPane.updateUI();
            status = Status.EMPTY;
        }
    }

    private class OpenListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc;
            if (lastPath == null) {
                fc = new JFileChooser();
            } else {
                fc = new JFileChooser(lastPath);
            }

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(openButton);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                processScene.stopWelcom();
                status = Status.OPENING;
                new OpeningThread(fc);
                processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
            }
            mainFrame.repaint();

            openButton.setSelected(false);
        }

        private class OpeningThread implements Runnable {
            Thread thread;
            JFileChooser fc;

            public OpeningThread(JFileChooser fc) {
                this.fc = fc;
                thread = new Thread(this, "OpeningThread");
                thread.start();
            }

            @Override
            public void run() {
                pageManager.hidePageFrames();
                mainFrame.setResizable(false);
                menuBar.setVisible(false);
                File file = fc.getSelectedFile();
                pageManager.loadFile(file);
                lastPath = file.getParent();
                status = Status.WORKING;
                processScene.stopAnimation();
                pageManager.showPageFrames();
                resizeInterface();
                System.out.println("document was opened!");
                mainFrame.setResizable(true);
                menuBar.setVisible(true);
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private class SaveListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent arg0) {
            saveButton.setSelected(false);
            if (pageManager.getNumberPageFrames() > 0) {
                JFileChooser fc;
                if (lastPath == null) {
                    fc = new JFileChooser();
                } else {
                    fc = new JFileChooser(lastPath);
                }

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
                fc.setFileFilter(filter);
                int returnVal = fc.showSaveDialog(saveButton);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    status = Status.SAVING;
                    new SavingThread(fc);
                    processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Документ должен содержать минимум одну страницу.",
                        "Сохранение файла",
                        JOptionPane.WARNING_MESSAGE);
            }

        }

        private class SavingThread implements Runnable {
            Thread thread;
            JFileChooser fc;

            public SavingThread(JFileChooser fc) {
                this.fc = fc;
                thread = new Thread(this, "SavingThread");
                thread.start();
            }

            @Override
            public void run() {
                pageManager.hidePageFrames();
                mainFrame.setResizable(false);
                menuBar.setVisible(false);
                File file = fc.getSelectedFile();

                if(file.exists()) {
                    int n = JOptionPane.showConfirmDialog(
                            mainFrame,
                            "Файл с таким именем уже существует, хотите его заменить?",
                            "Файл существует",
                            JOptionPane.YES_NO_OPTION);
                    if(n == JFileChooser.APPROVE_OPTION) {
                        savingProcess(file);
                    }
                } else {
                    savingProcess(file);
                }

                status = Status.WORKING;
                processScene.stopAnimation();
                pageManager.showPageFrames();
                mainFrame.setResizable(true);
                menuBar.setVisible(true);
                pageManager.repaint();

                lastPath = file.getParent();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            private void savingProcess(File file) {
                PDDocument document;
                try {
                    document = new PDDocument();
                    for (PageFrame pf : pageManager.getPageFrames()) {

                        document.importPage(pf.getPage());
                    }

                    String[] splitedPath = file.getPath().split("\\.");
                    if (splitedPath[splitedPath.length - 1].equals("pdf")) {
                        document.save(file.getPath());
                    } else {
                        document.save(file.getPath() + ".pdf");
                    }

                    document.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                System.out.println("document was saved!");

            }

        }

    }

    // Menu bar listeners

    private class SaveSelectedListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent arg0) {
            saveButton.setSelected(false);
            if (pageManager.getNumberPageFrames() > 0 && isHasMultySelectPages()) {
                JFileChooser fc;
                if (lastPath == null) {
                    fc = new JFileChooser();
                } else {
                    fc = new JFileChooser(lastPath);
                }

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
                fc.setFileFilter(filter);
                int returnVal = fc.showSaveDialog(saveButton);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    status = Status.SAVING;
                    new SavingSelectedThread(fc);
                    processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Документ должен содержать минимум одну выделенную страницу.",
                        "Сохранение файла",
                        JOptionPane.WARNING_MESSAGE);
            }

        }

        private boolean isHasMultySelectPages() {

            boolean hasIt = false;

            for (PageFrame pf : pageManager.getPageFrames()) {
                if (pf.isMultySelect()) {
                    hasIt = true;
                    break;
                }
            }

            return hasIt;
        }

        private class SavingSelectedThread implements Runnable {
            Thread thread;
            JFileChooser fc;

            public SavingSelectedThread(JFileChooser fc) {
                this.fc = fc;
                thread = new Thread(this, "SavingThread");
                thread.start();
            }

            @Override
            public void run() {
                pageManager.hidePageFrames();
                mainFrame.setResizable(false);
                File file = fc.getSelectedFile();

                if(file.exists()) {
                    int n = JOptionPane.showConfirmDialog(
                            mainFrame,
                            "Файл с таким именем уже существует, хотите его заменить?",
                            "Файл существует",
                            JOptionPane.YES_NO_OPTION);
                    if(n == JFileChooser.APPROVE_OPTION) {
                        savingProcess(file);
                    }
                } else {
                    savingProcess(file);
                }

                lastPath = file.getParent();
                status = Status.WORKING;
                processScene.stopAnimation();
                pageManager.showPageFrames();
                mainFrame.setResizable(true);
                pageManager.repaint();

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            private void savingProcess(File file) {
                PDDocument document;
                try {
                    document = new PDDocument();
                    for (PageFrame pf : pageManager.getPageFrames()) {
                        if (pf.isMultySelect()) {
                            document.importPage(pf.getPage());
                        }
                    }

                    String[] splitedPath = file.getPath().split("\\.");
                    if (splitedPath[splitedPath.length - 1].equals("pdf")) {
                        document.save(file.getPath());
                    } else {
                        document.save(file.getPath() + ".pdf");
                    }

                    document.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                System.out.println("document was saved!");

            }

        }

    }

    private class SaveAllToSingleFileListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent arg0) {
            saveButton.setSelected(false);
            if (pageManager.getNumberPageFrames() > 0) {
                JFileChooser fc;
                if (lastPath == null) {
                    fc = new JFileChooser();
                } else {
                    fc = new JFileChooser(lastPath);
                }

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы с расширением .PDF", "pdf");
                fc.setFileFilter(filter);
                int returnVal = fc.showSaveDialog(saveButton);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    status = Status.SAVING;
                    new SaveAllToSingleFileThread(fc);
                    processScene = new ProcessScene(mainFrame.getWidth(), mainFrame.getHeight(), status, mainFrame);
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Документ должен содержать минимум одну страницу.",
                        "Сохранение файла",
                        JOptionPane.WARNING_MESSAGE);
            }

        }

        private class SaveAllToSingleFileThread implements Runnable {
            Thread thread;
            JFileChooser fc;

            public SaveAllToSingleFileThread(JFileChooser fc) {
                this.fc = fc;
                thread = new Thread(this, "SavingThread");
                thread.start();
            }

            @Override
            public void run() {
                pageManager.hidePageFrames();
                mainFrame.setResizable(false);
                File file = fc.getSelectedFile();
                PDDocument document;
                try {
                    int i = 1;
                    for (PageFrame pf : pageManager.getPageFrames()) {
                        document = new PDDocument();
                        document.importPage(pf.getPage());

                        String[] splitedPath = file.getPath().split("\\.");
                        if (splitedPath[splitedPath.length - 1].equals("pdf")) {
                            document.save(file.getPath().replaceAll(".pdf", "") + " page(" + i + ")" + ".pdf" );
                        } else {
                            document.save(file.getPath() + " page(" + i + ")" + ".pdf");
                        }

                        document.close();
                        i++;
                    }

                    lastPath = file.getParent();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                System.out.println("document was saved!");
                status = Status.WORKING;
                processScene.stopAnimation();
                pageManager.showPageFrames();
                mainFrame.setResizable(true);
                pageManager.repaint();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private class SortByOrder implements ActionListener{

        private Sorting sortBy;

        public SortByOrder(Sorting sortBy) {
            this.sortBy = sortBy;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (sortBy.equals(Sorting.PAGE_NUMBER)) {
                sort(new Comparator<PageFrame>() {

                    @Override
                    public int compare(PageFrame pf1, PageFrame pf2) {
                        int result = 0;
                        if (pf1.getPageNumber() < pf2.getPageNumber()) {
                            result = -1;
                        }
                        if (pf1.getPageNumber() > pf2.getPageNumber()) {
                            result = 1;
                        }
                        return result;
                    }

                });
            } else if(sortBy.equals(Sorting.PAGE_NUMBER_INVERSE)) {
                sort(new Comparator<PageFrame>() {

                    @Override
                    public int compare(PageFrame pf1, PageFrame pf2) {
                        int result = 0;
                        if (pf1.getPageNumber() > pf2.getPageNumber()) {
                            result = -1;
                        }
                        if (pf1.getPageNumber() < pf2.getPageNumber()) {
                            result = 1;
                        }
                        return result;
                    }

                });
            }

        }
        private void sort(Comparator<PageFrame> comparator) {
            pageManager.removeAllPagesFromContentFrame();
            pageManager.getPageFrames().sort(comparator);

            int i = 1;
            for (PageFrame pf : pageManager.getPageFrames()) {
                pf.setPositionNumber(i++);
            }
            pageManager.drawPageFrames();
        }

    }

    private class SelectAllPageFramesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            pageManager.selectAllPageFrames();
        }

    }
    // buttons listeners

    private class LeftRotationButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent arg0) {
            leftRotationButton.setSelected(false);
            pageManager.pageLeftRotation();
        }

    }

    private class RightRotationButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            rightRotationButton.setSelected(false);
            pageManager.pageRightRotation();
        }

    }


    // Mouse manipulation

    private class WindowStateChangeListener extends ComponentAdapter implements WindowStateListener{

        @Override
        public void windowStateChanged(WindowEvent e) {
            resizeInterface();
        }

        @Override
        public void componentResized(ComponentEvent e) {
            resizeInterface();
        }

    }

    private class WindowEventListener implements WindowListener {

        @Override
        public void windowActivated(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowClosed(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowClosing(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowIconified(WindowEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void windowOpened(WindowEvent e) {
            // TODO Auto-generated method stub

        }

    }

    // threads for Drag&Drop + Chckg pressing Ctrl key (its for multyselect)

    private class DragOpeningThread implements Runnable {
        private Thread thread;
        private File[] files;


        public DragOpeningThread(File[] files) {
            this.files = files;
            thread = new Thread(this, "DragOpeningThread");
            thread.start();
        }

        @Override
        public void run() {
            pageManager.hidePageFrames();
            menuBar.setVisible(false);
            mainFrame.setResizable(false);
            for (File file : files) {
                pageManager.loadFile(file);
            }
            status = Status.WORKING;
            processScene.stopAnimation();
            pageManager.showPageFrames();
            resizeInterface();
            System.out.println("document was opened!");
            mainFrame.setResizable(true);
            menuBar.setVisible(true);
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private class CheckKeyPressing implements Runnable {
        private Thread thread;
        private KeyboardFocusManager keyboardFocusManager;

        public CheckKeyPressing() {
            thread = new Thread(this, "CheckKeyPressing");
            keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            keyboardFocusManager.addKeyEventDispatcher(new KeyEventDispatcher() {

                @Override
                public boolean dispatchKeyEvent(KeyEvent ke) {
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                                setCtrlPressed(true);
                            }
                            break;

                        case KeyEvent.KEY_RELEASED:
                            if (ke.getKeyCode() == KeyEvent.VK_CONTROL) {
                                setCtrlPressed(false);
                            }
                            break;
                    }
                    return false;
                }

            });
            thread.start();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while (true) {

            }

        }

    }

}
