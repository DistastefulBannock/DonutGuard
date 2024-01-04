package me.bannock.donutguard.views;

import com.google.inject.Inject;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class DummyView extends JPanel {

    @Inject
    public DummyView(){
        setLayout(new BorderLayout());
        add(new JLabel("Dummy View"), BorderLayout.CENTER);
    }

}
