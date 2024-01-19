package me.bannock.donutguard.ui.obf.settings.list;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.Setting;
import me.bannock.donutguard.ui.obf.settings.SettingFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public abstract class ListSetting<T> extends Setting<List<T>> implements ActionListener {

    private final Logger logger = LogManager.getLogger();

    public ListSetting(String name, ConfigDTO config, List<T> value, String fieldName) {
        super(name, config, value, fieldName);
    }

    private JPanel listPanel;
    private JPanel bottomBar;
    private JTextField inputField;

    @Override
    public JComponent createAndGetComponent() {
        logger.info("Constructing list setting ui component...");
        JPanel settingPanel = new JPanel();
        settingPanel.setLayout(new BorderLayout());
        settingPanel.setBorder(new EtchedBorder());

        // Scrollpane allows the user to scroll up and down if needed
        JScrollPane listContainer = new JScrollPane();
        listContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        listContainer.setBorder(new EtchedBorder());

        this.listPanel = new JPanel(true);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        refreshDisplayedList();
        listContainer.setViewportView(listPanel);

        // The bottom bar is used to type and add new entries into the list
        this.bottomBar = new JPanel();
        bottomBar.setLayout(new BorderLayout());
        this.inputField = new JTextField();
        JButton submitButton = new JButton("Submit");
        bottomBar.add(inputField, BorderLayout.CENTER);
        bottomBar.add(submitButton, BorderLayout.EAST);

        // The submit button should take the value from the input and add it to the list.
        // If the user wishes to remove the value then they can press "remove" next to the
        // entry on the list
        submitButton.addActionListener(this);

        // Label for the setting name at the top
        JLabel nameLabel = new JLabel("<html><h4>" + getName() + ":</h4></html>");

        settingPanel.add(listContainer, BorderLayout.CENTER);
        settingPanel.add(bottomBar, BorderLayout.SOUTH);
        settingPanel.add(nameLabel, BorderLayout.NORTH);

        logger.info("Successfully constructed list setting ui component");
        return settingPanel;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        logger.info("User pressed submit on a list setting, processing...");
        T value;
        try{
            value = getInputFieldValue();
        }catch (RuntimeException e){
            logger.warn("Could not submit value in list setting", e);
            return;
        }
        getInputField().setText("");
        getValue().add(value);
        refreshDisplayedList();
        logger.info("Value was successfully added to the list");
    }

    /**
     * Refreshes the list shown to the user
     */
    protected void refreshDisplayedList(){
        logger.info("Refreshing list setting panel...");
        listPanel.removeAll();
        for (T value : getValue()){
            ListSettingValueComponent<T> comp =
                    new ListSettingValueComponent<>(value, this);
            // idk why, but this makes it so swing places the comp at the top of the panel
            comp.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height)
            );
            listPanel.add(comp);
        }
        listPanel.revalidate();
        listPanel.repaint();
        logger.info("Successfully refreshed list setting panel");
    }

    /**
     * @return Processes the input in the text field and returns it as type T
     * @throws RuntimeException If there is an issue processing the input
     */
    protected abstract T getInputFieldValue() throws RuntimeException;

    protected JPanel getBottomBar() {
        return bottomBar;
    }

    /**
     * @return This setting's input field
     */
    protected JTextField getInputField() {
        return inputField;
    }

    protected JPanel getListPanel() {
        return listPanel;
    }

    @Override
    @Deprecated
    public Setting<List<T>> addFilter(SettingFilter<List<T>> filter) {
        logger.error("Filters are not compatible with list settings.");
        throw new RuntimeException("Filters are not supported with list settings.");
    }

    @Override
    @Deprecated
    public Setting<List<T>> removeFilter(SettingFilter<List<T>> filter) {
        logger.error("Filters are not compatible with list settings.");
        throw new RuntimeException("Filters are not supported with list settings.");
    }

    @Override
    @Deprecated
    public void clearFilters() {
        logger.error("Filters are not compatible with list settings.");
        throw new RuntimeException("Filters are not supported with list settings.");
    }

}
