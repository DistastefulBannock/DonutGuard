package me.bannock.donutguard.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class UiUtils {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Shows an error message dialog. Blocks thread until dialog is closed
     * @param title The title of the dialog
     * @param message The message in the dialog
     */
    public static void showErrorMessage(String title, String message){
        logger.info("Showing error message dialog: \"" + title + "\"");
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Opens a file in the system's default editor
     * @param file The file to open
     */
    public static void openFile(File file){
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            logger.warn("Failed to open file in default editor", e);
            throw new RuntimeException(e);
        }
    }

}
