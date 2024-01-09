package me.bannock.donutguard.ui.about;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.ui.MainFrame;

import javax.swing.JFrame;

public class AboutFrame extends JFrame {

    @Inject
    public AboutFrame(Injector injector, MainFrame mainFrame){
        setTitle("Details");
        setSize(350, 225);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(injector.getInstance(AboutView.class));
    }

}
