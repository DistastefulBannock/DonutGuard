package me.bannock.donutguard.ui.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JButton;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

public class HelpButton extends JButton {

    private final Logger logger = LogManager.getLogger();

    public HelpButton(String hoverText){
        super("?");
        setToolTipText(hoverText);
    }

    /**
     * Adds an action listener that opens a link when pressed
     * @param link The link the open when clicked
     * @return This
     */
    public HelpButton withLinkAction(URI link){
        addActionListener(evt -> {
            try {
                Desktop.getDesktop().browse(link);
            } catch (IOException e) {
                logger.warn(String.format("Couldn't open link \"%s\"", link), e);
            }
        });
        return this;
    }

}
