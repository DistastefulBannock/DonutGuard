package me.bannock.donutguard.ui.obf.views;

import com.google.inject.Inject;
import me.bannock.donutguard.ui.obf.models.ObfuscatorModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class ObfuscatorView extends JPanel {

    private final Logger logger = LogManager.getLogger();
    private final JPanel sidePane;
    private JComponent currentView;

    @Inject
    public ObfuscatorView(ObfuscatorModel model, ObfuscatorStartupView startupView) {
        super(true);
        logger.info("Constructing obfuscator view...");
        setLayout(new BorderLayout());

        // Populate this map with any more views for the obfuscator
        Map<String, JComponent> tmpObfuscatorViews;
        try{
            tmpObfuscatorViews = model.getObfuscatorViews();
        }catch (Exception e){
            logger.error("Something when wrong while grabbing the views from the model!", e);
            tmpObfuscatorViews = new HashMap<>();
        }
        // Because we use lambdas with this var later
        Map<String, JComponent> obfuscatorViews = tmpObfuscatorViews;


        // Scroll pane so we don't have to worry about the amount of categories in the side pane
        JScrollPane sidePaneContainer = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // Putting the scrollbar on the left looks better
        sidePaneContainer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Populating the side pane with buttons through a map,
        // so we can easily add more buttons in the future
        sidePane = new JPanel();
        sidePane.setLayout(new BoxLayout(sidePane, BoxLayout.Y_AXIS));
        obfuscatorViews.keySet().forEach(key -> {
            JButton viewSelectionButton = new JButton(key);
            viewSelectionButton.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, viewSelectionButton.getPreferredSize().height)
            );
            viewSelectionButton.addActionListener(e -> setView(obfuscatorViews.get(key)));
            sidePane.add(viewSelectionButton);
        });
        sidePaneContainer.setViewportView(sidePane);

        // Nothing will show unless we add the sidePane and contentPane to this view
        add(sidePaneContainer, BorderLayout.WEST);
        setView(startupView);

        logger.info("Obfuscator view successfully constructed");
    }

    /**
     * Sets the current view of the frame
     * @param view The view to set
     */
    private void setView(JComponent view){
        logger.info("Swapping obfuscator view to " + view.getClass().getSimpleName() + "...");
        if(currentView != null){
            remove(currentView);
        }
        currentView = view;
        add(currentView, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

}
