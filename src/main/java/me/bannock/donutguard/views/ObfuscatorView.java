package me.bannock.donutguard.views;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.views.obf.ObfuscatorSettingsView;
import me.bannock.donutguard.views.obf.impl.FileSetting;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObfuscatorView extends JPanel {

    private final JPanel sidePane;
    private JComponent currentView;

    @Inject
    public ObfuscatorView(ConfigDTO config) {
        super(true);
        setLayout(new BorderLayout());

        // Populate this map with any more views for the obfuscator
        Map<String, JComponent> obfuscatorViews = new LinkedHashMap<>();
        obfuscatorViews.put("IO Settings", new ObfuscatorSettingsView(
                new FileSetting("input", config, config.input, "input",
                        ".jar, .zip","jar", "zip"),
                new FileSetting("output", config, config.output, "output",
                        ".jar, .zip","jar", "zip")));
        obfuscatorViews.put("Dictionary Settings", new ObfuscatorSettingsView());

        // Populating the side pane with buttons through a map so we can easily add more buttons in the future
        sidePane = new JPanel();
        sidePane.setLayout(new GridLayout(Math.max(obfuscatorViews.size(), 20), 1));
        obfuscatorViews.keySet().forEach(key -> {
            JButton viewSelectionButton = new JButton(key);
            viewSelectionButton.addActionListener(e -> setView(obfuscatorViews.get(key)));
            sidePane.add(viewSelectionButton);
        });

        // Nothing will show unless we add the sidePane and contentPane to this view
        add(sidePane, BorderLayout.WEST);
        setView(new DummyView());
    }

    /**
     * Sets the current view of the frame
     * @param view The view to set
     */
    private void setView(JComponent view){
        SwingUtilities.invokeLater(() -> {
            if(currentView != null){
                remove(currentView);
            }
            currentView = view;
            add(currentView, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }

}
