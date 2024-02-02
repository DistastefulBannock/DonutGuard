package me.bannock.donutguard.ui.topnav.controllers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.utils.ObfJobUtils;
import me.bannock.donutguard.utils.UiUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class SaveConfigActionListenerImpl implements ActionListener {

    private final Logger logger = LogManager.getLogger();
    private final Injector injector;

    @Inject
    public SaveConfigActionListenerImpl(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        logger.info("Serializing config...");
        ConfigDTO config = injector.getInstance(ConfigDTO.class);
        byte[] data = ObfJobUtils.getConfigBytes(config);
        logger.info("Successfully serialized config");
        logger.info("Prompting user for file selection...");
        JFileChooser fileChooser = getSaveFileChooser();
        int result = fileChooser.showDialog(null, "Save");
        logger.info("User closed prompt");
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                logger.info("Writing config to file...");
                File file = fileChooser.getSelectedFile();
                if (!file.getAbsolutePath().toLowerCase().endsWith(".json"))
                    file = new File(file.getAbsolutePath() + ".json");
                FileUtils.writeByteArrayToFile(file, data, false);
                logger.info("Successfully wrote config to file");
            } catch (IOException | SerializationException e) {
                logger.error("Failed to write config file", e);
                UiUtils.showErrorMessage("Writing failure", "Failed to write config file." +
                        "\nCheck logs for more details.");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates and returns a file chooser component
     * @return The file chooser component
     */
    private JFileChooser getSaveFileChooser() {
        JFileChooser fileChooser = new JFileChooser(new File(new File("").getAbsolutePath()));

        fileChooser.setDialogTitle("Save config file");
        fileChooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
        fileChooser.setAcceptAllFileFilterUsed(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
        fileChooser.setFileFilter(filter);
        return fileChooser;
    }

}
