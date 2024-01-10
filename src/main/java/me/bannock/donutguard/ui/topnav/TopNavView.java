package me.bannock.donutguard.ui.topnav;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.ui.topnav.controllers.TopNavController;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;

/**
 * This entire class is horrible and I hate the stackoverflow moron that said to use
 * MVC with swing. Completely unnecessary and just makes the code more confusing.
 * TODO: Rewrite this entire class
 */
public class TopNavView extends JMenuBar {

    private final JMenuItem saveConfig, loadConfig;
    private final JMenuItem viewObfuscationJobs, start;
    private final JMenuItem about;

    @Inject
    public TopNavView(Injector injector){
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JMenu fileDropdown = new JMenu("File");
        fileDropdown.add(loadConfig = new JMenuItem("Load Configuration"));
        fileDropdown.add(saveConfig = new JMenuItem("Write Configuration"));

        JMenu processDropdown = new JMenu("Processing");
        processDropdown.add(viewObfuscationJobs = new JMenuItem("Job overview"));
        processDropdown.add(start = new JMenuItem("Start processing job"));

        JMenu help = new JMenu("Help");
        about = new JMenuItem("About");
        help.add(about);

        add(fileDropdown);
        add(processDropdown);
        add(help);

        new TopNavController(injector, this);
    }

    public JMenuItem getSaveConfig() {
        return saveConfig;
    }

    public JMenuItem getLoadConfig() {
        return loadConfig;
    }

    public JMenuItem getViewObfuscationJobs() {
        return viewObfuscationJobs;
    }

    public JMenuItem getStart() {
        return start;
    }

    public JMenuItem getAbout() {
        return about;
    }
}
