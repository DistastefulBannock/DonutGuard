package me.bannock.donutguard.ui.about;

import com.google.inject.Inject;
import me.bannock.donutguard.ui.MainFrame;

import javax.swing.JFrame;

public class AboutFrame extends JFrame {

    @Inject
    public AboutFrame(MainFrame mainFrame, AboutView aboutView){
        setTitle("Details");
        setSize(350, 225);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(aboutView);
    }

}
