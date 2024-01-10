package me.bannock.donutguard.ui.obf.settings.list;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListSettingValueComponent<T> extends JPanel implements ActionListener {

    private final Logger logger = LogManager.getLogger();
    private final T value;
    private final ListSetting<T> parentSetting;

    public ListSettingValueComponent(T value, ListSetting<T> parentSetting){
        logger.info("Constructing list value setting component...");
        this.value = value;
        this.parentSetting = parentSetting;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(4, 0, 4, 0));

        // This is the label that will be displayed on the left side of the panel
        add(new JLabel(value.toString()), BorderLayout.WEST);

        // Buttons appear on the right side under certain conditions
        // This panel allows us to stack multiple buttons side by side
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton remove = new JButton("Remove");
        remove.addActionListener(this);
        buttons.add(remove);

        add(buttons, BorderLayout.EAST);
        logger.info("Successfully constructed list value setting component");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("User pressed remove on list setting value...");
        parentSetting.getValue().remove(value);
        parentSetting.refreshDisplayedList();
        logger.info("Successfully removed value from list setting");
    }

}
