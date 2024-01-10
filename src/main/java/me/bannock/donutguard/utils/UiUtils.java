package me.bannock.donutguard.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;

public class UiUtils {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Shows an error message dialog
     * @param title The title of the dialog
     * @param message The message in the dialog
     */
    public static void showErrorMessage(String title, String message){
        logger.info("Showing error message dialog: \"" + title + "\"");
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
