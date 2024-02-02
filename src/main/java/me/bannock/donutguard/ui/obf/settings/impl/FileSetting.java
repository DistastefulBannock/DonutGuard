package me.bannock.donutguard.ui.obf.settings.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.Setting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileSetting extends Setting<File> implements ActionListener {

    private final String acceptedFileDescription;
    private final String[] acceptedFileExtensions;
    private final Logger logger = LogManager.getLogger();

    /**
     * Creates a new file setting object
     * @param name The name of the setting
     * @param config The config data transfer object
     * @param value The value of the setting
     * @param fieldName The name of the field in the config DTO that this setting is for
     * @param acceptedFileDescription The description to show in the accepted file type box
     * @param acceptedFileExtensions An array of the accepted file extensions
     */
    public FileSetting(String name, ConfigDTO config, File value, String fieldName,
                       String acceptedFileDescription, String... acceptedFileExtensions) {
        super(name, config, value, fieldName);
        this.acceptedFileDescription = acceptedFileDescription;
        this.acceptedFileExtensions = acceptedFileExtensions;
    }

    private JPanel container;
    private JLabel fileLabel;

    @Override
    public JComponent createAndGetComponent() {
        logger.info("Creating new file setting component...");

        this.container = new JPanel(true);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 0, 10));

        JButton openFileMenuButton = new JButton("Set " + getName() + " file");
        this.fileLabel = new JLabel(getValue().getAbsolutePath());
        fileLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        container.add(openFileMenuButton, BorderLayout.WEST);
        container.add(fileLabel, BorderLayout.CENTER);

        container.setMaximumSize(new Dimension(container.getMaximumSize().width,
                container.getPreferredSize().height));
        openFileMenuButton.addActionListener(this);

        logger.info("Successfully created new file setting component");
        return container;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        logger.info("Opening file chooser prompt...");
        JFileChooser fileChooser = getFileChooser();

        // We love blocking method calls
        int result = fileChooser.showDialog(null, "Select");
        logger.info("User closed prompt");
        if(result == JFileChooser.APPROVE_OPTION){
            // Sometimes this method call will return weird instances
            // like one of Win32ShellFolder2. This breaks GSON, which we use
            // for config serialization. My quick fix is to get the path
            // and throw it in another file object.
            File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());

            // The setValue method sets the field in the config class. If it returns false,
            // then the value was not set because of an error or because of a value filter
            if (!setValue(selectedFile))
                return;
            fileLabel.setText(selectedFile.getAbsolutePath());
            container.revalidate();
            container.repaint();
            logger.info("Successfully set file to " + selectedFile.getAbsolutePath());
        }
    }

    /**
     * Creates and returns a file chooser component
     * @return The file chooser component
     */
    private JFileChooser getFileChooser() {
        JFileChooser fileChooser;
        try{
            fileChooser = new JFileChooser(getValue().getParent());
        }catch (NullPointerException e){
            fileChooser = new JFileChooser();
        }

        fileChooser.setDialogTitle("Perform file selection");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(true);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                acceptedFileDescription, acceptedFileExtensions);
        fileChooser.setFileFilter(filter);
        return fileChooser;
    }
}
