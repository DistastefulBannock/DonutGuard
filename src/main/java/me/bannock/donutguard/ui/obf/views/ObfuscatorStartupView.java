package me.bannock.donutguard.ui.obf.views;

import me.bannock.donutguard.utils.ResourceUtils;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class ObfuscatorStartupView extends JScrollPane {

    public ObfuscatorStartupView(){
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel(ResourceUtils.readString("ui/obf/startupPage.html"));
        label.setVerticalAlignment(JLabel.TOP);

        setViewportView(label);
    }

}
