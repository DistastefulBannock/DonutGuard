package me.bannock.donutguard.ui.about;

import com.google.inject.Inject;
import me.bannock.donutguard.utils.HwidUtil;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class AboutView extends JPanel {

    @Inject
    public AboutView(){
        setLayout(new BorderLayout());

        JLabel bottomLabel = new JLabel("1995-1998 BannockWare Industries. All rights reserved ");
        {
            Font bottomLabelFont = bottomLabel.getFont();
            bottomLabelFont = bottomLabelFont.deriveFont(Font.ITALIC, (float)(bottomLabelFont.getSize() * 0.75));
            bottomLabel.setForeground(bottomLabel.getForeground().brighter().brighter().brighter());
            bottomLabel.setFont(bottomLabelFont);
        }
        bottomLabel.setHorizontalAlignment(JLabel.RIGHT);

        JPanel informationPanel = getInformationPanel();

        add(bottomLabel, BorderLayout.SOUTH);
        add(informationPanel, BorderLayout.CENTER);
    }

    private JPanel getInformationPanel() {
        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));

        JLabel applicationTitle = new JLabel("DonutGuard™ Java Bytecode Mutator");
        applicationTitle.setFont(applicationTitle.getFont().deriveFont(Font.PLAIN, (float)(applicationTitle.getFont().getSize() * 1.32187193213)));

        JLabel info = new JLabel("<html>" +
                "HWID: 0x" + Integer.toHexString(~HwidUtil.getHwid()).toUpperCase() +
                "<br/>" +
                "Authorized: <font color=green>true</font>" +
                "<br/>" +
                "Server Status: <font color=red>IOException</font>" +
                "<br/><br/>" +
                "Developer(s): @DistastefulBnok" +
                "<br/>" +
                "Support: github.com/DistastefulBannock/DonutGuard" +
                "</html>");
        info.setFont(info.getFont().deriveFont(Font.PLAIN, (float)(info.getFont().getSize() * 1.02383822)));

        informationPanel.add(applicationTitle);
        informationPanel.add(info);

        return informationPanel;
    }

}
