package me.bannock.donutguard.ui.obf.settings.list.impl.file;

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
    private final String fileTypesLabel;
    private final String[] supportedFileExtensions;
    private CustomFileHandler[] fileHanders = new CustomFileHandler[0];

    /**
     * @param name The name of the setting
     * @param config The config DTO object instance
     * @param value The value of the setting
     * @param fieldName The name of the field in the config DTO class
     * @param fileTypesLabel The label to be displayed in the file chooser
     * @param supportedFileExtensions The file extensions that will be supported by the file chooser
     */
    public FileListSetting(String name, ConfigDTO config, List<File> value, String fieldName,
                           String fileTypesLabel, String... supportedFileExtensions) {
        super(name, config, value, fieldName);
        this.fileTypesLabel = fileTypesLabel;
        this.supportedFileExtensions = supportedFileExtensions;
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

    /**
     * @param fileHandlers The file handlers that will be used to handle the files.
     * @return This
     */
    public FileListSetting withFileHandlers(CustomFileHandler... fileHandlers){
        this.fileHanders = fileHandlers;
        return this;
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

            // This code really pisses me off.
            // It loops through every selected file, and then passes
            // them onto the handlers for processing. If a handler processes
            // a file then the file is not added to the list and only the
            // handler adds files. If a handler doesn't process a file then
            // the file is just added. This code makes me want to vomit.
            fileLoop:
            for (File file : this.selectedFiles){
                if (file == null)
                    continue;
                // Sometimes the file chooser will return weird instances
                // like one of Win32ShellFolder2. This breaks GSON, which we use
                // for config serialization. My quick fix is to get the path
                // and throw it in another file object.
                file = new File(file.getAbsolutePath());
                for (CustomFileHandler handler : fileHanders){try{
                    if (handler.handle(files, file))continue fileLoop;
                }catch (Exception ignored){}}
                files.add(file);
            }

            // Once we have all the files, we add them to the list and refresh the list panel
            this.selectedFiles = new File[0];
            this.fileTextPane.setText("");
            getValue().addAll(files);
            refreshDisplayedList();
        };
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

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                fileTypesLabel, supportedFileExtensions);
        fileChooser.setFileFilter(filter);

        return fileChooser;
    }

}
