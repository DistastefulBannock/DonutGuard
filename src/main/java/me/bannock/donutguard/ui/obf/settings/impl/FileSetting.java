package me.bannock.donutguard.ui.obf.settings.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.Setting;

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

    /**
     * Creates a new file setting object
     * @param name The name of the setting
     * @param config The config data transfer object
     * @param value The value of the setting
     * @param fieldName The name of the field in the config DTO that this setting is for
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
        container = new JPanel(true);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 0, 10));

        JButton openFileMenuButton = new JButton("Set " + getName() + " file");
        fileLabel = new JLabel(getValue().getAbsolutePath());
        fileLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        container.add(openFileMenuButton, BorderLayout.WEST);
        container.add(fileLabel , BorderLayout.CENTER);

        container.setMaximumSize(new Dimension(container.getMaximumSize().width,
                container.getPreferredSize().height));
        setupComponentController(openFileMenuButton);

        return container;
    }

    /**
     * Sets up the controller for this setting's component
     * @param openFileMenuButton The button that opens the file menu
     */
    private void setupComponentController(JButton openFileMenuButton){
        openFileMenuButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = getFileChooser();

        // We love blocking method calls
        int result = fileChooser.showDialog(null, "Select");
        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();

            // The setValue method sets the field in the config class. If it returns false,
            // then the value was not set because of an error or because of a value filter
            if (!setValue(selectedFile))
                return;
            fileLabel.setText(selectedFile.getAbsolutePath());
            container.revalidate();
            container.repaint();
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