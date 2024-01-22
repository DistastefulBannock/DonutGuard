package me.bannock.donutguard.ui.obf.views;

import me.bannock.donutguard.ui.obf.settings.Setting;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

public class ObfuscatorSettingsView extends JScrollPane {

    public ObfuscatorSettingsView(Setting<?>... settings){
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //setLayout(new GridLayout(Math.max(settings.length, 10), 1));
        setLayout(new ScrollPaneLayout());

        JPanel settingsPanel = new JPanel(true);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        for(Setting<?> setting : settings){
            settingsPanel.add(setting.createAndGetComponent());
        }
        setViewportView(settingsPanel);
    }

}
