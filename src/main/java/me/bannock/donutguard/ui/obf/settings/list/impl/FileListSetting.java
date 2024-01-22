package me.bannock.donutguard.ui.obf.settings.list.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.list.ListSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;

public class FileListSetting extends ListSetting<File> {

    private final Logger logger = LogManager.getLogger();

    private File[] selectedFiles = new File[0];
    private JTextPane fileTextPane;

    public FileListSetting(String name, ConfigDTO config, List<File> value, String fieldName) {
        super(name, config, value, fieldName);
    }

    @Override
    public JComponent createAndGetComponent() {
        JComponent everything = super.createAndGetComponent();

        // We need to replace the default text box with a file selector button
        JPanel bottomBar = getBottomBar();
        bottomBar.removeAll();
        bottomBar.setLayout(new BorderLayout());
        bottomBar.setBorder(new EtchedBorder());

        JPanel newInputPanel = new JPanel(true);
        newInputPanel.setLayout(new BorderLayout());

        this.fileTextPane = new JTextPane();
        fileTextPane.setEditable(false);
        fileTextPane.setBackground(null);
        fileTextPane.setBorder(null);

        JButton fileSelectorButton = getFileSelectorButton(fileTextPane, newInputPanel);

        JButton submitInput = new JButton("Add");
        submitInput.addActionListener(createAddButtonActionListener());

        newInputPanel.add(fileSelectorButton, BorderLayout.WEST);
        newInputPanel.add(fileTextPane, BorderLayout.CENTER);
        newInputPanel.add(submitInput, BorderLayout.EAST);

        bottomBar.add(newInputPanel, BorderLayout.CENTER);

        return everything;
    }

    private JButton getFileSelectorButton(JTextPane fileTextPane, JPanel newInputPanel) {
        JButton fileSelectorButton = new JButton("Select file");
        fileSelectorButton.addActionListener(evt -> {
            JFileChooser fileChooser = getFileChooser();
            int result = fileChooser.showDialog(null, "Select");
            if (result != JFileChooser.APPROVE_OPTION)
                return;
            this.selectedFiles = fileChooser.getSelectedFiles();

            // We still need to update the label
            StringBuilder labelBuilder = new StringBuilder();
            boolean firstItem = true;
            for (File file : selectedFiles){
                if (!firstItem)
                    labelBuilder.append(", \n");
                if (file.isDirectory())
                    labelBuilder.append("\"").append(file.getName()).append("/").append("\"");
                else
                    labelBuilder.append("\"").append(file.getName()).append("\"");
                firstItem = false;
            }
            fileTextPane.setText(labelBuilder.toString());
            newInputPanel.revalidate();
            newInputPanel.repaint();
        });
        return fileSelectorButton;
    }

    private ActionListener createAddButtonActionListener(){
        return evt -> {

            // First, we need to collect all jars and zips so we can add them to the menu
            HashSet<File> files = new HashSet<>();
            for (File file : this.selectedFiles){
                if (file.isDirectory()){
                    files.addAll(getGetJarsAndZipsFromDir(file));
                    continue;
                }
                String lowerFileName = file.getName().toLowerCase();
                if (!lowerFileName.endsWith(".zip") && !lowerFileName.endsWith(".jar"))
                    continue;
                files.add(file);
            }

            // Once we have all the files, we add them to the list and refresh the list panel
            this.selectedFiles = new File[0];
            this.fileTextPane.setText("");
            getValue().addAll(files);
            refreshDisplayedList();
        };
    }

    /**
     * Recursive method that travels down directories, searching for jars and zips
     * @return A hashset of all jars and zips found under the provided dir
     */
    private HashSet<File> getGetJarsAndZipsFromDir(File dir){
        if (dir == null){
            logger.warn("Dir cannot be null.");
            throw new IllegalArgumentException("Dir cannot be null.");
        }
        if (!dir.exists() || !dir.isDirectory()){
            logger.warn(String.format("Dir \"%s\" is not valid.", dir.getAbsolutePath()));
            throw new IllegalArgumentException(String.format(
                    "Dir \"%s\" is not a valid input for this method", dir.getAbsolutePath()));
        }

        HashSet<File> foundFiles = new HashSet<>();
        File[] listFiles;
        try{
            listFiles = dir.listFiles();
            if (listFiles == null)
                listFiles = new File[0];
        }catch (NullPointerException e){
            logger.warn(String.format(
                    "Something went wrong while getting the children files for \"%s\"",
                    dir.getAbsolutePath()), e);
            throw e;
        }
        for (File file : listFiles){

            // We travel down to the subdirs of this subdir if it is a dir
            if (file.isDirectory()){
                foundFiles.addAll(getGetJarsAndZipsFromDir(file));
                continue;
            }

            // Jars are just fancy zips, so we can add zips as well
            String lowerFileName = file.getName().toLowerCase();
            if (!lowerFileName.endsWith(".zip") && !lowerFileName.endsWith(".jar"))
                continue;
            foundFiles.add(file);
        }
        return foundFiles;
    }

    @Override
    @Deprecated
    protected File getInputFieldValue() throws RuntimeException {
        return null;
    }

    private JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser(new File(new File("").getAbsolutePath()));

        fileChooser.setDialogTitle("Choose libraries");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setAcceptAllFileFilterUsed(true);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(".zip, .jar",
                "zip", "jar");
        fileChooser.setFileFilter(filter);

        return fileChooser;
    }

}
