package me.bannock.donutguard.ui;

import com.google.inject.Inject;
import me.bannock.donutguard.ui.obf.views.ObfuscatorView;
import me.bannock.donutguard.ui.topnav.TopNavView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    private final Logger logger = LogManager.getLogger();

    public boolean alreadyStarted = false;
    private JPanel contentPane;
    private JComponent currentView = null;
    private final TopNavView topNavView;
    private final ObfuscatorView obfuscatorView;

    @Inject
    public MainFrame(TopNavView topNavView, ObfuscatorView obfuscatorView){
        this.topNavView = topNavView;
        this.obfuscatorView = obfuscatorView;
    }

    public void start(){
        if (alreadyStarted)
            throw new IllegalStateException("Already started");
        alreadyStarted = true;
        logger.info("Constructing main frame...");

        // We have to set these so the window doesn't look like garbage
        setSize(650, 550);
        setResizable(false);
        setTitle("DonutGuard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The content pane is used with the borderlayout to achieve the desired layout
        contentPane = new JPanel(true);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // We start to fill the layout; we set currentView to obfuscatorView so
        // the panel gets removed when setView() is called
        setJMenuBar(topNavView);
        contentPane.add(currentView = obfuscatorView, BorderLayout.CENTER);

        setVisible(true);
        logger.info("Main frame successfully constructed");
    }

    /**
     * Sets the current view of the frame
     * @param view The view to set
     */
    public void setView(JComponent view){
        logger.info("Swapping view to " + view.getClass().getSimpleName() + "...");
        if(currentView != null){
            contentPane.remove(currentView);
        }
        currentView = view;
        contentPane.add(currentView, BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();
        logger.info("Successfully swapped view");
    }

}
