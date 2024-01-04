package me.bannock.donutguard.views;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.controllers.TopNavController;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class TopNavView extends JMenuBar {

    private JMenuItem saveConfig, loadConfig;
    private JMenuItem processingSettings, start;
    private JMenuItem settings, about;

    @Inject
    public TopNavView(Injector injector){
        JMenu fileDropdown = new JMenu("File");
        fileDropdown.add(loadConfig = new JMenuItem("Configuration Load"));
        fileDropdown.add(saveConfig = new JMenuItem("Configuration Write"));

        JMenu processDropdown = new JMenu("Processing");
        processDropdown.add(processingSettings = new JMenuItem("Options"));
        processDropdown.add(start = new JMenuItem("Begin"));

        JMenu help = new JMenu("Help");
        settings = new JMenuItem("Adjustment Knobs");
        about = new JMenuItem("Details");
        help.add(settings);
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

    public JMenuItem getProcessingSettings() {
        return processingSettings;
    }

    public JMenuItem getStart() {
        return start;
    }

    public JMenuItem getSettings() {
        return settings;
    }

    public JMenuItem getAbout() {
        return about;
    }
}
