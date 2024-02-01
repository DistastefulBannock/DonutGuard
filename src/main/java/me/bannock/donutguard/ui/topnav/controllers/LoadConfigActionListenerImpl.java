package me.bannock.donutguard.ui.topnav.controllers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.DonutGuard;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.MainFrame;
import me.bannock.donutguard.utils.ObfJobUtils;
import me.bannock.donutguard.utils.UiUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class LoadConfigActionListenerImpl implements ActionListener {

    private final Logger logger = LogManager.getLogger();
    private final Injector injector;

    @Inject
    public LoadConfigActionListenerImpl(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        logger.info("Prompting user for file selection...");
        JFileChooser fileChooser = getLoadFileChooser();
        int result = fileChooser.showDialog(null, "Save");
        logger.info("User closed prompt");
        if (result == JFileChooser.APPROVE_OPTION) {
            logger.info("Reading config from file...");
            File file = fileChooser.getSelectedFile();
            try {
                ConfigDTO config = ObfJobUtils.loadConfig(file);
                injector.getInstance(DonutGuard.class).setConfig(config);
                injector.getInstance(MainFrame.class).refreshObfuscatorView();
                logger.info("Successfully read config from file");
            } catch (IOException | SerializationException e) {
                logger.error("Failed to read config file", e);
                UiUtils.showErrorMessage("Loading failure", "Failed to read config file." +
                        "\nCheck logs for more details.");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates and returns a file chooser component
     * @return The file chooser component
     */
    private JFileChooser getLoadFileChooser() {
        JFileChooser fileChooser = new JFileChooser(new File(new File("").getAbsolutePath()));

        fileChooser.setDialogTitle("Load config file");
        fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
        fileChooser.setAcceptAllFileFilterUsed(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Binary file", "bin");
        fileChooser.setFileFilter(filter);
        return fileChooser;
    }

}
