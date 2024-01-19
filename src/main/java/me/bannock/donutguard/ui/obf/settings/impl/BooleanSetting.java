package me.bannock.donutguard.ui.obf.settings.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.Setting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooleanSetting extends Setting<Boolean> implements ActionListener {

    private final Logger logger = LogManager.getLogger();

    public BooleanSetting(String name, ConfigDTO config, Boolean value, String fieldName) {
        super(name, config, value, fieldName);
    }
    private JCheckBox toggleSwitch;

    @Override
    public JComponent createAndGetComponent() {
        logger.info("Creating new boolean setting component...");

        JPanel container = new JPanel(true);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 0, 10));

        JLabel nameLabel = new JLabel(getName());
        container.add(nameLabel, BorderLayout.WEST);

        this.toggleSwitch = new JCheckBox();
        toggleSwitch.setSelected(getValue());
        toggleSwitch.addActionListener(this);
        container.add(toggleSwitch, BorderLayout.EAST);

        container.setMaximumSize(new Dimension(container.getMaximumSize().width,
                container.getPreferredSize().height));

        logger.info("Successfully created new boolean setting component");
        return container;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("Trying to toggle value...");
        if (!setValue(!getValue()))
            return;
        toggleSwitch.setSelected(getValue());
        toggleSwitch.revalidate();
        toggleSwitch.repaint();
        logger.info("Successfully toggled value");
    }

}
