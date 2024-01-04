package me.bannock.donutguard.views;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;

public class ObfuscatorView extends JPanel {

    private JPanel sidePane;
    private JComponent currentView;

    @Inject
    public ObfuscatorView(Injector injector){
        super(true);
        setLayout(new BorderLayout());

        // Populate this map with any more views for the obfuscator
        Map<String, JComponent> obfuscatorViews = Map.of(
                "IO Settings", injector.getInstance(DummyView.class)
        );

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
