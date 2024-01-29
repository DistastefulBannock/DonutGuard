package me.bannock.donutguard.ui.obf.settings.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.Setting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnumSetting<T extends Enum<?>> extends Setting<T> implements ActionListener {

    private final Logger logger = LogManager.getLogger();
    private final T[] values;

    /**
     * @param name The name of the setting
     * @param config The config DTO object reference
     * @param value The current value of the setting
     * @param fieldName The field name to modify via reflection
     * @param values The values to show in the combo box
     */
    @SafeVarargs
    public EnumSetting(String name, ConfigDTO config, T value , String fieldName, T... values) {
        super(name, config, value, fieldName);
        this.values = values;
    }

    private JComboBox<T> comboBox;

    @Override
    public JComponent createAndGetComponent() {
        logger.info("Creating new enum setting component...");

        JPanel container = new JPanel(true);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 0, 10));

        JLabel nameLabel = new JLabel(getName());
        container.add(nameLabel, BorderLayout.WEST);

        this.comboBox = new JComboBox<>(this.values);
        comboBox.setSelectedItem(getValue());
        comboBox.addActionListener(this);
        container.add(comboBox, BorderLayout.EAST);

        container.setMaximumSize(new Dimension(container.getMaximumSize().width,
                container.getPreferredSize().height));

        logger.info("Successfully created new enum setting component");
        return container;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        T oldValue = getValue();
        if (!setValue((T)this.comboBox.getSelectedItem()))
            this.comboBox.setSelectedItem(oldValue);
    }

}
