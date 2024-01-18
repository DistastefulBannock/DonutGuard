package me.bannock.donutguard.ui.obf.settings.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.Setting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class IntegerSetting extends Setting<Integer> implements ChangeListener {

    private final Logger logger = LogManager.getLogger();

    private final Integer min, max, step;

    /**
     * @param name The name of the setting
     * @param config The config DTO object
     * @param value The default value of the setting
     * @param min The minimum value allowed for this setting
     * @param max The maximum value allowed for this setting
     * @param stepSize The step size that the user can use to change this setting
     * @param fieldName The name of the field to change in the config DTO class
     */
    public IntegerSetting(String name, ConfigDTO config, Integer value,
                          String fieldName, Integer min, Integer max, Integer stepSize) {
        super(name, config, value, fieldName);
        this.min = min;
        this.max = max;
        this.step = stepSize;
    }

    private JSpinner spinner;

    @Override
    public JComponent createAndGetComponent() {
        logger.info("Creating new integer setting component...");

        JPanel container = new JPanel(true);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 0, 10));

        JLabel nameLabel = new JLabel(getName());
        container.add(nameLabel, BorderLayout.WEST);

        this.spinner = new JSpinner();
        SpinnerModel model = new SpinnerNumberModel(getValue(), min, max, step);
        spinner.setModel(model);
        spinner.addChangeListener(this);
        ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setEditable(false);
        container.add(spinner, BorderLayout.EAST);

        container.setMaximumSize(new Dimension(container.getMaximumSize().width,
                container.getPreferredSize().height));

        logger.info("Successfully created new boolean setting component");
        return container;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        logger.info("Trying to change integer value...");
        int oldValue = getValue();
        if (!setValue((Integer) spinner.getValue())){
            spinner.setValue(oldValue);
            return;
        }
        logger.info("Successfully changed integer value");
    }

}
